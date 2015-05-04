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

import ee.mdd.model.component.Facet
import groovy.util.logging.Slf4j;


/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class FacetFactory extends FacetAwareFactory {
  String facetName

  Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
    Object ret
    def parent = builder.parent

    String path = Facet.isInstance(parent) ? "$parent.path$parent.name/" : '/'
    if(beanClass) {
      ret = super.newInstance(builder, name, value, attributes)
    } else if(facetName) {
      ret = prepareInstance(builder, value, new Facet(name: facetName))
    } else {
      ret = prepareInstance(builder, value, new Facet())
    }
    ret.path = path
    ret
  }

  Closure childClosure(FactoryBuilderSupport builder, node) {
    Closure ret
    if(facetName) {
      String closureAsText = facets.loadFacetScript(facetName, node.path)
      if(closureAsText) {
        ret = builder.evaluate(closureAsText)
      }
    }
    ret
  }

  @Override
  public String toString() {
    if(beanClass) {
      super.toString()
    } else {
      "${getClass().simpleName} [name=$name]"
    }
  }
}
