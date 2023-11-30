# GoLiSA: an abstract interpretation-based static analyzer for Go smart contracts and DApps
[![GitHub license](https://img.shields.io/github/license/lisa-analyzer/go-lisa)](https://github.com/lisa-analyzer/go-lisa/blob/master/LICENSE)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/lisa-analyzer/go-lisa/Gradle%20Build%20(master%20branch))
![GitHub last commit](https://img.shields.io/github/last-commit/lisa-analyzer/go-lisa)
![GitHub issues](https://img.shields.io/github/issues-raw/lisa-analyzer/go-lisa)

GoLiSA is a static analyzer based on abstract interpretation for smart contracts and decentralized applications written in Go. Currently, GoLiSA provides several static analyses for detecting issues in blockchain software written in Go, supporting Hyperledger Fabric, Cosmos SDK, and Tendermint frameworks.

## Usage
The main class is [GoLiSA](go-lisa/src/main/java/it/unive/golisa/GoLiSA.java) and it expects four parameters:
- `-i path`: the Go file to be analyzed
- `-o path`: the output directory
- `-f framework`: the blockchain framework used in the input file (`hyperledger-fabric`, `cosmos-sdk`, `tendermint-core`)
- `-a analysis`: the analysis to perform 
	- `non-determinism`  performs an analysis to detect explicit flows that lead to issues related to non-determinism in blockchain software
	- `non-determinism-ni`  performs an analysis to detect explicit and implicit flows that lead to issues related to non-determinism in blockchain software
	- `phantom-read` performs an analysis to detect phantom reads in blockchain software for Hyperledger Fabric 
	- `ucci`  (currently **NOT SUPPORTED**)
	- `dcci` performs an analysis to detect different cross-channel invocations in blockchain software for Hyperledger Fabric
	- `read-write` performs an analysis to detect __read-after-write__ and __over-write__ issues in blockchain software for Hyperledger Fabric 
	- `unhandled-errors` performs an analysis to detect unhandled errors in blockchain software for Hyperledger Fabric 
	
### Example of command line

`-i C:\Users\MyAccount\mycontract.go -o C:\Users\MyAccount\output -f hyperledger-fabric -a unhandled-errors`

## Publications
- Luca Olivieri, Luca Negrini, Vincenzo Arceri, Fabio Tagliaferro, Pietro Ferrara, Agostino Cortesi, Fausto Spoto: <i>Information Flow Analysis for Detecting Non-Determinism in Blockchain</i>. ECOOP 2023: 23:1-23:25 ([link](https://drops.dagstuhl.de/opus/volltexte/2023/18216/))
- Luca Olivieri, Fabio Tagliaferro, Vincenzo Arceri, Marco Ruaro, Luca Negrini, Agostino Cortesi, Pietro Ferrara, Fausto Spoto, Enrico Talin:
<i>Ensuring determinism in blockchain software with GoLiSA: an industrial experience report</i>. SOAP@PLDI 2022: 23-29 ([link](https://dl.acm.org/doi/10.1145/3520313.3534658))

## How to build the project ##
GoLiSA comes as a Gradle 8.0 project. For development with Eclipse, please install the [Gradle IDE Pack](https://marketplace.eclipse.org/content/gradle-ide-pack) plugin from the Eclipse marketplace, and make sure to import the project into the workspace as a Gradle project.
