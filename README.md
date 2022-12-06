# GoLiSA
[![GitHub license](https://img.shields.io/github/license/lisa-analyzer/go-lisa)](https://github.com/lisa-analyzer/go-lisa/blob/master/LICENSE)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/lisa-analyzer/go-lisa/Gradle%20Build%20(master%20branch))
![GitHub last commit](https://img.shields.io/github/last-commit/lisa-analyzer/go-lisa)
![GitHub issues](https://img.shields.io/github/issues-raw/lisa-analyzer/go-lisa)

GoLiSA: A Static Analyzer for Go Smart Contracts and Decentralized Applications

## How to use GoLiSA
The main class is [GoLiSA](go-lisa/src/main/java/it/unive/golisa/GoLiSA.java) and it expects four paramaters:
- `-i path`: the Go file to be analyzed
- `-o path`: the output directory
- `-f framework`: the blockchain framework used in the input file (hyperledger-fabric, cosmos-sdk, tendermint-core)
- `-a analysis`: the analysis to perform to detect issues of non-determinism (taint, non-interference)

### Example of command line

`-i C:\Users\MyAccount\mycontract.go -o C:\Users\MyAccount\output -f hyperledger-fabric -a taint`

## How to build the project ##
GoLiSA comes as a Gradle project. For development with Eclipse, please install the [Gradle IDE Pack](https://marketplace.eclipse.org/content/gradle-ide-pack) plugin from the Eclipse marketplace, and make sure to import the project into the workspace as a Gradle project.
