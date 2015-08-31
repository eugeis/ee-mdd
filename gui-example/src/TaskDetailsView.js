(function(){
	var app = angular.module("TaskDetailsViewInjector", ["Manipulator"]);

	var manipulator = angular.module("Manipulator").manipulator.getInstance("TaskDetailsView");
	manipulator.add("functionName", function() {
		// This could be your code
		// Include this file in the index.html

		// To manipulate the object just refer to self
		// The callback-function can take arguments (e.g. row, column)
		var self = this;
		console.info("This function has been injected");
	});

	var manipulator_Actions = angular.module("Manipulator").manipulator.getInstance("TaskDetailsViewActions");
	manipulator_Actions.add("functionName", function() {

	});

	var manipulator_Comments = angular.module("Manipulator").manipulator.getInstance("TaskDetailsViewComments");
	manipulator_Comments.add("functionName", function() {

	});
}());
