(function(){
	var app = angular.module("TaskExplorerView",["Table","View"]);
	var manipulator = angular.module("Manipulator").manipulator;

	// The TaskExplorerViewController broadcasts incoming
	// events from other view-controllers to child-controllers
	// e.g. a table-controller.
	// This is used for data-transmission between views.

	function TaskExplorerView($scope, $dispatcher) {
		var self = this;
		self.id = "TaskExplorerView";

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

		manipulator.getInstance("TaskExplorerView").inject(self);
		self.subscribeToDispatcher();
		self.registerClickEvent();
	}
	app.controller("TaskExplorerViewController", ['$scope', '$dispatcher',  TaskExplorerView]);

	//The table-controllers (called sub-controllers in gui-documentation) are so far used for displaying
	//data, fetching data, reacting to events and broadcasting events.
	function TaskExplorerViewTasks($scope, $http) {
		var self = this;

		self.id = "TaskExplorerViewTasks";
		self.entity = "Task";

		//This is done to use $scope and $http in the injection.
		self.$scope = $scope;
		self.$http = $http;

		self.parent = angular.module("Table").baseClass["TableBase"];
		self.parent.call(this, $scope);
		self.currentRow = undefined;

		self.columns = ["id","comments","created","closed","actions","size","order"];

		self.data = $http.get('data/TaskExplorerView.json')
			.success(function(data, status, headers, config) {
				self.data = data;
			})
			.error(function(data, status, headers, config) {
				console.error("HTTP - ERROR: " + status);
				self.data = {};
			});
			
		//the click event emits (sends a message to a PARENT-controller) relevant data
		self.click = function(column, row) {
			$scope.$emit("click", {
				childSource: self,
				sourceEntity: self.entity,
				targetView: ["TaskDetailsView.presenter"],
				column: column,
				row: row
			});
		}

		manipulator.getInstance("TaskExplorerViewTasks").inject(self);

	}
	TaskExplorerViewTasks.prototype = Object.create(angular.module("Table").baseClass["TableBase"].prototype);
	app.controller("TaskExplorerViewTasksController", ['$scope', '$http', TaskExplorerViewTasks]);
}());
