/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ee.mdd.builder

import groovy.util.FactoryBuilderSupport;

/**
 *
 * @author Eugen Eisler
 */
class MddFactory extends AbstractFactory {
	Class beanClass
	MddFactory parent
	Set<String> childFactories
	String valueProperty = 'name'

	@Override
	Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
		if (checkValue(name, value)) {
			return value
		}
		def ret = beanClass.newInstance()

		if(BuilderAware.isInstance(ret)) {
			ret.builder = builder
		}

		if(value != null) {
			//attributes[valueProperty] = value
			ret[valueProperty] = value
		}
		ret
	}


	boolean checkValue(Object name, Object value) {
		value != null && beanClass.isAssignableFrom(value.class)
	}

	boolean isChildAllowed(String childFactoryName) {
		( childFactories && childFactories.contains(childFactoryName) ) || ( parent && parent.isChildAllowed(childFactoryName) )
	}

	public void setParent( FactoryBuilderSupport builder, Object parent, Object child ) {
		child.parent = parent
	}

	public void setChild( FactoryBuilderSupport builder, Object parent, Object child ) {
		parent.add(child)
	}

	@Override
	public String toString() {
		return  "${getClass().simpleName} [beanClass=" + beanClass + "]"
	}
}

