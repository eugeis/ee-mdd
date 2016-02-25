(function(){
"use strict";
  var app = angular.module("ComLightbox",[]);

  app.factory("$lightbox", function() {
    return {
      lightBoxCtrl : {},
      setLightBox : function(lbCtrl) {
        this.lightBoxCtrl = lbCtrl;
      },
      create : function(info) {
        this.lightBoxCtrl.create(info);
      }
    };
  });
}());
