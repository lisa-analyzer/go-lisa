package golang

import (
	. "github.com/davidkhala/goutils"
	"github.com/hyperledger/fabric-chaincode-go/shim"
)

type KeyModification struct {
	TxId      string
	Value     []byte
	Timestamp TimeLong // as unix nano
	IsDelete  bool
}

func ParseHistory(iterator shim.HistoryQueryIteratorInterface, filter func(KeyModification) bool) []KeyModification {
	defer PanicError(iterator.Close())
	var result []KeyModification
	for iterator.HasNext() {
		keyModification, err := iterator.Next()
		PanicError(err)
		var timeStamp = keyModification.Timestamp
		var t TimeLong
		t = t.FromTimeStamp(*timeStamp)
		var translated = KeyModification{
			keyModification.TxId,
			keyModification.Value,
			t,
			keyModification.IsDelete}
		if filter == nil || filter(translated) {
			result = append(result, translated)
		}
	}
	return result
}

type StateKV struct {
	Namespace string
	Key       string
	Value     string
}
type QueryResponseMetadata struct {
	FetchedRecordsCount int
	Bookmark            string
}

func ParseStates(iterator shim.StateQueryIteratorInterface, filter func(StateKV) bool) []StateKV {
	defer PanicError(iterator.Close())
	var kvs []StateKV
	for iterator.HasNext() {
		kv, err := iterator.Next()
		PanicError(err)
		var stateKV = StateKV{kv.Namespace, kv.Key, string(kv.Value)}
		if filter == nil || filter(stateKV) {
			kvs = append(kvs, stateKV)
		}
	}
	return kvs
}

type Args struct {
	buff [][]byte
}

func ArgsBuilder(fcn string) Args {
	return Args{[][]byte{[]byte(fcn)}}
}

func (t *Args) AppendBytes(bytes []byte) *Args {
	t.buff = append(t.buff, bytes)
	return t
}
func (t *Args) AppendArg(str string) *Args {
	t.buff = append(t.buff, []byte(str))
	return t
}
func (t Args) Get() [][]byte {
	return t.buff
}

// a readable structure of peer.response
type PeerResponse struct {
	// A status code that should follow the HTTP status codes.
	Status int32 `json:"status,omitempty"`
	// A message associated with the response code.
	Message string `json:"message,omitempty"`
	// A payload that can be used to include metadata with this response.
	Payload string `json:"payload,omitempty"`
}
