(function() {
  var app = angular.module("eeTree", ["eeSeparator", "eePanel", "eeNode", "eeNodeAxis"]);

  app.directive("tree", function() {
    return {
      template: '<section class="tree hor"><div class="transclude" ng-click="treeCtrl.save()" ng-transclude></div><node-axis class="root" ng-model="treeCtrl.tree"></node-axis></section>',
      restrict: "E",
      replace: true,
      transclude: true,
      controller: ["$scope", "$http", "$attrs", function($scope, $http, $attrs) {
        var self = this;

        self.format = function(data) {
          if (data.mainViews) {
            self.mainViews = data.mainViews;
          }
          if (data.panels) {
            data.panels.forEach(function(d, i) {
              d.parent = data;
              if (d.panels && !d.split) {
                d.split = (d.parent.split === "hor") ? "vert" : "hor";
              }
              if (!d.panels && !d.tabs) {
                d.tabs = self.mainViews.slice();
              }
              self.format(d);
            });
          }
          return data;
        }

        self.delete = function(needle) {
          self.$delete(self.tree, needle);
          self.save();
        }

        self.$delete = function(haystack, needle) {
          if (haystack.panels) {
            var pos = haystack.panels.indexOf(needle);
            if (pos >= 0) {
              delete needle.parent;
              delete needle.panels;
              haystack.panels.splice(pos, 1);
              if (haystack.panels.length == 1) {
                self.tidy(haystack);
              }
            } else {
              haystack.panels.forEach(function(d, i) {
                self.$delete(d, needle);
              });
            }
          }
        }

        self.tidy = function(haystack) {
          if (haystack.parent) {
            var parent = haystack.parent;
            var pos = parent.panels.indexOf(haystack);
            if (haystack.panels[0].panels) {
              self.calcSizes(haystack, haystack.panels[0].panels);
              self.adoptGreatGrandChildren(haystack, parent, pos);
            } else {
              self.adoptGrandChildren(haystack, parent,pos);
            }
            delete haystack.parent;
            delete haystack.panels;
          } else {
            if (haystack.panels[0].panels) {
              self.crowning(haystack);
            } else {
              haystack.panels[0].size = 1;
            }
          }
        }

        self.calcSizes = function(haystack, grandchildren) {
          var sum = grandchildren.reduce(function(a,b) {
            return +a.size + +b.size;
          });

          grandchildren.forEach(function(d) {
            d.size = +d.size / sum * +haystack.size;
          });
        }

        self.adoptGreatGrandChildren = function(haystack, parent, pos) {
          parent.panels.splice(pos, 1);
          haystack.panels[0].panels.forEach(function(d,i) {
            d.parent = parent;
          });

          parent.panels.splice.apply(parent.panels, [pos, 0].concat(haystack.panels[0].panels));
        }

        self.adoptGrandChildren = function(haystack, parent, pos) {
          haystack.panels[0].parent = parent;
          haystack.panels[0].size = haystack.size;
          parent.panels[pos] = haystack.panels[0];
        }

        self.crowning = function(haystack) {
          self.tree = haystack.panels[0];
          delete self.tree.size;
          delete self.tree.parent;
        }

        self.save = function() {
          var json = {};
          json.split = self.tree.split;
          json.panels = [];

          self.$save(json.panels, self.tree.panels);

					localStorage.setItem('state',JSON.stringify(json));
        }

        self.$save = function(json, tree) {
          tree.forEach(function(d, i) {
            if (d.panels) {
              json.push({
                size: d.size,
                panels: []
              });
              self.$save(json[i].panels, d.panels);
            } else {
              json.push({
                size: d.size,
                view: d.view,
                tabs: d.tabs,
                selected: d.selected
              })
            }
          });
        }

        try {
          var storage;
					if (storage = JSON.parse(localStorage.getItem('state'))) {
            self.tree = self.format(storage);
          } else {
            throw "up";
          }
        } catch (e) {
          if (e === "up") {
            console.info("No state-storage found");
          }
          $http.get($attrs.url)
            .success(function(data, status, headers, config) {
              self.tree = self.format(data);
            })
            .error(function(data, status, headers, config) {
              console.error("HTTP - ERROR: " + status);
              self.tree = {
                "split": "vert",
                "panels": [
                  {
                    "view": "yellow",
                    "tabs": ["TaskExplorerView", "TaskExplorerView"],
                    "size": 1
                  }
                ]
              };
            });
        }
      }],
      controllerAs: "treeCtrl"
    };
  });
})();
