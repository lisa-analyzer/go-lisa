package main

import (
	"log"
	"math/big"

	"github.com/ethereum/go-ethereum/accounts/abi/bind"
	"github.com/ethereum/go-ethereum/accounts/abi/bind/backends"
	"github.com/ethereum/go-ethereum/core"
	"github.com/ethereum/go-ethereum/crypto"
)

//go:generate abigen --sol Math.sol --pkg main --out mathsc.go
func main() {
	// Blockchain Simulator: https://github.com/ethereum/go-ethereum/wiki/Native-DApps:-Go-bindings-to-Ethereum-contracts

	key, _ := crypto.GenerateKey()
	auth := bind.NewKeyedTransactor(key)
	alloc := make(core.GenesisAlloc)
	alloc[auth.From] = core.GenesisAccount{Balance: big.NewInt(133700000)}
	sim := backends.NewSimulatedBackend(alloc)

	addr, tx, token, err := DeployMath(
		auth,
		sim,
	)
	if err != nil {
		log.Fatalf("could not deploy contract: %v", err)
	}
	log.Println("transaction waiting to be mined:", tx.Hash().String())
	log.Println("contract pending deploy:", addr.String())
	log.Println("got token:", token)

	log.Println("mining...")
	sim.Commit()

	sum, err := token.Sum(&bind.CallOpts{Pending: true}, big.NewInt(1), big.NewInt(2))
	if err != nil {
		log.Println(err)
	}
	log.Println("got sum:", sum)


	res, err := token.Total(&bind.CallOpts{Pending: true})
	if err != nil {
		log.Println(err)
	}
	
	log.Println("got output:", res)

	out, err := token.Add(auth, big.NewInt(100))
	if err != nil {
		log.Println(err)
	}
	
	log.Println("got output:", out.Hash().String())

}