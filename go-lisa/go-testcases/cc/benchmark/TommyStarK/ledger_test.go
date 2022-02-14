package main

import (
	"encoding/json"
	"testing"

	"github.com/hyperledger/fabric-chaincode-go/shim"
)

func TestQueryWithMissingArgs(t *testing.T) {
	result := mstub.MockInvoke("test-query-with-missing-args", [][]byte{[]byte("query")})
	if result.Status != shim.ERROR {
		t.Error("chaincode invoke should have failed because 'query' invoke is expecting one arg: key and we provided none")
	}
}

func TestQueryUnknownKey(t *testing.T) {
	result := mstub.MockInvoke("test-query-unknown-key", [][]byte{[]byte("query"), []byte("unknown-key")})
	if len(result.Payload) > 0 {
		t.Error("payload should be empty as we queried an unknown key")
	}
}

func TestStoreWithMissingArgs(t *testing.T) {
	result := mstub.MockInvoke("test-store-with-missing-args", [][]byte{[]byte("store"), []byte("testmissingarg")})
	if result.Status != shim.ERROR {
		t.Error("chaincode invoke should have failed because 'store' invoke is expecting two args: key/value and we only provided one")
	}
}

func TestStoreUnmarshalAssetFailed(t *testing.T) {
	resultStore := mstub.MockInvoke("test-store", [][]byte{[]byte("store"), []byte("test1"), nil})
	if resultStore.Status != shim.ERROR {
		t.Error("should have returned an error when unmarshaling data to asset")
	}
}

func TestStoreThenQuery(t *testing.T) {
	resultStore := mstub.MockInvoke("test-store", [][]byte{[]byte("store"), []byte("test1"), []byte(`{"content": "this is a content test"}`)})
	if resultStore.Status != shim.OK {
		t.Error("chaincode invoke 'store' should have succeed")
	}

	resultQuery := mstub.MockInvoke("test-query", [][]byte{[]byte("query"), []byte("test1")})
	if resultQuery.Status != shim.OK {
		t.Error("chaincode invoke 'get' should have succeed")
	}

	var asset = &SimpleAsset{}
	if err := json.Unmarshal(resultQuery.Payload, asset); err != nil {
		t.Fatal(err)
	}

	if asset.Content != "this is a content test" {
		t.Error(`asset content should be a string with value "this is a content test"`)
	}

	if asset.TxID != "test-store" {
		t.Error(`asset transaction ID should be a string with value "store-asset1"`)
	}
}

func TestPrivateData(t *testing.T) {
	mstub.TransientMap = make(map[string][]byte)
	mstub.TransientMap["test-pdc"] = []byte("this is a test")

	resultStore := mstub.MockInvoke("test-store-private-data", [][]byte{[]byte("storePrivateData")})
	if resultStore.Status != shim.OK {
		t.Error("chaincode invoke 'storePrivateData' should have succeed")
	}

	resultQuery := mstub.MockInvoke("test-query-private-data", [][]byte{[]byte("queryPrivateData"), []byte("test-pdc")})
	if resultQuery.Status != shim.OK {
		t.Error("chaincode invoke 'queryPrivateData' should have succeed")
	}

	if string(resultQuery.Payload) != "this is a test" {
		t.Error(`payload as string should equal "this is a test"`)
	}
}

func TestPrivateDataFailureCases(t *testing.T) {
	resultQuery := mstub.MockInvoke("test-query-private-data-no-args", [][]byte{[]byte("queryPrivateData")})
	if resultQuery.Status != shim.ERROR {
		t.Error("should have failed as we provided no args")
	}

	resultQuery = mstub.MockInvoke("test-query-private-data-unknown-key", [][]byte{[]byte("queryPrivateData"), []byte("unknown-key")})
	if len(resultQuery.Payload) > 0 {
		t.Error("payload should be empty as we queried an unknown key")
	}
}

func TestSetEvent(t *testing.T) {
	res := mstub.MockInvoke("test-set-event", [][]byte{[]byte("setEvent"), []byte("event"), []byte("dummy")})
	if res.Status != shim.OK {
		t.Error("chaincode invoke 'setEvent' should have succeed")
	}
}

func TestSetEventFailureCases(t *testing.T) {
	res := mstub.MockInvoke("test-set-event", [][]byte{[]byte("setEvent")})
	if res.Status != shim.ERROR {
		t.Error("should have failed as we provided no args")
	}
}
