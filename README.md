# GoLiSA: an abstract interpretation-based static analyzer for Go
[![GitHub license](https://img.shields.io/github/license/lisa-analyzer/go-lisa)](https://github.com/lisa-analyzer/go-lisa/blob/master/LICENSE)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/lisa-analyzer/go-lisa/Gradle%20Build%20(master%20branch))
![GitHub last commit](https://img.shields.io/github/last-commit/lisa-analyzer/go-lisa)
![GitHub issues](https://img.shields.io/github/issues-raw/lisa-analyzer/go-lisa)

GoLiSA is a static analyzer based on abstract interpretation for applications written in Go. Currently, GoLiSA provides several static kinds of analyses  (numerical, strings, information flow)

## Usage
The main class is [GoLiSA](go-lisa/src/main/java/it/unive/golisa/GoLiSA.java) and it expects four parameters:
- `-i path`: the Go file to be analyzed
- `-o path`: the output directory
- `-a analysis`: the analysis to perform 
	- `sign`  performs an analysis with __sign__ abstract domain (numerical analysis)
	- `parity`  performs an analysis with __parity__ abstract domain (numerical analysis)
	- `intervals` performs an analysis with __intervals__ abstract domain (numerical analysis)
	- `pentagons`  performs an analysis with __pentagons__ abstract domain (numerical analysis)
	- `prefix` performs an analysis with __prefix__ abstract domain (string analysis)
	- `suffix` performs an analysis with __suffix__ abstract domain (string analysis)
	- `tarsis` performs an analysis with __tarsis__ abstract domain (string analysis)
	- `taint` performs a taint analysis (information flow analysis)

- `-e path`: the path of the file containing the list of entry points (method names) 
- `-sources path`: the path of the file containing the list of sources (method names) for taint analysis
- `-sinks path`: the path of the file containing the list of sinks (method names) for taint analysis
- `-sanitizers path`: the path of the file containing the list of sanitizers (method names) for taint analysis


### Example of command line

`-i C:\Users\MyAccount\myprogram.go -o C:\Users\MyAccount\output -a taint -e myentries.txt -sources mysources.txt -sinks mysinks.txt`

### Analysis Entry Points

The analysis by default does not require setting entry points. It automatically recognizes, if it is present in the code, the __main__ method and sets it as the entry point. If __main__ method is not present, the analysis considers all declared methods/functions as entry points. Otherwise, it is possible to specify entry points with the option `-e path`.

## How to build the project ##
GoLiSA comes as a Gradle 8.0 project. For development with Eclipse, please install the [Gradle IDE Pack](https://marketplace.eclipse.org/content/gradle-ide-pack) plugin from the Eclipse marketplace, and make sure to import the project into the workspace as a Gradle project. It can be executed within different Java runtime environments (currently up to JRE 20). At the end of the compilation, the artifacts are collected in an archive under `go-lisa/build/distributions`. The archive contains a launcher to run GoLiSA standalone and the GoLiSA binaries in jar format that can be imported into third-party Java applications to extend GoLiSA or to import single components.

Note that, you may need to add github credentials to download lisa with gradle. To do that, in the same folder of `build.gradle`, create a new file `gradle.properties` adding the following lines:

```
gpr.user=<github_username>
gpr.key=<github_token>
```

where `<github_username>` should be replaced with the name of your github account and `<github_token>` should be replaced with a generated GitHub fine-grained tokens.
To generate a token, you can log in to GitHub and following `Account` --> `Settings` --> `Developer Settings` --> `Personal access tokens` --> `Fine-grained tokens` --> `Generate new token`.
It is suggested to allow only read-only access to the generated token.

### Walkthrough to Analyze Running Example

- It is possible compile the go-lisa project within `go-lisa` folder using the command `gradle install`, then the compiled binaries will be created in the `build` folder
<img src="https://raw.githubusercontent.com/lisa-analyzer/go-lisa/issta23-demo-tool/img/build.png"/>

- The running example `running-example.go` is located in the root folder of repository. At this point, it is possible extract the binaries and the GoLiSA's launcher contained in the archive of `build` folder and analyze the running example
<img src="https://raw.githubusercontent.com/lisa-analyzer/go-lisa/issta23-demo-tool/img/go-lisa-run.png"/>

- The result of analysis will stored in the `output` directory which will be created in the root folder where the launcher runs.
<img src="https://raw.githubusercontent.com/lisa-analyzer/go-lisa/issta23-demo-tool/img/go-lisa-res.png"/>

The `output` directory will contain several files:

- `report.json` the analysis report in JSON format containing the warning triggered by the analysis
<img src="https://raw.githubusercontent.com/lisa-analyzer/go-lisa/issta23-demo-tool/img/warnings.png"/>

- `*.html` analysis dump of CFGs in HTML format, where each CFG node contains analysis information for each program point
<img src="https://raw.githubusercontent.com/lisa-analyzer/go-lisa/issta23-demo-tool/img/cfg.png"/>

- `js` folder that contains supporting material for HTML visualization of CFGs 

