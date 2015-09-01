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

## Set up GUI
* For troubleshooting refer to [gui-documentation.md](https://github.com/eugeis/ee-mdd/blob/master/documentation/gui-documentation.md)
* gui-example provides a generated and commented example - go to step 7 and treat "gui-example" like "ee-mdd_example-ui"

1. Install [XAMPP](https://www.apachefriends.org/de/) or another webserver of your choice
  * If you use a different webserver skip step 2 and replace {YourPathToXampp}/htdocs/ with your webserver's document root (e.g. /var/www/ for apache on linux)
2. Configure it by starting the webserver (e.g. via xampp-control) and navigating to http://localhost (see XAMPP FAQs for help)
3. After the configuration of xampp is done, it is recommended to backup your current htdocs-content outside the htdocs-folder.
4. Clear your htdocs-folder (**do not forget to backup** --> Step 3!)
5. Copy the content of /gui-dist to {YourPathToXampp}/htdocs/ (just the content, not the folder itself)
6. Run GenerateJsTest.groovy (ee-mdd/ee-mdd-gradle/src/test/groovy/ee/mdd/gradle/task/) in eclipse
7. Copy the files generated to ee-mdd/ee-mdd-gradle/temp/ee-mdd_example-ui to {YourPathToXampp}/htdocs/ (just the content, not the folder itself)
8. Navigate to http://localhost to see the results
9. When working with different versions you might want to create a subfolder in htdocs. You need to include all files (gui-dist + ee-mdd_example-ui) into
the subfolders! After this go to http://localhost/{subfolder} to inspect it.