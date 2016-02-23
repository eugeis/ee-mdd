(function() {
  var app = angular.module("eeSeparator", ["eeTree"]);

  app.directive("separator", function() {
    return {
      restrict: 'A',
      require: '^^tree',
      link: function(scope, elm, attrs, controller) {
        var d = elm[0];

				d.addEventListener("dragstart", dragstart, false);
				d.addEventListener("drag", drag, false);
				d.addEventListener("dragend", dragend, false);

        function getPos(obj) {
          var left = 0;
          var top = 0;
          if (obj.offsetParent)  {
            do {
              left += obj.offsetLeft;
              top += obj.offsetTop;
            } while (obj = obj.offsetParent);

            return [left,top];
          }
        }

        function dragstart(e) {
          var prev = d.previousElementSibling;
          var next = d.nextElementSibling
          d.sum = (+prev.style.flexGrow || 1) + (+next.style.flexGrow || 1);
          e.dataTransfer.setDragImage(this.cloneNode(true), 0, 0);

          var xy = getPos(prev);
          d.absoluteX = xy[0];
          d.absoluteY = xy[1];
        }
        function drag(e) {
          if (e.x !== 0 && e.y !== 0) {
            var prev = d.previousElementSibling;
            var next = d.nextElementSibling;
            var ges;
            var ratio;

            if (elm.hasClass("vert")) {
              ges = prev.offsetWidth + next.offsetWidth;
              ratio = (e.x - d.absoluteX) / ges;
            } else if (elm.hasClass("hor")) {
              ges = prev.offsetHeight + next.offsetHeight;
              ratio = (e.y - d.absoluteY) / ges;
            } else {
              return;
            }
            prev.style.flexGrow = d.sum * ratio;
            next.style.flexGrow = d.sum * (1 - ratio);
          }
        }
        function dragend() {
          var prev = d.previousElementSibling;
          var next = d.nextElementSibling;
          angular.element(prev).scope().node.size = prev.style.flexGrow;
          angular.element(next).scope().node.size = next.style.flexGrow;

          delete d.absoluteX;
          delete d.absoluteY;
          delete d.sum;

          controller.save();
        }
      }
    }
  });
})();
