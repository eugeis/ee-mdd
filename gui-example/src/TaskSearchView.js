  angular.module("TaskSearchViewInjector", ["Manipulator"])
    .run(["$manipulator", function($manipulator) {
      var manipulator = $manipulator.getInstance("TaskSearchView");
      manipulator.add("functionName", function(self) {
        return function() {
          // This could be your code
          // Include this file in the index.html

          // To manipulate the object just refer to self
          // The callback-function can take arguments (e.g. row, column)
          console.info("This function has been injected");
        };
      });

      var manipulator_Actions = $manipulator.getInstance("TaskSearchViewActions");
      manipulator_Actions.add("click", function(self) {
        return {
          exec: true,
          func: function() {
          }
        };
      });
  }]);
