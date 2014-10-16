

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

import ee.mdd.model.Element
import ee.mdd.model.component.Namespace


/**
 *
 * @author Eugen Eisler
 */
class AbstractFactoryBuilder extends FactoryBuilderSupport {
  protected  Map<String, Object> storedContinuationData
  RefAttributesResolver refAttrResolver
  AttributeToObject attributeToObject

  AbstractFactoryBuilder(Closure postInstantiateDelegate = null, boolean init = true) {
    super(init)

    attributeToObject = new AttributeToObject()
    attributeToObject.add('namespace', new MddFactory(beanClass: Namespace))
    addAttributeDelegate(attributeToObject.attributteDelegate)

    refAttrResolver = new RefAttributesResolver()
    addAttributeDelegate(refAttrResolver.attributteDelegate)
    addPostInstantiateDelegate(refAttrResolver.postInstantiateDelegate)
    addPostNodeCompletionDelegate(refAttrResolver.postNodeCompletionDelegate)

    if(postInstantiateDelegate) {
      addPostInstantiateDelegate(postInstantiateDelegate)
    }
  }

  def propertyMissing(String name) {
    throw new MissingPropertyException("Unrecognized property: ${name}", name, this.class)
  }

  public void registerBeanFactory(String nodeName, String groupName, Class beanClass) {
  }


  protected Factory resolveFactory(Object name, Map attributes, Object value) {
    checkFactoryAllowed(name)
    super.resolveFactory(name, attributes, value)
  }

  @Override
  protected Object createNode(Object name, Map attributes, Object value) {
    def node
    if(attributes == null && value != null) {
      node = super.createNode(name, [:], value)
    } else {
      node = super.createNode(name, attributes, value)
    }

    //no parent => root element, call init()
    Object current = getParent()
    if (current == null && node instanceof Element) {
      node.init()
    }
    node
  }

  Object getParent() {
    getProxyBuilder().getCurrent()
  }

  boolean checkFactoryAllowed(Object name) {
    MddFactory parent = getParentFactory()
    if(parent != null && !parent.isChildAllowed(name)) {
      throw new RuntimeException("Child element '$name' in not allowed for parent '$parent'.")
    }
    true
  }
}

