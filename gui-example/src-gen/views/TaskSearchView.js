(function(){
	var app = angular.module("TaskSearchView",["Table","View"]);
	var manipulator = angular.module("Manipulator").manipulator;

	// The TaskSearchViewController broadcasts incoming
	// events from other view-controllers to child-controllers
	// e.g. a table-controller.
	// This is used for data-transmission between views.

	function TaskSearchView($scope, $dispatcher) {
		var self = this;
		self.id = "TaskSearchView";

		self.$scope = $scope;
		self.$dispatcher = $dispatcher;

		self.event = function(args) {
			$scope.$broadcast("event", args);
		}

		self.subscribeToDispatcher = function() {
			$dispatcher.subscribe(self);
		}

		self.registerClickEvent = function() {
			$scope.$on("click", function(e, args) {
				// If the onselect is set add:
				$dispatcher.dispatch(self,args);
			});
		}

		manipulator.getInstance("TaskSearchView").inject(self);
		self.subscribeToDispatcher();
		self.registerClickEvent();
	}
	app.controller("TaskSearchViewController", ['$scope', '$dispatcher',  TaskSearchView]);


	function TaskSearchViewActions($scope, $http) {
		var self = this;

		self.id = "TaskSearchViewActions";
		self.entity = "TaskAction";

		self.$scope = $scope;
		self.$http = $http;

		self.parent = angular.module("Table").baseClass["TableBase"];
		self.parent.call(this, $scope);
		self.currentRow = undefined;

		self.columns = ["id","task","name"];


		self.fetchData = function(id) {
			self.data = $http.get('data/TaskDetailsView.php?id=' + id + '&type=Actions')
				.success(function(data, status, headers, config) {
					self.data = data;
				})
				.error(function(data, status, headers, config) {
					console.error("HTTP - ERROR: " + status);
					self.data = {};
				});
		};
		
		//registerEvent was created to be able to overwrite the "event"-Handler ($scope.$on)
		//in the manipulator
		self.registerEvent = function() {
			$scope.$on("event", function(e, args) {
				if (args.targetView.some(function(d) {
					return d === "TaskSearchView.presenter";
				})){
					if (args.sourceEntity === "Task") {
						if (args.row && self.currentRow !== args.row.id) {
							self.fetchData(args.row.id);
							self.currentRow = args.row.id;
						}
					}
				}
			});
		}

		manipulator.getInstance("TaskSearchViewActions").inject(self);
		self.registerEvent();

	}
	TaskSearchViewActions.prototype = Object.create(angular.module("Table").baseClass["TableBase"].prototype);
	app.controller("TaskSearchViewActionsController", ['$scope', '$http', TaskSearchViewActions]);
}());
