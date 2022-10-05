package pflag

import "strconv"
import "github.com/hyperledger/shim"
	func  main(stub shim.ChaincodeStubInterface, 
    		args []string) 	pb.Response {
    		Avalbytes := stub.GetState(A)
    		Bvalbytes := stub.GetState(B)
    		var A, B string    // Entities
    		var Aval, Bval int // Asset holdings
    		var X int          // Transaction value


    		Aval = strconv.Atoi(string(Avalbytes))
    		
    		Bval = strconv.Atoi(string(Bvalbytes))
    		X  = strconv.Atoi(args[2])
    		Aval = Aval - X
   
   			Bval = Bval + X
    	

    		err = stub.PutState(A, []byte(strconv.Itoa(Aval)))
    		err = stub.PutState(B, []byte(strconv.Itoa(Bval)))
    		return shim.Success(nil)
    	}