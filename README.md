# Java Architecture Analysis

**ArchCheck** is a tool that analyses the structure of Java source code using the top level directories 
as a way of splitting the code into separate components.
Dividing large programs into smaller more manageable components is a fundamental principle of programming.

## Installation

The installation steps are as follows:

1. Ensure the **Java** runtime is installed
1. Download and unzip the **archcheck** release.
1. Ensure that **archcheck** executable is added to you path.
1. Optionally install **graphviz**. Required for the graphical diagrams.
1. Ensure the **graphviz** executable is added to your path.

##  Running

Choose a project that you want analyze and the type the following:

```bash
archcheck <path-to-source-code>
```

Where `<path-to-source-code>` is the directory of just the java source code e.g. `archcheck src/main/java/`

To generate an html output type the following:

    archcheck --html <html-output-dir> <path-to-source-code>


You can also pass a configuration file to **archcheck**

    archcheck --config <config-file> --html <html-output-dir>


## The Configuration file

The configuration file is a json file in which comments and unquoted field names are allowed.

 item        | Description             |
-------------|-------------|
 **root-dir:**     | The path to the root directory of the project. can be either absolute or relative to the location of the configuration file   |
 **modules:**      | A json array with details of the list of modules in the project.       |
 **name:** | The name of the module.       |
 **path:** | the path from the root dir to the source code.        |
 **max-depth:** | Controls the maximum depth to descend into package directories before the sub dirs are merged together       |


#### Example configuration file

An example of configuration is as follows:

```groovy
// Example Json config for Googles bazel project
{
    root-dir: "project/bazel/",
    modules: [
        {
            name: "bazel",
            path: "bazel/src/main/java/com/google/devtools/build/lib/",
            max-depth: 0
        }
    ]
}
```


## Building 

Requirements:

* Java
* gradle

Type the following on the command line:

    gradle installApp
    
The executable will be in the `build/install/archcheck/bin` directory
