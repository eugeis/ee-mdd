
function TaskStatusLit(name, code) {
  this.name = name;
  this.code = code;
}
  
TaskStatusLit.prototype = {
  constructor: TaskStatusLit,
  isUnknown() : function() {
    return this === TaskStatus.UNKNOWN; 
  },
  
  isOpen() : function() {
    return this === TaskStatus.OPEN; 
  },
  
  isClosed() : function() {
    return this === TaskStatus.CLOSED; 
  }
}

var TaskStatus = {
  UNKNOWN: new TaskStatusLit('Unknown', -1),
  OPEN: new TaskStatusLit('Open', 1),
  CLOSED: new TaskStatusLit('Closed', 2)
}