
function Task() {
  // inherit from base class
  Task.prototype = new UmEntity();

  // correct the constructor pointer because it points to base class
  Task.prototype.constructor = Task;
}