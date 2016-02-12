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
<html ng-app="$project">
  <head>
    <title>$project</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="bootstrap/bootstrap.css">
    <link rel="stylesheet" href="stylesheet.css">
  	<style>
	[ng-cloak], [data-ng-cloak], [x-ng-cloak], .ng-cloak, .x-ng-cloak {
  		display: none !important;
	}
  	</style>
   </head>
  <body>
    <h1 style="text-align:center">GENERATED</h1>
    <lightbox></lightbox>

  	<section ng-controller="TabController as tabCtrl" class="ng-cloak">
  		<ul class="nav nav-tabs topbar">
  			<li ng-repeat="tab in tabCtrl.tabs" ng-class="{active: tabCtrl.isSelected(tab)}" ng-click="tabCtrl.setTab(tab.url)"><a href="{{tab.url}}" ng-bind="tab.label"></a></li>
  		</ul>
  		<br style="clear: both">
  		<div class="mainView" ng-view></div>
  	</section>

    <!-- Frameworks -->
    <script src="angular.js" type="text/javascript"></script>
    <script src="angular-route.js" type="text/javascript"></script>

    <!-- Main app -->
    <script src="app.js" type="text/javascript"></script>

    <!-- Services -->
    <script src="src-gen/base/Dispatcher.js" type="text/javascript"></script>
    <script src="src-gen/base/Manipulator.js" type="text/javascript"></script>
    <script src="src-gen/base/ModelHandler.js" type="text/javascript"></script>

    <!-- Table implementation -->
    <script src="src-gen/base/Table.js" type="text/javascript"></script>

    <!-- View implementation -->
    <script src="src-gen/base/View.js" type="text/javascript"></script>
    <script src="src-gen/base/TabController.js" type="text/javascript"></script>

    <!-- Lightbox implementation -->
    <script src="src-gen/base/Lightbox.js" type="text/javascript"></script>
    <script src="src-gen/base/ComLightbox.js" type="text/javascript"></script>

    <!-- Injections dependencies -->
    <script src="src/Injections.js" type="text/javascript"></script>

    <!-- Include View-Javascript -->
<%
  	item.children.each { child ->
%>\
    <script src="src-gen/views/${child.name}.js" type="text/javascript"></script>
<%
  	}
%>
  </body>
</html>
''')


  template('appjs', body: '''\
(function(){
"use strict";
  var dependencies = ["ngRoute", "Injections", "Table", "View", "Lightbox", "TabSelection"];
  var views = [\
<%
  	for (int i = 0; i < item.children.size(); i++) {
  		def iterator = item.children[i];
%>\
"$iterator.name"\
<%
    if (i < item.children.size() - 1)  {
%>\
,\
<%
    }
  }
%>\
];
  var app = angular.module("$project",dependencies.concat(views));

  app.config(['\\$compileProvider', function (\\$compileProvider) {
    //\\$compileProvider.debugInfoEnabled(false);
  }]);
}());
''')

  template('tabctrl', body: '''\
<%
def mainView;
for (int i = 0; i < item.children.size(); i++) {
  if (item.children[i].main) {
  	mainView = item.children[i];
  	i = item.children.size();
  }
}
%>\
(function(){
"use strict";
	var app = angular.module("TabSelection",[]);

	app.config(['\\$routeProvider', '\\$locationProvider', function(\\$routeProvider, \\$locationProvider) {
	\\$routeProvider.
<%
  	item.children.each { v ->
  		if (v.main) {
%>\
			when('/${v.name}', {template: '<ee-view template="${v.name}"></ee-view>'}).
<%
  		}
  	}
%>\
			otherwise({redirectTo: '/', template: '<ee-view template="${mainView.name}"></ee-view>'});

			\\$locationProvider.html5Mode(false);
	}]);

	app.controller("TabController", ['\\$scope', '\\$location', function (\\$scope, \\$location) {
			this.setTab = function(url) {
				if (url === "#" || url === "#/") {
					this.selected = "#/${mainView.name}";
				} else {
					this.selected = url;
				}
			};

			this.isSelected = function(tab) {
				return this.selected == tab.url;
			}

  		this.tabs = [
<%
  	item.children.each { v ->
  		if (v.main) {
%>\
			{ url: "#/${v.name}", label: "${v.name}"},
<%
  		}
  	}
%>\
			];

			this.setTab("#" + \\$location.url());
	}]);
}());
''')

  template('stylecss', body: '''\
section {
/*  border: 1px solid #000; */
/*  padding: 25px; */
/*  margin: 25px; */
clear: both;
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

.topbar {
  margin-top: 50px;
}

.mainView {
  width: 80%;
  margin: 0px auto;
}

.mainView section section{
  margin-top: 15px;
  border-top: 1px solid #ccc;
  padding-top: 20px;
}

.mainView section input.btn {
  float: right;
  margin: 4px;
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
(function(){
"use strict";
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
(function(){
"use strict";
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
(function(){
"use strict";
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
      dispatch: function(args) {
        var self = this;
        args.observerRefs.forEach(function(d) {
          self.subscribers.forEach(function(e) {
            if (d === e.id + ".presenter") {
              if (e.event) {
                e.event(args);
              } else {
                console.error("No event-function defined for:");
                console.log(e);
              }
            }
          });
        });
      }
    };
  });
}());
''')

  template('manipulatorjs', body: '''\
(function(){
"use strict";
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
                obj[d.name] = d.func(obj);
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

  template('modelhandlerjs', body: '''\
(function(){
"use strict";
  var app = angular.module("ModelHandler",[]);

  app.factory("\\$model", function() {
    return {
    refs: [],
      views: {},
      addView: function(view) {
        this.refs.push(view.id);
        this.views[view.id] = {
          view: view,
          children: view.viewRefs
        };
      },
      getControl : function(model, type) {

      }
  };
  });
}());
''')


  template('injectionsjs', body: '''\
(function(){
"use strict";
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
            <button type="submit" class="btn btn-primary">Add</button>
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
            <button type="submit" class="btn btn-danger" ng-class="{disabled: !lightboxCtrl.checked}">Delete</button>
          </form>
      </div>
    </div>
</div>
''')


  template('lightboxjs', body: '''\
(function(){
"use strict";
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
(function(){
"use strict";
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
<section class="${item.name}">
<h4 style="padding-left:10px">${item.name}</h4>
<%
  item.controls.each { control ->
    if (control.widgetType == "Button") {
%>\
\
<%
  if (control.name.capitalize().startsWith("Add")) {
%>\
    <input type="button" class="btn btn-primary" value="${control.name.capitalize()}" ng-click="${item.name.toLowerCase()}Ctrl.lightbox('add',\
<%
  } else if (control.name.capitalize().startsWith("Delete")) {
%>\
    <input type="button" class="btn btn-danger btn-xs" value="${control.name.capitalize()}" ng-click="${item.name.toLowerCase()}Ctrl.lightbox('delete',\
<%
  } else if (control.name.capitalize().startsWith("Accept")) {
%>\
    <input type="button" class="btn btn-primary" value="${control.name.capitalize()}" ng-click="${item.name.toLowerCase()}Ctrl.lightbox('accept',\
<%
  } else if (control.name.capitalize().startsWith("Discard")) {
%>\
    <input type="button" class="btn btn-warning" value="${control.name.capitalize()}" ng-click="${item.name.toLowerCase()}Ctrl.lightbox('discard',\
<%
  } else {
%>\
    <input type="button" class="btn btn-info" value="${control.name.capitalize()}" ng-click="${item.name.toLowerCase()}Ctrl.lightbox('search',\
<%
  }
%>\
<%
      if (control.onAction) {
%>\
'$control.onAction.name'\
<%
      } else {
%>\
''
<%
      }
%>\
)">
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
    <label>${control.name.capitalize()}</label> <input type="text" placeholder="${control.name.capitalize()}">
\
<%
    }
  }
%>\
<% item.viewRefs.each { viewRef -> %>\
  <ee-view template="${viewRef.view.name}" ng-controller="${viewRef.view.name}Controller as ${viewRef.view.name.toLowerCase()}Ctrl"></ee-view>
<% } %>\
</section>
''')

  template('framejs', body: '''\
(function(){
"use strict";
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
"Manipulator", "ComLightbox","ModelHandler"]);
<%
  if (hasControl["Table"] > 0) {
%>\

  app.controller("${item.name}Controller", ['\\$scope', '\\$dispatcher', '\\$manipulator', '\\$lightbox', '\\$model', function (\\$scope, \\$dispatcher, \\$manipulator, \\$lightbox, \\$model) {
    var self = this;
    self.id = "${item.name}";
    self.model = \
<%
    if (item.model) {
%>\
$item.viewModel
.name\
<%
    } else {
%>\
false\
<%
  	}
%>\
;

    self.\\$scope = \\$scope;
    self.\\$dispatcher = \\$dispatcher;

    self.viewRefs = [\
<%
    if (item.viewRefs) {
      for (def i = 0; i < item.viewRefs.size(); i++) {
        def viewRef = item.viewRefs[i]
%>\
"$viewRef.view.name"\
<%
        if(i < item.viewRefs.size()-1) {
%>\
,\
<%
        }
      }
    }
%>\
];

    self.event = function(args) {
      self.\\$scope.\\$broadcast("transEvent", args);
    }

    self.subscribeToDispatcher = function() {
      self.\\$dispatcher.subscribe(self);
    }

    self.registerClickEvent = function() {
      self.\\$scope.\\$on("subEvent", function(e, args) {
        self.\\$dispatcher.dispatch(args);
      });
    }

    self.lightbox = function(type, model) {
      var \\$model = {getInfo: function() { return false; }};

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

    self.init = function() {};

    \\$manipulator.getInstance("${item.name}").inject(self);
    \\$model.addView(self);
    self.subscribeToDispatcher();
    self.registerClickEvent();
    self.init();
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
    self.parentView = "${item.name}";

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
    self.data = self.\\$http.get('data/${item.name}.json')
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
      \\$scope.\\$emit("subEvent", {
        type: "click",
        eventSource: self,
        sourceEntity: self.entity,
        observerRefs: [\
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
      self.data = self.\\$http.get('data/TaskDetailsView.php?id=' + id + '&type=${control.name.capitalize()}')
        .success(function(data, status, headers, config) {
          self.data = data;
        })
        .error(function(data, status, headers, config) {
          console.error("HTTP - ERROR: " + status);
          self.data = {};
        });
    };

    self.registerEvent = function() {
      self.\\$scope.\\$on("transEvent", function(e, args) {
<%
        opposite.each { prop ->
%>\
        if (args.observerRefs.some(function(d) {
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
    self.init = function() {};
    \\$manipulator.getInstance("${item.name}${control.name.capitalize()}").inject(self);
<%
    if (hasOpposite) {
%>\
    self.registerEvent();
<%
    }
%>\
    self.init();
  }]);
<%
    }
  }
%>\
}());
''')

  template('framesrcjs', body: '''\
(function(){
"use strict";
  var app = angular.module("${item.name}Injector", ["Manipulator"]);

  app.run(["\\$manipulator", function(\\$manipulator) {
    var manipulator = \\$manipulator.getInstance("${item.name}");
    manipulator.add("functionName", function(self) {
      return function() {
        // This could be your code
        // Include this file in the index.html

        // To manipulate the object just refer to self
        // The callback-function can take arguments (e.g. row, column)
        console.info("This function has been injected");
      };
    });
<%
  item.controls.each { control ->
    if (control.widgetType == "Table") {
%>
    var manipulator_${control.name.capitalize()} = \\$manipulator.getInstance("${item.name}${control.name.capitalize()}");
    manipulator_${control.name.capitalize()}.add("click", function(self) {
      return function() {
      };
    });
<%
    }
  }
%>\
  }]);
}());
''')
}
