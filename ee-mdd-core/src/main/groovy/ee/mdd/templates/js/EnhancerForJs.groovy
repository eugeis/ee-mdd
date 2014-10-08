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
package ee.mdd.templates.js

import javafx.beans.binding.*
import javafx.beans.property.*
import javafx.collections.*
import javafx.scene.control.*
import ee.mdd.generator.Context
import ee.mdd.model.Element
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.CompilationUnit
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Literal
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Prop



/**
 *
 * @author Eugen Eisler
 */
class EnhancerForJs {
  private static final Object[] EMPTY_ARGUMENTS = {}

  static void enhanceClasses() {
    ExpandoMetaClass.enableGlobally()

    def properties = Collections.synchronizedMap([:])
    Map<String, String> typeToTestValue = [String: '\"TestString\"', Long: '1', long: '1',
      Integer: '1', int: '1', Date: 'new Date()', boolean: 'true', Boolean: 'true']

    Element.metaClass {
    }

    Literal.metaClass {

      getIsCall << {
        ->
        def key = System.identityHashCode(delegate) + 'is'
        if(!properties.containsKey(key)) {
          properties[key] = "is$delegate.cap()"
        }
        properties[key]
      }
	  
	  getIs << {
		  ->
		  def key = System.identityHashCode(delegate) + 'is'
		  if(!properties.containsKey(key)) {
			properties[key] = "is$delegate.cap"
		  }
		  properties[key]
		}

      getInit << {
        ->
        def key = System.identityHashCode(delegate) + 'init'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.body ? "'$delegate.name', $delegate.body" : "$delegate.name"
        }
        properties[key]
      }
    }

    Prop.metaClass {

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
        if(!properties.containsKey(key)) {
          properties[key] = delegate.paramsCustom.collect { it.uncap }.join(', ')
        }
        properties[key]
      }

      getSignature << {
        ->
        def key = System.identityHashCode(delegate) + 'signature'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.paramsCustom.collect { it.signature }.join(', ')
        }
        properties[key]
      }

      signature << { Context c ->
        //register usage of the type, in order to calculate imports, etc.
        delegate.params.each { c.name(it.type) }
        delegate.signature
      }

      getParamsName << {
        ->
        def key = System.identityHashCode(delegate) + 'paramsName'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.paramsCustom.collect { it.cap }.join('And')
        }
        properties[key]
      }
    }

    CompilationUnit.metaClass {

      getSignature << {
        ->
        def key = System.identityHashCode(delegate) + 'signature'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.props.collect { it.signature }.join(', ')
        }
        properties[key]
      }
    }

    EnumType.metaClass {

      getSignature << {
        ->
        def key = System.identityHashCode(delegate) + 'signature'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.props ? 'name, ' + delegate.props.collect { it.signature }.join(', ') : 'name'
        }
        properties[key]
      }
    }

    Attribute.metaClass {

      getSignature << {
        ->
        def key = System.identityHashCode(delegate) + 'signature'
        if(!properties.containsKey(key)) {
          //use 'name', because of primitive types
          properties[key] = "$delegate.uncap"
        }
        properties[key]
      }

      signature << { Context c ->
        //register usage of the type, in order to calculate imports, etc.
        c.name(delegate.type)
        delegate.signature
      }

      resolveValue << { Context c ->
        def ret
        def value = "$delegate.value"
        if (delegate.prop == null) {
          ret = "$delegate.value"
        } else {
          if(delegate.value[0].equals('#')) {
            ret = "this.$delegate.prop.uncap = ${c.macros.generate("${value.substring(1, value.size())}", c)}"
        } else {
          ret = "this.$delegate.prop.uncap = $delegate.value"
        }
      }
      ret
    }

  }
}
}

