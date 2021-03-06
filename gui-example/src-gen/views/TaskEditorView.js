  angular.module("TaskEditorView",["Manipulator", "ComLightbox"])
  .controller("TaskEditorViewController", ['$scope', '$dispatcher', '$manipulator', '$lightbox', function ($scope, $dispatcher, $manipulator, $lightbox) {
    var self = this;
    self.model = false;
    self.presenter = "TaskEditorPresenter";

    self.$scope = $scope;
    self.$dispatcher = $dispatcher;

    self.viewRefs = ["TaskExplorerView","TaskDetailsView"];

    self.lightbox = function(type, model) {
      if (type === "add") {
        $lightbox.create({
          caller: this,
          type: type,
          columnInfo: [{name: "name"}, {name: "age"}]
        });
      } else if (type === "delete") {
        $lightbox.create({
          caller: this,
          type: type,
          columnInfo: [{name: "name", value: "Jonas"}, {name: "age", value: "20"}],
          rowNr: 3
        });
      }
    };

    $manipulator.getInstance("TaskEditorView").inject(self);
  }]);
