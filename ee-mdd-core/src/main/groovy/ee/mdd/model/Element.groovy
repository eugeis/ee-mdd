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
package ee.mdd.model

import java.beans.Introspector

import ee.mdd.model.component.Component



/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Element extends Base {
  String desc
  String uncap, cap, underscored, sqlName, xmlName, xmlValue, description, uri
  boolean xml = true;
  
  Component component() { (this instanceof Component) ? this : parent.component() }

  String getSqlName() {
    if(sqlName == null) {
      sqlName = getUnderscored().replaceAll(/(?<!^)(?<!_)[QEUIOAJY]/, '')
      sqlName = sqlName.replaceAll(/(\w)\1+/, '$1')
    }; sqlName
  }
  
  String getXmlName() {
    if(xmlName == null) {
      xmlName = getName()
    }
    xmlName
  }

  String getUri() {
    if(uri == null && parent) {
      uri = "${parent.getUri()}"
    }; uri
  }

  String getCap() {
    if(cap == null) {
      cap = getName().capitalize()
    }; cap
  }

  String getUncap() {
    if(uncap == null) {
      uncap = decapitalize(getName())
    }; uncap
  }

  String decapitalize(value) {
    Introspector.decapitalize("" + value)
  }

  String getUnderscored() {
    if(underscored == null) {
      underscored = getName().replaceAll(/(\B[A-Z])/,'_$1').toUpperCase()
    }; underscored
  }

  String getReference() {
    getName()
  }

  void fillReference(Map<String, Base> fillRefToResolved) {
    fillRefToResolved[reference] = this
  }
  
  def resolve(ref, type = null, assertNotNull = false) {
    //println "Resolve $ref in '$this, type=$type, assertNotNull=$assertNotNull"
    def ret = null
    if(ref) {
      def tempRet, relRef
      if(ref.startsWith('//')) {
        relRef = ref.substring(2)
        ret = component
      } else if(ref.startsWith('/')) {
        relRef = ref.substring(1)
        ret = module()
      } else {
        relRef = ref
        ret = this
      }
      //println "Resolve $relRef in '$ret"
      def parts = relRef.split('\\.')
      def i = 0

      def chain = [] as Set
      def isTypeOk = { parts.length != i || ( type == null ? true : type.isInstance(it) ) }
      parts.each { String partRef ->
        //println "\nTry to resolve '$partRef' in $ret"
        i++
        if(ret) {
          assert !chain.contains(ret), "Recursion detected at resolving of ref='$ref' in this=$this"
          chain << ret

          tempRet = null
          try {
            tempRet = ret."${partRef}"
            if(!isTypeOk(tempRet)) { tempRet = null }
          } catch(e) {}

          if(!tempRet) {
            //check children
            if(ret.children) { ret = ret.children.find { partRef.equalsIgnoreCase(it.getName()) && isTypeOk(it) }
            } else { ret = null }
            if(assertNotNull) { assert ret != null, "Ref='$ref' can't be resolved in this='$this', partRef='$partRef', ret='$ref'" }
          } else {
            ret = tempRet
          }
        }
      }
    }
    if(assertNotNull) { assert ret != null, "Ref='$ref' can't be resolved in this=$this" }
    //println "Resolved $ref to '$ret' in '$this, type=$type, assertNotNull=$assertNotNull"
    ret
  }
  
}
