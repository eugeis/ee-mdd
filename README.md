A text DSL and generator for different languages written in Groovy
======
The software is in alpha development stage, the documentation and examples will provided soon.
# Getting started
## Installation
1. Install [GGTS - Groovy/Grails Tool Suite](https://grails.org/products/ggts) or [Groovy Eclipse Plugin](http://groovy.codehaus.org/Eclipse+Plugin) in your eclipse desctribution
2. Clone the [ee-mdd](https://github.com/eugeis/ee-mdd.git) GitHub repository in your Eclipse workspace, you can use *Git Repository Exploring* Eclipse perspective.
3. Import the *ee-mdd* Eclipse project from the repository into workspace with *Import projects* operation.

## Code generation
* ModelBuilderExample.groovy is an example model written in the DSL
* Main class *GeneratorForJava.groovy* to start generation of Java code from the model. 
* Main class *GeneratorForJs.groovy* to start generation of JavaScript code from the model. 
* The code will be generated into console and also into folder specified in *fileProcessor* - 'fileProcessor('D:/git/ee-mdd-example')'
