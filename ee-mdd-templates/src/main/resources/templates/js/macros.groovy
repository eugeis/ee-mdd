/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 *
 * @author Eugen Eisler
 */

templates ('macros') {

  useMacros('commonMacros', '/common/macros')

  template('propsMember', body: '''<% item.props.each { prop -> %>
  this.$prop.uncap;<% } %>''')

  template('propsInit', body: '''<% item.props.each { prop -> %>
  this.$prop.uncap = $prop.uncap;<% } %>''')

  template('impl', body: '''<% if(!c.className) { c.className=item.name } %>
function $c.className($item.signature) {${macros.generate('propsInit', c)}
}''')

  template('implExtends', body: '''<% c.src=true %><% if(!c.className) { c.className=item.name } %>
function $c.className() {
  // inherit from base class
  ${c.className}.prototype = new <% if (item.superUnit) {%>$item.superUnit.cap()<% } else { %>${c.className}Base()<% } %>;

  // correct the constructor pointer because it points to base class
  ${c.className}.prototype.constructor = ${c.className};
}''')

  template('enum', body: '''<% if(!c.className) { c.className=item.name }; def classNameLit = "${c.className}Lit" %>
function $classNameLit($item.signature) {
  this.name = name;${macros.generate('propsInit', c)}
}

${classNameLit}.prototype = {
  constructor: ${classNameLit},<% last = item.literals.last(); item.literals.each { lit -> %>

  $lit.is : function() {
    return this === ${c.className}.$lit.underscored;
  }${lit == last ? '' : ','}<% } %>
}

var $c.className = {<% last = item.literals.last(); item.literals.each { lit -> %>
  $lit.underscored: new $classNameLit($lit.init)${lit == last ? '' : ','}<% } %>
}''')

	template('indexhtml', body: '''\
<!DOCTYPE html>
<html ng-app="$item.name">
	<head>
		<title>$item.name</title>
		<meta charset="UTF-8">
		<link rel="stylesheet" href="bootstrap/bootstrap.css">
		<link rel="stylesheet" href="stylesheet.css">
 	</head>
	<body>
		<h1 style="text-align:center">GENERATED</h1>
		<lightbox></lightbox>

		<!-- Include MainView -->
		<ee-view template="$item.name"></ee-view>

		<!-- Frameworks -->
		<script src="angular.js" type="text/javascript"></script>

		<!-- Main app -->
		<script src="app.js" type="text/javascript"></script>

		<!-- Services -->
		<script src="src-gen/base/Dispatcher.js" type="text/javascript"></script>
		<script src="src-gen/base/Manipulator.js" type="text/javascript"></script>

		<!-- Table implementation -->
		<script src="src-gen/base/Table.js" type="text/javascript"></script>

		<!-- View implementation -->
		<script src="src-gen/base/View.js" type="text/javascript"></script>

		<!-- Lightbox implementation -->
		<script src="src-gen/base/Lightbox.js" type="text/javascript"></script>
		<script src="src-gen/base/ComLightbox.js" type="text/javascript"></script>

		<!-- Injections dependencies -->
		<script src="src/Injections.js" type="text/javascript"></script>

		<!-- Include ViewRef-Javascript -->
<%
def traverse
traverse = {v ->
    v.viewRefs.each { ref ->
		traverse(ref.view);%>\
		<script src="src-gen/views/${ref.view.name}.js" type="text/javascript"></script>
<%	}
}

traverse(item);
%>
		<!-- Include ViewRef-Source-Dependencies-Javascript -->

	</body>
</html>
''')


	template('appjs', body: '''\
"use strict";
(function(){
	var dependencies = ["Injections", "Table", "View", "Lightbox"];
	var views = [\
<%
def list = [];

def traverse
traverse = {v ->
    v.viewRefs.each { ref ->
		traverse(ref.view)
		list.add(ref.view)
	}
}

traverse(item);

	for (int i = 0; i < list.size(); i++) {
		def iterator = list[i];
%>\
"$iterator.view.name"\
<%
		if (i < list.size() - 1)  {
%>\
,\
<%
		}
	}
%>\
];
	var app = angular.module("TaskEditorView",dependencies.concat(views));
}());
''')


	template('stylecss', body: '''\
section {
	border: 1px solid #000;
	padding: 25px;
	margin: 25px;
}

.entityTable {
/*  width: 800px; */
}

.entityTable tr.selected {
	border-left: 2px solid #0d355a;
	border-right: 2px solid #0d355a;
}

.entityTable .tableInput {
    background-color:rgba(0, 0, 0, 0);
    color:black;
    border: none;
    outline:none;
}

.entityTable .tableSubmit span {
  padding: 4px 4px 4px 0px;
  cursor: pointer;
}

.entityTable .idcolumn span:hover {
	font-weight: bold;
	cursor: pointer;
}

.lightboxBG {
	background: rgba(0,0,0,0.6);
	width: 100%;
	height: 100%;
	z-index: 100;
	position: fixed;
	margin: 0px;
	padding: 0px;
	top: 0px;
	left: 0px;
}

.lightbox {
	background: white;
	margin: 0px auto;
	width: 500px;
	top: 50%;
	transform: translateY(-50%);
	position: relative;
}

.lightbox>div{
	padding: 20px;
}
''')

	template('tablehtml', body: '''\
<table class="entityTable table table-striped table-bordered">
	<tr>
		<th ng-repeat="column in tableCtrl.columns" ng-click="tableCtrl.click(column)">{{column}}</th>
	</tr>
	<tr ng-repeat="row in tableCtrl.data" ng-class="{selected: tableCtrl.selected==\\$index}">
		<td ng-repeat="column in tableCtrl.columns" ng-click="tableCtrl.click(column, \\$parent.\\$index)">{{row[column]}}</td>
	</tr>
</table>
''')

	template('tablejs', body: '''\
"use strict";
(function(){
	var app = angular.module("Table",["Dispatcher"]);

	app.directive("eeTable", function() {
		return {
			restrict: 'E',
			templateUrl: "src-gen/templates/table.html",
			replace: true
		};
	});
}());
''')

	template('viewjs', body: '''\
"use strict";
(function(){
	var app = angular.module("View",[]);

	app.directive("eeView", function() {
		return {
			restrict: 'E',
			templateUrl: function(elem, attrs) {
				return "src-gen/templates/" + attrs.template + ".html";
			},
			replace: true,
			link: function (scope, element, attrs) {
				element.removeAttr('template');
			}
		};
	});
}());
''')

	template('dispatcherjs', body: '''\
"use strict";
(function(){
	var app = angular.module("Dispatcher",[]);

	// Dispatches a message (including data) to all
	// subscribed controllers (excluding the one who
	// dispatched it).

	// The subscribe-method returns the unsubscribe
	// function

	app.factory("\\$dispatcher", function() {
		return {
			subscribers: [],
			subscribe : function(obj) {
				var self = this;
				self.subscribers.push(obj);
				return function() {
					var pos = self.subscribers.indexOf(obj);
					if (pos >= 0) {
						self.subscribers.splice(pos,1);
					}
				};
			},
			dispatch: function(source, args) {
				var self = this;
				self.subscribers.forEach(function(d) {
					if (d !== source) {
						if (d.event) {
							d.event(args);
						} else {
							console.error("No event-function defined for:");
							console.log(d);
						}
					}
				});
			}
		};
	});
}());
''')

	template('manipulatorjs', body: '''\
"use strict";
(function(){
	var app = angular.module("Manipulator",[]);

	//app.manipulator
	//returns: object
	// - func: getInstance(id)
	// - var:  instances

	//getInstance(id)
	//returns: object
	// - func: add(name, func)
	// - func: inject(obj)
	// - var:  injections

	//when 'getInstance' is called:
	//if no object of this id exists in 'instances', it is created, saved to 'instances' and returned
	//otherwise the object in 'instances' is returned

	//when 'add' is called:
	//save an object with a name associated with a function to 'injections'

	//when 'inject' is called:
	//go through the 'injections'-array and bind the function and name to the object

	app.factory("\\$manipulator", function() {
		return {
			instances : {},
			getInstance : function(id) {
				if (!this.instances[id]) {
					this.instances[id] = {
						injections: [],
						add: function(name, func) {
							this.injections.push({name: name, func: func});
							return this;
						},
						inject: function(obj) {
							this.injections.forEach(function(d,i) {
								obj[d.name] = d.func;
							});
							return this;
						}
					}
				}
				return this.instances[id];
			}
		};
	});
}());
''')

	template('injectionsjs', body: '''\
"use strict";
(function(){
	var app = angular.module("Injections",[]);
}());
''')

	template('lightboxhtml', body: '''\
<div class="lightboxBG ng-cloak" ng-click="lightboxCtrl.hide()" ng-show="lightboxCtrl.show">
    <div class="lightbox" ng-click="lightboxCtrl.prevent(\\$event)">
      <div>
          <form ng-if="lightboxCtrl.type=='add'" ng-submit="lightboxCtrl.submit('add')">
            <div class="form-group" ng-repeat="column in lightboxCtrl.columnInfo">
              <label for="{{ 'lightboxcolumn' + \\$id}}">{{column.name}}</label>
              <input type="text" class="form-control" ng-model="column.value" ng-attr-id="{{ 'lightboxcolumn' + \\$id}}" placeholder="{{column.name}}" required>
            </div>
            <button type="submit" class="btn btn-default">Add</button>
          </form>
          <form ng-if="lightboxCtrl.type=='delete'" ng-submit="lightboxCtrl.submit('delete')">
            <div class="form-group" ng-repeat="column in lightboxCtrl.columnInfo">
                <label for="{{ 'lightboxcolumn' + \\$id}}">{{column.name}}</label>
                <input type="text" class="form-control" ng-model="column.value" ng-attr-id="{{ 'lightboxcolumn' + \\$id}}" ng-readonly="true">
            </div>
            <div class="checkbox">
              <label>
                <input type="checkbox" ng-model="lightboxCtrl.checked" required> Are you sure, you want to delete this entry?
              </label>
            </div>
            <button type="submit" class="btn btn-default" ng-class="{disabled: !lightboxCtrl.checked}">Delete</button>
          </form>
      </div>
    </div>
</div>
''')


	template('lightboxjs', body: '''\
"use strict";
(function(){
    var app = angular.module("Lightbox",["ComLightbox"]);

    app.directive("lightbox", ["\\$document", "\\$rootScope", function(\\$document, \\$rootScope) {
        return {
            restrict: 'E',
            templateUrl: "src-gen/templates/lightbox.html",
            controller: ["\\$scope", "\\$lightbox", "\\$rootScope", LightboxController],
            controllerAs: 'lightboxCtrl',
            link: function() {
                \\$document.bind('keydown', function(e) {
                    \\$rootScope.\\$broadcast("keypress", e);
                });
            }
        };
    }]);

    function LightboxController(\\$scope, \\$lightbox, \\$rootScope) {
		var self = this;
		\\$lightbox.setLightBox(this);

		self.columnInfo = undefined;
		self.type = undefined;
		self.caller = undefined;
		self.rowNr = undefined;
		self.show = false;

		self.create = function (info) {
			if (info.type !== "add" && info.type !== "delete") {
				return self.hide();
			}

			if (info.type === "add") {
				if (self.type !== "add") {
					self.columnInfo = info.columnInfo;
				}
			} else if (info.type === "delete") {
				self.rowNr = info.rowNr;
				self.columnInfo = info.columnInfo;
			}

			self.caller = info.caller;
			self.type = info.type;
			self.show = true;
		};

		self.submit = function (type) {
			var success;
			if (type === "add") {
				success = self.caller.add(self.columnInfo);
			} else if (type === "delete") {
				success = self.caller.delete(self.rowNr);
			} else {
				success = false;
			}

			if (success) {
				return self.hide();
			}
		};

		self.hide = function() {
			self.checked = false;
			self.show = false;
		}

		self.prevent = function(e) {
			e.stopPropagation();
		};
		\\$rootScope.\\$on("keypress", function(onEvent, keyEvent) {
			var key = (keyEvent.charCode || keyEvent.keyCode);
			if (key == 27) {
				\\$scope.\\$apply(self.hide());
			}
		});
	}
}());
''')

	template('comlightboxjs', body: '''\
"use strict";
(function(){
	var app = angular.module("ComLightbox",[]);

	app.factory("\\$lightbox", function() {
		return {
			lightBoxCtrl : {},
			setLightBox : function(lbCtrl) {
				this.lightBoxCtrl = lbCtrl;
			},
			create : function(info) {
				this.lightBoxCtrl.create(info);
			}
		};
	});
}());
''');

	template('framehtml', body: '''\
<section id="${item.name}">
<h4 style="padding-left:10px">${item.name}</h4>
<% item.viewRefs.each { viewRef -> %>\
	<ee-view template="${viewRef.view.name}" ng-controller="${viewRef.view.name}Controller as ${viewRef.view.name.toLowerCase()}Ctrl"></ee-view>
<% } %>\
<%
	item.controls.each { control ->
		if (control.widgetType == "Button") {
%>\
\
		<input type="button" value="${control.name.capitalize()}" ng-click="${item.name.toLowerCase()}Ctrl.lightbox('\
<%
	if (control.name.capitalize().startsWith("Add")) {
%>\
add\
<%
	} else if (control.name.capitalize().startsWith("Delete")) {
%>\
delete\
<%
	} else if (control.name.capitalize().startsWith("Accept")) {
%>\
accept\
<%
	} else if (control.name.capitalize().startsWith("Discard")) {
%>\
discard\
<%
	} else {
%>\
search\
<%
	}
%>\
')">
\
<%
		}
		if (control.widgetType == "Table") {
%>\
\
		<ee-table ng-controller="${item.name}${control.name.capitalize()}Controller as tableCtrl"></ee-table>
\
<%
		}
		if (control.widgetType == "TextField") {
%>\
\
		<label>${control.name.capitalize()}</label> <input readonly>
\
<%
		}
	}
%>\
</section>
''')

	template('framejs', body: '''\
"use strict";
(function(){
	var app = angular.module("$item.name",[\
<%
	def hasControl = [:];
	item.controls.each { control ->
		if(!hasControl[control.widgetType]) {
			hasControl[control.widgetType] = 0;
		}
		hasControl[control.widgetType]++;
	}
%>\
"Manipulator", "ComLightbox"]);
<%
	if (hasControl["Table"] > 0) {
%>\

	// The ${item.name}Controller broadcasts incoming
	// events from other view-controllers to child-controllers
	// e.g. a table-controller.
	// This is used for data-transmission between views.

	app.controller("${item.name}Controller", ['\\$scope', '\\$dispatcher', '\\$manipulator', '\\$lightbox', function (\\$scope, \\$dispatcher, \\$manipulator, \\$lightbox) {
		var self = this;
		self.id = "${item.name}";

		self.\\$scope = \\$scope;
		self.\\$dispatcher = \\$dispatcher;

		self.event = function(args) {
			\\$scope.\\$broadcast("event", args);
		}

		self.subscribeToDispatcher = function() {
			\\$dispatcher.subscribe(self);
		}

		self.registerClickEvent = function() {
			\\$scope.\\$on("click", function(e, args) {
				// If the onselect is set add:
				\\$dispatcher.dispatch(self,args);
			});
		}

		self.lightbox = function(type) {
			if (type === "add") {
				\\$lightbox.create({
					caller: this,
					type: type,
					columnInfo: [{name: "name"}, {name: "age"}]
				});
			} else if (type === "delete") {
				\\$lightbox.create({
					caller: this,
					type: type,
					columnInfo: [{name: "name", value: "Jonas"}, {name: "age", value: "20"}],
					rowNr: 3
				});
			}
		};

		self.add = function() {
			return true;
		}

		self.delete = function() {
			return true;
		}

		\\$manipulator.getInstance("${item.name}").inject(self);
		self.subscribeToDispatcher();
		self.registerClickEvent();
	}]);
<%
	}
	item.controls.each { control ->
		if (control.widgetType == "Table") {

			def hasMulti = false;
			def hasOpposite = false;

			def multi = [];
			def opposite = [];

			control.type.props.each { prop ->
				if (prop.multi) {
					hasMulti = true;
					multi.push(prop);
				}
				if (prop.opposite) {
					hasOpposite = true;
					opposite.push(prop);
				}
			}
%>\


	app.controller("${item.name}${control.name.capitalize()}Controller", ['\\$scope', '\\$http', '\\$manipulator', function (\\$scope, \\$http, \\$manipulator) {
		var self = this;

		self.id = "${item.name}${control.name.capitalize()}";
		self.entity = "${control.type.name}";

		self.\\$scope = \\$scope;
		self.\\$http = \\$http;

		self.click = function(column, row) { console.info("TableBase: click() is not defined"); };
		self.currentRow = undefined;

		self.columns = [\
<%
			for (def i = 0; i < control.type.props.size(); i++) {
				def prop = control.type.props[i]
%>\
"$prop.name"\
<%
				if(i < control.type.props.size()-1) {
%>\
,\
<%
				}
			}
%>\
];

<%
			if (hasMulti) {
				if (!hasOpposite) {
%>\
		self.data = \\$http.get('data/${item.name}.json')
			.success(function(data, status, headers, config) {
				self.data = data;
			})
			.error(function(data, status, headers, config) {
				console.error("HTTP - ERROR: " + status);
				self.data = {};
			});
<%
				}
%>\

		self.click = function(column, rowNr) {
			self.selected = rowNr;
			\\$scope.\\$emit("click", {
				childSource: self,
				sourceEntity: self.entity,
				targetView: [\
<%
				if (control.onSelect.observerRefs) {
					for (def i = 0; i < control.onSelect.observerRefs.size(); i++) {
%>\
"${control.onSelect.observerRefs[i]}"\
<%
						if(i < control.onSelect.observerRefs.size() - 1) {
%>\
,\
<%
						}
					}
				}
%>\
],
				column: column,
				row: self.data[rowNr]
			});
		}
<%
			}
			if (hasOpposite) {
%>
		self.fetchData = function(id) {
			self.data = \\$http.get('data/TaskDetailsView.php?id=' + id + '&type=${control.name.capitalize()}')
				.success(function(data, status, headers, config) {
					self.data = data;
				})
				.error(function(data, status, headers, config) {
					console.error("HTTP - ERROR: " + status);
					self.data = {};
				});
		};

		self.registerEvent = function() {
			\\$scope.\\$on("event", function(e, args) {
<%
				opposite.each { prop ->
%>\
				if (args.targetView.some(function(d) {
					return d === "${item.name}.presenter";
				})){
					if (args.sourceEntity === "${prop.type.name}") {
						if (args.row && self.currentRow !== args.row.id) {
							self.fetchData(args.row.id);
							self.currentRow = args.row.id;
						}
					}
				}
<%
				}
%>\
			});
		}
<%
		}
%>\
		\\$manipulator.getInstance("TaskDetailsViewActions").inject(self);
<%
		if (hasOpposite) {
%>\
		self.registerEvent();
<%
		}
%>\
	}]);
<%
		}
	}
%>\
}());
''')

	template('framesrcjs', body: '''\
"use strict";
(function(){
	var app = angular.module("${item.name}Injector", ["Manipulator"]);

	app.run(["\\$manipulator", function(\\$manipulator) {
		var manipulator = \\$manipulator.getInstance("${item.name}");
		manipulator.add("functionName", function() {
			// This could be your code
			// Include this file in the index.html

			// To manipulate the object just refer to self
			// The callback-function can take arguments (e.g. row, column)
			var self = this;
			console.info("This function has been injected");
		});
<%
	item.controls.each { control ->
		if (control.widgetType == "Table") {
%>
		var manipulator_${control.name.capitalize()} = \\$manipulator.getInstance("${item.name}${control.name.capitalize()}");
		manipulator_${control.name.capitalize()}.add("click", function() {
		});
<%
		}
	}
%>\
	}]);
}());
''')
}