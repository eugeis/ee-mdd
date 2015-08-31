(function(){
	var app = angular.module("TaskDetailsView",["Table","View"]);
	var manipulator = angular.module("Manipulator").manipulator;

	// The TaskDetailsViewController broadcasts incoming
	// events from other view-controllers to child-controllers
	// e.g. a table-controller.
	// This is used for data-transmission between views.

	function TaskDetailsView($scope, $dispatcher) {
		var self = this;
		self.id = "TaskDetailsView";

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

		manipulator.getInstance("TaskDetailsView").inject(self);
		self.subscribeToDispatcher();
		self.registerClickEvent();
	}
	app.controller("TaskDetailsViewController", ['$scope', '$dispatcher',  TaskDetailsView]);


	//The table-controllers (called sub-controllers in gui-documentation) are so far used for displaying
	//data, fetching data, reacting to events and broadcasting events.
	function TaskDetailsViewActions($scope, $http) {
		var self = this;

		self.id = "TaskDetailsViewActions";
		self.entity = "TaskAction";

		//This is done to use $scope and $http in the injection.
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
					return d === "TaskDetailsView.presenter";
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

		manipulator.getInstance("TaskDetailsViewActions").inject(self);
		self.registerEvent();

	}
	TaskDetailsViewActions.prototype = Object.create(angular.module("Table").baseClass["TableBase"].prototype);
	app.controller("TaskDetailsViewActionsController", ['$scope', '$http', TaskDetailsViewActions]);


	function TaskDetailsViewComments($scope, $http) {
		var self = this;

		self.id = "TaskDetailsViewComments";
		self.entity = "Comment";

		self.$scope = $scope;
		self.$http = $http;

		self.parent = angular.module("Table").baseClass["TableBase"];
		self.parent.call(this, $scope);
		self.currentRow = undefined;

		self.columns = ["id","task","testProp","dateOfCreation","newTask"];

		//the click event emits (sends a message to a PARENT-controller) relevant data
		self.click = function(column, row) {
			$scope.$emit("click", {
				childSource: self,
				sourceEntity: self.entity,
				targetView: [],
				column: column,
				row: row
			});
		}

		self.fetchData = function(id) {
			self.data = $http.get('data/TaskDetailsView.php?id=' + id + '&type=Comments')
				.success(function(data, status, headers, config) {
					self.data = data;
				})
				.error(function(data, status, headers, config) {
					console.error("HTTP - ERROR: " + status);
					self.data = {};
				});
		};

		self.registerEvent = function() {
			$scope.$on("event", function(e, args) {
				if (args.targetView.some(function(d) {
					return d === "TaskDetailsView.presenter";
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

		manipulator.getInstance("TaskDetailsViewComments").inject(self);
		self.registerEvent();

	}
	TaskDetailsViewComments.prototype = Object.create(angular.module("Table").baseClass["TableBase"].prototype);
	app.controller("TaskDetailsViewCommentsController", ['$scope', '$http', TaskDetailsViewComments]);
}());
