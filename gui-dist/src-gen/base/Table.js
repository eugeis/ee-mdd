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
