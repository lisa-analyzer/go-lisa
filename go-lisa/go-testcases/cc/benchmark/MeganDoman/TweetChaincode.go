package main
import (
"fmt"
"github.com/hyperledger/fabric-chaincode-go/shim"
"github.com/hyperledger/fabric/core/peer"
"encoding/json"
"io/ioutil"
"encoding/csv"
)
//create structure for the chaincode
type LetterChaincode struct {

}
//structure for individual lines of CSV file
type CsvLine struct{
	Column1 string
	Column2 string
}
//metadata to query for the output file
}
type CompositeFile struct{
	Filename string 'json:"filename"'
	IncludedFiles string[] 'json:"includedfiles"'
}

//initialize the chaincode
//only call when the chaincode is instantiated
func (t *LetterChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	fmt.Println("##### Init Chaincode #####")
	//get args from transaction proposal
	args := stub.GetStringArgs()

	filesSlice := []

	//should only have 1 argument, throws error otherwise
	if len(args) != 1 {

		return shim.Error("Incorrect arguments. Expecting output file name.")
	}

	//Store the outfile in the ledger

	err := stub.PutState(args[0]))
	//tries to commit current state of output file
	if err != nil {
		return shim.Error(ftm.Sprintf("Failed to create asset: %s", args[0]))
	}
	return shim.Success(nil)
}

//invoke function is the basis of chaincode calls, call an argument
func (t *LetterChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	fmt.println("##### Invoke Chaincode #####")
	//extract function and args from transaction proposal
	fn, args := stub.GetFunctionAndParameters()

	var result string

	var err error
	//checks if the called function is filter
	if fn  == "filter"{

		result, err = filter(stub, args)

	} else { //assume query
		result, err = query(stub, args)
	}
}

if err != nil { 
	//failed to get function and/or arguments from transaction proposal
	return shim.Error(err.Error())
}

//Return result as success

return shim.Success([]byte(result))
}

//function for filtering the data as prescribed for the smart contract logic
func filter(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	//throw error if not two argumets, input and output files
	if len(args) != 2 {
		return "", fmt.Errorf("Incorrect arguments. Expecting an input and output file name")

	}
	//set file names
	string(inputName) := args[1]
	string(outputName) := args[0]
	//adds the input name to the slice for querying later
	filesSlice.append(inputName)

	//commits current slice to chaincode
	err := stub.PutState(args[0], filesSlice)

	if err != nil{

		return "", fmt Errorf ("Failed to set asset: %s", args[0])

	}
	
	//open input csv file
	lines, err := ReadCsv(inputName)
	if err != nil{
		panic(err)
	}

	//loop through lines and turn into object
	for _, line := range lines{
		data := CsvLine{
			Column1: line[0],
			Column2: line[1],
		}	
	}

	//reads CSV
	func ReadCsv(filename string) ([][]string, error){

		//open csv file
		f, err := os.Open(inputFile)
		if err != nil{
			return [][]string{}, err
		}
		defer f.Close()

		//read files into a variable
		lines, err := csv.NewReader(f).ReadAll()
		if err != nil {
			return [][]string {}, err
		}
		return lines, nil
	}
	if err != nil{

		fmt.Println(err)
	}
	//parses the values of the input file
	return args[1], nil
	func parseLetters(file string) (map[string]*Point, error) {
		f, err := os.Open(file)
		if err != nil {
			return nil, err
		}
		defer f.Close()
	
		csvr := csv.NewReader(f)
		//maps the values of the CSV to the column name
		letters := map[string]*Point{}
		for {
			row, err := csvr.Read()
			if err != nil {
				if err == io.EOF {
					err = nil
				}
				return letters, err
				// writeLines writes the lines to the given file.
					func writeLine(line string, path string) error {
						file, err := os.Create(path)
						if err != nil {
	 				 	return err
						}
				for i in len(letters)
						if letters[i][1] := 'yes'{
							writeLine(letters[i], outfile)
						}
			}
	
		}
	}

}

//query previous commits made to the chaincode
func query(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	//only taking in one argument, output file name
	if len(args) != 1 {

		return "", ftm.Errorf("Incorrect arguments. Expecting an output file name")

	}

	//returns error if this file has not been committed to the chaincode before
	value, err := stub.GetState(args[0])
	
		if err != nil{

			return "", fmt.Errorf("Failed to get asset: %s with error: %s", args[0], err)
		}
		if value == nil{

			return "", fmt.Errorf("Asset not found: %s", args[0])
		}

	return string(value), nil

}

//starts the chaincode
func main(){

	err := shim.Start(new(SampleChaincode))
	if err != nil {
		fmt.Println("could not start TweetChaincode")

	}
	else{

		fmt.Println("TweetChaincode successfully started.")
	}
}