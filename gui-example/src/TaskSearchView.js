(function(){
	var app = angular.module("TaskSearchViewInjector", ["Manipulator"]);

	var manipulator = angular.module("Manipulator").manipulator.getInstance("TaskSearchView");
	manipulator.add("functionName", function() {
		// This could be your code
		// Include this file in the index.html

		// To manipulate the object just refer to self
		// The callback-function can take arguments (e.g. row, column)
		var self = this;
		console.info("This function has been injected");
	});

	var manipulator_Actions = angular.module("Manipulator").manipulator.getInstance("TaskSearchViewActions");
	manipulator_Actions.add("functionName", function() {

	});
}());
