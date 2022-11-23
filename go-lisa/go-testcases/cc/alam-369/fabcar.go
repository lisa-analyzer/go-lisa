package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"math/rand"
	"strconv"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

type SmartContract struct {
}

type Car struct {
	Make   string `json:"make"`
	Model  string `json:"model"`
	Colour string `json:"colour"`
	Owner  string `json:"owner"`
}
type Movie struct {
	Name     string
	Director string
	Year     int
	Genre    string
	Doctype  string
}
type Voter struct {
	Nid       int64
	Email     string
	BirthDate string
	Name      string
	Password  string
	Division  string
	District  string
	Thana     string
	Doctype   string
}

type Candidate struct {
	Name        string
	ElectionKey string
	Thana       string
	Totalvote   int
	Sign        string
	Doctype     string
}

type Election struct {
	Name     string
	Division string
	District string
	Thana    string
	State    bool
	Doctype  string
}
type Vote struct {
	Voterkey     string
	Electionkey  string
	Candidatekey string
	Doctype      string
}
type Comission struct {
	Name     string
	Email    string
	Password string
	Key      string
	Doctype  string
}
type Area struct {
	Name     string
	Division string
	District string
	Thana    string
	Doctype  string
}
type News struct {
	Anouncement string
	Text        string
	Date        string
	Doctype     string
}

//////////////////////key/////////////////
const StringLength = 10
const AllRunes = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRresultSTUVWXYZ"

func RandomString() string {
	var buffer bytes.Buffer

	for i := 0; i < StringLength; i++ {
		r := AllRunes[rand.Intn(len(AllRunes))]
		buffer.WriteRune(rune(r))
	}

	return buffer.String()
}

/////////////////////////////////////////

func constructQueryResponseFromIterator(resultsIterator shim.StateQueryIteratorInterface) (*bytes.Buffer, error) {
	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	return &buffer, nil
}

func getQueryResultForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]byte, error) {

	fmt.Printf("- getQueryResultForQueryString queryString:\n%s\n", queryString)

	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	buffer, err := constructQueryResponseFromIterator(resultsIterator)
	if err != nil {
		return nil, err
	}

	fmt.Printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer.String())

	return buffer.Bytes(), nil
}

func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {
	return shim.Success(nil)
}

func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {

	function, args := APIstub.GetFunctionAndParameters()

	if function == "queryCar" {
		return s.queryCar(APIstub, args)
	} else if function == "initLedger" {
		return s.initLedger(APIstub)
	} else if function == "queryAllRecords" {
		return s.queryAllRecords(APIstub)
	} else if function == "createVoter" {
		return s.createVoter(APIstub, args)
	} else if function == "createElection" {
		return s.createElection(APIstub, args)
	} else if function == "createCandidate" {
		return s.createCandidate(APIstub, args)
	} else if function == "login" {
		return s.login(APIstub, args)
	} else if function == "admin" {
		return s.admin(APIstub, args)
	} else if function == "createArea" {
		return s.createArea(APIstub, args)
	} else if function == "currentElection" {
		return s.currentElection(APIstub, args)
	} else if function == "allElection" {
		return s.allElection(APIstub, args)
	} else if function == "removeElection" {
		return s.removeElection(APIstub, args)
	} else if function == "voterElection" {
		return s.voterElection(APIstub, args)
	} else if function == "resultElection" {
		return s.resultElection(APIstub, args)
	} else if function == "hasVote" {
		return s.hasVote(APIstub, args)
	} else if function == "candidateList" {
		return s.candidateList(APIstub, args)
	} else if function == "addVote" {
		return s.addVote(APIstub, args)
	} else if function == "createNews" {
		return s.createNews(APIstub, args)
	} else if function == "allNews" {
		return s.allNews(APIstub, args)
	}

	return shim.Error("Invalid Smart Contract function name.")
}

func (s *SmartContract) queryCar(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	carAsBytes, _ := APIstub.GetState(args[0])
	return shim.Success(carAsBytes)
}

func (s *SmartContract) initLedger(APIstub shim.ChaincodeStubInterface) sc.Response {
	// cars := []Car{
	// 	Car{Make: "Toyota", Model: "Prius", Colour: "blue", Owner: "Tomoko"},
	// 	Car{Make: "Ford", Model: "Mustang", Colour: "red", Owner: "Brad"},
	// 	Car{Make: "Hyundai", Model: "Tucson", Colour: "green", Owner: "Jin Soo"},
	// 	Car{Make: "Volkswagen", Model: "Passat", Colour: "yellow", Owner: "Max"},
	// 	Car{Make: "Tesla", Model: "S", Colour: "black", Owner: "Adriana"},
	// 	Car{Make: "Peugeot", Model: "205", Colour: "purple", Owner: "Michel"},
	// 	Car{Make: "Chery", Model: "S22L", Colour: "white", Owner: "Aarav"},
	// 	Car{Make: "Fiat", Model: "Punto", Colour: "violet", Owner: "Pari"},
	// 	Car{Make: "Tata", Model: "Nano", Colour: "indigo", Owner: "Valeria"},
	// 	Car{Make: "Holden", Model: "Barina", Colour: "brown", Owner: "Shotaro"},
	// }

	comissions := []Comission{
		Comission{Name: "Tasnim", Email: "tasnimalam064@gmail.com", Password: "123456", Key: RandomString(), Doctype: "comission"},
		Comission{Name: "Katha", Email: "tasnimalam064@gmail.com", Password: "654321", Key: RandomString(), Doctype: "comission"},
	}

	i := 0
	for i < len(comissions) {
		fmt.Println("i is ", i)
		comissionAsBytes, _ := json.Marshal(comissions[i])
		APIstub.PutState(comissions[i].Key, comissionAsBytes)
		fmt.Println("Added", comissions[i].Key)
		i = i + 1
	}

	elecKey := RandomString()
	s.createElection(APIstub, []string{elecKey, "Basail Union Election 2018", "Dhaka", "Tangail", "Basail"})
	s.createCandidate(APIstub, []string{RandomString(), "Jaraf As-ad", elecKey, "Basail", "0", "Play Station", "Candidate"})
	s.createCandidate(APIstub, []string{RandomString(), "Jahin Afroz", elecKey, "Basail", "0", "Spotify", "Candidate"})
	s.createCandidate(APIstub, []string{RandomString(), "Jamin As-ad", elecKey, "Basail", "0", "Red Car", "Candidate"})

	elecKey = RandomString()
	s.createElection(APIstub, []string{elecKey, "Sokhipur Union Election 2018", "Dhaka", "Tangail", "Sokhipur"})
	s.createCandidate(APIstub, []string{RandomString(), "Jibon Sen", elecKey, "Sokhipur", "0", "Play Station", "Candidate"})
	s.createCandidate(APIstub, []string{RandomString(), "Najir chowdhury", elecKey, "Sokhipur", "0", "Spotify", "Candidate"})
	s.createCandidate(APIstub, []string{RandomString(), "Nafis Mahnud", elecKey, "Sokhipur", "0", "Red Car", "Candidate"})

	elecKey = RandomString()
	s.createElection(APIstub, []string{elecKey, "DormoPasa Union Election 2018", "Sylhet", "Sumangongy", "Dormopasa"})
	s.createCandidate(APIstub, []string{RandomString(), "Taslina Akter", elecKey, "Dormopasa", "0", "Play Station", "Candidate"})
	s.createCandidate(APIstub, []string{RandomString(), "Janatul Israt", elecKey, "Dormopasa", "0", "Spotify", "Candidate"})
	s.createCandidate(APIstub, []string{RandomString(), "Tumpa", elecKey, "Dormopasa", "0", "Red Car", "Candidate"})

	elecKey = RandomString()
	s.createElection(APIstub, []string{elecKey, "Muktagacha Election 2018", "Mymonsihg", "Mymonsihg Sadar", "Muktagacha"})
	s.createCandidate(APIstub, []string{RandomString(), "Taslina Akter", elecKey, "Muktagacha", "0", "Play Station", "Candidate"})
	s.createCandidate(APIstub, []string{RandomString(), "Janatul Israt", elecKey, "Muktagacha", "0", "Spotify", "Candidate"})
	s.createCandidate(APIstub, []string{RandomString(), "Tumpa", elecKey, "Muktagacha", "0", "Red Car", "Candidate"})

	text := "This election will be held to elect the Mayor of the Basail thana, Tangail ."
	s.createNews(APIstub, []string{RandomString(), "Basail Union Election 2018", text, "2018-11-26"})
	text = "This election will be held to elect the Mayor of the Dormopasa thana, Sumangongy, Sylhet ."
	s.createNews(APIstub, []string{RandomString(), "Dormopasa Union Election 2018", text, "2018-11-26"})
	text = "This election will be held to elect the Mayor of the Sokhipur thana, Tangail, Dhaka"
	s.createNews(APIstub, []string{RandomString(), "Sokhipur Union Election 2018", text, "2018-11-26"})
	text = "This election will be held to elect the Mayor of the Muktagacha thana, Mymonsihg Sadar, Mymonsihg ."
	s.createNews(APIstub, []string{RandomString(), "Muktagacha Union Election 2018", text, "2018-11-26"})

	return shim.Success(nil)
}

//Voter
func (s *SmartContract) createVoter(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	var voterkey = args[0]
	nid, err := strconv.ParseInt(args[1], 10, 64)

	if err != nil {
		fmt.Println("The nid shold be a number")
	}
	email := args[2]
	name := args[3]
	birthDate := args[4]
	division := args[5]
	distric := args[6]
	thana := args[7]
	passord := args[8]

	var doctype string = "voter"

	var voter = Voter{Nid: nid, Email: email, Name: name, BirthDate: birthDate, Division: division, District: distric, Thana: thana, Password: passord, Doctype: doctype}

	voterAsBytes, _ := json.Marshal(voter)
	APIstub.PutState(voterkey, voterAsBytes)

	return shim.Success(nil)
}

//election
func (s *SmartContract) createElection(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	var vkey = args[0]

	var name string = args[1]
	division := args[2]
	distric := args[3]
	thana := args[4]

	var doctype string = "election"
	state := false

	var election = Election{Name: name, Division: division, District: distric, Thana: thana, State: state, Doctype: doctype}

	electionAsBytes, _ := json.Marshal(election)
	APIstub.PutState(vkey, electionAsBytes)

	return shim.Success(nil)
}

func (s *SmartContract) createNews(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	var vkey = args[0]

	var name string = args[1]
	text := args[2]
	date := args[3]
	var doctype string = "news"

	var news = News{Anouncement: name, Text: text, Date: date, Doctype: doctype}

	electionAsBytes, _ := json.Marshal(news)
	APIstub.PutState(vkey, electionAsBytes)

	return shim.Success(nil)
}

func (s *SmartContract) removeElection(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	var vkey = args[0]

	currentElectionData, _ := APIstub.GetState(vkey)

	var currentElection Election
	_ = json.Unmarshal(currentElectionData, &currentElection)

	currentElection.State = true
	fmt.Println(currentElection)
	electionAsBytes, _ := json.Marshal(currentElection)
	APIstub.PutState(vkey, electionAsBytes)

	return shim.Success(nil)
}

//Candidate
func (s *SmartContract) createCandidate(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 7 {
		return shim.Error("Incorrect number of arguments, required 9, given " + strconv.Itoa(len(args)))
	}

	var ckey = args[0]
	var name = args[1]
	var electionkey = args[2]

	thana := args[3]
	totalvote, err := strconv.Atoi(args[4])

	if err != nil {
		fmt.Println("The year shold be a number")
	}
	var sign = args[5]
	var doctype = "Candidate"

	var candidte = Candidate{Name: name, ElectionKey: electionkey, Thana: thana, Totalvote: totalvote, Sign: sign, Doctype: doctype}

	candidateAsBytes, _ := json.Marshal(candidte)
	APIstub.PutState(ckey, candidateAsBytes)

	return shim.Success(nil)
}

//Area
func (s *SmartContract) createArea(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments, required 4, given " + strconv.Itoa(len(args)))
	}

	var ckey = RandomString()
	var name = args[0]
	var division = args[1]
	var distric = args[2]
	var thana = args[3]
	var doctype = "area"

	var area = Area{Name: name, Division: division, District: distric, Thana: thana, Doctype: doctype}

	candidateAsBytes, _ := json.Marshal(area)
	APIstub.PutState(ckey, candidateAsBytes)

	return shim.Success(nil)
}
func (s *SmartContract) addVote(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	var ckey = args[0]
	var userkey = args[1]
	var election = args[2]
	var candidtekey = args[3]
	var doctype = "vote"

	currentCandidateData, _ := APIstub.GetState(candidtekey)
	var currentCandidate Candidate
	json.Unmarshal(currentCandidateData, &currentCandidate)
	currentCandidate.Totalvote = currentCandidate.Totalvote + 1
	jsonCurrentCandidate, _ := json.Marshal(currentCandidate)
	APIstub.PutState(candidtekey, jsonCurrentCandidate)

	var vote = Vote{Voterkey: userkey, Electionkey: election, Candidatekey: candidtekey, Doctype: doctype}

	voteAsBytes, _ := json.Marshal(vote)
	APIstub.PutState(ckey, voteAsBytes)

	return shim.Success(nil)
}
func (s *SmartContract) hasVote(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	userkey := args[0]
	electionName := args[1]
	doctype := "vote"

	electionQuery := newCouchQueryBuilder().addSelector("Doctype", doctype).addSelector("Voterkey", userkey).addSelector("Electionkey", electionName).getQueryString()
	userData, _ := getQueryResultForQueryString(APIstub, electionQuery)
	return shim.Success(userData)

}

/////////////////Voter_login//////////////////

func (s *SmartContract) login(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 3 {
		return shim.Error("you need 3 arguments, but you have given " + strconv.Itoa(len(args)))
	}

	Nid, _ := strconv.ParseInt(args[0], 10, 64)
	Email := args[1]
	Password := args[2]

	queryString := newCouchQueryBuilder().addSelector("Nid", Nid).addSelector("Email", Email).addSelector("Password", Password).getQueryString()

	userData, _ := getQueryResultForQueryString(APIstub, queryString)
	return shim.Success(userData)
}
func (s *SmartContract) admin(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 2 {
		return shim.Error("you need 2 arguments, but you have given " + strconv.Itoa(len(args)))
	}

	Email := args[0]
	Password := args[1]
	doctype := "comission"

	// h := sha256.New()
	// h.Write([]byte(Password))
	// Password = fmt.Sprintf("%x", h.Sum(nil))

	queryString := newCouchQueryBuilder().addSelector("Email", Email).addSelector("Password", Password).addSelector("Doctype", doctype).getQueryString()

	userData, _ := getQueryResultForQueryString(APIstub, queryString)
	return shim.Success(userData)
}

func (s *SmartContract) currentElection(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	doctype := "election"
	ElectionName := args[0]

	electionQuery := newCouchQueryBuilder().addSelector("Name", ElectionName).addSelector("Doctype", doctype).getQueryString()
	userData, _ := getQueryResultForQueryString(APIstub, electionQuery)

	return shim.Success(userData)
}
func (s *SmartContract) allElection(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	doctype := "election"
	state := false

	electionQuery := newCouchQueryBuilder().addSelector("Doctype", doctype).addSelector("State", state).getQueryString()
	userData, _ := getQueryResultForQueryString(APIstub, electionQuery)

	return shim.Success(userData)
}
func (s *SmartContract) resultElection(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	doctype := "election"
	state := true

	electionQuery := newCouchQueryBuilder().addSelector("Doctype", doctype).addSelector("State", state).getQueryString()
	userData, _ := getQueryResultForQueryString(APIstub, electionQuery)

	return shim.Success(userData)
}
func (s *SmartContract) voterElection(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	doctype := "election"
	state := false
	thana := args[0]

	electionQuery := newCouchQueryBuilder().addSelector("Doctype", doctype).addSelector("State", state).addSelector("Thana", thana).getQueryString()
	userData, _ := getQueryResultForQueryString(APIstub, electionQuery)

	return shim.Success(userData)
}
func (s *SmartContract) candidateList(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	doctype := "Candidate"
	electionname := args[0]
	thana := args[1]

	electionQuery := newCouchQueryBuilder().addSelector("ElectionKey", electionname).addSelector("Thana", thana).addSelector("Doctype", doctype).getQueryString()
	userData, _ := getQueryResultForQueryString(APIstub, electionQuery)

	return shim.Success(userData)
}

func (s *SmartContract) allNews(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	doctype := "news"

	electionQuery := newCouchQueryBuilder().addSelector("Doctype", doctype).getQueryString()
	userData, _ := getQueryResultForQueryString(APIstub, electionQuery)

	return shim.Success(userData)
}

//////////////////////////////////////////////////////////////////////////////////

func (s *SmartContract) queryAllRecords(APIstub shim.ChaincodeStubInterface) sc.Response {

	startKey := ""
	endKey := "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"

	resultsIterator, err := APIstub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- queryAllrecords:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

func main() {

	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}
