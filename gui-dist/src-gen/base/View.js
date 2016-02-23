(function(){
"use strict";
  var app = angular.module("View",[]);

  app.directive("eeView", function() {
    return {
      restrict: 'E',
      scope: { template: '@'},
      replace: true,
      template: "<div ng-include=\"'src-gen/templates/' + template + '.html'\"></div>"
    };
  });
}());
