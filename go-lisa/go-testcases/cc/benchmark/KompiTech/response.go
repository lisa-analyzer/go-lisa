package expect

import (
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
	g "github.com/onsi/gomega"
)

// ResponseOk expects peer.Response has shim.OK status and message has okSubstr prefix
func ResponseOk(response peer.Response, okSubstr ...string) peer.Response {
	g.Expect(int(response.Status)).To(g.Equal(shim.OK), response.Message)

	if len(okSubstr) > 0 {
		g.Expect(response.Message).To(g.HavePrefix(okSubstr[0]), "ok message not match: "+response.Message)
	}
	return response
}
