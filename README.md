# GoLiSA
GoLiSA: A Static Analyzer for Go Smart Contracts and DApps (SOAP 2022)

## How to use GoLiSA
The main class is [GoLiSA](go-lisa/src/main/java/it/unive/golisa/GoLiSA.java) and it expects four paramaters:
- `-i path`: the Go file to be analyzed
- `-o path`: the output directory
- `-a analysis`: the analysis to perform to detect issues of non-determinism (semantic, syntactic)

### Example of command line

`-i C:\Users\MyAccount\mycontract.go -o C:\Users\MyAccount\output -a semantic`

## How to build the project ##
GoLiSA comes as a Gradle 6.0 project. For development with Eclipse, please install the [Gradle IDE Pack](https://marketplace.eclipse.org/content/gradle-ide-pack) plugin from the Eclipse marketplace, and make sure to import the project into the workspace as a Gradle project.
