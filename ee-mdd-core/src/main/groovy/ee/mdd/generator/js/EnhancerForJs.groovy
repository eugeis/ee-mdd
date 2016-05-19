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
package ee.mdd.generator.js

import ee.mdd.generator.Context
import ee.mdd.model.component.*

/**
 *
 * @author Eugen Eisler
 */
class EnhancerForJs {
    private static final Object[] EMPTY_ARGUMENTS = {}

    static void enhanceClasses() {

        def properties = Collections.synchronizedMap([:])
        Map<String, String> typeToTestValue = [String: '\"TestString\"', Long: '1', long: '1',
                                               Integer: '1', int: '1', Date: 'new Date()', boolean: 'true', Boolean: 'true']


        def meta = Literal.metaClass

        meta.getIsCall = {
            ->
            def key = System.identityHashCode(delegate) + 'is'
            if (!properties.containsKey(key)) {
                properties[key] = "is$delegate.cap()"
            }
            properties[key]
        }

        meta.getIs = {
            ->
            def key = System.identityHashCode(delegate) + 'is'
            if (!properties.containsKey(key)) {
                properties[key] = "is$delegate.cap"
            }
            properties[key]
        }

        meta.getInit = {
            ->
            def key = System.identityHashCode(delegate) + 'init'
            if (!properties.containsKey(key)) {
                properties[key] = delegate.body ? "'$delegate.name', $delegate.body" : "$delegate.name"
            }
            properties[key]
        }


        meta = Prop.metaClass

        meta.getCall = {
            ->
            def key = System.identityHashCode(delegate) + 'call'
            if (!properties.containsKey(key)) {
                properties[key] = "set$delegate.cap($delegate.uncap)"
            }
            properties[key]
        }

        meta.getTestable = {
            ->
            def key = System.identityHashCode(delegate) + 'testable'
            if (!properties.containsKey(key)) {
                properties[key] = typeToTestValue.containsKey(delegate.type.name)
            }
            properties[key]
        }

        meta.getTestValue = {
            ->
            def key = System.identityHashCode(delegate) + 'testValue'
            if (!properties.containsKey(key)) {
                properties[key] = typeToTestValue.get(delegate.type.name)
            }
            properties[key]
        }


        meta = LogicUnit.metaClass

        meta.getCall = {
            ->
            def key = System.identityHashCode(delegate) + 'call'
            if (!properties.containsKey(key)) {
                properties[key] = delegate.paramsCustom.collect { it.uncap }.join(', ')
            }
            properties[key]
        }

        meta.getSignature = {
            ->
            def key = System.identityHashCode(delegate) + 'signature'
            if (!properties.containsKey(key)) {
                properties[key] = delegate.paramsCustom.collect { it.signature }.join(', ')
            }
            properties[key]
        }

        meta.signature = { Context c ->
            //register usage of the type, in order to calculate imports, etc.
            delegate.params.each { c.name(it.type) }
            delegate.signature
        }

        meta.getParamsName = {
            ->
            def key = System.identityHashCode(delegate) + 'paramsName'
            if (!properties.containsKey(key)) {
                properties[key] = delegate.paramsCustom.collect { it.cap }.join('And')
            }
            properties[key]
        }


        meta = CompilationUnit.metaClass

        meta.getSignature = {
            ->
            def key = System.identityHashCode(delegate) + 'signature'
            if (!properties.containsKey(key)) {
                properties[key] = delegate.props.collect { it.signature }.join(', ')
            }
            properties[key]
        }


        meta = EnumType.metaClass

        meta.getSignature = {
            ->
            def key = System.identityHashCode(delegate) + 'signature'
            if (!properties.containsKey(key)) {
                properties[key] = delegate.props ? 'name, ' + delegate.props.collect {
                    it.signature
                }.join(', ') : 'name'
            }
            properties[key]
        }


        meta = Attribute.metaClass

        meta.getSignature = {
            ->
            def key = System.identityHashCode(delegate) + 'signature'
            if (!properties.containsKey(key)) {
                //use 'name', because of primitive types
                properties[key] = "$delegate.uncap"
            }
            properties[key]
        }

        meta.signature = { Context c ->
            //register usage of the type, in order to calculate imports, etc.
            c.name(delegate.type)
            delegate.signature
        }

        meta.resolveValue = { Context c ->
            def ret
            def value = "$delegate.value"
            if (delegate.prop == null) {
                ret = "$delegate.value"
            } else {
                if (delegate.value[0].equals('#')) {
                    ret = "this.$delegate.prop.uncap = ${c.macros.generate("${value.substring(1, value.size())}", c)}"
                } else {
                    ret = "this.$delegate.prop.uncap = $delegate.value"
                }
            }
            ret
        }

    }
}

