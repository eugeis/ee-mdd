### Table of Contents
* [Architecture](#Architecture)
  * [View](#View)
  * [Directives and Templates](#Directives)
  * [Controllers](#Controllers)
    * [View-Controllers](#ViewControllers)
    * [Sub-Controllers](#SubControllers)
  * [Services](#Services)
    * [Dispatcher](#Dispatcher)
    * [Manipulator](#Manipulator)
    * [Extensibility](#Extensibility)
* [Structure](#Structure)
* [Troubleshooting](#Troubleshooting)
  * [Anything XAMPP related](#XAMPP)
  * [Website is blank](#blank)

<a name="Architecture" />
# Architecture

<a name="View" />
## Views

In the DSL a view is defined as either the main-view (main: true) or
a referenced view (sub-view):

```
	view ('TaskEditor', main: true) {
		viewRef(view: 'TaskExplorerView') {}
		button('accept') { onAction() }
		//etc.
	}

	view ('TaskExplorer') {            
		button('addTask') { onAction() }
		table('tasks', type: 'Task') { onSelect() }
		//etc.
	}
```

All views can own controls:
* button
* table
* textField
* *See Control.groovy's subclasses*

<a name="Directives" />
### Directives and Templates

Angular comes with a handy option for creating custom html-elements called [directive](https://docs.angularjs.org/guide/directive). 
These directives are used to include templates for the views. Each view is represented
by a <ee-view>-element, which is defined in View.js:

```javascript
	app.directive("eeView", function() {
		return {
			restrict: 'E',
			controllerAs: 'ctrl',
			templateUrl: function(elem, attrs) {
				return "src-gen/templates/" + attrs.template + ".html";
			},
			replace: true,
			link: function (scope, element, attrs) {
				element.removeAttr('template');
			}
		};
	});
```

In this example the directive eeView has several properties:
* "restrict: 'E'" - it is used for an html-element (as opposed to e.g. an attribute)
* "controllerAs: 'ctrl'" - it uses a controller alias; this reduces scope problems and makes templates more flexible
* "templateUrl: ..." - the template's url is calculated using the template-attribute of the html-element
* "replace: true" - the custom directive gets replaced by the root element of the template
* "link: ..." - the template attribute is removed

The directive could be used like this snippet in the html-file.
Notice the translation from camelCase to dash-case (eeView -> ee-view).

```html
	<!-- index.html -->
	<ee-view template="TaskExplorerView" ng-controller="TaskExplorerViewController"></ee-view>
```

A typicall view might look like this:
* two buttons
* a table

```html
	<!-- TaskExplorerView.html -->
	<section id="TaskExplorerView">
			<input type="button" value="AddTask">
			<input type="button" value="DeleteTask">
		<section id="TaskExplorerView-Table-Tasks">
			<ee-table ng-controller="TaskExplorerViewTasksController as tableCtrl"></ee-table>
		</section>
	</section>
```

The table directive (ee-table) is again replaced with the table-template.

```html
	<!-- table.html -->
	<table class="entityTable table table-striped table-bordered">
		<tr>
			<th ng-repeat="column in tableCtrl.columns" ng-click="tableCtrl.click(column)">{{column}}</th>
		</tr>
		<tr ng-repeat="row in tableCtrl.data">
			<td ng-repeat="column in tableCtrl.columns" ng-click="tableCtrl.click(column, row)">{{row[column]}}</td>
		</tr>
	</table>
```

<a name="Controllers" />
### Controllers

In AngularJS controllers are used for the business logic in a website. They add behaviour and a state to their
scope.

Currently there are two types of controllers used in the application. For convenience they are called:
1. view-controllers
2. sub-controllers

<a name="ViewControllers" />
#### View-Controllers

These controllers (e.g. TaskExplorerViewController) are attributed to a view-directive:

```html
	<!-- index.html -->
	<ee-view template="TaskExplorerView" ng-controller="TaskExplorerViewController"></ee-view>
```

On creation they register by default at the $dispatcher.

Any view-controller can wrap several sub-controllers forming a parent-child relationship; view-controllers
are usually siblings to each other.
These relationships are used by the $dispatcher to create a broadcast-system for e.g. event-handling.

<a name="SubControllers" />
#### Sub-Controllers

A sub-controller manages the view's controls. 

```html
	<!-- TaskExplorerView.html -->
	<section id="TaskExplorerView"> <!-- ngController: TaskExplorerViewController -->
		<section id="TaskExplorerView-Table-Tasks">
			<ee-table ng-controller="TaskExplorerViewTasksController as tableCtrl"></ee-table>
		</section>
	</section>
```

When a sub-controller emits an event, the view-controller dispatches the event to all view-controllers.
These broadcast the event to their sub-controllers resulting in a event-broadcast-system. The sub-controllers
can then decide whether to react to an event on let it pass.

**Table-Controller** -- *emits* --> **View-Controller** -- *dispatches* --> **View-Controllers** -- *broadcasts* --> **Table-Controllers**

<a name="Services" />
## Services

<a name="Dispatcher" />
### Dispatcher

The $dispatcher is used to transmit events between view-controllers.
Every controller can subscribe to the $dispatcher to receive events
from other controllers.

To dispatch an event with arguments (args) use the snippet below
inside your view.

```javascript
	$dispatcher.dispatch(this,args);
```

![The dispatcher creates a broadcast-system](https://github.com/eugeis/ee-mdd/blob/master/documentation/images/dispatcher.png "The dispatcher")

[Click here to see the svg.](https://github.com/eugeis/ee-mdd/blob/master/documentation/images/dispatcher.svg)


<a name="Manipulator" />
### Manipulator

The manipulator ist used to inject and manipulate functions of the
controllers. Manipulators are Singeltons created for every controller
using the getInstance()-method. Injectors located in /src/ can invoke
a manipulator and add functions to the manipulator's controller.

Invoking a manipulator for "MyView"

```javascript
	var manipulator = angular.module("Manipulator").getInstance("MyView");
```

Adding a function to inject

```javascript
	manipulator.add("click", function(column, row) {
		console.log([column, row]);
	}
```

<a name="Extensibility" />
### Extensibility

Using the manipulator you can extend the functionality of the generated controllers.
By convention the views' controllers are manipulated in their complement js-file in
the src-folder.

After using the manipulator to add functions to the controller, you need to add this
snippet to your index.html.

```html
	<!-- Include ViewRef-Source-Dependencies-Javascript -->
	<script src="src/{InjectionFile}.js" type="text/javascript"></script>
```

<a name="Structure" />
# Structure

* /
  * [angular.js](https://code.angularjs.org/1.4.2/angular.js)
  * app.js
  * index.html
  * stylesheet.css
* /bootstrap
  * [bootstrap.css](http://getbootstrap.com/)
* /src
  * {SomeViewsName}.js
* /src-gen/
  * /base
      * Dispatcher.js
      * Manipulator.js
      * Table.js
  * /scripts
      * {SomeViewsName}.js
  * /templates
      * table.html
      * {SomeViewsName}.js

<a name="Troubleshooting" />
# Troubleshooting

<a name="XAMPP" />
## Anything XAMPP related (installation failed / webserver does not start) 

See XAMPP FAQ for [Windows](https://www.apachefriends.org/faq_windows.html), [Linux](https://www.apachefriends.org/faq_linux.html) or [OSX](https://www.apachefriends.org/faq_osx.html)

<a name="blank" />
## Website is blank

* Make sure you are on http://localhost or your subfolder's url http://localhost/{subfolder}
* Make sure you have copied the generated files and the gui-dist into your htdocs-folder. There is supposed to be neither a gui-dist- nor a ee-mdd_example-ui-folder in your htdocs-folder just their contents.
* Open developer tools (right-click > inspect element for chrome, firefox) and look for errors in the console

  ### Possible error-messages:

  ```"Uncaught ReferenceError: angular is not defined"```

  * this is an indicator, that the angular.js-file is not available
  * make sure you have copied the content of the gui-dist-folder in to htdocs-folder
  
  ```"Error: [ngRepeat:dupes] Duplicates in a repeater are not allowed. Use 'track by' expression to specify unique keys. Repeater: row in tableCtrl.data, Duplicate key: string:p, Duplicate value: p"```
  
  * most likely your php engine is not running, thus returning "TaskDetailsView.php" uninterpreted 