# Static Program Analysis of Golang with GoLiSA 

# Conference 
 ACM SIGSOFT International Symposium on Software Testing and Analysis (ISSTA) 2023 Tool Demonstration
## Abstract 

This paper presents GoLiSA, an open-source static analyzer written in Java and based on abstract interpretation, that targets programs written in the Go language. Its general architecture allows one to apply distinct static analyses to Go programs by simply specifying abstract domains and checkers, and to define new domains relying on its predefined components, such as its fixpoint algorithm. To show the versatility of GoLiSA, this paper applies it to a specific context, that is, to the analysis of smart contracts for blockchain.

# GoLiSA
[![GitHub license](https://img.shields.io/github/license/lisa-analyzer/go-lisa)](https://github.com/lisa-analyzer/go-lisa/blob/master/LICENSE)

## How to build the GoLiSA project ##

GoLiSA comes as a Gradle 8.0.2 project. For development with Eclipse, please install the [Gradle IDE Pack](https://marketplace.eclipse.org/content/gradle-ide-pack) plugin from the Eclipse marketplace, and make sure to import the project into the workspace as a Gradle project.
It can be executed within different Java runtime environments (currently up to JRE 20). At the end of the compilation, the artifacts are collected in an archive under `go-lisa/build/distributions`. The archive contains a launcher to run GoLiSA standalone and the GoLiSA binaries in jar format that can be imported into third-party Java applications to extend GoLiSA or to import single components.

## How to use GoLiSA

This version of GoLiSA can be run by using the launcher with the following command line: 
```
go-lisa -i <go-file-path> -o <output-dir-path>
```
where `<go-file-path>` is the path of the file written in Go to analyze and `<output-dir-path>` is the directory path of GoLiSA outputs. The overflow analysis and the Hyperledger Fabric framework in this case are set by default.

Alternatively, if GoLiSA is imported to an external project and used as a library, the following snipped can be used to setup and run the analysis:
```
Program program = GoFrontEnd.processFile(filePath);
LiSAConfiguration conf = new LiSAConfiguration();
conf.abstractState = new SimpleAbstractState<>(
	new PointBasedHeap(),
	new ValueEnvironment<>(new Interval()),
	LiSAFactory.getDefaultFor(TypeDomain.class));
conf.semanticChecks.add(new NumericalOverflowChecker());
conf.analysisGraphs = GraphType.HTML;
conf.jsonOutput = true;
conf.optimize = false;
conf.workdir = outputDir;
LiSA lisa = new LiSA(conf);
lisa.run(program);
```

### Analyze Running Example

TODO
