package ee.mdd.builder

import ee.mdd.model.component.MetaAttribute
import ee.mdd.model.component.Type

class MetaAttributeHolder {
	Map<Type, MetaAttribute> typeToMetaArrtibute = [:]

	MetaAttribute forType(Type type) {
		if(!typeToMetaArrtibute.containsKey(type)) {
			typeToMetaArrtibute[type] = new MetaAttribute(type: type)
		}
		typeToMetaArrtibute[type]
	}
}
