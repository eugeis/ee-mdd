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
package ee.mdd.templates.java

import javafx.beans.binding.*
import javafx.beans.property.*
import javafx.collections.*
import javafx.scene.control.*
import ee.mdd.generator.Context
import ee.mdd.model.Element
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.Literal
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Prop



/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class EnhancerForJava {
  private static final Object[] EMPTY_ARGUMENTS = {}

  static void enhanceClasses() {
    ExpandoMetaClass.enableGlobally()

    def properties = Collections.synchronizedMap([:])
    Map<String, String> typeToTestValue = [String: '\"TestString\"', Long: 'Long.value(1)', long: '1L',
      Integer: 'Integer.value(1)', int: '1', Date: 'new Date()', boolean: 'true', Boolean: 'Boolean.TRUE']

    Element.metaClass {
    }

    Literal.metaClass {

      getIs << {
        ->
        def key = System.identityHashCode(delegate) + 'is'
        if(!properties.containsKey(key)) {
          properties[key] = "is$delegate.cap()"
        }
        properties[key]
      }
    }

    Prop.metaClass {

      getGetter << {
        ->
        def key = System.identityHashCode(delegate) + 'getter'
        if(!properties.containsKey(key)) {
          properties[key] = "get$delegate.cap()"
        }
        properties[key]
      }

      getSetter << {
        ->
        def key = System.identityHashCode(delegate) + 'setter'
        if(!properties.containsKey(key)) {
          properties[key] = "set$delegate.cap($delegate.type.name $delegate.uncap)"
        }
        properties[key]
      }

      getCall << {
        ->
        def key = System.identityHashCode(delegate) + 'call'
        if(!properties.containsKey(key)) {
          properties[key] = "set$delegate.cap($delegate.uncap)"
        }
        properties[key]
      }

      getTestable << {
        ->
        def key = System.identityHashCode(delegate) + 'testable'
        if(!properties.containsKey(key)) {
          properties[key] = typeToTestValue.containsKey(delegate.type.name)
        }
        properties[key]
      }

      getTestValue << {
        ->
        def key = System.identityHashCode(delegate) + 'testValue'
        if(!properties.containsKey(key)) {
          properties[key] = typeToTestValue.get(delegate.type.name)
        }
        properties[key]
      }
    }

    LogicUnit.metaClass {

      getCall << {
        ->
        def key = System.identityHashCode(delegate) + 'call'
        def customParams = delegate.params.findAll { it.value == null }
        if(!properties.containsKey(key)) {
          properties[key] = customParams.collect { it.uncap }.join(', ')
        }
        properties[key]
      }

      getSignature << {
        ->
        def key = System.identityHashCode(delegate) + 'signature'
        def customParams = delegate.params.findAll { it.value == null }
        if(!properties.containsKey(key)) {
          properties[key] = customParams.collect { it.signature }.join(', ')
        }
        properties[key]
      }

      signature << { Context c ->
        //register usage of the type, in order to calculate imports, etc.
        delegate.params.each { c.name(it.type) }
        delegate.signature
      }
    }

    Attribute.metaClass {

      getSignature << {
        ->
        def key = System.identityHashCode(delegate) + 'signature'
        if(!properties.containsKey(key)) {
          //use 'name', because of primitive types
          properties[key] = "$delegate.type.name $delegate.uncap"
        }
        properties[key]
      }

      signature << { Context c ->
        //register usage of the type, in order to calculate imports, etc.
        c.name(delegate.type)
        delegate.signature
      }
    }
  }
}
