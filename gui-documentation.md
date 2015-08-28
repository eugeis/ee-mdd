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

Using the manipulator you can extend the functionality of the generated controllers.
By convention the views' controllers are manipulated in their complement js-file in
the src-folder.

After using the manipulator to add functions to the controller, you need to add this
snippet to your index.html.

```html
	<!-- Include ViewRef-Source-Dependencies-Javascript -->
	<script src="src/{InjectionFile}.js" type="text/javascript"></script>
```
	
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

# Troubleshooting

## Anything XAMPP related (installation failed / webserver does not start) 

See XAMPP FAQ for [Windows](https://www.apachefriends.org/faq_windows.html), [Linux](https://www.apachefriends.org/faq_linux.html) or [OSX](https://www.apachefriends.org/faq_osx.html)

## Website is blank

* Make sure you are on http://localhost
* Make sure you have copied the generated files and the gui-dist into your htdocs-folder
* Open developer tools (right-click > inspect element for chrome, firefox) and look for errors in the console

  ###Possible error-messages:

  ```"Uncaught ReferenceError: angular is not defined"```

  * this is an indicator, that the angular.js-file is not available
  * make sure you have copied the content of the gui-dist-folder in to htdocs-folder