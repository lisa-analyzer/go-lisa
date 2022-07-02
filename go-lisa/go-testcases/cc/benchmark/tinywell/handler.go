package server

import (
	"github.com/golang/protobuf/proto"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	"github.com/hyperledger/fabric-protos-go/peer"
	"github.com/pkg/errors"
)

// HandleState ..
const (
	HandleStateCreated = iota
	HandleStateRegistered
	HandleStateReady
)

// PeerChaincodeStream ..
type PeerChaincodeStream interface {
	Send(*peer.ChaincodeMessage) error
	Recv() (*peer.ChaincodeMessage, error)
}

// StateDB ..
type StateDB interface {
	Put(txid string, key string, value []byte) error
	Get(txid string, key string) ([]byte, error)
	Del(txid string, key string) error
	Commit(txid string) error
	Clear(txid string) error
}

// TxEventer .
type TxEventer interface {
	SendCompleted(txid string, res *peer.Response)
	SendError(txid string, errMsg string)
}

// DBSupport ..
type DBSupport interface {
	GetDB(channel string) StateDB
}

// Handler ..
type Handler struct {
	stream    PeerChaincodeStream
	state     int
	dbSupport DBSupport
	eventer   TxEventer
}

// NewHandler ..
func NewHandler(
	stream PeerChaincodeStream,
	db DBSupport,
	event TxEventer) *Handler {
	return &Handler{
		stream:    stream,
		state:     HandleStateCreated,
		dbSupport: db,
		eventer:   event,
	}
}

// Invoke ...
func (h *Handler) Invoke(txid, channel, fn string, args []string) {

	var isInit bool
	var inputArgs [][]byte
	var oargs []string
	if fn == "Init" {
		isInit = true
		oargs = args
	} else {
		oargs = make([]string, 0, len(args)+1)
		oargs = append(oargs, fn)
		oargs = append(oargs, args...)
	}
	inputArgs, err := argsToBytes(oargs...)
	if err != nil {
		h.eventer.SendError(txid, err.Error())
		return
	}
	input := &peer.ChaincodeInput{
		Args:   inputArgs,
		IsInit: isInit,
	}
	raw, err := proto.Marshal(input)
	if err != nil {
		h.eventer.SendError(txid, errors.WithMessage(err, "序列化 input 参数出错").Error())
		return
	}
	cctype := peer.ChaincodeMessage_TRANSACTION
	if isInit {
		cctype = peer.ChaincodeMessage_INIT
	}
	msg := &peer.ChaincodeMessage{
		Type:      cctype,
		Payload:   raw,
		Txid:      txid,
		ChannelId: channel,
	}
	err = h.stream.Send(msg)
	if err != nil {
		h.eventer.SendError(txid, err.Error())
	}
}

// Keepalive 心跳
func (h *Handler) Keepalive() error {
	msg := &peer.ChaincodeMessage{Type: peer.ChaincodeMessage_KEEPALIVE}
	return h.stream.Send(msg)
}

// HandleMessage ..
func (h *Handler) HandleMessage(msg *peer.ChaincodeMessage) error {
	if msg == nil {
		return errors.New("消息为空")
	}
	switch msg.Type {
	case peer.ChaincodeMessage_REGISTER:
		return h.handleRegister()
	case peer.ChaincodeMessage_COMPLETED:
		return h.handleCompleted(msg)
	case peer.ChaincodeMessage_ERROR:
		return h.handleError(msg)
	case peer.ChaincodeMessage_GET_STATE:
		return h.handleGetState(msg)
	case peer.ChaincodeMessage_PUT_STATE:
		return h.handlePutState(msg)
	case peer.ChaincodeMessage_DEL_STATE:
	default:
		return nil
	}
	return nil
}

func (h *Handler) handleRegister() error {
	registeredMsg := &peer.ChaincodeMessage{Type: peer.ChaincodeMessage_REGISTERED}
	err := h.stream.Send(registeredMsg)
	if err != nil {
		return err
	}
	h.state = HandleStateRegistered
	readyMsg := &peer.ChaincodeMessage{Type: peer.ChaincodeMessage_READY}
	h.stream.Send(readyMsg)
	if err != nil {
		return err
	}
	h.state = HandleStateReady
	return nil
}

func (h *Handler) handlePutState(msg *peer.ChaincodeMessage) error {
	putState := &peer.PutState{}
	err := proto.Unmarshal(msg.Payload, putState)
	if err != nil {
		return errors.WithMessage(err, "解析 PutState 数据出错")
	}
	db := h.dbSupport.GetDB(msg.ChannelId)
	err = db.Put(msg.Txid, putState.Key, putState.Value)
	if err != nil {
		return errors.WithMessagef(err, "putstate 出错,txid=%s key=%s", msg.Txid, putState.Key)
	}
	rsp := &peer.ChaincodeMessage{
		Type:      peer.ChaincodeMessage_RESPONSE,
		Txid:      msg.Txid,
		ChannelId: msg.ChannelId}
	err = h.stream.Send(rsp)
	if err != nil {
		return errors.WithMessage(err, "发送 putstate 回复消息出错")
	}
	return nil
}

func (h *Handler) handleGetState(msg *peer.ChaincodeMessage) error {
	getState := &peer.GetState{}
	err := proto.Unmarshal(msg.Payload, getState)
	if err != nil {
		return errors.WithMessage(err, "解析 PutState 数据出错")
	}
	db := h.dbSupport.GetDB(msg.ChannelId)
	res, err := db.Get(msg.Txid, getState.Key)
	if err != nil {
		return errors.WithMessagef(err, "getstate 出错，txid=%s key=%s", msg.Txid, getState.Key)
	}
	rsp := &peer.ChaincodeMessage{
		Type:      peer.ChaincodeMessage_RESPONSE,
		Payload:   res,
		Txid:      msg.Txid,
		ChannelId: msg.ChannelId,
	}
	err = h.stream.Send(rsp)
	if err != nil {
		return errors.WithMessage(err, "发送 getstate 结果出错")
	}
	return nil
}

func (h *Handler) handleCompleted(msg *peer.ChaincodeMessage) error {
	rsp := &peer.Response{}
	err := proto.Unmarshal(msg.Payload, rsp)
	if err != nil {
		return errors.WithMessagef(err, "解析交易结果出错，txid=%s", msg.Txid)
	}
	db := h.dbSupport.GetDB(msg.ChannelId)
	if rsp.Status == shim.OK {
		db.Commit(msg.Txid)
	} else {
		db.Clear(msg.Txid)
	}

	h.eventer.SendCompleted(msg.Txid, rsp)
	return nil
}

func (h *Handler) handleError(msg *peer.ChaincodeMessage) error {
	h.eventer.SendError(msg.Txid, string(msg.Payload))
	return nil
}
func argsToBytes(args ...string) ([][]byte, error) {

	res := make([][]byte, 0, len(args))
	for _, a := range args {
		raw := []byte(a)
		res = append(res, raw)
	}
	return res, nil
}
