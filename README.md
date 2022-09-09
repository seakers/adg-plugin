# AdgPlugin

Cameo Systems Modeler plugin for design space exploration and problem formulation through decision modeling.


## Installation

### Dependencies

- Cameo Systems Modeler ~v19.0 SP4
- Java ~OpenJDK 11.0.2

### Configuration

The only configuration required is pointing the gradle build tool to the directory where Cameo Systems Modeler is installed.
This can be done by setting the `CAMEO_PATH` variable in `/lib/build.gradle` to the path of your Cameo Systems Modeler home folder.

    def CAMEO_PATH = '/path/to/cameo'


### Building

1. Open a terminal shell and navigate to this project's root dir
2. Run command `./gradlew build`
3. Copy plugin dir `/adg` to your Cameo Systems Modeler plugins dir 
4. Run Cameo

### Running

1. Open Cameo












