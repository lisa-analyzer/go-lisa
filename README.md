# GoLiSA
GoLiSA: A Static Analyzer for Go Smart Contracts and Decentralized Applications (NFM 2022)

## How to use GoLiSA
The main class is [GoLiSA](go-lisa/src/main/java/it/unive/golisa/cli/GoLiSA.java) and it expects two paramaters:
- `input_file.go`: the Go file to be analyzed
- `output_dir`: the output directory

The following parameters can be selected:
- `-box`: performs the interval-based analysis
- `-oct`: performs the octagon-based analysis
- `-poly`: performs the polyehedra-based analysis
- `-over-under-flow-check`: performs the overflow/underflow semantic checker (it is required to select a numerical analysis between intervals, octagons, and polyhedra)
- `-div-by-zero-check`: performs the division-by-zero semantic checker (it is required to select a numerical analysis between intervals, octagons, and polyhedra)
- `-break-consens-check`: performs the break-consensus syntactic checker
- `-syn-map-range-check`: performs the iteration-over-map syntactic checker
- `-sem-map-range-check`: performs the iteration-over-map semantic checker

In order to run numerical analyses, it is required to set up the environment variable `LD_LIBRARY_PATH`, pointing to the Apron installation directory. Hence, it is required to install [Apron](https://github.com/antoinemine/apron). In order to run polyhedra-based analysis, it is required to install [PPL](https://www.bugseng.com/ppl-download).

## How to build the project ##
GoLiSA comes as a Gradle 6.0 project. For development with Eclipse, please install the [Gradle IDE Pack](https://marketplace.eclipse.org/content/gradle-ide-pack) plugin from the Eclipse marketplace, and make sure to import the project into the workspace as a Gradle project.
