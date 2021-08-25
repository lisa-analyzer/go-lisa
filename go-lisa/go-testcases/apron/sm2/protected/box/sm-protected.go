package pflag

import "strconv"
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
   
   			if Bval > 0 &&  X > 0 && Bval + X < 100 {
   				Bval = Bval + X
    		} else {
    			Bval = 0;
    		}

    		err = stub.PutState(A, []byte(strconv.Itoa(Aval)))
    		err = stub.PutState(B, []byte(strconv.Itoa(Bval)))
    		return shim.Success(nil)
    	}