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
package ee.mdd.templates.java.cg

import ee.mdd.model.component.ExternalType
import ee.mdd.model.component.Model
import ee.mdd.model.component.Namespace



/**
 *
 * @author Eugen Eisler
 */
class ExtTypesForJavaCg {
  boolean firstModel = true
  def externalTypeNameToNamespace = ['ClusterSingleton':'com.siemens.ra.cg.pl.env.model']
  def postInstantiateDelegate = { FactoryBuilderSupport builder, Map attributes, Object node ->
    if(firstModel && node instanceof Model) {
      firstModel = false

      externalTypeNameToNamespace.each { name, namespace ->
        ExternalType newExternalType = new ExternalType(name: name)
        newExternalType.namespace = new Namespace(name: namespace, parent: newExternalType).init()
        node.add(newExternalType)
        builder.postInstantiate(null, null, newExternalType)
      }
    }
  }
}
