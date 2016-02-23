(function() {
  var app = angular.module("eePanel", []);

  app.directive('panel', function() {
    return {
      restrict: 'E',
      replace: true,
      templateUrl: 'src-gen/templates/panel.html',
      controller: ["$scope", function($scope) {
        var self = this;
        self.selected = $scope.node.selected || 0;
        self.tabs = $scope.node.tabs;

        self.isActive = function(tab) {
          return tab === self.selected;
        }
        self.setActive = function(tab) {
          $scope.node.selected = self.selected = tab;
        }
        self.remove = function(index) {
          self.tabs.splice(index, 1);
          if (index < self.selected || self.selected >= self.tabs.length) {
            self.selected--;
          }
          if (self.selected < 0) {
            self.selected = 0;
          }
          $scope.node.selected = self.selected;

          if (self.tabs.length <= 0) {
            self.close();
          }
        }
        self.close = function() {
          var root = $scope;
          while(!root.treeCtrl) {
            root = root.$parent;
          }

          root.treeCtrl.delete($scope.node);
        }
      }],
      controllerAs: "panelCtrl"
    };
  });
})();
