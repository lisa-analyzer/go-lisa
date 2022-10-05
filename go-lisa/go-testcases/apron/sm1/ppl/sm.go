package pflag

import "strconv"
import "github.com/hyperledger/shim"

	func main(stub shim.ChaincodeStubInterface, FixedRateBPS int, FloatingRateBPS int, PA int, referenceRate int) pb.Response {
  		referenceRateBytes := stub.GetState("rr" + irs.ReferenceRate)
  		referenceRate := strconv.Atoi(string(referenceRateBytes))

  		// calculate payment
  		p1  := (PA * FixedRateBPS) / 100
  		p2  := (PA * (FloatingRateBPS + (referenceRate))) / 100
  		payment := strconv.Itoa(p1 - p2)
  		err = stub.PutState(paymentID, []byte(payment))
  		return shim.Success([]byte(payment))
  	} 