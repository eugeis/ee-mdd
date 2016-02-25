  angular.module("TaskEditor2View",["Manipulator", "ComLightbox"])
  .controller("TaskEditor2ViewController", ['$scope', '$dispatcher', '$manipulator', '$lightbox', function ($scope, $dispatcher, $manipulator, $lightbox) {
    var self = this;
    self.model = false;
    self.presenter = "TaskEditor2Presenter";

    self.$scope = $scope;
    self.$dispatcher = $dispatcher;

    self.viewRefs = ["TaskDetailsView"];

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

    $manipulator.getInstance("TaskEditor2View").inject(self);
  }]);
