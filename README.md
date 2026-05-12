<img src="logo.png" alt="logo" width="300"/>

---
# GoLiSA — Go Frontend of LiSA (Library for Static Analysis)

[![GitHub license](https://img.shields.io/github/license/lisa-analyzer/go-lisa)](https://github.com/lisa-analyzer/go-lisa/blob/master/LICENSE)
[![Built on LiSA](https://img.shields.io/badge/Built%20on-LiSA-informational)](https://github.com/lisa-analyzer/lisa)

**GoLiSA** is a static analysis tool for Go programs, built on top of the [LiSA (Library for Static Analysis)](https://github.com/lisa-analyzer/lisa) framework. It provides a front-end that translates Go source files into LiSA's control flow graph (CFG) representation, enriches it with the semantics of a subset of the Go standard library, and runs configurable abstract interpretation analyses to detect bugs and verify program properties.

---

## Blockchains and Smart Contracts Verification

GoLiSA provides several static analyses for detecting issues in blockchain software written in Go, supporting the semantics of a subset of instructions and functions from Hyperledger Fabric, Cosmos SDK, and Tendermint frameworks.

---

## Table of Contents

- [Overview](#overview)
- [Installation](#installation)
- [Usage](#usage)
- [Command-Line Options](#command-line-options)
- [Supported Analysis](#supported-analysis)
- [Publications](#publications)
- [Awards, Honors, and Honorable Mentions](#awards)
- [Experimental Evaluations](#experimental-evaluations)
- [Analysis Examples](#analysis-examples)
- [Analysis Examples](#analysis-examples)
- [Other Licences](#other-licences)

---

## Overview

GoLiSA translates Go source code into [LiSA](https://github.com/lisa-analyzer/lisa)'s intermediate representation and runs abstract interpretation analyses over the resulting program model. The analysis configuration is fully customizable — abstract domains, interprocedural strategy, and semantic checkers can all be selected independently. 

---

## Installation

**Prerequisites:** Java 17+, Gradle (wrapper included), GitHub credentials for the LiSA dependency.

### LiSA Dependency

LiSA packages are hosted on [GitHub Packages](https://github.com/lisa-analyzer/lisa/packages). Add your credentials to `~/.gradle/gradle.properties`:

```properties
gpr.user=<your-github-username>
gpr.key=<your-github-personal-access-token>
```

Or export the environment variables `USERNAME` and `TOKEN`.

### Build

```bash
git clone https://github.com/lisa-analyzer/go-lisa.git
cd go-lisa/go-lisa
./gradlew build
```

The archive containing the tool is written to `build/distributions/golisa-0.1.zip`.

---

## Usage

After building the distribution, run GoLiSA directly:

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa --help
```

The command run the tool printing the help message

---

## Command-Line Options

| Option | Long Option         | Argument          | Description                                                                                                                                                               |
| ------ | ------------------- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `-i`   | `--input`           | `file`            | Go source file to analyze (multiple files can be analyzed using multiple input options)			          					          					               |
| `-ci`  | `--contractinfo`    | `name` `channel`  | Deployment name and channel of the contract to analyze (`hyperledger-fabric` framework only)				          					          					       |
| `-o`   | `--outdir`          | `directory`       | Output directory for analysis results                                                                                                                                     |
| `-f`   | `--framework`       | `framework`       | Framework used by the input files: `hyperledger-fabric`, `cosmos-sdk`, `tendermint-core`                                                                                  |
| `-a`   | `--analysis`        | `analysis`        | Analysis to perform : `non-determinism`, `non-determinism-ni`, `phantom-read`, `ucci`, `cchi`, `read-write`, `unhandled-errors`, `var-numerical-overflow`,  `div-by-zero` |
| `-xc`  | `--crosscontract`   | —                 | Enable cross-contract analysis                                                                                                                                            |
| `-d`   | `--dumpAnalysis`    | —				   | Dump the analysis                                                                                                                                                         |
| `-h`   | `--help`            | —                 | Print the help message                                                                                                                                                    |

---

## Supported Analysis

| Analysis                 | Description                                                                                                                                |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------ |
| `non-determinism`        | performs an analysis to detect explicit flows that lead to issues related to __non-determinism__ in blockchain software                    |
| `non-determinism-ni`     | performs an analysis to detect explicit and implicit flows that lead to issues related to __non-determinism__ in blockchain software       |
| `phantom-read`           | performs an analysis to detect __phantom__ __reads__ in blockchain software for `hyperledger-fabric` framework                             |
| `ucci`                   | performs an analysis to detect __untrusted__ __cross-contract__ __invocations__ in blockchain software for `hyperledger-fabric` framework  |
| `cchi`                   | performs an analysis to detect issues related to __cross-channel__ __invocations__ in blockchain software for `hyperledger-fabric` framework |
| `read-write`             | performs an analysis to detect __read-after-write__ and __over-write__ issues in blockchain software for `hyperledger-fabric` framework    |
| `unhandled-errors`       | performs an analysis to detect __unhandled__ __errors__ in blockchain software                                                             |
| `var-numerical-overflow` | performs an analysis to detect the __integer__ __overflow/underflow__ of program variables in Go software                                  |
| `div-by-zero`            | performs an analysis to detect __divison__ __by__ __zero__ in Go software                                                                  |

---

## Publications 

### GoLiSA and its analyses
- Luca Olivieri, Luca Negrini: <i>Don’t Panic: Error Handling Patterns in Go Smart Contracts and Blockchain Software</i>, in Proceedings of 7th Conference on Blockchain Research & Applications for Innovative Networks and Services (BRAINS), 2025 ([link]([10.1109/BRAINS67003.2025.11302935](http://doi.org/10.1109/BRAINS67003.2025.11302935)))
- Luca Olivieri: <i>Detection of Cross-Channel Invocation Risks in Hyperledger Fabric</i>, in Proceedings of 36th International Symposium on Software Reliability Engineering (ISSRE), 2025 ([link]([10.1109/ISSRE66568.2025.00023](http://doi.org/10.1109/ISSRE66568.2025.00023)))
- Luca Olivieri, Luca Negrini, Vincenzo Arceri, Pietro Ferrara, Agostino Cortesi: <i>Detection of Read-Write Issues in Hyperledger Fabric Smart Contracts</i>, in Proceedings of The 40th ACM/SIGAPP Symposium On Applied Computing (SAC), 2025 ([link]([10.1145/3672608.3707721](http://doi.org/10.1145/3672608.3707721)))
- Luca Olivieri, Luca Negrini, Vincenzo Arceri, Pietro Ferrara, Agostino Cortesi, Fausto Spoto: <i>Static Detection of Untrusted Cross-Contract Invocations in Go Smart Contracts</i> in Proceedings of The 40th ACM/SIGAPP Symposium On Applied Computing (SAC), 2025 ([link]([10.1145/3672608.3707728](http://doi.org/10.1145/3672608.3707728)))
- Luca Olivieri, Luca Negrini,  Vincenzo Arceri, Badar Chachar, Pietro Ferrara, Agostino Cortesi: <i>Detection of Phantom Reads in Hyperledger Fabric</i>, in IEEE Access, vol. 12, pp. 80687-80697, 2024 ([link]([10.1109/ACCESS.2024.3410019](http://doi.org/10.1109/ACCESS.2024.3410019)))
- Luca Olivieri, Luca Negrini, Vincenzo Arceri, Fabio Tagliaferro, Pietro Ferrara, Agostino Cortesi, Fausto Spoto: <i>Information Flow Analysis for Detecting Non-Determinism in Blockchain</i>. ECOOP 2023: 23:1-23:25 ([link](https://drops.dagstuhl.de/opus/volltexte/2023/18216/))
- Luca Olivieri, Fabio Tagliaferro, Vincenzo Arceri, Marco Ruaro, Luca Negrini, Agostino Cortesi, Pietro Ferrara, Fausto Spoto, Enrico Talin: <i>Ensuring determinism in blockchain software with GoLiSA: an industrial experience report</i>. SOAP@PLDI 2022: 23-29 ([link](https://dl.acm.org/doi/10.1145/3520313.3534658))

### Other publications involving GoLiSA
- Luca Olivieri, David Beste, Luca Negrini, Lea Schönherr, Antonio Emanuele Cinà, Pietro Ferrara: <i>Code Generation of Smart Contracts with LLMs: A Case Study on Hyperledger Fabric</i>, in Proceedings of 36th International Symposium on Software Reliability Engineering (ISSRE), 2025 ([link]([10.1109/ISSRE66568.2025.00034](http://doi.org/10.1109/ISSRE66568.2025.00034)))
- Luca Negrini, Vincenzo Arceri, Agostino Cortesi, Pietro Ferrara: <i>Tarsis: An effective automata-based abstract domain for string analysis</i>, in Journal of Software: Evolution and Process, 2024 ([link]([10.1002/smr.2647](http://doi.org/10.1002/smr.2647)))

---

## Awards, Honors, and Honorable Mentions
- Best Artifact Award. Static Detection of Cross-Channel Invocation Issues in Hyperledger Fabric. IEEE 36th International Symposium on Software Reliability Engineering (ISSRE'25), 2025. Artifact: ([link]([10.5281/zenodo.17193302](http://doi.org/10.5281/zenodo.17193302)))
- Candidate for Best Paper Award. Don't Panic: Error Handling Patterns in Go Smart Contracts and Blockchain Software. 7th Conference on Blockchain Research &amp; Applications for Innovative Networks and Services (BRAINS'25), 2025. 

---

## Experimental Evaluations
The code in this branch is under development. Please look at the specific artifacts or branches cited in the corresponding research papers to reproduce experimental evaluations and results. 

---

## Analysis Examples

### Non-determinism Analysis

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i code.go -o output -f hyperledger-fabric -a non-determinism
```

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i code.go -o output -f hyperledger-fabric -a non-determinism-ni
```

### Phantom Read Analysis

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i code.go -o output -f hyperledger-fabric -a phantom-read
```

### Untrusted Cross-Contract Invocations Analysis

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i code.go -o output -f hyperledger-fabric -a ucci
```

### Cross-Channel Invocations Analysis

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i codeA.go -o output -f hyperledger-fabric -a cchi
```

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i codeA.go  -ci "contractA" "otherchannel" -i codeB.go -ci "contractB" "mychannel" -o output -f hyperledger-fabric -a cchi -xc
```

### Read-after-write and Over-write Analysis

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i code.go -o output -f hyperledger-fabric -a read-write
```

### Unhandled Errors Analysis

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i code.go -o output -f hyperledger-fabric -a unhandled-errors
```

### Numerical Issues Analysis

```bash
./build/distributions/go-lisa-0.1/bin/go-lisa -i code.go -o output -f hyperledger-fabric -a numerical-issues
```  

---

## Other Licences

### Logo 
The Go gopher was designed by the Renee French. The design is licensed under the Creative Commons 4.0 Attributions license.
Read [link1](http://blog.golang.org/gopher) and [link2](https://go.dev/wiki/Gopher) for more details.
