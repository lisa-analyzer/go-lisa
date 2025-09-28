package distribution

import (
	"time"

	"github.com/cosmos/cosmos-sdk/telemetry"
	sdk "github.com/cosmos/cosmos-sdk/types"
	"github.com/cosmos/cosmos-sdk/x/distribution/keeper"
	"github.com/cosmos/cosmos-sdk/x/distribution/types"
)


func BeginBlocker(ctx sdk.Context, k keeper.Keeper) error {

	defer func() {
	    if r := recover(); r != nil {
	        fmt.Println("Handle panic")
	    }
	}()
	
	panic("PANIC AT THE DISCO!")
	
	return nil
}