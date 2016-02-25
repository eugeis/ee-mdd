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
    <div style="position: absolute; right: 0px;bottom: 0px; size: 10px;z-index:100000">
      <span id="x">0</span>
      <span id="y">0</span>
      <br>
      <span id="a">0</span>
      <span id="b">0</span>
    </div>
    <script>
      function \\$(a) {
        return document.getElementById(a);
      }
      document.body.onmousemove = function(e) {
        \\$("x").innerHTML = e.x;
        \\$("y").innerHTML = e.y;


        \\$("a").innerHTML = e.clientX;
        \\$("b").innerHTML = e.clientY;
      }
    </script>

    <lightbox></lightbox>
    <tree data-url="data.php"></tree>

    <!-- Frameworks -->
    <script src="angular.js" type="text/javascript"></script>
    <script src="angular-route.js" type="text/javascript"></script>

    <!-- Main app -->
    <script src="app.js" type="text/javascript"></script>

    <!-- Services -->
    <script src="src-gen/base/Dispatcher.js" type="text/javascript"></script>
    <script src="src-gen/base/Manipulator.js" type="text/javascript"></script>
    <script src="src-gen/base/ModelHandler.js" type="text/javascript"></script>

    <!-- Lightbox implementation -->
    <script src="src-gen/base/Lightbox.js" type="text/javascript"></script>
    <script src="src-gen/base/ComLightbox.js" type="text/javascript"></script>

    <!-- Injections dependencies -->
    <script src="src/Injections.js" type="text/javascript"></script>

    <!-- Table implementation -->
    <script src="src-gen/base/Table.js" type="text/javascript"></script>

    <!-- View implementation -->
    <script src="src-gen/base/View.js" type="text/javascript"></script>

    <!-- Include tree-script -->
    <script src="src-gen/base/Node.js" type="text/javascript"></script>
    <script src="src-gen/base/Nodeaxis.js" type="text/javascript"></script>
    <script src="src-gen/base/Panel.js" type="text/javascript"></script>
    <script src="src-gen/base/Separator.js" type="text/javascript"></script>
    <script src="src-gen/base/Tree.js" type="text/javascript"></script>

    <!-- Include View-Javascript -->
<%
  	item.children.each { child ->
%>\

        <!-- Include ${child.name} and its controls -->
        <script src="src-gen/views/${child.name}.js" type="text/javascript"></script>
<%
  		child.controls.each { control ->
  			if (control.widgetType == "Table") {
%>\
        <script src="src-gen/controls/${control.view.name}${control.type.name}.js" type="text/javascript"></script>
<%
  			}
  		}
  	}
%>\
  </body>
</html>
''')


  template('appjs', body: '''\
var dependencies = ["Injections", "Table", "View", "Lightbox", "eeTree"];
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
  angular.module("$project",dependencies.concat(views))
	.config(['\\$compileProvider', function (\\$compileProvider) {
  	//\\$compileProvider.debugInfoEnabled(false);
  }]);
''')

  template('framehtml', body: '''\
<section class="${item.name}" ng-controller="${item.name}Controller as ${item.name.toLowerCase()}Ctrl">
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
    <ee-table ng-controller="${item.name}${control.type.name.capitalize()}Controller as tableCtrl"></ee-table>
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
  <ee-view template="${viewRef.view.name}"></ee-view>
<% } %>\
</section>
''')

  template('framejs', body: '''\
  angular.module("$item.name",["Manipulator", "ComLightbox","ModelHandler"])
  .controller("${item.name}Controller", ['\\$scope', '\\$dispatcher', '\\$manipulator', '\\$lightbox', '\\$model', function (\\$scope, \\$dispatcher, \\$manipulator, \\$lightbox, \\$model) {
    var self = this;
    self.model = \
<%
    if (item.viewModel) {
%>\
"$item.viewModel.name"\
<%
    } else {
%>\
false\
<%
  	}
%>\
;
    self.presenter = \
<%
    if (item.presenter) {
%>\
"$item.presenter.name"\
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
<%
  		if (!c.main) {
%>\

    self.\\$destroy = self.\\$scope.\\$on("\\$destroy", function() {
      self.unsubscribe();
    });

    self.event = function(args) {
      self.\\$scope.\\$broadcast("transEvent", args);
    }

    self.unsubscribe = self.\\$dispatcher.subscribe(self);

    self.subEvent = self.\\$scope.\\$on("subEvent", function(e, args) {
        self.\\$dispatcher.dispatch(args);
    });
<%
  		}
%>\

    self.lightbox = function(type, model) {
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

    \\$manipulator.getInstance("${item.name}").inject(self);
  }]);
''')

  template('framesrcjs', body: '''\
  angular.module("${item.name}Injector", ["Manipulator"])
    .run(["\\$manipulator", function(\\$manipulator) {
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
        return {
          exec: true,
          func: function() {
          }
        };
      });
<%
    }
  }
%>\
  }]);
''')

  template('tablejs', body: '''\
<%
  def view = item.view;

  def hasMulti = false;
  def hasOpposite = false;

  def multi = [];
  def opposite = [];

  item.type.props.each { prop ->
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
  angular.module("$view.name")
  .controller("${view.name}${item.type.name.capitalize()}Controller", ['\\$scope', '\\$http', '\\$manipulator', function (\\$scope, \\$http, \\$manipulator) {
    var self = this;
    self.entity = "${item.type.name}";

    self.\\$scope = \\$scope;
    self.\\$http = \\$http;

    self.columns = [\
<%
      for (def i = 0; i < item.type.props.size(); i++) {
        def prop = item.type.props[i]
%>\
"$prop.name"\
<%
        if(i < item.type.props.size()-1) {
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
    self.data = self.\\$http.get('data/${view.name}.json')
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
        if (item.onSelect.observerRefs) {
          for (def i = 0; i < item.onSelect.observerRefs.size(); i++) {
%>\
"${item.onSelect.observerRefs[i]}"\
<%
            if(i < item.onSelect.observerRefs.size() - 1) {
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
      } else {
%>\
    self.click = function(column, row) { console.info("TableBase: click() is not defined"); };

<%
      }
      if (hasOpposite) {
%>\
    self.fetchData = function(id) {
      self.data = self.\\$http.get('data/TaskDetailsView.php?id=' + id + '&type=${item.name.capitalize()}')
        .success(function(data, status, headers, config) {
          self.data = data;
        })
        .error(function(data, status, headers, config) {
          console.error("HTTP - ERROR: " + status);
          self.data = {};
        });
    };

    self.transEvent = self.\\$scope.\\$on("transEvent", function(e, args) {
<%
        opposite.each { prop ->
%>\
	  if (args.sourceEntity === "${prop.type.name}") {
	    if (args.row && self.currentRow !== args.row.id) {
	      self.fetchData(args.row.id);
	    }
	  }
<%
        }
%>\
    });

<%
    }
%>\
    \\$manipulator.getInstance("${view.name}${item.name.capitalize()}").inject(self);
  }]);
''')

  template('nop', body: '''nop''')
}
