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

import ee.mdd.model.Composite
import ee.mdd.model.Element


/**
 *
 * @author Eugen Eisler
 */
class RefGlobalResolveHandler implements RefResolveHandler {
  final static SEPARATOR = '.'
  String name
  Class type

  Map<String, Object> refToResolved
  Map<String, List<Closure>> notResolvedRefToSetters = [:]
  Map<String, List<Closure>> notResolvedPathRefToResolvers = [:]

  void onElement(Element el) {
    if (type.isInstance(el)) {
      def ref = el.reference
      //resolve not resolved yet
      if(notResolvedRefToSetters.containsKey(ref)) {
        resolve(ref, el)
      }
    }
  }

  private resolve(String ref, Element el) {
    notResolvedRefToSetters[ref].each { it.call(el) }
    notResolvedRefToSetters.remove(ref)
  }

  void addResolveRequest(String ref, Composite parent, Closure setter) {
    if (refToResolved.containsKey(ref)) {
      setter.call(refToResolved[ref])
    } else {
      if(ref.contains(SEPARATOR)) {
        addResolvePathRequest(ref, parent, setter)
      } else {
        addRequest(ref, setter)
      }
    }
  }

  private addResolvePathRequest(String ref, Composite parent, Closure setter) {
    def parts = ref.split("\\$SEPARATOR")
    String refPart = parts[0]
    if(refToResolved.containsKey(refPart)) {
      resolve(refToResolved[refPart], parts[1..parts.length-1], setter)
    } else {
      def subParts = parts[1..parts.length-1]
      notResolvedRefToSetters[refPart] = { resolve(it, subParts, setter) }
    }
  }

  private void resolve(Element base, List<String> parts, Closure setter) {
    def el = base
    def partsSize = parts.size()
    for(int i = 0; i < partsSize; i++) {
      String refPart = parts[i]
      def resolved = el.resolve(refPart)
      if(resolved) {
        el = resolved
      } else {
        def subParts = (i < partsSize-1) ? parts[i+1..partsSize-1] : []
        notResolvedPathRefToResolvers[refPart] = { resolve(el.resolve(it), subParts, setter) }
        //println "Can not resolve ${refPart} in '$el.reference'"
        el = null
        break
      }
    }

    if(el) {
      setter(el)
    }
  }

  private addRequest(String ref, Closure setter) {
    if (!notResolvedRefToSetters[ref]) {
      notResolvedRefToSetters[ref] = []
    }
    notResolvedRefToSetters[ref] << setter
  }

  @Override
  boolean isResolved() {
    notResolvedRefToSetters.isEmpty() && notResolvedPathRefToResolvers.isEmpty()
  }

  @Override
  void printNotResolved() {

    if(!notResolvedRefToSetters.isEmpty()) {
      println "$name: ${notResolvedRefToSetters.keySet()}"
    }

    if(!notResolvedPathRefToResolvers.isEmpty()) {
      println "$name: ${notResolvedPathRefToResolvers.keySet()}"
    }
  }
}
