  angular.module("TaskSearchView")
  .controller("TaskSearchViewTaskActionController", ['$scope', '$http', '$manipulator', function ($scope, $http, $manipulator) {
    var self = this;
    self.entity = "TaskAction";

    self.$scope = $scope;
    self.$http = $http;

    self.columns = ["id","task","name"];

    self.click = function(column, row) { console.info("TableBase: click() is not defined"); };

    self.fetchData = function(id) {
      self.data = self.$http.get('data/TaskDetailsView.php?id=' + id + '&type=Actions')
        .success(function(data, status, headers, config) {
          self.data = data;
        })
        .error(function(data, status, headers, config) {
          console.error("HTTP - ERROR: " + status);
          self.data = {};
        });
    };

    self.transEvent = self.$scope.$on("transEvent", function(e, args) {
	  if (args.sourceEntity === "Task") {
	    if (args.row && self.currentRow !== args.row.id) {
	      self.fetchData(args.row.id);
	    }
	  }
    });

    $manipulator.getInstance("TaskSearchViewActions").inject(self);
  }]);
