A text DSL and generator for different languages written in Groovy
======
The software is in alpha development stage, the documentation and examples will provided soon.
# Getting started
## Installation
1. Install [Gradle](http://www.gradle.org/) build system
2. Install [GGTS - Groovy/Grails Tool Suite](https://grails.org/products/ggts) or [Groovy Eclipse Plugin](http://groovy.codehaus.org/Eclipse+Plugin) in your eclipse desctribution
3. Clone the [ee-mdd](https://github.com/eugeis/ee-mdd.git) GitHub repository in your Eclipse workspace, you can use *Git Repository Exploring* Eclipse perspective.
4. Import *ee-mdd* Eclipse project from the repository into workspace with *Import projects* operation.
5. Open terminal / console and change to the *ee-mdd/ee-mdd-core* folder of your file system and execute *gradle cleanEclipse eclipse* command which creates Eclipse projects files with dependencies to your local gradle repository. Do same step in *ee-mdd/ee-mdd-example* folder
6. Go to Eclipse, refresh the repository and import *ee-mdd-core* and *ee-mdd-example* Eclipse projects from the repository into workspace with *Import projects* operation.

## Code generation
* ModelBuilderExample.groovy is an example model written in the DSL
* Main class *GeneratorForJava.groovy* to start generation of Java code from the model. 
* Main class *GeneratorForJs.groovy* to start generation of JavaScript code from the model. 
* The code will be generated into console and also into folder specified in *fileProcessor* - 'fileProcessor('D:/views/git/ee-mdd/ee-mdd-example')'. Please adjust it to absolute path of 'ee-mdd-example' project on your file system.
