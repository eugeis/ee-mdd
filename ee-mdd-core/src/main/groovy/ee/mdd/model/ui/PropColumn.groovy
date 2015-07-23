package ee.mdd.model.ui;

import ee.mdd.model.component.Prop;

class PropColumn extends Column {
	Prop prop
	String deriveName() {
		prop.name
	}
}