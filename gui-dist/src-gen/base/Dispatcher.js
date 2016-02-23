(function(){
"use strict";
  var app = angular.module("Dispatcher",[]);

  // Dispatches a message (including data) to all
  // subscribed controllers (excluding the one who
  // dispatched it).

  // The subscribe-method returns the unsubscribe
  // function

  app.factory("$dispatcher", function() {
    return {
      subscribers: [],
      clear: function() {
        this.subscribers = [];
      },
      subscribe : function(obj) {
        var self = this;
        self.subscribers.push(obj);
        return function() {
          var pos = self.subscribers.indexOf(obj);
          if (pos >= 0) {
            self.subscribers.splice(pos,1);
          }
        };
      },
      dispatch: function(args) {
        var self = this;
        args.observerRefs.forEach(function(d) {
          self.subscribers.forEach(function(e) {
            if (d === e.id + ".presenter") {
              if (e.event) {
                e.event(args);
              } else {
                console.error("No event-function defined for:");
                console.log(e);
              }
            }
          });
        });
      }
    };
  });
}());
