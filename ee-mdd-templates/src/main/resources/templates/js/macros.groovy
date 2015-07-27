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

  /*
   *
   * Entity
   *
   *
   *
   *
   *
   *
   *
   *
   *
   *
   */


    template('myhtmlheadermacro', body: '''
<!DOCTYPE html>
<html ng-app="${item.name}">
  <head>
    <title>${item.name}</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="bootstrap-3.3.5-dist/css/bootstrap.css">
    <link rel="stylesheet" href="${item.name}.css">
    <script src="angular.js" type="text/javascript"></script>
    <script src="${item.name}.js" type="text/javascript"></script>
</head>
<body>
''')

    template('myhtmlbodymacro', body: '''

  <form ng-controller="${item.name}Controller" ng-submit="submit()" name="tableForm">
    <table class="entityTable table table-bordered table-striped" ng-init="init()">
      <tr>

<% item.props.each { %><th ng-click="sort$it.name()">$it.name</th>\n<% } %>

      </tr>
      <tr ng-repeat="entity in entities">
        <td class="${item.props[0].name}column" ng-mouseover="displayCross(entity)" ng-mouseleave="display${item.props[0].name}(entity)">
          <span ng-click="promptDelete(entity)">{{entity.tag}}</span>
        </td>

<% for (int i = 1; i < item.props.size(); i++) {
def it = item.props[i]
%>
      <td ng-click="edit(entity)">{{entity.$it.name}}</td>\n
<% } %>

      </tr>
      <tr>
        <td class="tableSubmit" ng-click="submit()"><span>{{tableForm.\\$valid ? "&#x2713 " : "&#x25B7"}}</span></td>
        <td><input class="tableInput" type="text" ng-model="newEntity.${item.props[1].name}" focus-on="newEntityAdded" required>

<% if (item.props.size() == 2) { %>
        <input type="submit" style="position: absolute; left: -9999px; width: 1px; height: 1px;" tabindex="-1" />
<% } %>

        </td>\n

<% for (int i = 2; i < item.props.size() - 1; i++) {
def it = item.props[i]
%>
        <td><input class="tableInput" type="text" ng-model="newEntity.$it.name" required></td>\n
<% } %>

<% if (item.props.size() > 2) { %>

        <td><input class="tableInput" type="text" ng-model="newEntity.${item.props[item.props.size()-1].name}" required>
          <input type="submit" style="position: absolute; left: -9999px; width: 1px; height: 1px;" tabindex="-1" />
        </td>

<% } %>

      </tr>
    </table>
    <input type="button" ng-click="getJSON()" value="Get JSON">
    <textarea id="json-area"></textarea>
  </form>
''')

    template('myangularmacro', body: '''
function \\$(a){return document.getElementById(a);}

(function(){
  var app = angular.module("${item.name}",[]);

  app.controller("${item.name}Controller", function(\\$scope) {
    \\$scope.currentID = 0;

    \\$scope.newEntity = {};
    \\$scope.entities = [];

    \\$scope.init = function() {
      //myEntities.forEach(function(entity,index) {
      //	\\$scope.add(entity);
      //});
    }

    \\$scope.add = function(entity) {
      if (!entity.hasOwnProperty("${item.props[0].name}") && !entity.hasOwnProperty("tag")) {
        entity.${item.props[0].name} = entity.tag = ++\\$scope.currentID;
      }
      \\$scope.entities.push(entity);
      \\$scope.\\$broadcast('newEntityAdded');
      \\$scope.resetTo${item.props[0].name}();
    };

    \\$scope.edit = function(entity) {
      if (!\\$scope.tableForm.\\$valid) {
        \\$scope.newEntity = \\$scope.remove(entity);
        \\$scope.resetTo${item.props[0].name}();
      }
    };

    \\$scope.promptDelete = function(entity) {
      if (window.confirm("Are you sure you want to delete this entry?")) {
        \\$scope.remove(entity);
        \\$scope.resetTo${item.props[0].name}();
      }
    }

    \\$scope.remove = function(entity) {
      return \\$scope.entities.splice(\\$scope.entities.indexOf(entity),1)[0];
    }

    \\$scope.submit = function() {
      if(\\$scope.tableForm.\\$valid) {
        \\$scope.add(\\$scope.newEntity);
        \\$scope.newEntity = {};
      }
    }

    \\$scope.resetTo${item.props[0].name} = function() {
      \\$scope.entities.forEach(function(entity) {
        \\$scope.display${item.props[0].name}(entity);
      });
    }

    \\$scope.displayCross = function(entity) {
      entity.tag = '\u00D7';
    }

    \\$scope.display${item.props[0].name} = function(entity) {
      entity.tag = entity.${item.props[0].name};
    }

<% for (int i = 0; i < item.props.size(); i++ ) {
def it = item.props[i] %>

    \\$scope.sort${it.name} = function() {
      \\$scope.entities = \\$scope.entities.sort(function(a,b) {
        return a.${it.name}.toString().localeCompare(b.${it.name}.toString());
      });
    }

<% } %>

    \\$scope.getJSON = function() {
      var retArray = [];
      \\$scope.entities.forEach(function(d) {
        retArray.push(new ${item.name}Entity(<%
for (int i = 0; i < item.props.size()-1; i++) {
def it = item.props[i];
%>d.$it.name, <% } %>d.${item.props[item.props.size()-1].name}));
      });
      \\$("json-area").innerHTML = JSON.stringify(retArray).replace(/({.*?},)/g,"\\$1\\\\n");
    }
  });

app.directive('focusOn', function() {
  return function(scope, elem, attr) {
    scope.\\$on(attr.focusOn, function(e) {
      elem[0].focus();
    });
  };
});
}());
''')

    /*
     *
     *
     * Views
     *
     *
     *
     *
     */


	/*
	 *
	 * Index file
	 *
	 */

    template('indexheader', body: '''\
<!DOCTYPE html>
<html ng-app="${item.name}">
	<head>
		<title>${item.name}</title>
		<meta charset="UTF-8">
		<link rel="stylesheet" href="bootstrap-3.3.5-dist/css/bootstrap.css">
		<link rel="stylesheet" href="stylesheet.css">
		<script src="TableControllerBase.js" type="text/javascript"></script>
		<script src="angular.js" type="text/javascript"></script>
		<script src="app.js" type="text/javascript"></script>
<% item.viewRefs.each { %> \
		<script src="${it.view.name}.js" type="text/javascript"></script>
<% } %> \
	</head>
	<body>
''')

	template('htmlfootermacro', body: '''
	</body>
</html>
''')

	template('stylesheet', body: '''
.entityTable {
  width: 800px;
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

	template('appjs', body: '''\
(function(){
	var app = angular.module("${item.name}",[\
<%
for (int i = 0; i < item.viewRefs.size(); i++) {
def iterator = item.viewRefs[i];
%>\
"$iterator.view.name"\
<% if (i < item.viewRefs.size() - 1)  { %>\
,\
<% } %>\
<% } %>\
]);
\
<%
item.controls.each { iterator ->
if (iterator.widgetType == "Table") {
%>\
	app.controller("${iterator.type.name}Controller", function(\\$scope) {
	});
<% } %>\
<% } %>\

	app.directive('focusOn', function() {
	  return function(scope, elem, attr) {
	    scope.\\$on(attr.focusOn, function(e) {
	      elem[0].focus();
	    });
	  };
	});

}());
''')

	template('includedviews', body: '''
<% item.viewRefs.each { %>\
<section id="${it.view.name}" ng-include="'${it.view.name}.html'"></section>
<% } %>\
''')

	template('tableControllerBase', body: '''\
function TableControllerBase(injector) {
	return function(\\$scope) {
		\\$scope.currentID = 0;

		\\$scope.newEntity = {};
		\\$scope.entities = [];

		\\$scope.init = function() {
		  //myEntities.forEach(function(entity,index) {
		  //	\\$scope.add(entity);
		  //});
		}

		\\$scope.add = function(entity) {
		  if (!entity.hasOwnProperty("id") && !entity.hasOwnProperty("tag")) {
			entity.id = entity.tag = ++\\$scope.currentID;
		  }
		  \\$scope.entities.push(entity);
		  \\$scope.\\$broadcast('newEntityAdded');
		  \\$scope.resetToId();
		};

		\\$scope.edit = function(entity) {
		  if (!\\$scope.tableForm.\\$valid) {
			\\$scope.newEntity = \\$scope.remove(entity);
			\\$scope.resetToId();
		};
		  }

		\\$scope.promptDelete = function(entity) {
		  if (window.confirm("Are you sure you want to delete this entry?")) {
			\\$scope.remove(entity);
			\\$scope.resetToId();
		  }
		}

		\\$scope.remove = function(entity) {
		  return \\$scope.entities.splice(\\$scope.entities.indexOf(entity),1)[0];
		}

		\\$scope.submit = function() {
		  if(\\$scope.tableForm.\\$valid) {
			\\$scope.add(\\$scope.newEntity);
			\\$scope.newEntity = {};
		  }
		}

		\\$scope.resetToId = function() {
		  \\$scope.entities.forEach(function(entity) {
			\\$scope.displayId(entity);
		  });
		}

		\\$scope.displayCross = function(entity) {
		  entity.tag = '×';
		}

		\\$scope.displayId = function(entity) {
		  entity.tag = entity.id;
		}

		if (injector !== undefined) {
			injector.forEach(function(d) {
				d(\\$scope);
			});
		}
	}
}
''')

	/*
	 *
	 * Modules' files
	 *
	 */

	template('modulejs', body: '''\
(function(){
	var app = angular.module("${item.name}",[]);

<%
item.controls.each { iterator ->
if (iterator.widgetType == "Table") {
%>\

	function ${iterator.type.name}Sorting(\\$scope) {
	<% for (int i = 0; i < iterator.type.props.size(); i++ ) {
		def it = iterator.type.props[i] %>\

			\\$scope.sort${it.name} = function() {
			  \\$scope.entities = \\$scope.entities.sort(function(a,b) {
				return a.${it.name}.toString().localeCompare(b.${it.name}.toString());
			  });
			}

		<% } %>\
	};

	app.controller("${iterator.type.name}Controller", ['\\$scope', TableControllerBase([${iterator.type.name}Sorting])]);
<% } %>\
<% } %>\
}());
''')

    template('html', body: '''\
\
<%
item.controls.each { iterator ->
if (iterator.widgetType == "Table") {
def param = iterator.type
%>\
\
	<form ng-controller="${param.name}Controller" ng-submit="submit()" name="tableForm">
		<table class="entityTable table table-bordered table-striped" ng-init="init()">
			<tr>
\
<% param.props.each { %>\
				<th ng-click="sort$it.name()">$it.name</th>
<% } %>\
\
			</tr>
			<tr ng-repeat="entity in entities">
				<td class="idcolumn" ng-mouseover="displayCross(entity)" ng-mouseleave="displayId(entity)">
					<span ng-click="promptDelete(entity)">{{entity.tag}}</span>
				</td>
\
<%
for (int i = 1; i < param.props.size(); i++) {
def it = param.props[i]
%>\
				<td ng-click="edit(entity)">{{entity.$it.name}}</td>
<% } %>\
\
			</tr>
			<tr>
				<td class="tableSubmit" ng-click="submit()"><span>{{tableForm.\\$valid ? "&#x2713 " : "&#x25B7"}}</span></td>
				<td>
					<input class="tableInput" type="text" ng-model="newEntity.${param.props[1].name}" focus-on="newEntityAdded" required>
\
<% if (param.props.size() == 2) { %>\
					<input type="submit" style="position: absolute; left: -9999px; width: 1px; height: 1px;" tabindex="-1" />
<% } %>\
\
				</td>
\
<%
for (int i = 2; i < param.props.size() - 1; i++) {
def it = param.props[i]
%>\
\
				<td>
					<input class="tableInput" type="text" ng-model="newEntity.$it.name" required>
				</td>
\
<% } %>\
\
<% if (param.props.size() > 2) { %>\
\
				<td>
					<input class="tableInput" type="text" ng-model="newEntity.${param.props[param.props.size()-1].name}" required>
					<input type="submit" style="position: absolute; left: -9999px; width: 1px; height: 1px;" tabindex="-1">
				</td>
\
<% } %>\
			</tr>
		</table>
	</form>
\
\
<% } %>\
<% if (iterator.widgetType == "Button") { %>\
	<input type="button" value="$iterator.name"> $iterator.onAction.observers
<% } %>\
<% if (iterator.widgetType == "TextField") { %>\
	<input type="text" value="$iterator.name">
<% } %>\
<% } %>\
''')
}
