package engine

import (
	"github.com/KompiTech/rmap"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/pkg/errors"
)

type Iterator struct {
	ctx      ContextInterface
	iterator shim.StateQueryIteratorInterface
}

type IteratorInterface interface {
	Next(resolve bool) (*rmap.Rmap, error)
	HasNext() bool
	Close() error
}

func (i Iterator) Close() error {
	return i.iterator.Close()
}

// Next returns next asset, optionally resolves it
// if it returns (nil, nil), then the iterator is exhausted
func (i Iterator) Next(resolve bool) (*rmap.Rmap, error) {
	// end of iterator
	if !i.iterator.HasNext() {
		return nil, nil
	}

	nextElem, err := i.iterator.Next()
	if err != nil {
		return nil, errors.Wrap(err, "iterator.Next() failed")
	}

	nextAsset, err := rmap.NewFromBytes(nextElem.GetValue())
	if err != nil {
		return nil, errors.Wrap(err, "NewFromBytes() failed")
	}

	// delete keys that couchdb returns extra
	delete(nextAsset.Mapa, "_rev")
	delete(nextAsset.Mapa, "_id")

	if resolve {
		if err := (resolver{}).WalkReferences(i.ctx, nextAsset, true); err != nil {
			return nil, errors.Wrap(err, "(resolver{}).WalkReferences() failed")
		}
	}

	return &nextAsset, nil
}

func (i Iterator) HasNext() bool {
	return i.iterator.HasNext()
}
