(function() {
  var app = angular.module("eeNode", []);

  app.directive('node', ["$compile", function($compile) {
    return {
      restrict: 'E',
      replace: true,
      template: '<div class="eeaxis-container" ng-style="{\'flex-grow\': node.size}"></div>',
      link: function(scope, elm, attrs) {
        if (scope.node.panels && scope.node.panels.length > 0) {
          var childNode = $compile('<node-axis ng-model="node"></node-axis>')(scope);
          elm.append(childNode);
        } else {
          var childNode = $compile('<panel class="eepanel"></panel>')(scope);
          elm.append(childNode);
        }
      }
    };
  }]);
})();
