package ee.mdd.model.component.java

import ee.mdd.model.component.Facet

class Cdi extends Facet {
	protected Map nameToNamespace() {
		['Produces' : 'javax.enterprise.inject.Produces']
	}
}
