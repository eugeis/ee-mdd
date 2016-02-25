  angular.module("TaskDetailsView")
  .controller("TaskDetailsViewCommentController", ['$scope', '$http', '$manipulator', function ($scope, $http, $manipulator) {
    var self = this;
    self.entity = "Comment";

    self.$scope = $scope;
    self.$http = $http;

    self.columns = ["id","task","testProp","dateOfCreation","newTask"];

    self.click = function(column, rowNr) {
      self.selected = rowNr;
      $scope.$emit("subEvent", {
        type: "click",
        eventSource: self,
        sourceEntity: self.entity,
        observerRefs: [],
        column: column,
        row: self.data[rowNr]
      });
    }

    self.fetchData = function(id) {
      self.data = self.$http.get('data/TaskDetailsView.php?id=' + id + '&type=Comments')
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

    $manipulator.getInstance("TaskDetailsViewComments").inject(self);
  }]);
