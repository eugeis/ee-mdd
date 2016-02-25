  angular.module("TaskExplorerView")
  .controller("TaskExplorerViewTaskController", ['$scope', '$http', '$manipulator', function ($scope, $http, $manipulator) {
    var self = this;
    self.entity = "Task";

    self.$scope = $scope;
    self.$http = $http;

    self.columns = ["id","comments","created","closed","actions","size","order"];

    self.data = self.$http.get('data/TaskExplorerView.json')
      .success(function(data, status, headers, config) {
        self.data = data;
      })
      .error(function(data, status, headers, config) {
        console.error("HTTP - ERROR: " + status);
        self.data = {};
      });

    self.click = function(column, rowNr) {
      self.selected = rowNr;
      $scope.$emit("subEvent", {
        type: "click",
        eventSource: self,
        sourceEntity: self.entity,
        observerRefs: ["TaskDetailsView.presenter"],
        column: column,
        row: self.data[rowNr]
      });
    }

    $manipulator.getInstance("TaskExplorerViewTasks").inject(self);
  }]);
