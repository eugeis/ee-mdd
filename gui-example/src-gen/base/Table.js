(function(){
	var app = angular.module("Table",["Dispatcher"]);

	//See: gui-documentation.md or angular doc on directive
	app.directive("eeTable", function() {
		return {
			restrict: 'E',
			templateUrl: "src-gen/templates/table.html",
			replace: true
		};
	});

	app.baseClass = {};
	app.baseClass["TableBase"] = function ($scope) {
		var self = this;
		self.click = function(column, row) { console.info("TableBase: click() is not defined"); };
	}
}());
