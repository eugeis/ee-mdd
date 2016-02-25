(function(){
"use strict";
    var app = angular.module("Lightbox",["ComLightbox"]);

    app.directive("lightbox", ["$document", "$rootScope", function($document, $rootScope) {
        return {
            restrict: 'E',
            templateUrl: "src-gen/templates/lightbox.html",
            controller: ["$scope", "$lightbox", "$rootScope", LightboxController],
            controllerAs: 'lightboxCtrl',
            link: function() {
                $document.bind('keydown', function(e) {
                    $rootScope.$broadcast("keypress", e);
                });
            }
        };
    }]);

    function LightboxController($scope, $lightbox, $rootScope) {
    var self = this;
    $lightbox.setLightBox(this);

    self.columnInfo = undefined;
    self.type = undefined;
    self.caller = undefined;
    self.rowNr = undefined;
    self.show = false;

    self.create = function (info) {
      if (info.type !== "add" && info.type !== "delete") {
        return self.hide();
      }

      if (info.type === "add") {
        if (self.type !== "add") {
          self.columnInfo = info.columnInfo;
        }
      } else if (info.type === "delete") {
        self.rowNr = info.rowNr;
        self.columnInfo = info.columnInfo;
      }

      self.caller = info.caller;
      self.type = info.type;
      self.show = true;
    };

    self.submit = function (type) {
      var success;
      if (type === "add") {
        success = self.caller.add(self.columnInfo);
      } else if (type === "delete") {
        success = self.caller.delete(self.rowNr);
      } else {
        success = false;
      }

      if (success) {
        return self.hide();
      }
    };

    self.hide = function() {
      self.checked = false;
      self.show = false;
    }

    self.prevent = function(e) {
      e.stopPropagation();
    };
		
    $rootScope.$on("keypress", function(onEvent, keyEvent) {
      var key = (keyEvent.charCode || keyEvent.keyCode);
      if (key == 27) {
        $scope.$apply(self.hide());
      }
    });
  }
}());
