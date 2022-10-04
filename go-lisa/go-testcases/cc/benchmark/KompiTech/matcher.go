package testing

import (
	"fmt"

	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
	g "github.com/onsi/gomega"
)

// variant of ResponseError that accepts substring, not just a prefix
func ResponseErrorSubstr(response peer.Response, errorSubstr ...interface{}) peer.Response {
	g.Expect(int(response.Status)).To(g.BeNumerically(">=", shim.ERRORTHRESHOLD), response.Message)

	if len(errorSubstr) > 0 {
		g.Expect(response.Message).To(g.ContainSubstring(fmt.Sprintf(`%s`, errorSubstr[0])), "error message not match: "+response.Message)
	}

	return response
}
