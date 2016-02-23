(function(){
"use strict";
  var app = angular.module("ModelHandler",[]);

  app.factory("$model", function() {
    return {
    refs: [],
      views: {},
      addView: function(view) {
        this.refs.push(view.id);
        this.views[view.id] = {
          view: view,
          children: view.viewRefs
        };
      },
      getControl : function(model, type) {

      }
  };
  });
}());
