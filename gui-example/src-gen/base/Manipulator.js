(function(){
	var app = angular.module("Manipulator",[]);

	//app.manipulator
	//returns: object
	// - func: getInstance(id)
	// - var:  instances
	
	//getInstance(id)
	//returns: object
	// - func: add(name, func)
	// - func: inject(obj)
	// - var:  injections
	
	//when 'getInstance' is called:
	//if no object of this id exists in 'instances', it is created, saved to 'instances' and returned
	//otherwise the object in 'instances' is returned
	
	//when 'add' is called:
	//save an object with a name associated with a function to 'injections'
	
	//when 'inject' is called:
	//go through the 'injections'-array and bind the function and name to the object
	
	app.manipulator = new function() {
		var self = this;

		self.instances = {};
		self.getInstance = function(id) {
			if (!self.instances[id]) {
				self.instances[id] = {
					injections: [],
					add: function(name, func) {
						this.injections.push({name: name, func: func});
						return this;
					},
					inject: function(obj) {
						this.injections.forEach(function(d,i) {
							obj[d.name] = d.func;
						});
						return this;
					}
				}
			}
			return self.instances[id];
		}
	}
}());
