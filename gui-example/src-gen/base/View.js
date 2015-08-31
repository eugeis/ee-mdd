(function(){
	var app = angular.module("View",[]);

	//See: gui-documentation.md or angular doc on directive
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
