# Architecture

## Services

### Dispatcher

The dispatcher is used to transmit events between view-controllers.
Every controller can subscribe to the dispatcher to receive events
from other controllers.

To dispatch an event with arguments (args) use the snippet below
inside your view.

```javascript
	$dispatcher.dispatch(this,args);
```

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

### Extensibility

The controllers defined in /src-gen/scripts are extendable:

In this example we use the "TaskExplorerViewTasks"-Controller. This is a
table-controller in the "TaskExplorerView", which emits information to its
parent controller when clicked.

To change this behaviour you can add the snippet below to the TaskExplorerView.js file in /src/, 
thus logging the column and row in the console.

```javascript
	var manipulator = angular.module("Manipulator").manipulator.getInstance("TaskExplorerViewTasks");
	manipulator.add("click", function(column, row) {
		console.log({column: column, row: row});
	});
```

Moreover you have to include the TaskExplorerView.js in the index.html:

```html
	<!-- Include ViewRef-Source-Dependencies-Javascript -->
	<script src="src/TaskExplorerView.js" type="text/javascript"></script>
```
	
# Structure


/
--[angular.js](https://code.angularjs.org/1.4.2/angular.js)
--app.js
--index.html
--stylesheet.css

/bootstrap
--[bootstrap.css](http://getbootstrap.com/)

/src
--{SomeViewsName}.js

/src-gen/base
--Dispatcher.js
--Manipulator.js
--Table.js

/src-gen/scripts
--{SomeViewsName}.js

/src-gen/templates
--table.html
--{SomeViewsName}.js

# Troubleshooting

* Anything XAMPP related (installation failed / webserver does not start) 
  --> see XAMPP FAQ for [Windows](https://www.apachefriends.org/faq_windows.html), [Linux](https://www.apachefriends.org/faq_linux.html) or [OSX](https://www.apachefriends.org/faq_osx.html)

* Website is blank
  --> make sure you are on http://localhost
  --> make sure you have copied the generated files and the gui-dist
      into your htdocs-folder
  --> open developer tools (right-click > inspect element for chrome, firefox)
      and look for errors in the console
     
      ** "Uncaught ReferenceError: angular is not defined"
        --> make sure you have copied the gui-dist-folder in to htdocs-folder,
            this is an indicator, that the angular.js-file is not available