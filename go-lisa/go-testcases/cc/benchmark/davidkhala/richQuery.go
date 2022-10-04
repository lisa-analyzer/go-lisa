package golang

import (
	. "github.com/davidkhala/goutils"
	"github.com/hyperledger/fabric-chaincode-go/shim"
)

// It is only supported for state databases that support rich query, e.g., CouchDB.
func (cc CommonChaincode) GetQueryResult(query string) shim.StateQueryIteratorInterface {
	var result, err = cc.CCAPI.GetQueryResult(query)
	PanicError(err)
	return result
}

// It is only supported for state databases that support rich query, e.g., CouchDB.
func (cc CommonChaincode) GetQueryResultWithPagination(query string, pageSize int, bookmark string) (shim.StateQueryIteratorInterface, QueryResponseMetadata) {
	var result, metadata, err = cc.CCAPI.GetQueryResultWithPagination(query, int32(pageSize), bookmark)
	PanicError(err)
	return result, QueryResponseMetadata{int(metadata.FetchedRecordsCount), metadata.Bookmark}
}
