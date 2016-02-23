(function(){
"use strict";
	var app = angular.module("TabSelection",["Dispatcher"]);

	app.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	$routeProvider.
			when('/TaskEditorView', {template: '<ee-view template="TaskEditorView"></ee-view>'}).
			when('/TaskEditor2View', {template: '<ee-view template="TaskEditor2View"></ee-view>'}).
			otherwise({redirectTo: '/', template: '<ee-view template="TaskEditorView"></ee-view>'});

			$locationProvider.html5Mode(false);
	}]);

	app.controller("TabController", ['$scope', '$location', '$dispatcher', function ($scope, $location, $dispatcher) {
			this.setTab = function(url) {
				if (url !== this.selected) {
					$dispatcher.clear();
				}
				
				if (url === "#" || url === "#/") {
					this.selected = "#/TaskEditorView";
				} else {
					this.selected = url;
				}
			};

			this.isSelected = function(tab) {
				return this.selected == tab.url;
			}

  		this.tabs = [
			{ url: "#/TaskEditorView", label: "TaskEditorView"},
			{ url: "#/TaskEditor2View", label: "TaskEditor2View"},
			];

			this.setTab("#" + $location.url());
	}]);
}());
