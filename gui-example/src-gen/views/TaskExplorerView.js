  angular.module("TaskExplorerView",["Manipulator", "ComLightbox"])
  .controller("TaskExplorerViewController", ['$scope', '$dispatcher', '$manipulator', '$lightbox', function ($scope, $dispatcher, $manipulator, $lightbox) {
    var self = this;
    self.model = false;
    self.presenter = "TaskExplorerPresenter";

    self.$scope = $scope;
    self.$dispatcher = $dispatcher;

    self.viewRefs = ["TaskSearchView"];

    self.$destroy = self.$scope.$on("$destroy", function() {
      self.unsubscribe();
    });

    self.event = function(args) {
      self.$scope.$broadcast("transEvent", args);
    }

    self.unsubscribe = self.$dispatcher.subscribe(self);

    self.subEvent = self.$scope.$on("subEvent", function(e, args) {
        self.$dispatcher.dispatch(args);
    });

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

    $manipulator.getInstance("TaskExplorerView").inject(self);
  }]);
