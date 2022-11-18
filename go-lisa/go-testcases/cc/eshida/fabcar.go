/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * The sample smart contract for documentation topic:
 * Writing Your First Blockchain Application
 */

package main

/* Imports
 * 4 utility libraries for formatting, handling bytes, reading and writing JSON, and string manipulation
 * 2 specific Hyperledger Fabric specific libraries for Smart Contracts
 */
import (
	//"bytes"
	"encoding/json"
	"fmt"
	"github.com/cd1/utils-golang"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
	"math/rand"
	"strconv"
	"time"
)

// Define the Smart Contract structure
type SmartContract struct {
}

//func (s *SmartContract) Init(stub shim.ChaincodeStubInterface) sc.Response {
//	panic("implement me")
//}
//
//func (s *SmartContract) Invoke(stub shim.ChaincodeStubInterface) sc.Response {
//	panic("implement me")
//}

// Define the car structure, with 4 properties.  Structure tags are used by encoding/json library
//type User struct {
//	// userId will give as key
//	Doctype string
//	UserName string
//	Certificate string
//	PubKey string
//	Password string
//	key  string
//}
type UserCertificate struct {
	Doctype                       string
	UserName                      string
	Usermail                      string
	PublicKey                     string
	TrustValue                    float64
	PeriodOfValidity              string
	Signature                     string
	Version                       string
	TotalCertified                float64
	IsAvailable                   bool
	AccountAddress                string
	AccountValue                  float64
	VersionNumber                 string
	SignatureAlgorithmID          string
	IssuerName                    string
	PublicKeyAlgorithm            string
	CertificateSignatureAlgorithm string
	//CertificateSignature string
	Password string
	Key      string
}

type BankAccount struct {
	Doctype        string
	Pubkey         string
	Value          float64
	AccountAddress string
	Key          string
}
type TransactionTracker struct {
	Doctype                        string
	EndorserAddress                 string
	CertificateHolderAddress       string
	GivenMoneyByEndorser           float64
	ReturnMoneyByCertificateHolder float64
	EndorserTransactionTime	            time.Time
	CertificateHolderTransactionTime	time.Time
	Key                            string
}
type LendSeeker struct {
	Doctype            string
	LendSeekerAddress string
	Money              float64
	NeededMoneyFor     int
	Key                string
}
type LendingTracker struct{
	Doctype string
	LendSeekerAddress string
	LenderAddress string
	GivenMoneyByLender float64
	MoneyHasGivenFor int
	InterestRate int
	TransactionTime time.Time
	ReturnMoneyByLendSeeker float64
	TotalInstallment int
	Installment int
	Key string
}
type MoneyTransaction struct {
	Doctype         string
	SenderAddress   string
	RecieverAddress string
	Amount          float64
	Indicator        int
	TransactionTime time.Time
	Key             string
}
 type Notification struct{
 	Doctype string
 	NotificationAddress string
 	Message string
 	Key string
 }
/*
 * The Init method is called when the Smart Contract "fabcar" is instantiated by the blockchain network
 * Best practice is to have any Ledger initialization in separate function -- see initLedger()
 */
func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {
	return shim.Success(nil)
}

/*
 * The Invoke method is called as a result of an application request to run the Smart Contract "fabcar"
 * The calling application program has also specified the particular smart contract function to be called, with arguments
 */
func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {

	// Retrieve the requested Smart Contract function and arguments
	function, args := APIstub.GetFunctionAndParameters()
	// Route to the appropriate handler function to interact with the ledger appropriately
	if function == "initLedger" {
		return s.initLedger(APIstub)
	} else if function == "createUser" {
		return s.createUser(APIstub, args)
	} else if function == "login" {
		return s.login(APIstub, args)
	} else if function == "AllUser" {
		return s.AllUser(APIstub, args)
	} else if function == "EndorserTransaction" {
		return s.EndorserTransaction(APIstub, args)
	} else if function == "CertificateHolderTransaction" {
		return s.CertificateHolderTransaction(APIstub, args)
	}else if function == "LendSeekingRequest" {
		return s.LendSeekingRequest(APIstub, args)
	}else if function == "LendingTransaction" {
		return s.LendingTransaction(APIstub, args)
	}else if function == "ReturnInstallment" {
		return s.ReturnInstallment(APIstub, args)
	}
	//}else if function =="returnMoneyToTheEndorser"{
	//	return s.returnMoneyToTheEndorser(APIstub,args)
	//}

	return shim.Error("Invalid Smart Contract function name.")
}

func createBankAccount(APIstub shim.ChaincodeStubInterface, args string) []byte {
	var bankAccount = BankAccount{}
	bankAccount.Doctype = "Account Holder"
	bankAccount.Pubkey = args
	bankAccount.Value = 100000.00000000
	bankAccount.Key = utils.RandomString()
	////Creating fixed length of random number
	var letter = []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
	b := make([]rune, 15)
	for i := range b {
		b[i] = letter[rand.Intn(len(letter))]
	}
	bankAccount.AccountAddress = string(b)

	jsonBankHolder, _ := json.Marshal(bankAccount)


	_ = APIstub.PutState(bankAccount.Key, jsonBankHolder)
	return jsonBankHolder
}

func (s *SmartContract) AllUser(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	queryString := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").getQueryString()

	userData, _ := allUserQueryVAlueForQueryString(APIstub, queryString)

	var userArray []UserCertificate
	_ = json.Unmarshal(userData, &userArray)
	//println(userData)
	return shim.Success(userData)
}

func (s *SmartContract) initLedger(APIstub shim.ChaincodeStubInterface) sc.Response {
	userCertificate := []UserCertificate{
		{Doctype: "User Certificate", UserName: "null", Usermail: "null", PublicKey: "null", TrustValue: 0, Signature: "null", PeriodOfValidity: "null", Password: "null", Version: "null", TotalCertified: 0, IsAvailable: false, AccountAddress: "null", AccountValue: 0, VersionNumber: "null", SignatureAlgorithmID: "p-256", IssuerName: "TrusTMoney@org.com", PublicKeyAlgorithm: "EcdsaWithprime256v1", CertificateSignatureAlgorithm: "EcdsaWithprime256v1"},
	}
	i := 0
	for i < len(userCertificate) {
		//fmt.Println("i is ", i)
		carAsBytes, _ := json.Marshal(userCertificate[i])
		_= APIstub.PutState(utils.RandomString(), carAsBytes)
		//	fmt.Println("Added", userCertificate[i])
		i = i + 1
	}

	return shim.Success(nil)
}

func (s *SmartContract) createUser(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	//if len(args) != 6 {
	//	return shim.Error(fmt.Sprintf("Incorrect number of arguments. Expecting 6, found %v", len(args)))
	//}
	/// Current time
	fmt.Println("\n\n\n - create User \n---------------")
	for _, arg := range args {
		fmt.Println(arg)
		fmt.Println("**--**")
	}
	fmt.Println("=================")

	var userCertificate = UserCertificate{}
	userCertificate.Doctype = "User Certificate"
	userCertificate.UserName = args[0]
	userCertificate.Usermail = args[1]
	userCertificate.PublicKey = args[2]
	userCertificate.TrustValue = 0.00
	userCertificate.Signature = args[3]
	userCertificate.PeriodOfValidity = args[4]
	userCertificate.Password = args[5]
	userCertificate.Version = "V1"
	userCertificate.TotalCertified = 0.00
	userCertificate.IsAvailable = true
	//Creating Bank Account

	 accountHolderData := createBankAccount(APIstub, userCertificate.PublicKey)
	 var userData = BankAccount{}
	 _=json.Unmarshal(accountHolderData,&userData)

	userCertificate.AccountAddress = userData.AccountAddress
	userCertificate.AccountValue = userData.Value
	userCertificate.VersionNumber = "1"
	userCertificate.SignatureAlgorithmID = "p-256"
	userCertificate.IssuerName = "TrusTMoney@org.com"
	userCertificate.PublicKeyAlgorithm = "EcdsaWithprime256v1"
	userCertificate.CertificateSignatureAlgorithm = "EcdsaWithprime256v1"
	userCertificate.Key = utils.RandomString()

	//userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").getQueryString()
	//fmt.Println(userQuery)

	//user, _ := lastQueryValueForQueryString(APIstub, userQuery)
	//var userData UserCertificate
	//_ = json.Unmarshal(user, &userData)

	//if userData.Usermail != userCertificate.Usermail {
	//key := utils.RandomString()
	//userAsBytes, _ := json.Marshal(user)

	userBytes, _ := json.Marshal(userCertificate)
	_ = APIstub.PutState(userCertificate.Key, userBytes)
	fmt.Println("created this user.", userCertificate.UserName)

	//} else {
	//	return shim.Error("Duplicate email Addres found. Cannot create Account")
	//}
	return shim.Success(nil)
}

//logIn
func (s *SmartContract) login(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	fmt.Println("\n\n\n\n\n - Log In \n----------------")
	for _, arg := range args {
		fmt.Println(arg)
		fmt.Println("**--**")
	}
	fmt.Println("=================")
	email := args[0]
	pass := args[1]
	fmt.Println(email, pass)
	userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", email).getQueryString()

	fmt.Println("\n\n", userQuery)

	user, _ := lastQueryValueForQueryString(APIstub, userQuery)
	fmt.Println("hello from login")
	var userData UserCertificate
	_ = json.Unmarshal(user, &userData)
	if userData.Password != pass {
		return shim.Error("Password Doesn't Match ")
	}
	fmt.Println("USER FOUND")
	fmt.Println(userData.Password)
	fmt.Println("\n\n Eshita \n\n")
	return shim.Success([]byte(user))
}
func (s *SmartContract) EndorserTransaction(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	//if len(args)!=3{
	//	return shim.Error("Expected number 3.")
	//}

	fmt.Println("\n\n\n - Endorser Transaction \n---------------")
	for _, arg := range args {
		fmt.Println(arg)
		fmt.Println("**--**")
	}
	fmt.Println("=================")

	k, _ := strconv.ParseFloat(args[2], 64)
	//senderAdd := args[0]
	//recieverAdd :=args[1]
	fmt.Println("From Endorser Transaction Function", args[0], args[1], args[2], k)
	userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", args[0]).getQueryString()
	queryValue,_:=lastQueryValueForQueryString(APIstub,userQuery)

	var userData UserCertificate
	_=json.Unmarshal(queryValue,&userData)

	accountAddress := userData.AccountAddress
	fmt.Println(accountAddress)




	var MoneyTransaction = MoneyTransaction{
		Doctype:         "Transaction",
		SenderAddress:   accountAddress,
		RecieverAddress: args[1],
		Amount:          k,
		TransactionTime: time.Now(),
		Indicator: 1,
		Key:             utils.RandomString(),
	}
	var str =" You GoT " + fmt.Sprintf("%f",k) + "from this " + accountAddress +" account"
	fmt.Println("\n\n\n Notifictaion", str)
	var notify = Notification{
		Doctype:"Notification",
		NotificationAddress: args[1],
		Message: str,
		Key:utils.RandomString(),
	}
	notifyBytes, _:= json.Marshal(notify)
	transactionTracker := TransactionTracker{}
	transactionTracker.Doctype = "Transaction Tracker"
	transactionTracker.EndorserAddress = accountAddress
	transactionTracker.CertificateHolderAddress = args[1]
	transactionTracker.GivenMoneyByEndorser = k
	transactionTracker.ReturnMoneyByCertificateHolder = 0.00
	transactionTracker.EndorserTransactionTime =time.Now()
	transactionTracker.CertificateHolderTransactionTime= time.Time{}
	transactionTracker.Key = utils.RandomString()
	trackTransaction, _ := json.Marshal(transactionTracker)
	transaction, _ := json.Marshal(MoneyTransaction)
	if CheckingValidMoney(APIstub, args) {
		_ = APIstub.PutState(MoneyTransaction.Key, transaction)
		_=APIstub.PutState(notify.Key,notifyBytes)
		RedeemMoney(APIstub, args)
		AddMoney(APIstub, args)
		_ = APIstub.PutState(transactionTracker.Key, trackTransaction)

	} else {
		return shim.Error("Don't have sufficent money in the account")
	}
	return shim.Success(nil)
}

func (s *SmartContract) CertificateHolderTransaction(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	////if len(args)!=3{
	//	return shim.Error("Expected number 3.")
	//}
	fmt.Println("\n\n\n - CertificateHolder Transaction \n---------------")
	for _, arg := range args {
		fmt.Println(arg)
		fmt.Println("**--**")
	}
	fmt.Println("=================")


	k, _ := strconv.ParseFloat(args[2], 64)
	//senderAdd := args[0]
	//recieverAdd :=args[1]
	userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", args[0]).getQueryString()
	queryValue,_:=lastQueryValueForQueryString(APIstub,userQuery)

	var userData UserCertificate
	_=json.Unmarshal(queryValue,&userData)

	accountAddress := userData.AccountAddress

	MoneyTransaction := MoneyTransaction{}
	MoneyTransaction.Doctype = "Transaction"
	MoneyTransaction.SenderAddress = accountAddress
	MoneyTransaction.RecieverAddress = args[1]
	MoneyTransaction.Amount = k
	MoneyTransaction.Indicator = 0
	MoneyTransaction.TransactionTime =  time.Now()

	var str =" You GoT " + fmt.Sprintf("%f",k) + "from this " + accountAddress +"account"
	var notify = Notification{
		Doctype:"Notification",
		NotificationAddress: args[1],
		Message: str,
		Key:utils.RandomString(),
	}
	notifyBytes, _:= json.Marshal(notify)


	transaction, _ := json.Marshal(MoneyTransaction)

	queryString := newCouchQueryBuilder().addSelector("Doctype", "Transaction Tracker").addSelector("CertificateHolderAddress",accountAddress).addSelector("EndorserAddress",args[1]).getQueryString()
	queryValue,_ = lastQueryValueForQueryString(APIstub, queryString)
	var certificateHolderTransactionTrack TransactionTracker
	_ = json.Unmarshal(queryValue, &certificateHolderTransactionTrack)

	fmt.Println("\n\n\n From certificate Holder", queryString,"\n\n\n" )

	//	trackTransaction,_:=json.Marshal(transactionTracker)

	if CheckingValidMoney(APIstub, args) {
		_ = APIstub.PutState(MoneyTransaction.Key, transaction)
		_=APIstub.PutState(notify.Key,notifyBytes)
		RedeemMoney(APIstub, args)
		AddMoney(APIstub, args)
		certificateHolderTransactionTrack.ReturnMoneyByCertificateHolder = k
		certificateHolderTransactionTrack.CertificateHolderTransactionTime = time.Now()
		fmt.Println("\n\n\n From certificate Holder", certificateHolderTransactionTrack.ReturnMoneyByCertificateHolder,"\n\n\n" )
		certificateHolderTransactionTrackBytes,_ := json.Marshal(certificateHolderTransactionTrack)
		_= APIstub.PutState(certificateHolderTransactionTrack.Key,certificateHolderTransactionTrackBytes)
		UpdateTrustValue(APIstub, args)
	} else {
		return shim.Error("Don't have sufficent money in the account")
	}
	return shim.Success(nil)
}

func (s *SmartContract) LendSeekingRequest(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	u ,_:=strconv.ParseFloat(args[1],64)
	v ,_:=strconv.Atoi(args[2])
	userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", args[0]).getQueryString()
	queryValue,_:=lastQueryValueForQueryString(APIstub,userQuery)

	var userData UserCertificate
	_=json.Unmarshal(queryValue,&userData)

	accountAddress := userData.AccountAddress



	lendSeeking := LendSeeker{
		Doctype:           "Lend Seeking",
		LendSeekerAddress: accountAddress,
		Money:             u,
		NeededMoneyFor:    v,
		Key:              utils.RandomString(),
	}
	lendSeekingBytes ,_:=json.Marshal(lendSeeking)
	_=APIstub.PutState(lendSeeking.Key,lendSeekingBytes)
	return shim.Success(nil)
}

func (s *SmartContract) LendingTransaction(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	k, _ :=strconv.ParseFloat(args[2],64)
	userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", args[0]).getQueryString()
	queryValue,_:=lastQueryValueForQueryString(APIstub,userQuery)

	var userData UserCertificate
	_=json.Unmarshal(queryValue,&userData)

	accountAddress := userData.AccountAddress
	var MoneyTransaction = MoneyTransaction{
		Doctype:         "Transaction",
		SenderAddress:   accountAddress,
		RecieverAddress: args[1],
		Amount:          k,
		TransactionTime: time.Now(),
		Indicator: 1,
		Key:             utils.RandomString(),
	}
	transaction ,_:= json.Marshal(MoneyTransaction)
	_= APIstub.PutState(MoneyTransaction.Key, transaction)
	l,_:= strconv.Atoi(args[3])
	m,_:=strconv.Atoi(args[4])
	n,_:=strconv.Atoi(args[5])
	o,_:=strconv.Atoi(args[6])
	var lendingTransaction = LendingTracker{
		Doctype:             "Lend Tracker",
		LendSeekerAddress:   args[1],
		LenderAddress:       accountAddress,
		GivenMoneyByLender:  k,
		MoneyHasGivenFor:    l,
		InterestRate:        m,
		TotalInstallment: n,
		Installment:o,
		TransactionTime:     time.Now(),
		ReturnMoneyByLendSeeker: 0,
		Key:                 utils.RandomString(),
	}
	var str = accountAddress+ " has given you " + fmt.Sprintf("%f",k) + "from " + time.Now().String() + " based on" + fmt.Sprintf("%f",m) + " % interest Rate. You have to return it in " +fmt.Sprintf("%f",n) + "installment."
	var notify = Notification{
		Doctype:"Notification",
		NotificationAddress: args[1],
		Message: str,
		Key:utils.RandomString(),
	}
	notifyBytes, _:= json.Marshal(notify)

	if CheckingValidMoney(APIstub, args) {
		RedeemMoney(APIstub,args)
		AddMoney(APIstub,args)
		LendingTransactionBytes ,_:= json.Marshal(lendingTransaction)
		_= APIstub.PutState(lendingTransaction.Key, LendingTransactionBytes)
		_= APIstub.PutState(notify.Key,notifyBytes)
	} else {
		return shim.Error("Don't have sufficent money in the account")
	}



	return shim.Success(nil)
}

func (s *SmartContract) ReturnInstallment(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	k, _ := strconv.ParseFloat(args[2], 64)
	userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", args[0]).getQueryString()
	queryValue,_:=lastQueryValueForQueryString(APIstub,userQuery)

	var userData UserCertificate
	_=json.Unmarshal(queryValue,&userData)

	accountAddress := userData.AccountAddress

	var moneyTransaction = MoneyTransaction{
		Doctype:         "Transaction",
		SenderAddress:   accountAddress,
		RecieverAddress: args[1],
		Amount:          k,
		TransactionTime: time.Now(),
		Indicator: 0,
		Key:             utils.RandomString(),
	}

	transaction, _ := json.Marshal(moneyTransaction)
	queryString := newCouchQueryBuilder().addSelector("Doctype", "Lend Tracker").addSelector("LenderAddress", args[1]).addSelector("LendSeekerAddress",args[0]).getQueryString()
	queryValue, _ = lastQueryValueForQueryString(APIstub,queryString)
	var lendTracker LendingTracker
	_ = json.Unmarshal(queryValue, &lendTracker)



	lendTrackerBytes, _:=json.Marshal(lendTracker)
	var str =" You GoT " + fmt.Sprintf("%f",k) + "from this " + accountAddress +"account as your " +  strconv.Itoa(lendTracker.Installment) + "installment "
	var notify = Notification{
		Doctype:"Notification",
		NotificationAddress: args[1],
		Message: str,
		Key:utils.RandomString(),
	}
	notifyBytes, _:= json.Marshal(notify)
	if lendTracker.TotalInstallment != lendTracker.Installment{
		RedeemMoney(APIstub,args)
		AddMoney(APIstub,args)
		_= APIstub.PutState(moneyTransaction.Key,transaction)
		lendTracker.Installment+=1
		lendTracker.ReturnMoneyByLendSeeker = k
		_=APIstub.PutState(lendTracker.Key, lendTrackerBytes)
		_=APIstub.PutState(notify.Key,notifyBytes)
	}


	 return shim.Success(nil)
}

func UpdateTrustValue(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	//
	//if len(args) != 3{
	//	return shim.Error("Expecting 3")
	//}
	fmt.Println("\n\n\n -Update Trust Value \n---------------")
	for _, arg := range args {
		fmt.Println(arg)
		fmt.Println("**--**")
	}
	fmt.Println("=================")
	userqueryString := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", args[0]).getQueryString()
	userqueryResult, _ := lastQueryValueForQueryString(APIstub, userqueryString)

	var userCertificate UserCertificate
	_ = json.Unmarshal(userqueryResult, &userCertificate)
	fmt.Println(userCertificate.UserName)
	queryString := newCouchQueryBuilder().addSelector("Doctype", "Transaction Tracker").addSelector("CertificateHolderAddress", userCertificate.AccountAddress).addSelector("EndorserAddress",args[1]).getQueryString()
	queryValue, _ := lastQueryValueForQueryString(APIstub, queryString)

	fmt.Println("\n\n", queryValue, "\n\n")
	var tracker TransactionTracker
	_ = json.Unmarshal(queryValue, &tracker)
	k, _ := strconv.ParseFloat(args[2], 64)
	if tracker.ReturnMoneyByCertificateHolder == k {
		difference := tracker.GivenMoneyByEndorser - k
		fmt.Println("\n\n\n Difference between recieve and send money", difference,"\n\n")
		if difference == 0 {
			fmt.Println("\n", userCertificate.TrustValue,userCertificate.TotalCertified,"\n\n\n\n")
			u := (userCertificate.TrustValue + 10.00) / (userCertificate.TotalCertified + 1)
			userCertificate.TrustValue = u
			userCertificate.TotalCertified += 1
			userCertificateBytes,_:=json.Marshal(userCertificate)
			fmt.Println("\n\n\n\n\n updated user certificate", u , userCertificate.TrustValue,userCertificate.TotalCertified, "\n\n\n\n")
			_= APIstub.PutState(userCertificate.Key,userCertificateBytes)
		} else {
			v := (tracker.ReturnMoneyByCertificateHolder * 10.00) / tracker.GivenMoneyByEndorser
			u := (userCertificate.TrustValue + v) / (userCertificate.TotalCertified + 1)
			userCertificate.TrustValue = u
			userCertificate.TotalCertified += 1
			userCertificateBytes,_:=json.Marshal(userCertificate)
			_= APIstub.PutState(userCertificate.Key,userCertificateBytes)
		}
	} else {
		return shim.Error("Error from update trust model")
	}
	return shim.Success(nil)
}
func CheckingValidMoney(APIstub shim.ChaincodeStubInterface, args []string) bool {
	//if len(args) !=2 {
	////return shim.Error("Don't have enough argument. Expecting 2")
	//	return false
	//}
	userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", args[0]).getQueryString()
	queryValue,_:=lastQueryValueForQueryString(APIstub,userQuery)

	var userData UserCertificate
	_=json.Unmarshal(queryValue,&userData)

	accountAddress := userData.AccountAddress
	fmt.Println("\n\n\n")
	queryString := newCouchQueryBuilder().addSelector("Doctype", "Account Holder").addSelector("AccountAddress", accountAddress).getQueryString()
	fmt.Println(queryString)
	queryResult, _ := lastQueryValueForQueryString(APIstub, queryString)
	fmt.Println("checkin valid money from ", accountAddress, "got money", args[1])
	var accountHolder BankAccount
	_ = json.Unmarshal(queryResult, &accountHolder)
	k, _ := strconv.ParseFloat(args[2], 64)
	fmt.Println("Account holder account have", accountHolder.Value)
	fmt.Println("Account holder account", accountHolder.AccountAddress)
	fmt.Println(k)

	fmt.Println("\n\n\n")
	if (accountHolder.Value - k) >= 0 {
		fmt.Println("Account holder account have", accountHolder.Value)
		return true
		//	return shim.Success([]byte(accountHolder))
	} else {
		// fmt.Println(accountHolder.Value)
		return false
		//return shim.Error("Don't have sufficient money in the account")
	}

}
func RedeemMoney(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	//if len(args) != 2{
	//	return shim.Error("Expecting 2")
	//}
	fmt.Println("\n\n redeem money \n\n")
	userQuery := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("Usermail", args[0]).getQueryString()
	queryValue,_:=lastQueryValueForQueryString(APIstub,userQuery)

	var userData UserCertificate
	_=json.Unmarshal(queryValue,&userData)

	accountAddress := userData.AccountAddress



	queryString := newCouchQueryBuilder().addSelector("Doctype", "Account Holder").addSelector("AccountAddress", accountAddress).getQueryString()
	queryResult, _ := lastQueryValueForQueryString(APIstub, queryString)
	//queryString1 := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("AccountAddress", args[0]).getQueryString()
	//queryResult1, _ := lastQueryValueForQueryString(APIstub, queryString1)
	//var updatingUserCertificate UserCertificate
	//_=json.Unmarshal(queryResult1,& updatingUserCertificate)
	var accountHolder BankAccount
	_ = json.Unmarshal(queryResult, &accountHolder)
	k, _ := strconv.ParseFloat(args[2], 64)

	accountHolder.Value = accountHolder.Value - k
	userData.AccountValue = accountHolder.Value
	accountHolderBytes, _ := json.Marshal(accountHolder)
	userDataBytes, _ := json.Marshal(userData)
	err := APIstub.PutState(accountHolder.Key, accountHolderBytes)
	_ =APIstub.PutState(userData.Key, userDataBytes)

	if err!=nil {
		fmt.Println("Huge Error occured: ")
		fmt.Println(err.Error())

	} else {
		fmt.Println(accountHolder)
		fmt.Println("K = " , k)
	}

	return shim.Success(nil)
}
func AddMoney(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	//if len(args) != 2{
	//	return shim.Error("Expecting 2")
	//}

	queryString := newCouchQueryBuilder().addSelector("Doctype", "Account Holder").addSelector("AccountAddress", args[1]).getQueryString()
	queryResult, _ := lastQueryValueForQueryString(APIstub, queryString)

	queryString1 := newCouchQueryBuilder().addSelector("Doctype", "User Certificate").addSelector("AccountAddress", args[1]).getQueryString()
	queryResult1, _ := lastQueryValueForQueryString(APIstub, queryString1)
	var updatingUserCertificate UserCertificate
	_=json.Unmarshal(queryResult1,& updatingUserCertificate)

	var accountHolder BankAccount
	_ = json.Unmarshal(queryResult, &accountHolder)
	k, _ := strconv.ParseFloat(args[2], 64)

	accountHolder.Value = accountHolder.Value + k
	updatingUserCertificate.AccountValue = accountHolder.Value
	updatingUserCertificateBytes, _ := json.Marshal(updatingUserCertificate)
	_= APIstub.PutState(updatingUserCertificate.Key, updatingUserCertificateBytes)
	accountHolderBytes, _ := json.Marshal(accountHolder)
	_ = APIstub.PutState(accountHolder.Key, accountHolderBytes)
	return shim.Success(nil)
}

// The main function is only relevant in unit test mode. Only included here for completeness.
func main() {
	// Create a new Smart Contract
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}
