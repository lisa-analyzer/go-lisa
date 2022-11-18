
 package main

 import (
	 "fmt"
  	"time"
	 // "reflect"
	// "bytes"
	 "encoding/json"
	//  "strconv"
	 "github.com/hyperledger/fabric/core/chaincode/shim"
	 "github.com/hyperledger/fabric/protos/peer"
 )



 	/********************************************************
	 상태값
	 Succcess - 운전자와 화물 의뢰자 간 계약이 채결이 된 상태
	 FAIL - 화물 계약 취소
         Yet - 운전자와 화물 의뢰자 간 계약이 채결이 되기 전 상태
	 COMPLETE - 화물 이송이 끝나고 계약이 완료됨
	**********************************************************/
 const (
	 SUCCESS string = "Success"
	 FAIL string = "Fail"
	 YET string = "Yet"
	 COMPLETE string = "Complete"
 )


 // SmartContract implements a simple chaincode to manage an asset
 type SmartContract struct {
 }

 	/********************************************************
	  화물 계약
	  화물계약은 CARGO + YYYYMMDD + _ + TxID 형식이 키이다

	Weight - 화물 무게
	Distance - 거리
	Money - 의뢰 금액
	Date - 의뢰 일
	Registrant - 화물 의뢰자
	Driver - 화물 운송업자
	Recipient - 화물 수령자
	Status - 계약 상태
	**********************************************************/
type CargoContract struct {
	 Weight int `json:"weight"`
	 Distance float64 `json:"distance"`
	 Money int `json:"money"`
	 Date string `json:"date"`
	 Registrant string `json:registrant`
	 Driver string `json:driver`
	 Recipient string `json:recipient`
	 Status string `json:status`
 }
/*********************************************************
     날짜별 계약관리
     Start - 시작 인덱스
     End - 끝 인덱스
**********************************************************/
 type CargoContext struct{
        Start int `json:"start"`
	      End int `json:"end"`
 }

 /********************************************************
	 계정 별 포인트 현황
	 User - 계정 ID
	 Total - 계정 보유 포인트
	**********************************************************/
 type PointContract struct {
	 Username string `json:username`
	 Total int `json:total`
 }

 var logger = shim.NewLogger("example_cc0")

 // Init is called during chaincode instantiation to initialize any
 // data. Note that chaincode upgrade also calls this function to reset
 // or to migrate data.
 func (t *SmartContract) Init(stub shim.ChaincodeStubInterface) peer.Response {
	 logger.Info("########### example_cc0 Init ###########")

	 return shim.Success(nil)
 }

 // Invoke is called per transaction on the chaincode. Each transaction is
 // either a 'get' or a 'set' on the asset created by Init function. The Set
 // method may create a new asset by specifying a new key-value pair.
 func (t *SmartContract) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	 logger.Info("########### example_cc0 Invoke ###########")
	 // Extract the function and args from the transaction proposal
	 fn, args := stub.GetFunctionAndParameters()


	 switch method := fn; method {
			case "initLedger":
				return t.initLedger(stub)
			case "createContract":
				return t.createContract(stub, args)
			case "queryCargo" :
				return t.queryCargo(stub, args)
			case "queryAllCargo" :
				return t.queryCargo(stub, args)
			case "signContract":
					return t.signContract(stub, args)
			case "cancelContract":
				return t.cancelContract(stub,args)
			case "completeContract":
				return t.completeContract(stub,args)
			case "queryPoint" :
				return t.queryPoint(stub, args)
			case "addPoint":
				return t.addPoint(stub, args)
			case "subtractPoint":
				return t.subtractPoint(stub, args)
			case "createUser":
				return t.createUser(stub,args)
			case "queryMylist":
				return t.queryMylist(stub,args)
			default :
				return shim.Success([]byte(nil))
	}

 }


 	/********************************************************
	 체인코드 실행 시 실행되는 초기 데이터 셋
	 docker-compose에 정의됨
	 cargo , point에 관련된 데이터 셋 정의
	**********************************************************/
 func (t *SmartContract) initLedger(stub shim.ChaincodeStubInterface) peer.Response {

	now := time.Now()
	dateTestValue := now.Format("2006-01-02")

	cargo := CargoContract{Weight: 3, Distance: 5.0, Money: 5, Date: dateTestValue, Registrant : "you", Driver : "me", Recipient : "him" , Status : SUCCESS}
	cargocontext := CargoContext{Start :1 ,End :1}

	 cargoAsBytes, _ := json.Marshal(cargo)
	 cargocontextAsBytes,_ := json.Marshal(cargocontext)


	 date := now.Format("20060102")
     fmt.Println(date)
	 stub.PutState("CARGO" + date + "_1" , cargoAsBytes)
	 stub.PutState("CARGO" + date, cargocontextAsBytes)
//06-06 _ 1
	 testdate := time.Date(
        2018, 6, 6, 0, 0, 0, 0, time.UTC)

	 dateTestValue2 := testdate.Format("2006-01-02")
	 cargo1 := CargoContract{Weight: 3, Distance: 5.0, Money: 5, Date: dateTestValue2, Registrant : "you", Driver : "me", Recipient : "her" , Status : YET}
	 cargocontext1 := CargoContext{Start :1 ,End :1}

	 cargoAsBytes1, _ := json.Marshal(cargo1)
	 cargocontextAsBytes1,_ := json.Marshal(cargocontext1)


	 date1 := testdate.Format("20060102")
	 stub.PutState("CARGO" + date1 + "_1" , cargoAsBytes1)
	 stub.PutState("CARGO" + date1, cargocontextAsBytes1)

//06-06 _ 2
	 cargo2 := CargoContract{Weight: 3, Distance: 5.0, Money: 5, Date: dateTestValue2, Registrant : "you", Driver : "me", Recipient : "her" , Status : FAIL}
	 cargocontext2 := CargoContext{Start :1 ,End :2}

	 cargoAsBytes2, _ := json.Marshal(cargo2)
	 cargocontextAsBytes2,_ := json.Marshal(cargocontext2)

	 stub.PutState("CARGO" + date1 + "_2" , cargoAsBytes2)
	 stub.PutState("CARGO" + date1, cargocontextAsBytes2)



	 point := []PointContract{
		PointContract{Username : "kim", Total : 1000},
		PointContract{Username : "lim", Total : 1000},
	 }

	 pointAsBytes, _ := json.Marshal(point[0])
	 stub.PutState("redtree0" , pointAsBytes)
	 pointAsBytes1, _ := json.Marshal(point[1])
	 stub.PutState("color0e" , pointAsBytes1)


	return shim.Success(nil)
 }



 // main function starts up the chaincode in the container during instantiate
 func main() {
	 if err := shim.Start(new(SmartContract)); err != nil {
		 fmt.Printf("Error starting SmartContract chaincode: %s", err)
	 }
 }
