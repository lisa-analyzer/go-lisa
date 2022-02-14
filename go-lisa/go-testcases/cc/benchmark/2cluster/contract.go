package chaincode

import (
	"encoding/json"
	// "encoding/base64"
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/pkg/statebased"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"

	// ledgerapi "github.com/2cluster/tradable-asset/ledger-api"
)

type SmartContract struct {
	contractapi.Contract
}


// InitLedger adds a base set of assets to the ledger
func (s *SmartContract) InitLedger(ctx TransactionContextInterface) string {
	return "Successfully initialized!"
}


// CreateAsset issues a new asset to the world state with given details.
func (s *SmartContract) CreateAsset(ctx TransactionContextInterface, id string, color string, size int, appraisedValue int) (*Asset, error) {

	identity, err := getIdentity(ctx)
	if err != nil {
		return nil, err
	}
	
	exists, err := assetExists(ctx, identity.Name, id)
	if err != nil {
		return nil, err
	}
	if exists {
		return nil, fmt.Errorf("the asset %s already exists", id)
	}

	asset := Asset{
		ID:             id,
		Color:          color,
		Size:           size,
		Owner:      	*identity,
		AppraisedValue: appraisedValue,
	}

	asset.SetIssued()

	err = ctx.GetAssetList().AddAsset(&asset)
	if err != nil {
		return nil, err
	}

	// err = addOrgToSBE(ctx, asset.ID)
	// if err != nil {
	// 	return nil, fmt.Errorf("failed setting state based endorsement for new owner: %v", err)
	// }

	return &asset, nil
}



// ReadAsset returns the asset stored in the world state with given id.
func (s *SmartContract) ReadAsset(ctx TransactionContextInterface, owner string,  id string) (*Asset, error) {
	exists, err := assetExists(ctx, owner, id)
	if err != nil {
		return nil, err
	}
	if !exists {
		return nil, fmt.Errorf("the asset %s:%s does not exist", owner, id)
	}
	
	asset, err := ctx.GetAssetList().GetAsset(owner, id)
	if err != nil {
		return nil, err
	}

	return asset, nil
}



// UpdateAsset updates an existing asset in the world state with provided parameters.
func (s *SmartContract) UpdateAsset(ctx TransactionContextInterface, id string, color string, size int, appraisedValue int) (*Asset, error) {
	identity, err := getIdentity(ctx)
	if err != nil {
		return nil, err
	}

	exists, err := assetExists(ctx, identity.Name, id)
	if err != nil {
		return nil, err
	}
	if !exists {
		return nil, fmt.Errorf("the asset %s:%s does not exist", identity.Name, id)
	}
	
	asset, err := ctx.GetAssetList().GetAsset(identity.Name, id)
	if err != nil {
		return nil, err
	}

	if identity.Org != asset.Owner.Org {
		return nil, fmt.Errorf("%s is not the owner of this asset",identity.Org)
	}

	if identity.ID != asset.Owner.ID {
		return nil, fmt.Errorf("%s is not the owner of this asset",identity.ID)
	}
	asset.Color = color
	asset.Size = size
	asset.AppraisedValue = appraisedValue

	err = ctx.GetAssetList().UpdateAsset(asset)
	if err != nil {
		return nil, err
	}

	return asset, nil
}

func (s *SmartContract) DeleteAsset(ctx TransactionContextInterface, id string) (*Asset, error) {
	identity, err := getIdentity(ctx)
	if err != nil {
		return nil, err
	}

	exists, err := assetExists(ctx, identity.Name, id)
	if err != nil {
		return nil, err
	}
	if !exists {
		return nil, fmt.Errorf("the asset %s:%s does not exist", identity.Name, id)
	}
	
	asset, err := ctx.GetAssetList().GetAsset(identity.Name, id)
	if err != nil {
		return nil, err
	}

	if identity.Org != asset.Owner.Org {
		return nil, fmt.Errorf("%s is not the owner of this asset",identity.Org)
	}

	if identity.ID != asset.Owner.ID {
		return nil, fmt.Errorf("%s is not the owner of this asset",identity.ID)
	}
	err = ctx.GetAssetList().DeleteAsset(identity.Name, id)
	if err != nil {
		return nil, fmt.Errorf("failed to delete asset: %v", err)
	}
	return nil, nil
}

func (s *SmartContract) QueryAllAssets(ctx TransactionContextInterface) ([]*Asset, error) {
	identity, err := getIdentity(ctx)
	if err != nil {
		return nil, err
	}
	queryString := fmt.Sprintf(`{"selector":{"class":"Asset"}}`, identity.Name)
	return getQueryResultForQueryString(ctx, queryString)
}

func (s *SmartContract) QueryRequestsByID(ctx TransactionContextInterface) ([]*Asset, error) {
	identity, err := getIdentity(ctx)
	if err != nil {
		return nil, err
	}
	queryString := fmt.Sprintf(`{"selector":{"class":"Asset"}}`, identity.Name)
	return getQueryResultForQueryString(ctx, queryString)
}

func (s *SmartContract) QueryAssetsByOwner(ctx TransactionContextInterface) ([]*Asset, error) {
	identity, err := getIdentity(ctx)
	if err != nil {
		return nil, err
	}
	queryString := fmt.Sprintf(`{"selector":{"owner":{"name":"%s"}}}`, identity.Name)
	return getQueryResultForQueryString(ctx, queryString)
}

func (s *SmartContract) QueryAssetsByOrg(ctx TransactionContextInterface) ([]*Asset, error) {
	identity, err := getIdentity(ctx)
	if err != nil {
		return nil, err
	}
	queryString := fmt.Sprintf(`{"selector":{"owner":{"org":"%s"}}}`, identity.Org)
	return getQueryResultForQueryString(ctx, queryString)
}


func constructQueryResponseFromIterator(resultsIterator shim.StateQueryIteratorInterface) ([]*Asset, error) {
	var assets []*Asset
	for resultsIterator.HasNext() {
		queryResult, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		var asset Asset
		err = json.Unmarshal(queryResult.Value, &asset)
		if err != nil {
			return nil, err
		}
		assets = append(assets, &asset)
	}

	return assets, nil
}

func constructQueryResponseFromIteratorRequests(resultsIterator shim.StateQueryIteratorInterface) ([]*buyRequest, error) {
	var reqs []*buyRequest
	for resultsIterator.HasNext() {
		queryResult, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		var req buyRequest
		err = json.Unmarshal(queryResult.Value, &req)
		if err != nil {
			return nil, err
		}
		reqs = append(reqs, &req)
	}

	return reqs, nil
}

func getQueryResultForQueryString(ctx TransactionContextInterface, queryString string) ([]*Asset, error) {
	resultsIterator, err := ctx.GetStub().GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	return constructQueryResponseFromIterator(resultsIterator)
}



func getIdentity(ctx TransactionContextInterface) (*Identity, error) {
	
	var caller = Identity{}

	userOrgID, err := getClientOrgID(ctx, true)
	if err != nil {
		return nil, fmt.Errorf("failed to get verified OrgID: %v", err)
	}

	userName, err := getUser(ctx)
	if err != nil {
		return nil, fmt.Errorf("failed to get verified username: %v", err)
	}

	userHash, err := getID(ctx)
	if err != nil {
		return nil, fmt.Errorf("failed to get verified user ID: %v", err)
	}

	caller.ID = userHash
	caller.Name = userName
	caller.Org = userOrgID


	return &caller , nil
}

func assetExists(ctx TransactionContextInterface, owner string, id string) (bool, error) {

	asset, err := ctx.GetAssetList().GetAsset(owner, id)
	if err != nil {
		return false, fmt.Errorf("failed to read from world state: %v", err)
	}
	return asset != nil, nil
}

func getClientOrgID(ctx TransactionContextInterface, verifyOrg bool) (string, error) {
	clientOrgID, err := ctx.GetClientIdentity().GetMSPID()
	if err != nil {
		return "", fmt.Errorf("failed getting client's orgID: %v", err)
	}

	if verifyOrg {
		err = verifyClientOrgMatchesPeerOrg(clientOrgID)
		if err != nil {
			return "", err
		}
	}
	return clientOrgID, nil
}

func verifyClientOrgMatchesPeerOrg(clientOrgID string) error {
	peerOrgID, err := shim.GetMSPID()
	if err != nil {
		return fmt.Errorf("failed getting peer's orgID: %v", err)
	}

	if clientOrgID != peerOrgID {
		return fmt.Errorf("client from org %s is not authorized to read or write private data from an org %s peer",
			clientOrgID,
			peerOrgID,
		)
	}

	return nil
}

func addOrgToSBE(ctx TransactionContextInterface, assetID string) error {
	endorsementPolicy, err := statebased.NewStateEP(nil)
	if err != nil {
		return err
	}

	clientOrgID, err := getClientOrgID(ctx, true)
	if err != nil {
		return fmt.Errorf("failed to get verified OrgID: %v", err)
	}

	err = endorsementPolicy.AddOrgs(statebased.RoleTypePeer, clientOrgID)
	if err != nil {
		return fmt.Errorf("failed to add org to endorsement policy: %v", err)
	}
	err = endorsementPolicy.AddOrgs(statebased.RoleTypePeer, "Org2MSP")
	if err != nil {
		return fmt.Errorf("failed to add org to endorsement policy: %v", err)
	}

	policy, err := endorsementPolicy.Policy()
	if err != nil {
		return fmt.Errorf("failed to create endorsement policy bytes from org: %v", err)
	}
	err = ctx.GetStub().SetStateValidationParameter(assetID, policy)
	if err != nil {
		return fmt.Errorf("failed to set validation parameter on asset: %v", err)
	}

	return nil
}

// func rmOrgFromSBE(ctx contractapi.TransactionContextInterface, assetID string) error {
// 	endorsementPolicy, err := statebased.NewStateEP(nil)
// 	if err != nil {
// 		return err
// 	}
// 	clientOrgID, err := getClientOrgID(ctx, true)
// 	if err != nil {
// 		return nil, fmt.Errorf("failed to get verified OrgID: %v", err)
// 	}
// 	err = endorsementPolicy.DelOrgs(clientOrgID)
// 	if err != nil {
// 		return fmt.Errorf("failed to remove org from endorsement policy: %v", err)
// 	}
// 	policy, err := endorsementPolicy.Policy()
// 	if err != nil {
// 		return fmt.Errorf("failed to create endorsement policy bytes from org: %v", err)
// 	}
// 	err = ctx.GetStub().SetStateValidationParameter(assetID, policy)
// 	if err != nil {
// 		return fmt.Errorf("failed to set validation parameter on asset: %v", err)
// 	}
// 	return nil
// }


func getUser(ctx TransactionContextInterface) (string, error) {
	resp, exists, err := ctx.GetClientIdentity().GetAttributeValue("email")
	if !exists {
		return "", fmt.Errorf("attribute email does not exist: %v", err)
	}

	return resp, nil
}

func getID(ctx TransactionContextInterface) (string, error) {
	b64ID, err := ctx.GetClientIdentity().GetID()
	if err != nil {
		return "", fmt.Errorf("Failed to read clientID: %v", err)
	}
	return string(b64ID), nil
}

func getAttrVal(ctx TransactionContextInterface, attr string) (string, error) {
	resp, exists, err := ctx.GetClientIdentity().GetAttributeValue(attr)
	if !exists {
		return "", fmt.Errorf("attribute does not exist: %v", err)
	}

	return resp, nil
}

func getSBE(ctx TransactionContextInterface, id string) (string, error) {
	resp, err := ctx.GetStub().GetStateValidationParameter(id)
	if err != nil {
		return "", fmt.Errorf("failed to set validation parameter on asset: %v", err)
	}

	return string(resp), nil
}


func getCollectionName(org string) (string, error) {
	orgCollection := org + "_view"

	return orgCollection, nil
}

// func (s *SmartContract) GetX509(ctx contractapi.TransactionContextInterface) (string, error) {
// 	resp, exists, err := ctx.GetStub().GetStateValidationParameter("ASSET35")
// 	if err != nil {
// 		return "", err
// 	}

// 	return string(resp)
// }


// GetID() (string, error)

// // Return the MSP ID of the client
// GetMSPID() (string, error)

// // GetAttributeValue returns the value of the client's attribute named `attrName`.
// // If the client possesses the attribute, `found` is true and `value` equals the
// // value of the attribute.
// // If the client does not possess the attribute, `found` is false and `value`
// // equals "".
// GetAttributeValue(attrName string) (value string, found bool, err error)



// // GetX509Certificate returns the X509 certificate associated with the client,
// // or nil if it was not identified by an X509 certificate.
// GetX509Certificate() (*x509.Certificate, error)