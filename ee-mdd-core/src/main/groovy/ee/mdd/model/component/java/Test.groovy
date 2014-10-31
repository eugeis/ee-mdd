package ee.mdd.model.component.java

import ee.mdd.model.component.Facet

class Test extends Facet {
	protected Map nameToNamespace() {
		['Test': 'org.junit', 'Before': 'org.junit', 'Assert': 'static junit.framework.Assert.*']
	}
}
