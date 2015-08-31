(function(){
	var app = angular.module("TaskEditorViewInjector", ["Manipulator"]);

	var manipulator = angular.module("Manipulator").manipulator.getInstance("TaskEditorView");
	manipulator.add("functionName", function() {
		// This could be your code
		// Include this file in the index.html

		// To manipulate the object just refer to self
		// The callback-function can take arguments (e.g. row, column)
		var self = this;
		console.info("This function has been injected");
	});
}());
