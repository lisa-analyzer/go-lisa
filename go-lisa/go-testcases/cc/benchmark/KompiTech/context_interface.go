package engine

import (
	"time"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// ContextInterface is extension of contractapi.TransactionContextInterface
type ContextInterface interface {
	contractapi.TransactionContextInterface
	contractapi.SettableTransactionContextInterface
	SetConfFunc(func() Configuration)
	GetConfiguration() Configuration
	GetRegistry() *Registry
	Get(key string) interface{}
	Set(key string, value interface{})
	Stub() shim.ChaincodeStubInterface
	Time() (time.Time, error)
	Params() map[string]interface{}
	Param(name string) (interface{}, error)
	ParamString(name string) (string, error)
	ParamBytes(name string) ([]byte, error)
	ParamInt(name string) (int, error)
	ParamBool(name string) (bool, error)
	Logger() Logger
}
