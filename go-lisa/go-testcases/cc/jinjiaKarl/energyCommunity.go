package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"

	pb "github.com/hyperledger/fabric/protos/peer"
	"io"
	"crypto/rand"
	"crypto/md5"
	"encoding/base64"
	"encoding/hex"
	"strconv"
	"errors"
	"encoding/json"
	"time"
)

/*
家庭用户
账户地址
剩余能量 //部分家庭没有太阳能电池板，值为0
账户余额（电子货币）
编号
状态 //0：不可购买， 1：可以购买
账户公钥
账户私钥
交易(一笔交易必须同时具有卖方和买方的公钥签名，方能承认这笔交易。公钥签名生成规则，公钥+待创建交易的ID号，在本交易类型中，只要买家有足够的货币，卖家自动会对交易进行签名)
购买方地址
销售方地址
电量销售量
电量交易金额
编号
交易时间
 */

 // 账户私钥应该由安装在本地的客户端生成，本例中为了简便，使用模拟私钥和公钥。每位用户的私钥为guid+“1”，公钥为guid+“2”。签名方式简化为私钥+"1"
type SimpleChaincode struct {

}

var homeNo int = 0    // home的ID，0代表第一个
var transactionNo int = 0  // transaction的ID，0代表第一个

type Home struct {
	Address string  // 家庭地址
	Energy int //剩余能量
	Money int //账户余额
	Id int //编号
	Status int // 状态 0：不可购买 1：可以购买
	PriKey string  //私钥
	PubKey string  //公钥
}

type Transaction struct {
	BuyerAddress string //买家的账户地址
	BuyerAddressSign string  //买家的签名
	SellerAddress string  //卖家的账户地址
	Energy int //销售的电量
	Money int //电量交易金额
	Id int   //编号
	Time int64  // 交易时间
}





func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response  {
	return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args :=  stub.GetFunctionAndParameters()

	if function == "createUser" {
		return t.createUser(stub, args)
	}else if function == "buyByAddress" {
		return t.buyByAddress(stub, args)
	}else if function == "changeStatus" {
		return t.changeStatus(stub, args)
	}else if function == "getTransactionById" {
		return t.getTransactionById(stub, args[0])
	}else if function == "getTransactions" {
		return t.getTransactions(stub)
	}else if function == "getHomes" {
		return t.getHomes(stub)  // 还未实现
	}else if function == "getHomeByAddress" {
		return t.getHomeByAddress(stub, args[0])
	}

	return shim.Success(nil)

}

func GetAddress() (string, string, string){
	var address, priKey, pubKey string
	b := make([]byte, 48)

	if _, err := io.ReadFull(rand.Reader, b); err != nil{
		return "", "", ""
	}

	h := md5.New()
	h.Write([]byte(base64.URLEncoding.EncodeToString(b)))

	address = hex.EncodeToString(h.Sum(nil))
	priKey = address + "1"
	pubKey = address + "2"

	return address, priKey, pubKey

}

func (t *SimpleChaincode) createUser(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var energy, money int
	var err error
	var homeBytes []byte
	if len(args) != 2{
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}
	// 获取地址，私钥和公钥
	address, priKey, pubKey := GetAddress()
	energy, err = strconv.Atoi(args[0])
	if err != nil {
		return shim.Error("want integer number")
	}
	money, err = strconv.Atoi(args[1])
	if err != nil {
		return shim.Error("want integer number")
	}
	fmt.Printf("HomeInfo: address = %v, energy = %v, money = %v, homeNo = %v, priKey = %v, pubKey = %v\n", address, energy, money, homeNo, priKey, pubKey)
	home := Home{Address: address, Energy: energy, Money: money, Id: homeNo, Status: 1, PriKey: priKey, PubKey: pubKey}

	// 将内容写入账本中
	err = writeHome(stub, home)
	if err != nil {
		return  shim.Error("write Error" + err.Error())
	}

	// 将homeBytes返回
	homeBytes, err = json.Marshal(&home)
	if err != nil {
		return shim.Error("Error retrieve")
	}
	homeNo = homeNo + 1
	return shim.Success(homeBytes)
}

func (t *SimpleChaincode) buyByAddress(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4")
	}
	var homeSeller, homeBuyer Home
	homeSellerRe := t.getHomeByAddress(stub, args[0])
	homeBuyerRe := t.getHomeByAddress(stub, args[2])

	err := json.Unmarshal(homeSellerRe.Payload, homeSeller)
	err = json.Unmarshal(homeBuyerRe.Payload, homeBuyer)
	//  这里判断签名有问题!!!!!!!!! ，签名简化为 账户地址+1
	if args[1] != args[2]+"1" {
		return shim.Error("Verify sign data failed!")
	}

	// args[3] 想要购买的电量数值
	buyValue, erro := strconv.Atoi(args[3])
	if erro != nil {
		return shim.Error("want integer number")
	}


	// 太阳能电池板的剩余电量为可以售出的电力的值，为了简化，单位为1.需要电力的家庭可以向有足够余额的电力的家庭购买电力。
	if homeSeller.Energy < buyValue && homeBuyer.Money < buyValue {
		return shim.Error("not enough money or energy")
	}

	fmt.Println("Before transaction:")
	fmt.Printf("    homeSeller.Energy = %d, homeSeller.Money = %d\n", homeSeller.Energy, homeSeller.Money)
	fmt.Printf("    homeBuyer.Energy = %d, homeBuyer.Money = %d\n", homeBuyer.Energy, homeBuyer.Money)

	// 对应剩余电量和价钱的变化
	homeSeller.Energy = homeSeller.Energy - buyValue
	homeSeller.Money = homeSeller.Money + buyValue
	homeBuyer.Energy = homeBuyer.Energy + buyValue
	homeBuyer.Money = homeBuyer.Money - buyValue

	fmt.Println("After transaction:")
	fmt.Printf("    homeSeller.Energy = %d, homeSeller.Money = %d\n", homeSeller.Energy, homeSeller.Money)
	fmt.Printf("    homeBuyer.Energy = %d, homeBuyer.Money = %d\n", homeBuyer.Energy, homeBuyer.Money)

	err = writeHome(stub, homeSeller)
	if err != nil {
		return  shim.Error("write Error" + err.Error())
	}

	err = writeHome(stub, homeBuyer)
	if err != nil {
		return  shim.Error("write Error" + err.Error())
	}

	fmt.Println("TransactionInfo:")
	fmt.Println("    BuyerAddress:", args[2])
	fmt.Println("    BuyerAddressSign:", args[1])
	fmt.Println("    SellerAddress:", args[0])
	fmt.Println("    Energy:", buyValue)
	fmt.Println("    Money:", buyValue)
	fmt.Println("    Id:", transactionNo)

	transaction := Transaction{BuyerAddress: args[2], BuyerAddressSign: args[1],
		SellerAddress: args[0], Energy: buyValue, Money: buyValue, Id: transactionNo, Time: time.Now().Unix()}
		// 将交易内容写入账本
	err = writeTransaction(stub, transaction)
	transactionNo = transactionNo + 1
	txBytes, err := json.Marshal(&transaction)
	if err != nil {
		return shim.Error("Error retrieving schoolBytes")
	}
	return shim.Success(txBytes)
}

func (t *SimpleChaincode) changeStatus(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}
	var home Home
	homeBytes := t.getHomeByAddress(stub, args[0])

	_ := json.Unmarshal(homeBytes.Payload, home)
	if args[1] == args[0]+"1" {
		status, _ := strconv.Atoi(args[2])
		home.Status = status
		err := writeHome(stub, home)
		if err != nil {
			return shim.Success(homeBytes.Payload)
		}
	}
	return shim.Success(nil)
}


func writeHome(stub shim.ChaincodeStubInterface, home Home) error {
	homeBytes, err := json.Marshal(&home)
	if err != nil {
		return errors.New("Marshalling Error" + err.Error())
	}
	// 将地址对应的home写入账本，Key:address Value:home
	err = stub.PutState(home.Address, homeBytes)
	if err != nil {
		return errors.New("PutState Error" + err.Error())
	}
	return nil
}

func writeTransaction(stub shim.ChaincodeStubInterface, transaction Transaction) error {
	txBytes, err := json.Marshal(transaction)
	if err != nil {
		return errors.New("Marshalling Error" + err.Error())
	}

	id := strconv.Itoa(transaction.Id)

	// key: "transaction"+id  value: transaction
	err = stub.PutState("transaction"+id, txBytes)
	if err != nil {
		return errors.New("PutState Error" + err.Error())
	}
	return nil
}

func (t *SimpleChaincode) getTransactionById(stub shim.ChaincodeStubInterface, id string) pb.Response {

	txBytes, err := stub.GetState("transaction" + id)
	if err != nil {
		fmt.Println("Error retrieving home")
	}

	return shim.Success(txBytes)
}

func (t *SimpleChaincode) getTransactions(stub shim.ChaincodeStubInterface ) pb.Response {
	// 模仿edu中的合约代码规则来改写
	var transactions []Transaction
	var number string
	var err error
	var transaction Transaction
	var transactionsBytes []byte

	if transactionNo <= 10 {
		i := 0
		for i < transactionNo {
			number = strconv.Itoa(i)
			transactionRe := t.getTransactionById(stub, number)
			err = json.Unmarshal(transactionRe.Payload, transaction)
			if err != nil {
				return shim.Error("Error get detail")
			}
			transactions = append(transactions, transaction)
			i = i + 1
		}

	} else {
		i := 0
		for i < 10 {
			number = strconv.Itoa(i)
			transactionRe := t.getTransactionById(stub, number)
			err = json.Unmarshal(transactionRe.Payload,transaction)
			if err != nil {
				return shim.Error("Error get detail")
			}
			transactions = append(transactions, transaction)
			i = i + 1
		}
	}
	transactionsBytes, err = json.Marshal(&transactions)
	if err != nil {
		shim.Error("Error get records")
	}
	return shim.Success(transactionsBytes)
}

func (t *SimpleChaincode) getHomes(stub shim.ChaincodeStubInterface) pb.Response {

}

func (t *SimpleChaincode) getHomesById(stub shim.ChaincodeStubInterface, id string) pb.Response {
	// 可以通过id找到对于的地址，然后通过地址来找到对应在账本中的信息
	return shim.Success(nil)
}





func (t *SimpleChaincode)getHomeByAddress(stub shim.ChaincodeStubInterface, address string) pb.Response{
	var home Home
	//从账本中获取数据
	homeBytes, err := stub.GetState(address)
	if err != nil {
		fmt.Println("Error retrieving home")
	}

	err = json.Unmarshal(homeBytes,home)
	if err != nil {
		fmt.Println("Error unmarshalling home")
	}

	return shim.Success(homeBytes)
}




func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil{
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}

}
