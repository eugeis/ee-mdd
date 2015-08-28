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
\
\
		<!-- Include ViewRefs -->
<% item.viewRefs.each { viewRef -> %>\
		<ee-view template="${viewRef.view.name}" ng-controller="${viewRef.view.name}Controller"></ee-view>
<% } %>\
\

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

		<!-- Include ViewRef-Javascript -->
<% item.viewRefs.each { viewRef -> %>\
		<script src="src-gen/scripts/${viewRef.view.name}.js" type="text/javascript"></script>
<% } %>\

		<!-- Include ViewRef-Source-Dependencies-Javascript -->

	</body>
</html>
''')


	template('appjs', body: '''\
(function(){
	var app = angular.module("${item.name}",[\
<%
	for (int i = 0; i < item.viewRefs.size(); i++) {
		def iterator = item.viewRefs[i];
%>\
"$iterator.view.name"\
<%
		if (i < item.viewRefs.size() - 1)  {
%>\
,\
<%
		}
	}
%>\
]);
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
''')

	template('tablehtml', body: '''\
<table class="entityTable table table-striped table-bordered">
	<tr>
		<th ng-repeat="column in tableCtrl.columns" ng-click="tableCtrl.click(column)">{{column}}</th>
	</tr>
	<tr ng-repeat="row in tableCtrl.data">
		<td ng-repeat="column in tableCtrl.columns" ng-click="tableCtrl.click(column, row)">{{row[column]}}</td>
	</tr>
</table>
''')

	template('tablejs', body: '''\
(function(){
	var app = angular.module("Table",["Dispatcher"]);

	app.directive("eeTable", function() {
		return {
			restrict: 'E',
			templateUrl: "src-gen/templates/table.html",
			replace: true
		};
	});

	app.baseClass = {};
	app.baseClass["TableBase"] = function (\\$scope) {
		var self = this;
		self.click = function(column, row) { console.info("TableBase: click() is not defined"); };
	}
}());
''')

	template('viewjs', body: '''\
(function(){
	var app = angular.module("View",[]);

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
}());
''')

	template('dispatcherjs', body: '''\
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
(function(){
	var app = angular.module("Manipulator",[]);

	app.manipulator = new function() {
		var self = this;

		self.instances = {};
		self.getInstance = function(id) {
			if (!self.instances[id]) {
				self.instances[id] = {
					injections: [],
					add: function(name, func) {
						this.injections.push({name: name, func: func});
					},
					inject: function(obj) {
						this.injections.forEach(function(d,i) {
							obj[d.name] = d.func;
						});
					}
				}
			}
			return self.instances[id];
		}
	}
}());
''')

	template('framehtml', body: '''\
<section id="${item.name}">
<%
	item.controls.each { control ->
		if (control.widgetType == "Button") {
%>\
\
		<input type="button" value="${control.name.capitalize()}">
\
<%
		}
		if (control.widgetType == "Table") {
%>\
\
	<section id="$item.name-Table-${control.name.capitalize()}">
		<ee-table ng-controller="${item.name}${control.name.capitalize()}Controller as tableCtrl"></ee-table>
	</section>
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
	if (hasControl["Table"] > 0) {
%>\
"Table",\
<%
	}
%>\
"View"]);
	var manipulator = angular.module("Manipulator").manipulator;
<%
	if (hasControl["Table"] > 0) {
%>\

	// The ${item.name}Controller broadcasts incoming
	// events from other view-controllers to child-controllers
	// e.g. a table-controller.
	// This is used for data-transmission between views.

	function ${item.name}(\\$scope, \\$dispatcher) {
		var self = this;
		\\$dispatcher.subscribe(self);
		self.id = "${item.name}";

		self.\\$scope = \\$scope;
		self.\\$dispatcher = \\$dispatcher;

		\\$scope.\\$on("click", function(e, args) {
			// If the onselect is set add:
			\\$dispatcher.dispatch(self,args);
		});

		self.event = function(args) {
			\\$scope.\\$broadcast("event", args);
		}

		manipulator.getInstance("${item.name}").inject(self);
	}
	app.controller("${item.name}Controller", ['\\$scope', '\\$dispatcher',  ${item.name}]);
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


	function ${item.name}${control.name.capitalize()}(\\$scope, \\$http) {
		var self = this;

		self.id = "${item.name}${control.name.capitalize()}";
		self.entity = "${control.type.name}";

		self.\\$scope = \\$scope;
		self.\\$http = \\$http;

		self.parent = angular.module("Table").baseClass["TableBase"];
		self.parent.call(this, \\$scope);
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

		self.click = function(column, row) {
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
				row: row
			});
		}
<%
			}
			if (hasOpposite) {
%>\
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

		\\$scope.\\$on("event", function(e, args) {
<%
				opposite.each { prop ->
%>\
			if (args.targetView.some(function(d) {
				return d === "${item.name}.presenter";
			})){
				if (args.sourceEntity === "${prop.type.name}") {
					if (args.row.id && self.currentRow !== args.row.id) {
						self.fetchData(args.row.id);
					}
					self.currentRow = args.row.id;
				}
			}
<%
				}
%>\
		});
<%
			}
%>\

		manipulator.getInstance("${item.name}${control.name.capitalize()}").inject(self);
	}
	${item.name}${control.name.capitalize()}.prototype = Object.create(angular.module("Table").baseClass["TableBase"].prototype);
	app.controller("${item.name}${control.name.capitalize()}Controller", ['\\$scope', '\\$http', ${item.name}${control.name.capitalize()}]);
<%
		}
	}
%>\
}());
''')

	template('framesrcjs', body: '''\
(function(){
	var app = angular.module("${item.name}Injector", ["Manipulator"]);

	var manipulator = angular.module("Manipulator").manipulator.getInstance("${item.name}");
	manipulator.add("functionName", function() {
		// This could be your code
		// Include this file in the index.html

		// To manipulate the object just refer to self
		var self = this;
		console.info("This function has been injected");
	});
<%
	item.controls.each { control ->
		if (control.widgetType == "Table") {
%>
	var manipulator_${control.name.capitalize()} = angular.module("Manipulator").manipulator.getInstance("${item.name}${control.name.capitalize()}");
	manipulator_${control.name.capitalize()}.add("functionName", function() {

	});
<%
		}
	}
%>\
}());
''')
}