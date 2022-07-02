package engine

import (
	"bytes"
	"fmt"
	"sort"
	"strconv"
	"strings"

	"github.com/KompiTech/fabric-cc-core/v2/pkg/kompiguard"
	. "github.com/KompiTech/fabric-cc-core/v2/pkg/konst"
	. "github.com/KompiTech/rmap"
	lru "github.com/hashicorp/golang-lru"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
	"github.com/pkg/errors"
)

const CacheSize = 1000 // minimum is 1!

// Registry manages assets and singletons
type Registry struct {
	ctx       ContextInterface
	changelog *Changelog      // changelog handler is lazy initialized when it is needed
	changeSet map[string]Rmap // modifications done to state in this TX. key: composite state key

	riCache  *lru.Cache // caches recently used registryItems. key: composite state key, value: Rmap
	sCache   *lru.Cache // caches recently used singletons. key: composite state key, value: Rmap
	aCache   *lru.Cache // caches recently read asset instances. key: composite state key, value: Rmap
	lvaCache *lru.Cache // caches latest versions for asset names. key: lowercase asset name, value: int
	lvsCache *lru.Cache // caches latest versions for singleton names. key lowercase singleton name, value: int
}

type RegistryInterface interface {
	BulkUpsertItems(items []bulkItem) error
	UpsertItem(registryItemToUpsert Rmap, assetName string) (Rmap, int, error)
	GetThisIdentity() (Rmap, error)
	GetThisIdentityResolved() (Rmap, error)
	GetItem(name string, requestedVersion int) (Rmap, int, error)
	MakeAsset(name, id string, version int) (Rmap, error)
	MarkAssetAsExisting(name, id string, data Rmap) error
	GetAsset(name, id string, resolve bool, failOnNotFound bool) (Rmap, error)
	ListItems() ([]string, error)
	ListSingletons() ([]string, error)
	PutAsset(asset Rmap, isCreate bool) error
	GetQueryIterator(name string, query Rmap, bookmark string, pageSize int) (IteratorInterface, string, error)
	QueryAssets(name string, query Rmap, bookmark string, resolve bool, paginate bool, pageSize int) ([]Rmap, string, error)
	DeleteAsset(asset Rmap) error
	GetAssetHistory(asset Rmap) ([]Rmap, error)
	UpsertSingleton(singletonItemToUpsert Rmap, singletonName string) (int, error)
	BulkUpsertSingletons(items []bulkItem) error
	ExistsSingleton(name string, version int) (bool, error)
	ExistsAsset(name, id string) (bool, error)
	GetSingleton(name string, version int) (Rmap, int, error)
}

// bulkItem is used when inserting multiple items
type bulkItem struct {
	Name  string
	Value Rmap
}

// NewEngine initializes Registry from existing state or bootstraps empty one
func newRegistry(ctx ContextInterface) (RegistryInterface, error) {
	riCache, err := lru.New(CacheSize)
	if err != nil {
		return nil, err
	}

	sCache, err := lru.New(CacheSize)
	if err != nil {
		return nil, err
	}

	aCache, err := lru.New(CacheSize)
	if err != nil {
		return nil, err
	}

	lvaCache, err := lru.New(CacheSize)
	if err != nil {
		return nil, err
	}

	lvsCache, err := lru.New(CacheSize)
	if err != nil {
		return nil, err
	}

	return &Registry{
		ctx,
		nil,
		map[string]Rmap{},
		riCache,
		sCache,
		aCache,
		lvaCache,
		lvsCache,
	}, nil
}

// writeChangelog writes to changelog if ChangelogItem contains any change
func (r *Registry) writeChangelog(ci ChangelogItem) error {
	if ci.IsEmpty() {
		// if ChangelogItem does not contains any changes, do not write it
		return nil
	}

	if r.changelog == nil {
		cl, err := NewChangelog(r.ctx)
		if err != nil {
			return errors.Wrap(err, "NewChangelog() failed")
		}
		r.changelog = cl
	}

	if err := r.changelog.Create(ci); err != nil {
		return errors.Wrap(err, "r.changelog.Create() failed")
	}
	return nil
}

// BulkUpsertItems upsert multiple items, updates lvm and changelog
func (r *Registry) BulkUpsertItems(items []bulkItem) error {
	now, err := r.ctx.Time()
	if err != nil {
		return errors.Wrap(err, "ctx.Time() failed")
	}

	ci := ChangelogItem{
		TxId:      r.ctx.Stub().GetTxID(),
		Timestamp: now,
		Changes:   make([]Change, 0, len(items)),
	}

	for _, bui := range items {
		_, change, _, err := r.upsertItem(bui.Value, bui.Name)
		if err != nil {
			return errors.Wrap(err, "r.upsertItem() failed")
		}

		if !change.IsEmpty() {
			// only add change if it did already occur - in case of no schema change, do not append
			ci.Changes = append(ci.Changes, change)
		}
	}

	if len(ci.Changes) > 0 {
		// only write changelog if there are any changes
		if err := r.writeChangelog(ci); err != nil {
			return errors.Wrap(err, "r.writeChangelog() failed")
		}
	}

	return nil
}

// UpsertItem upsert single item, updates lvm and changelog
func (r *Registry) UpsertItem(registryItemToUpsert Rmap, assetName string) (Rmap, int, error) {
	regItem, change, version, err := r.upsertItem(registryItemToUpsert, assetName)
	if err != nil {
		return Rmap{}, -1, errors.Wrap(err, "r.upsertItem() failed")
	}

	if !change.IsEmpty() {
		// only write to changelog if there are changes
		now, err := r.ctx.Time()
		if err != nil {
			return Rmap{}, -1, errors.Wrap(err, "ctx.Time() failed")
		}

		ci := ChangelogItem{
			TxId:      r.ctx.Stub().GetTxID(),
			Timestamp: now,
			Changes:   []Change{change},
		}

		if err := r.writeChangelog(ci); err != nil {
			return Rmap{}, -1, errors.Wrap(err, "r.writeChangelog() failed")
		}
	}

	return regItem, version, nil
}

// GetThisIdentity returns identity asset for current user
// it also manages migrating
func (r Registry) GetThisIdentity() (Rmap, error) {
	myFP, err := GetMyFingerprint(r.ctx)
	if err != nil {
		return Rmap{}, errors.Wrap(err, "getMyFingerprint() failed")
	}

	thisIdentity, err := r.GetAsset(IdentityAssetName, myFP, false, false)
	if err != nil {
		return Rmap{}, errors.Wrap(err, "r.GetAsset() failed")
	}

	if thisIdentity.IsEmpty() {
		return Rmap{}, ErrorBadRequest(fmt.Sprintf("cannot get identity for fingerprint: %s. Did you call identityAddMe?", myFP))
	}

	return thisIdentity, nil
}

// GetThisIdentityResolved returns identity asset for current user and resolves all roles it has
func (r Registry) GetThisIdentityResolved() (Rmap, error) {
	thisIdentity, err := r.GetThisIdentity()
	if err != nil {
		return Rmap{}, errors.Wrap(err, "r.GetThisIdentity() failed")
	}

	if thisIdentity.Exists(IdentityRolesKey) {
		// manual resolve of roles, to prevent infinite recursion if some user business logic does the same
		roles, err := thisIdentity.GetIterable(IdentityRolesKey)
		if err != nil {
			return Rmap{}, errors.Wrap(err, "thisIdentity.GetIterable() failed")
		}

		for roleIndex, roleI := range roles {
			roleAsset, err := r.GetAsset(RoleAssetName, roleI.(string), false, true)
			if err != nil {
				return Rmap{}, errors.Wrap(err, "reg.GetAsset() failed")
			}

			// set resolved role asset
			if err := thisIdentity.SetJPtr("/"+IdentityRolesKey+"/"+strconv.Itoa(roleIndex), roleAsset.Mapa); err != nil {
				return Rmap{}, errors.Wrap(err, "thisIdentity.SetJPtr() failed")
			}
		}
	}

	return thisIdentity, nil
}

// upsertItem creates new or updates existing registryItem
// If latest version matches the one being upserted, nothing is done, no error is returned
// returns RegItem, Change, actual version
func (r *Registry) upsertItem(registryItemToUpsert Rmap, assetName string) (Rmap, Change, int, error) {
	assetName = strings.ToLower(assetName)

	if assetName == RoleAssetName || assetName == IdentityAssetName {
		return Rmap{}, Change{}, -1, fmt.Errorf("unable to upsert registry for internal asset name: %s", assetName)
	}

	// validate registryItem form
	if err := registryItemToUpsert.ValidateSchemaBytes([]byte(RegistryItemSchema)); err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "registryItemToUpsert.ValidateSchemaBytes() failed")
	}

	// validate schema itself
	sch, err := registryItemToUpsert.GetRmap("schema")
	if err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "registryItemToUpsert.GetRmap() failed")
	}

	if !sch.IsValidJSONSchema() {
		return Rmap{}, Change{}, -1, fmt.Errorf("schema for: %s is not a valid JSON schema", assetName)
	}

	// JSONSchema "type" must be "object"
	typ, err := registryItemToUpsert.GetJPtrString(SchemaTypeJPtr)
	if err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "registryItemToUpsert.GetJPtrString() failed")
	}

	if typ != "object" {
		return Rmap{}, Change{}, -1, fmt.Errorf("value: %s, type on top level is not: object", registryItemToUpsert.String())
	}

	// JSONSchema "additionalProperties" must be false
	ap, err := registryItemToUpsert.GetJPtrBool(SchemaAdditionalPropertiesJPtr)
	if err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "registryItemToUpsert.GetJPtrBool() failed")
	}
	if ap {
		return Rmap{}, Change{}, -1, fmt.Errorf("value: %s, additionalProperties on top level must be set to false", registryItemToUpsert.String())
	}

	// get iterator of existing registry items
	iterator, err := r.ctx.Stub().GetStateByPartialCompositeKey(RegistryItemPrefix, []string{strings.ToUpper(assetName)})
	if err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "ctx.Stub().GetStateByPartialCompositeKey() failed")
	}
	defer func() { _ = iterator.Close() }()

	latestVersion := 0
	var latestData []byte

	for iterator.HasNext() {
		item, err := iterator.Next()
		if err != nil {
			return Rmap{}, Change{}, -1, errors.Wrap(err, "iterator.Next() failed")
		}
		_, elems, err := r.ctx.Stub().SplitCompositeKey(item.GetKey())
		if err != nil {
			return Rmap{}, Change{}, -1, errors.Wrap(err, "ctx.Stub().SplitCompositeKey() failed")
		}

		// parse version as int from last elem of composite key
		version, err := strconv.Atoi(elems[len(elems)-1])
		if err != nil {
			return Rmap{}, Change{}, -1, errors.Wrap(err, "strconv.Atoi() failed")
		}

		// update latest version info if newer
		if version > latestVersion {
			latestVersion = version
			latestData = item.GetValue()
		}
	}

	var targetVersion int
	var isCreate bool

	if latestVersion == 0 {
		// no version exists, create version 1
		targetVersion = 1
		isCreate = true
	} else {
		// some version already exists, latestVersion contains the latest number
		latestRegistryItem, err := NewFromBytes(latestData)
		if err != nil {
			return Rmap{}, Change{}, -1, errors.Wrap(err, "rmap.NewFromBytes() failed")
		}

		// compare latest by hash with one being upserted
		if registryItemToUpsert.Hash() == latestRegistryItem.Hash() {
			// has is identical to latest, do not update, do not fail
			return latestRegistryItem, Change{}, latestVersion, nil
		}
		// new version is +1 latest
		targetVersion = latestVersion + 1
		// it is not allowed to change destination between versions
		latestDestination, err := latestRegistryItem.GetString(RegistryItemDestinationKey)
		if err != nil {
			return Rmap{}, Change{}, -1, errors.Wrap(err, "latestRegistryItem.GetString() failed")
		}

		newDestination, err := registryItemToUpsert.GetString(RegistryItemDestinationKey)
		if err != nil {
			return Rmap{}, Change{}, -1, errors.Wrap(err, "registryItemToUpsert.GetString() failed")
		}

		if latestDestination != newDestination {
			return Rmap{}, Change{}, -1, fmt.Errorf("unable to change destination of: %s, from: %s, to: %s", assetName, latestDestination, newDestination)
		}
		isCreate = false
	}
	// create composite key for new registry item: RegistryItemPrefix | ASSET_NAME | VERSION
	key, err := r.ctx.Stub().CreateCompositeKey(RegistryItemPrefix, []string{strings.ToUpper(assetName), strconv.Itoa(targetVersion)})
	if err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "ctx.Stub().CreateCompositeKey(RegistryItemPrefix, ...) failed")
	}

	// write new registryItem
	if err := putRmapToState(r.ctx, key, true, registryItemToUpsert); err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "putRmapToState(registryItem) failed")
	}

	// create composite key for new latest item: LatestRegistryItemPrefix | ASSET_NAME
	latestKey, err := r.ctx.Stub().CreateCompositeKey(LatestRegistryItemPrefix, []string{strings.ToUpper(assetName)})
	if err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "r.ctx.Stub().CreateCompositeKey(LatestRegistryItemPrefix, ...) failed")
	}

	// create latest registry obj
	latestObj := NewFromMap(map[string]interface{}{
		LatestObjNameKey:    strings.ToLower(assetName),
		LatestObjVersionKey: targetVersion,
	})

	// create/update latestObj
	if err := putRmapToState(r.ctx, latestKey, isCreate, latestObj); err != nil {
		return Rmap{}, Change{}, -1, errors.Wrap(err, "putRmapToState(latestObj) failed")
	}

	// write to cache
	r.riCache.Add(key, registryItemToUpsert)

	var operation string
	if isCreate {
		operation = ChangelogCreateOperation
	} else {
		operation = ChangelogUpdateOperation
	}

	return registryItemToUpsert, Change{
		AssetName: assetName,
		Version:   targetVersion,
		Operation: operation,
	}, targetVersion, nil
}

// GetItem loads existing registryItem from state
// returns registry item and its actual version
func (r *Registry) GetItem(name string, requestedVersion int) (Rmap, int, error) {
	name = strings.ToLower(name)
	// identity, role assets have their schema hardcoded
	if name == IdentityAssetName || name == RoleAssetName {
		var hardcodedSchema []byte
		if name == IdentityAssetName {
			hardcodedSchema = []byte(IdentitySchema)
		}

		if name == RoleAssetName {
			hardcodedSchema = []byte(RoleSchema)
		}

		schema, err := NewFromBytes(hardcodedSchema)
		if err != nil {
			return Rmap{}, -1, errors.Wrap(err, "rmap.NewFromBytes() failed")
		}

		fakeRegistryItem := NewFromMap(map[string]interface{}{
			RegistryItemDestinationKey: StateDestinationValue,
			RegistryItemSchemaKey:      schema,
		})

		// hardcoded assets are always version 1
		return fakeRegistryItem, 1, nil
	}

	var version int

	if requestedVersion <= 0 {
		// version placeholder is used -> actual version needs to be determined
		// try getting latest version from lvaCache
		cached, exists := r.lvaCache.Get(name) //r.lvaCache[name]
		if !exists {
			// cache record does not exist, needs to be fetched from state
			// latest requestedVersion is requested, fetch it from latestObj
			// create composite key for new latest item: LatestRegistryItemPrefix | ASSET_NAME
			latestKey, err := r.ctx.Stub().CreateCompositeKey(LatestRegistryItemPrefix, []string{strings.ToUpper(name)})
			if err != nil {
				return Rmap{}, -1, errors.Wrap(err, "r.ctx.Stub().CreateCompositeKey(LatestRegistryItemPrefix, ...) failed")
			}

			latestObj, err := newRmapFromState(r.ctx, latestKey, true)
			if err != nil {
				return Rmap{}, -1, errors.Wrap(err, "newRmapFromState(latestKey, ...) failed")
			}

			latestName, err := latestObj.GetString(LatestObjNameKey)
			if err != nil {
				return Rmap{}, -1, errors.Wrap(err, "latestObj.GetString() failed")
			}

			version, err = latestObj.GetInt(LatestObjVersionKey)
			if err != nil {
				return Rmap{}, -1, errors.Wrap(err, "latestObj.GetInt() failed")
			}

			if strings.ToLower(latestName) != name {
				return Rmap{}, -1, fmt.Errorf("latestKey: %s, unexpected name: %s, expected: %s", latestKey, latestName, name)
			}

			// set in cache to save future access
			r.lvaCache.Add(name, version)
		} else {
			// found in cache, use cached
			version = cached.(int)
		}
	} else {
		// concrete version is requested
		version = requestedVersion
	}

	key, err := r.ctx.Stub().CreateCompositeKey(RegistryItemPrefix, []string{strings.ToUpper(name), strconv.Itoa(version)})
	if err != nil {
		return Rmap{}, -1, errors.Wrap(err, "ctx.Stub().CreateCompositeKey() failed")
	}

	var registryItem Rmap
	cachedRegistryItem, cachedExists := r.riCache.Get(key)
	if cachedExists {
		registryItem = cachedRegistryItem.(Rmap)
	} else {
		registryItem, err = newRmapFromState(r.ctx, key, false)
		if err != nil {
			return Rmap{}, -1, errors.Wrap(err, "newRmapFromState() failed")
		}
		if registryItem.IsEmpty() {
			return Rmap{}, -1, ErrorNotFound(fmt.Sprintf("registryItem name: %s, version: %d not found", name, version))
		}
		r.riCache.Add(key, registryItem)
	}

	return registryItem, version, err
}

// MakeAsset creates asset with service keys set
func (r Registry) MakeAsset(name, id string, version int) (Rmap, error) {
	_, actualVersion, err := r.GetItem(name, version)
	if err != nil {
		return Rmap{}, errors.Wrap(err, "r.GetItem() failed")
	}

	var idKey string
	// identity is the only asset identified by fingerprint instead of uuid key
	if name == IdentityAssetName {
		idKey = AssetFingerprintKey
	} else {
		idKey = AssetIdKey
	}

	asset := NewFromMap(map[string]interface{}{
		idKey:           id,
		AssetDocTypeKey: strings.ToUpper(name),
		AssetVersionKey: actualVersion,
	})

	return asset, nil
}

func (r *Registry) getAssetCompositeKey(name, id string) (string, error) {
	return r.ctx.Stub().CreateCompositeKey(strings.ToUpper(name), []string{strings.ToLower(id)})
}

// MarkAssetAsExisting emulates asset existence for WalkReferences and other methods (old TXS refactor)
// data are presented in ChangeSet as were sent by the client and are accessible
// after executing business logic, changeSet version is replaced by its result
func (r *Registry) MarkAssetAsExisting(name, id string, data Rmap) error {
	key, err := r.getAssetCompositeKey(name, id)
	if err != nil {
		return errors.Wrap(err, "r.getAssetCompositeKey() failed")
	}

	r.aCache.Add(key, data)
	r.changeSet[key] = data

	return nil
}

// GetAsset loads asset instance from persistent storage (state or private data - depends on configuration)
func (r *Registry) GetAsset(name, id string, resolve bool, failOnNotFound bool) (Rmap, error) {
	// get composite state key
	key, err := r.getAssetCompositeKey(name, id)
	if err != nil {
		return Rmap{}, errors.Wrap(err, "r.getAssetCompositeKey() failed")
	}

	var asset Rmap

	// check, if asset is in current changeSet (higher priority than cache)
	if changed, exists := r.changeSet[key]; exists {
		asset = changed
	} else {
		// check, if asset is in cache
		if cachedI, exists := r.aCache.Get(key); exists {
			// aCache uses two kinds of values: nil (to signify only eventual existence) and actual value
			// GetAsset requires actual value
			cached := cachedI.(Rmap)
			if cached.Mapa != nil {
				asset = cached
			}
		}
	}

	if asset.Mapa == nil {
		// both changeSet and cache missed, asset needs to be retrieved from state
		// get registryItem for asset name to determine storage location
		regItem, _, err := r.GetItem(name, -1)
		if err != nil {
			return Rmap{}, errors.Wrap(err, "reg.GetItem() failed")
		}

		destination, err := regItem.GetString(RegistryItemDestinationKey)
		if err != nil {
			return Rmap{}, errors.Wrap(err, "regItem.GetString() failed")
		}

		// load asset from state or private data
		asset, err = newRmapFromDestination(r.ctx, name, key, destination, failOnNotFound)
		if err != nil {
			return Rmap{}, errors.Wrap(err, "newRmapFromDestination() failed")
		}

		// save asset to cache in unresolved form (if it has some data)
		if !asset.IsEmpty() {
			r.aCache.Add(key, asset)
		}
	}

	// make a copy, so record in cache is not modified by resolving
	asset = asset.Copy()

	// resolve if needed
	// if not needed, skip resolved altogether (this is not the point when refs needs to be validated)
	if resolve {
		if err := (resolver{}).WalkReferences(r.ctx, asset, true); err != nil {
			return Rmap{}, errors.Wrap(err, "(resolver{}).WalkReferences() failed")
		}
	}

	return asset, nil
}

// listSomething iterates through all keys starting with prefix, it then returns a list of all
func (r Registry) listSomething(prefix string) ([]string, error) {
	// get iterator for prefix
	iterator, err := r.ctx.Stub().GetStateByPartialCompositeKey(prefix, []string{})
	if err != nil {
		return nil, errors.Wrap(err, "ctx.Stub().GetStateByPartialCompositeKey() failed")
	}
	defer func() { _ = iterator.Close() }()

	found := NewEmpty()

	for iterator.HasNext() {
		item, err := iterator.Next()
		if err != nil {
			return nil, errors.Wrap(err, "iterator.Next() failed")
		}

		// get version and name from composite key
		_, elems, err := r.ctx.Stub().SplitCompositeKey(item.GetKey())
		if err != nil {
			return nil, errors.Wrap(err, "ctx.Stub().SplitCompositeKey() failed")
		}

		if len(elems) != 2 {
			return nil, fmt.Errorf("invalid composite key len(): %d, expected: 2, elems: %+v", len(elems), elems)
		}

		// parse name as middle elem of composite key
		name := strings.ToLower(elems[len(elems)-2])
		found.Mapa[name] = struct{}{}
	}

	return found.KeysSliceString(), nil
}

// ListItems returns list of all available registryItem names
func (r Registry) ListItems() ([]string, error) {
	return r.listSomething(RegistryItemPrefix)
}

// ListSingletons lists all available singleton names
func (r Registry) ListSingletons() ([]string, error) {
	return r.listSomething(SingletonItemPrefix)
}

// PutAsset creates new or updates existing asset instance in persistent storage
func (r *Registry) PutAsset(asset Rmap, isCreate bool) error {
	return r.putAsset(asset, isCreate, false)
}

// putAsset creates new or updates existing asset instance in persistent storage
func (r *Registry) putAsset(asset Rmap, isCreate bool, skipWalkReferences bool) error {
	name, err := AssetGetDocType(asset)
	if err != nil {
		return errors.Wrap(err, "asset.GetDocType() failed")
	}

	version, err := AssetGetVersion(asset)
	if err != nil {
		return errors.Wrap(err, "asset.GetVersion() failed")
	}

	// get schema from registry
	regItem, _, err := r.GetItem(name, version)
	if err != nil {
		return errors.Wrap(err, "r.GetItem() failed")
	}

	schema, err := regItem.GetRmap(RegistryItemSchemaKey)
	if err != nil {
		return errors.Wrap(err, "regItem.GetRmap() failed")
	}

	if err := r.addGlobalDefinitionsToSchema(schema); err != nil {
		return errors.Wrap(err, "r.addGlobalDefinitionsToSchema() failed")
	}

	if name != IdentityAssetName {
		// identity is the only asset with service keys hardcoded
		if err := r.addServiceKeysToSchema(schema); err != nil {
			return errors.Wrap(err, "r.addServiceKeysToSchema() failed")
		}
	}

	oldDefsKey := r.ctx.GetConfiguration().SchemaDefinitionCompatibility

	if oldDefsKey != "" && oldDefsKey != SchemaDefinitionsKey {
		// if SchemaDefinitionCompatibility is set to same value as $defs, it is obvious error and do not replace anything
		// handle compat with pre draft-07 schemas, if enabled
		// first step - replace legacy definitions target to proper $defs form
		template := "#/%s/"

		oldD := []byte(fmt.Sprintf(template, oldDefsKey))
		newD := []byte(fmt.Sprintf(template, SchemaDefinitionsKey))

		// replacement is done on bytes form
		schemaBytes := bytes.Replace(schema.Bytes(), oldD, newD, -1)

		schema, err = NewFromBytes(schemaBytes)
		if err != nil {
			return err
		}

		// second step - add any legacy definitions to $defs already containing injected global definitions
		oldDefsI, exists := schema.Mapa[oldDefsKey]
		if exists {
			legacyDefs, err := NewFromInterface(oldDefsI)
			if err != nil {
				return err
			}

			delete(schema.Mapa, oldDefsKey)

			if err := schema.Inject(SchemaDefinitionsJPtr, legacyDefs); err != nil {
				return err
			}
		}
	}

	// validate JSON schema
	if err := asset.ValidateSchema(schema); err != nil {
		return errors.Wrapf(err, "asset.ValidateSchema() failed on assetName: %s", name)
	}

	if !skipWalkReferences {
		if err := (resolver{}).WalkReferences(r.ctx, asset, false); err != nil {
			return errors.Wrap(err, "(resolver{}).WalkReferences() failed")
		}
	}

	// determine PK
	var idKey string
	if name == IdentityAssetName {
		// identity is special, because it is identified by fingerprint, not IdKey
		idKey = AssetFingerprintKey
	} else {
		idKey = AssetIdKey
	}

	id, err := asset.GetString(idKey)
	if err != nil {
		return errors.Wrap(err, "asset.GetString() failed")
	}

	// determine state key
	key, err := r.getAssetCompositeKey(name, id)
	if err != nil {
		return errors.Wrap(err, "r.getAssetCompositeKey() failed")
	}

	// determine storage location
	destination, err := regItem.GetString(RegistryItemDestinationKey)
	if err != nil {
		return errors.Wrap(err, "regItem.GetString() failed")
	}

	if destination == StateDestinationValue {
		if err := putRmapToState(r.ctx, key, isCreate, asset); err != nil {
			return errors.Wrap(err, "putRmapToState() failed")
		}
	} else {
		if err := putRmapToPrivateData(r.ctx, name, key, isCreate, asset); err != nil {
			return errors.Wrap(err, "putRmapToPrivateData() failed")
		}
	}

	// update changeSet and cache with concrete asset value
	r.changeSet[key] = asset
	r.aCache.Add(key, asset)

	return nil
}

// GetQueryIterator returns iterator for some rich query
// pageSize <=0 means no pagination, positive number selects pageSize (but TX then cannot be RW)
// remember to .Close() iterator when done with it
func (r *Registry) GetQueryIterator(name string, query Rmap, bookmark string, pageSize int) (IteratorInterface, string, error) {
	null := Iterator{}

	// check query for not unexpected keys, these will make chaincode panic if sent to CouchDB, which we do not want
	var invalidKeys []string
	allowedKeys, _ := NewFromSlice([]interface{}{QuerySelectorKey, QueryFieldsKey, QueryBookmarkKey, QueryLimitKey, QuerySortKey})

	for k := range query.Mapa {
		if !allowedKeys.Exists(k) {
			invalidKeys = append(invalidKeys, k)
		}
	}

	if len(invalidKeys) > 0 {
		sort.Strings(invalidKeys)
		return null, "", fmt.Errorf("unexpected key(s) in query: %s. only: %s are allowed", strings.Join(invalidKeys, ","), strings.Join(allowedKeys.KeysSliceString(), ","))
	}

	if !query.Exists(QuerySelectorKey) {
		// if no selector in query -> init empty
		query.Mapa[QuerySelectorKey] = NewEmpty().Mapa
	}

	if query.Exists(QueryFieldsKey) {
		// client wants some particular fields, we must make sure that all service keys are also present for business logic execution in next step
		docTypeExists, err := query.Contains(QueryFieldsKey, AssetDocTypeKey)
		if err != nil {
			return null, "", errors.Wrap(err, "query.ContainsJPtr() failed")
		}

		idKeyExists, err := query.Contains(QueryFieldsKey, AssetIdKey)
		if err != nil {
			return null, "", errors.Wrap(err, "query.ContainsJPtr() failed")
		}

		versionExists, err := query.Contains(QueryFieldsKey, AssetVersionKey)
		if err != nil {
			return null, "", errors.Wrap(err, "query.ContainsJPtr() failed")
		}

		overridesExists, err := query.Contains(QueryFieldsKey, OverridesKey)
		if err != nil {
			return null, "", errors.Wrap(err, "query.ContainsJPtr() failed")
		}

		fields, err := query.GetIterable(QueryFieldsKey)
		if err != nil {
			return null, "", errors.Wrap(err, "query.GetIterable() failed")
		}

		if !docTypeExists {
			fields = append(fields, AssetDocTypeKey)
		}

		if !idKeyExists {
			fields = append(fields, AssetIdKey)
		}

		if !versionExists {
			fields = append(fields, AssetVersionKey)
		}

		if !overridesExists {
			fields = append(fields, OverridesKey)
		}

		query.Mapa[QueryFieldsKey] = fields
	}

	// set docType in selector from name
	if err := query.SetJPtr("/"+QuerySelectorKey+"/"+AssetDocTypeKey, strings.ToUpper(name)); err != nil {
		return null, "", errors.Wrap(err, "query.SetJPtr() failed")
	}

	// we assume that most of results from query will be assets with latest version present, so fetch it now
	registryItem, _, err := r.GetItem(name, -1)
	if err != nil {
		return null, "", errors.Wrap(err, "reg.GetItem() failed")
	}

	// determine which DB to query - state or private data
	destination, err := registryItem.GetString(RegistryItemDestinationKey)
	if err != nil {
		return null, "", errors.Wrap(err, "registryItem.GetString() failed")
	}

	var metadata *pb.QueryResponseMetadata
	var iter shim.StateQueryIteratorInterface

	// get iter for correct DB
	if destination == StateDestinationValue {
		if pageSize > 0 {
			iter, metadata, err = r.ctx.Stub().GetQueryResultWithPagination(query.String(), int32(pageSize), bookmark)
			if err != nil {
				return null, "", errors.Wrap(err, "ctx.Stub().GetQueryResultWithPagination() failed")
			}
		} else {
			iter, err = r.ctx.Stub().GetQueryResult(query.String())
			if err != nil {
				return null, "", errors.Wrap(err, "ctx.Stub().GetQueryResult()")
			}
		}
	} else {
		iter, err = r.ctx.Stub().GetPrivateDataQueryResult(strings.ToUpper(name), query.String())
		if err != nil {
			return null, "", errors.Wrap(err, "ctx.Stub().GetPrivateDataQueryResult() failed")
		}
		// fabric 1.4 does not support pagination for private data, fake empty bookmark
		metadata = &pb.QueryResponseMetadata{}
	}

	if pageSize > 0 {
		// trim extra quotes returned by Fabric
		bookmark = strings.Replace(metadata.GetBookmark(), `"`, "", -1)
	} else {
		bookmark = ""
	}

	thisIter := Iterator{
		ctx:      r.ctx,
		iterator: iter,
	}

	return thisIter, bookmark, nil
}

// QueryAssets does rich query on CouchDB state database. Exhausts iterator and returns assets as slice. Not efficient for large datasets, use GetQueryIterator() and manage iterator yourself
func (r *Registry) QueryAssets(name string, query Rmap, bookmark string, resolve bool, paginate bool, pageSize int) ([]Rmap, string, error) {
	// paginate param is duplicate with pageSize, but left for compatibility
	var realPageSize int

	if paginate {
		realPageSize = pageSize
	}

	// new API cares only for pageSize param, if it is greater than 0, then it paginates
	iter, bookmark, err := r.GetQueryIterator(name, query, bookmark, realPageSize)
	if err != nil {
		return nil, "", err
	}

	defer (func() {
		_ = iter.Close()
	})()

	var outputSlice []Rmap

	for {
		nextAsset, err := iter.Next(resolve)
		if err != nil {
			return nil, "", err
		}

		if nextAsset == nil {
			// end of iterator
			break
		}

		outputSlice = append(outputSlice, *nextAsset)
	}

	return outputSlice, bookmark, nil
}

// addGlobalDefinitionsToSchema adds all hardcoded definitions to schema
// TODO storage for user defined definitions
func (r Registry) addGlobalDefinitionsToSchema(schema Rmap) error {
	definitions, err := NewFromBytes([]byte(SchemaDefinitions))
	if err != nil {
		return errors.Wrap(err, "NewFromBytes() failed")
	}

	if err := schema.Inject(SchemaDefinitionsJPtr, definitions); err != nil {
		return errors.Wrap(err, "schema.Inject() failed")
	}

	return nil
}

// addServiceKeysToSchema adds all service keys to schema properties and to required properties
func (r Registry) addServiceKeysToSchema(schema Rmap) error {
	serviceKeys, err := NewFromBytes([]byte(SchemaServiceKeys))
	if err != nil {
		return errors.Wrap(err, "NewFromBytes() failed")
	}

	// add service keys to properties
	if err := schema.Inject(SchemaPropertiesJPtr, serviceKeys); err != nil {
		return errors.Wrap(err, "schema.Inject() failed")
	}

	// add service keys to required properties
	var required []interface{}

	if schema.Exists(SchemaRequiredKey) {
		required, err = schema.GetIterable(SchemaRequiredKey)
		if err != nil {
			return errors.Wrap(err, "schema.GetIterable() failed")
		}
	} else {
		required = []interface{}{}
	}

	required = append(required, AssetIdKey, AssetDocTypeKey, AssetVersionKey)

	schema.Mapa[SchemaRequiredKey] = required

	return nil
}

// DeleteAsset deletes existing asset instance from persistent storage
func (r *Registry) DeleteAsset(asset Rmap) error {
	docType, err := AssetGetDocType(asset)
	if err != nil {
		return errors.Wrap(err, "asset.GetDocType() failed")
	}

	docType = strings.ToUpper(docType)

	version, err := AssetGetVersion(asset)
	if err != nil {
		return errors.Wrap(err, "asset.GetVersion() failed")
	}

	item, _, err := r.GetItem(docType, version)
	if err != nil {
		return errors.Wrap(err, "r.GetItem() failed")
	}

	destination, err := item.GetString(RegistryItemDestinationKey)
	if err != nil {
		return errors.Wrap(err, "item.GetString() failed")
	}

	id, err := AssetGetID(asset)
	if err != nil {
		return errors.Wrap(err, "AssetGetID(asset) failed")
	}

	key, err := r.getAssetCompositeKey(docType, id)
	if err != nil {
		return errors.Wrap(err, "r.getAssetCompositeKey() failed")
	}

	if destination == StateDestinationValue {
		if err := r.ctx.Stub().DelState(key); err != nil {
			return errors.Wrap(err, "r.ctx.Stub().DelState() failed")
		}
	} else {
		if err := r.ctx.Stub().DelPrivateData(docType, key); err != nil {
			return errors.Wrap(err, "r.ctx.Stub().DelPrivateData() failed")
		}
	}

	delete(r.changeSet, key)

	return nil
}

// GetAssetHistory returns history for some asset instance
func (r Registry) GetAssetHistory(asset Rmap) ([]Rmap, error) {
	thisIdentity, err := r.GetThisIdentityResolved()
	if err != nil {
		return nil, errors.Wrap(err, "r.GetThisIdentityResolved() failed")
	}

	kmpg, err := kompiguard.New()
	if err != nil {
		return nil, errors.Wrap(err, "kompiguard.New() failed")
	}

	granted, reason, err := kmpg.EnforceAsset(asset, thisIdentity, "get_history")
	if err != nil {
		return nil, errors.Wrap(err, "kompiguard.New().EnforceAsset() failed")
	}

	if !granted {
		return nil, ErrorForbidden(reason)
	}

	docType, err := AssetGetDocType(asset)
	if err != nil {
		return nil, errors.Wrap(err, "assetGetDocType(asset) failed")
	}

	id, err := AssetGetID(asset)
	if err != nil {
		return nil, errors.Wrap(err, "assetGetID(asset) failed")
	}

	key, err := r.getAssetCompositeKey(docType, id)
	if err != nil {
		return nil, errors.Wrap(err, "r.getAssetCompositeKey() failed")
	}

	iterator, err := r.ctx.Stub().GetHistoryForKey(key)
	if err != nil {
		return nil, errors.Wrap(err, "r.ctx.Stub().GetHistoryForKey() failed")
	}

	defer (func() { _ = iterator.Close() })()

	var output []Rmap

	for iterator.HasNext() {
		next, err := iterator.Next()
		if err != nil {
			return nil, errors.Wrap(err, "iterator.Next() failed")
		}

		value, err := NewFromBytes(next.GetValue())
		if err != nil {
			return nil, errors.Wrap(err, "NewFromBytes() failed")
		}

		hItem := NewFromMap(map[string]interface{}{
			HistoryItemIsDeleteKey:  next.GetIsDelete(),
			HistoryItemTimestampKey: next.GetTimestamp(),
			HistoryItemTxIdKey:      next.GetTxId(),
			HistoryItemValueKey:     value.Mapa,
		})

		output = append(output, hItem)
	}

	return output, nil
}

// upsertSingleton upserts single singletonItem and creates latestObj
func (r *Registry) upsertSingleton(singletonItemToUpsert Rmap, singletonName string) (int, error) {
	if err := singletonItemToUpsert.ValidateSchemaBytes([]byte(SingletonItemSchema)); err != nil {
		return -1, errors.Wrap(err, "singletonItemToUpsert.ValidateSchemaBytes() failed")
	}

	// get iterator of existing singletons
	iterator, err := r.ctx.Stub().GetStateByPartialCompositeKey(SingletonItemPrefix, []string{strings.ToUpper(singletonName)})
	if err != nil {
		return -1, errors.Wrap(err, "ctx.Stub().GetStateByPartialCompositeKey() failed")
	}
	defer func() { _ = iterator.Close() }()

	latestVersion := 0
	var latestData []byte

	for iterator.HasNext() {
		item, err := iterator.Next()
		if err != nil {
			return -1, errors.Wrap(err, "iterator.Next() failed")
		}
		_, elems, err := r.ctx.Stub().SplitCompositeKey(item.GetKey())
		if err != nil {
			return -1, errors.Wrap(err, "ctx.Stub().SplitCompositeKey() failed")
		}

		// parse version as int from last elem of composite key
		version, err := strconv.Atoi(elems[len(elems)-1])
		if err != nil {
			return -1, errors.Wrap(err, "strconv.Atoi() failed")
		}

		// update latest version info if newer
		if version > latestVersion {
			latestVersion = version
			latestData = item.GetValue()
		}
	}

	var targetVersion int
	var isLatestCreate bool

	if latestVersion == 0 {
		// no version exists, create version 1
		targetVersion = 1
		isLatestCreate = true
	} else {
		// some version already exists, latestVersion contains the latest number
		latestSingletonItem, err := NewFromBytes(latestData)
		if err != nil {
			return -1, errors.Wrap(err, "rmap.NewFromBytes() failed")
		}

		// compare latest by hash with one being upserted
		if singletonItemToUpsert.Hash() == latestSingletonItem.Hash() {
			// has is identical to latest, do not update, do not fail
			return latestVersion, nil
		}

		// new version is +1 latest
		targetVersion = latestVersion + 1
		isLatestCreate = false
	}

	// create composite key for new singleton: PREFIX | NAME | VERSION
	key, err := r.ctx.Stub().CreateCompositeKey(SingletonItemPrefix, []string{strings.ToUpper(singletonName), strconv.Itoa(targetVersion)})
	if err != nil {
		return -1, errors.Wrap(err, "ctx.Stub().CreateCompositeKey() failed")
	}

	// write new singletonItem
	if err := putRmapToState(r.ctx, key, true, singletonItemToUpsert); err != nil {
		return -1, errors.Wrap(err, "putRmapToState() failed")
	}

	// create composite key for latestObj LatestSingletonPrefix | NAME
	latestKey, err := r.ctx.Stub().CreateCompositeKey(LatestSingletonPrefix, []string{strings.ToUpper(singletonName)})
	if err != nil {
		return -1, errors.Wrap(err, "ctx.Stub().CreateCompositeKey() failed")
	}

	latestObj := NewFromMap(map[string]interface{}{
		LatestObjNameKey:    strings.ToLower(singletonName),
		LatestObjVersionKey: targetVersion,
	})

	if err := putRmapToState(r.ctx, latestKey, isLatestCreate, latestObj); err != nil {
		return -1, errors.Wrap(err, "putRmapToState() failed")
	}

	return targetVersion, nil
}

// UpsertSingleton upserts single singletonItem and persists lsm
func (r *Registry) UpsertSingleton(singletonItemToUpsert Rmap, singletonName string) (int, error) {
	version, err := r.upsertSingleton(singletonItemToUpsert, singletonName)
	if err != nil {
		return -1, errors.Wrap(err, "r.upsertSingleton() failed")
	}

	return version, nil
}

// BulkUpsertSingletons upserts multiple singletonItems in one call and persists lsm
func (r *Registry) BulkUpsertSingletons(items []bulkItem) error {
	for _, item := range items {
		if _, err := r.upsertSingleton(item.Value, item.Name); err != nil {
			return errors.Wrap(err, "r.upsertSingleton() failed")
		}
	}

	return nil
}

// determines latest version for name and version
// if result is "" and error is nil, the latest key was not found and no such singleton exists
func (r *Registry) getSingletonKeyForVersion(name string, version int) (string, int, error) {
	if version <= 0 {
		// latest version needs to be determined
		// try getting latest version from cache
		cached, exists := r.lvsCache.Get(name)
		if !exists {
			// not found in cache
			// latest version is requested, fetch it from latestObj
			latestKey, err := r.ctx.Stub().CreateCompositeKey(LatestSingletonPrefix, []string{strings.ToUpper(name)})
			if err != nil {
				return "", -1, errors.Wrap(err, "r.ctx.Stub().CreateCompositeKey(LatestSingletonPrefix, ...) failed")
			}

			latestObj, err := newRmapFromState(r.ctx, latestKey, false)
			if err != nil {
				return "", -1, errors.Wrap(err, "newRmapFromState(latestKey, ...) failed")
			}

			if latestObj.IsEmpty() {
				return "", -1, nil // not found
			}

			latestName, err := latestObj.GetString(LatestObjNameKey)
			if err != nil {
				return "", -1, errors.Wrap(err, "latestObj.GetString() failed")
			}

			version, err = latestObj.GetInt(LatestObjVersionKey)
			if err != nil {
				return "", -1, errors.Wrap(err, "latestObj.GetInt() failed")
			}

			if strings.ToLower(latestName) != strings.ToLower(name) {
				return "", -1, fmt.Errorf("latestKey: %s, unexpected name: %s, expected: %s", latestKey, latestName, name)
			}

			//save in cache
			r.lvsCache.Add(name, version)
		} else {
			// cached value exists, use it
			version = cached.(int)
		}
	}

	key, err := r.ctx.Stub().CreateCompositeKey(SingletonItemPrefix, []string{strings.ToUpper(name), strconv.Itoa(version)})
	if err != nil {
		return "", -1, errors.Wrap(err, "r.ctx.Stub().CreateCompositeKey() failed")
	}

	return key, version, nil
}

func (r *Registry) ExistsSingleton(name string, version int) (bool, error) {
	var err error
	var key string
	name = strings.ToLower(name)
	key, version, err = r.getSingletonKeyForVersion(name, version)
	if err != nil {
		return false, errors.Wrap(err, "r.getSingletonKeyForVersion() failed")
	}

	if key == "" {
		return false, nil
	}

	return true, nil
}

func (r *Registry) ExistsAsset(name, id string) (bool, error) {
	key, err := r.getAssetCompositeKey(name, id)
	if err != nil {
		return false, errors.Wrap(err, "r.getAssetCompositeKey() failed")
	}

	if _, exists := r.changeSet[key]; exists {
		return true, nil
	}

	if _, exists := r.aCache.Get(key); exists {
		return true, nil
	}

	// both caches missed, must get from State or private_data
	// get registryItem for asset name to determine storage location
	regItem, _, err := r.GetItem(name, -1)
	if err != nil {
		return false, errors.Wrap(err, "reg.GetItem() failed")
	}

	destination, err := regItem.GetString(RegistryItemDestinationKey)
	if err != nil {
		return false, errors.Wrap(err, "regItem.GetString() failed")
	}

	// load asset from state or private data
	asset, err := newRmapFromDestination(r.ctx, name, key, destination, false)
	if err != nil {
		return false, errors.Wrap(err, "newRmapFromDestination() failed")
	}

	// add to assetCache
	r.aCache.Add(key, asset)

	return !asset.IsEmpty(), nil
}

// GetSingleton returns singleton data for some singleton name (case insensitive) and requestedVersion
// if requestedVersion is <= 0 value, it returns latest available version
// returns registryItem and actual version
func (r *Registry) GetSingleton(name string, version int) (Rmap, int, error) {
	var err error
	var key string

	name = strings.ToLower(name)
	key, version, err = r.getSingletonKeyForVersion(name, version)
	if err != nil {
		return Rmap{}, -1, errors.Wrap(err, "r.getSingletonKeyForVersion() failed")
	}

	if key == "" {
		return Rmap{}, -1, ErrorNotFound(fmt.Sprintf("singleton name: %s not found", name))
	}

	var singleton Rmap
	cachedSingleton, cachedExists := r.sCache.Get(key)
	if cachedExists {
		singleton = cachedSingleton.(Rmap)
	} else {
		singleton, err = newRmapFromState(r.ctx, key, true)
		if err != nil {
			return Rmap{}, -1, errors.Wrap(err, "newRmapFromState() failed")
		}
		r.sCache.Add(key, singleton)
	}

	return singleton, version, err
}
