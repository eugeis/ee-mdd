  angular.module("TaskDetailsViewInjector", ["Manipulator"])
    .run(["$manipulator", function($manipulator) {
      var manipulator = $manipulator.getInstance("TaskDetailsView");
      manipulator.add("functionName", function(self) {
        return function() {
          // This could be your code
          // Include this file in the index.html

          // To manipulate the object just refer to self
          // The callback-function can take arguments (e.g. row, column)
          console.info("This function has been injected");
        };
      });

      var manipulator_Actions = $manipulator.getInstance("TaskDetailsViewActions");
      manipulator_Actions.add("click", function(self) {
        return {
          exec: true,
          func: function() {
          }
        };
      });

      var manipulator_Comments = $manipulator.getInstance("TaskDetailsViewComments");
      manipulator_Comments.add("click", function(self) {
        return {
          exec: true,
          func: function() {
          }
        };
      });
  }]);
