var dependencies = ["Injections", "Table", "View", "Lightbox", "eeTree"];
var views = ["TaskEditorView","TaskEditor2View","TaskExplorerView","TaskDetailsView","TaskSearchView"];
  angular.module("eeMddUi",dependencies.concat(views))
	.config(['$compileProvider', function ($compileProvider) {
  	//$compileProvider.debugInfoEnabled(false);
  }]);
