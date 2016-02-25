(function() {
  var app = angular.module("eeNodeAxis", []);

  app.directive('nodeAxis', function() {
      return {
        templateUrl: 'src-gen/templates/nodeaxis.html',
        replace: true,
        restrict: 'E',
        scope: {
          tree: '=ngModel'
        },
      };
  });
})();
