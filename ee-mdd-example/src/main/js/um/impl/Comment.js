
function Comment() {
  // inherit from base class
  Comment.prototype = new UmEntity();

  // correct the constructor pointer because it points to base class
  Comment.prototype.constructor = Comment;
}