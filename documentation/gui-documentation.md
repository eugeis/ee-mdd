### Table of Contents
* [Filesystem](#Filesystem)
* [Architecture](#Architecture)
  * [Views](#Views)
  * [Panel Tree-Structure](#PanelTreeStructure)
  * [Services](#Services)
    * [Dispatcher](#Dispatcher)
    * [Manipulator](#Manipulator)
* [Troubleshooting](#Troubleshooting)
  * [Anything XAMPP related](#XAMPP)
  * [Website is blank](#blank)

<a name="Filesystem" />
# Filesystem

* /
  * [angular.js](https://code.angularjs.org/1.4.2/angular.js)
  * app.js
  * index.html
  * stylesheet.css
* /bootstrap
  * [bootstrap.css](http://getbootstrap.com/)
* /src
  * [Injections.js](#Manipulator)
  * {SomeViewsName}.js
* /src-gen/
  * /base
      * [/lightbox](#Lightbox)
          * ComLightbox.js	 
          * Lightbox.js 
      * [/tree](#PanelTreeStructure)
	      * Node.js
		  * Nodeaxis.js
		  * Panel.js
		  * Separator.js
		  * Tree.js
      * [Dispatcher.js](#Dispatcher)
      * [Manipulator.js](#Manipulator)
      * Table.js
	  * [View.js](#View)
  * /controls
      * {SomeControlsName}.js
  * /templates
      * lightbox.html
      * nodeaxis.html
      * panel.html
      * table.html
      * {SomeViewsName}.js
  * /views
      * {SomeViewsName}.js
  
<a name="Architecture" />
# Architecture

<a name="Views" />
## Views

In the DSL a view is defined as either a main-view (main: true) or
a sub-view. Each view can reference another view or own controls.

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

In this snippet "TaskEditor" is a main-view, which references a sub-view called
"TaskExplorer". Moreover, "TaskEditor" owns a button and "TaskExplorer" owns a table.

Views can own these controls (for more see *Control.groovy*'s subclasses):
* button
* table
* textField
* ...

![The website consists of different panels, which contain the views.](https://github.com/eugeis/ee-mdd/blob/master/documentation/images/site.png "The website's base frame")

The website is parted into panels. Panels can be created (not yet implemented), deleted and resized. They
contain a list of main-views (the tabs), which define the content of the panel. In the picture above the
main-views are FileView, TaskEditorView, TaskWatchView, ConsoleView, StateView, DependencyView, WindowView and
TreeView (note that these are only example names).

<a name="PanelTreeStructure" />
## Panel-Tree-Structure

![The website with the tree's first children colored](https://github.com/eugeis/ee-mdd/blob/master/documentation/images/grid.png "The website with the tree's first children colored")

Every node in the tree has a size attribute and is either an axis or a panel. The size-attribute determines
how much space of the parent-node the node occupies relativ to its siblings. Every axis-node introduces a new
split - if the parent was horizontal split, the axis is vertical split and vice versa. Additionally, they have
a list of child-nodes (so either panels or axis) called "panels". Panels on the other hand have the size-attribute
and - this is not depicted in the image - a list of tabs.

![The tree structure for the image above](https://github.com/eugeis/ee-mdd/blob/master/documentation/images/tree.png "The tree structure for the image above")

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

<a name="Lightbox" />
### Lightbox

The lightbox is a popup-like formular. This allows the user to add, edit or delete
data. When the dependency "ComLightbox" is available, a lightbox can be created with

```javascript
	$lightbox.create(info)
```

"info" is an object containing information relevant to the lightbox:
  *  caller - which controller created the lightbox
  *  type - add / delete / edit (or custom)
  *  columnInfo - information which is to displayed to the user

<a name="Manipulator" />
### Manipulator

The manipulator is used to inject and manipulate functions of the
controllers. Manipulators are Singeltons created for every controller
using the getInstance()-method. Injectors located in /src/ can invoke
a manipulator and add functions to the manipulator's controller.

Getting a manipulator for "MyView":

```javascript
	var manipulator = $manipulator.getInstance("MyView");
```

Replacing / Adding the "click"-function in "MyView":

```javascript
	manipulator.add("click", function(self) {
		return function() {
			console.log("This will add this function as 'click' to 'MyView'");
		};
	});
```

The closure's "self"-argument refers to the controller. This allows for further
manipulation. Moreover, it's possible to invoke the function after the injection.

```javascript
	var manipulator = $manipulator.getInstance("MyView");
	manipulator.add("exampleInit", function(self) {
		return {
			exec: true,
			func: function() {
				self.id = "Init-Id"
			}
		};
	});
```

Using the manipulator you can extend the functionality of the generated controllers.
By convention the views' controllers are manipulated in their complement js-file in
the src-folder.

After using the manipulator to add functions to the controller, you need to load
the injection in your index.html (e.g. "src/MyView.js").

```html
	<!-- Include ViewRef-Source-Dependencies-Javascript -->
	<script src="src/{InjectionFile}.js" type="text/javascript"></script>
```

and set a dependency in src/Injections.js (e.g. "MyViewInjector"):

```html
	angular.module("Injections",["{InjectionModule}"]);
```

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