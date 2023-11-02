# GoLiSA: an abstract interpretation-based static analyzer for Go smart contracts and DApps
[![GitHub license](https://img.shields.io/github/license/lisa-analyzer/go-lisa)](https://github.com/lisa-analyzer/go-lisa/blob/master/LICENSE)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/lisa-analyzer/go-lisa/Gradle%20Build%20(master%20branch))
![GitHub last commit](https://img.shields.io/github/last-commit/lisa-analyzer/go-lisa)
![GitHub issues](https://img.shields.io/github/issues-raw/lisa-analyzer/go-lisa)

GoLiSA is a static analyzer based on abstract interpretation for smart contracts and decentralized applications written in Go. At the moment, GoLiSA provides static analyses based on information flow analyses for detecting critical non-deterministic behaviors in blockchain software written in Go, supporting Hyperledger Fabric, Cosmos SDK, and Tendermint frameworks.

## Building GoLiSA
Compiling GoLiSA requires:
- JDK >= 11
- [Gradle](https://gradle.org/install/) >= 6.0 

```
git clone https://github.com/lisa-analyzer/go-lisa
cd go-lisa/go-lisa
./gradlew build
```
In order to bundle GoLiSA as a distribution:
```
./gradlew distZip`
unzip build/distributions/go-lisa-0.1 
```
Finally, to run GoLiSA:

```
./build/distributions/go-lisa-0.1/bin/go-lisa
```

### Building GoLiSA with snapshots

It is possible that GoLiSA refers to a snapshot release of LiSA to exploit unreleased features, and, when building, you get the following error message:

```
> Could not resolve io.github.lisa-analyzer:lisa-project:ver-SNAPSHOT.
  > Could not get resource 'https://maven.pkg.github.com/lisa-analyzer/lisa/io/github/lisa-analyzer/lisa-project/ver-SNAPSHOT/maven-metadata.xml'.
    > Could not GET 'https://maven.pkg.github.com/lisa-analyzer/lisa/io/github/lisa-analyzer/lisa-project/ver-SNAPSHOT/maven-metadata.xml'. Received status code 401 from server: Unauthorized
```

In this case, you need to perform the following steps:
- create a GitHub Personal Access Token following [this guide](https://docs.github.com/en/enterprise-cloud@latest/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token) and grant `read:packages` permission
- create a `gradle.properties` file at `go-lisa/go-lisa` (where the `gradlew` scripts are located) with the following content:
```
gpr.user=your-github-username
gpr.key=github-access-token
```

Finally, re-execute the build to have the snapshot dependencies downloaded.

### Development with Eclipse
GoLiSA comes as a Gradle 6.0 project. For development with Eclipse, please install the [Gradle IDE Pack](https://marketplace.eclipse.org/content/gradle-ide-pack) plugin from the Eclipse marketplace, and make sure to import the project into the Eclipse workspace as a Gradle project.


## Running GoLiSA
The entry point is the [GoLiSA](go-lisa/src/main/java/it/unive/golisa/GoLiSA.java) class, expecting four parameters:
- `-i <path>`: the Go input file to be analyzed
- `-o <path>`: the output directory
- `-f <framework>`: the blockchain framework used in the Go input file (`hyperledger-fabric`, `cosmos-sdk`, `tendermint-core`)
- `-a <analysis>`: the analysis to perform to detect issues of non-determinism (`taint`, `non-interference`)

### Example

`go-lisa -i mycontract.go -o output_dir -f hyperledger-fabric -a taint`

## Publications
- Luca Olivieri, Luca Negrini, Vincenzo Arceri, Fabio Tagliaferro, Pietro Ferrara, Agostino Cortesi, Fausto Spoto: <i>Information Flow Analysis for Detecting Non-Determinism in Blockchain</i>. ECOOP 2023: 23:1-23:25 ([link](https://drops.dagstuhl.de/opus/volltexte/2023/18216/))
- Luca Olivieri, Fabio Tagliaferro, Vincenzo Arceri, Marco Ruaro, Luca Negrini, Agostino Cortesi, Pietro Ferrara, Fausto Spoto, Enrico Talin:
<i>Ensuring determinism in blockchain software with GoLiSA: an industrial experience report</i>. SOAP@PLDI 2022: 23-29 ([link](https://dl.acm.org/doi/10.1145/3520313.3534658))
