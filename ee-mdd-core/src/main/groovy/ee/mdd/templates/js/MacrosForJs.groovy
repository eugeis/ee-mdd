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

import ee.mdd.TemplatesBuilder;
import ee.mdd.generator.TemplateGroup



/**
 *
 * @author Eugen Eisler
 */
class MacrosForJs {

  static TemplateGroup build() {
    new TemplatesBuilder().templates ('macros') {

      template('header', body: '''/* EE Software */''')


      template('propsMember', body: '''<% item.props.each { prop -> %>
  this.$prop.uncap;<% } %>''')

      template('propsInit', body: '''<% item.props.each { prop -> %>
  this.$prop.uncap = $prop.uncap;<% } %>''')

      template('impl', body: '''<% if(!c.className) { c.className=item.name } %>
function $c.className($item.signature) {${macros.generate('propsInit', c)}
}''')

      template('implExtends', body: '''<% c.src=true %><% if(!c.className) { c.className=item.name } %>
function $c.className() {
  // inherit from base class
  ${c.className}.prototype = new <% if (item.superUnit) {%>$item.superUnit.cap()<% } else { %>${c.className}Base()<% } %>;

  // correct the constructor pointer because it points to base class
  ${c.className}.prototype.constructor = ${c.className};
}''')

      template('enum', body: '''<% if(!c.className) { c.className=item.name }; def classNameLit = "${c.className}Lit" %>
function $classNameLit($item.signature) {
  this.name = name;${macros.generate('propsInit', c)}
}
  
${classNameLit}.prototype = {
  constructor: ${classNameLit},<% last = item.literals.last(); item.literals.each { lit -> %>
  
  $lit.is : function() {
    return this === ${c.className}.$lit.underscored; 
  }${lit == last ? '' : ','}<% } %>
}

var $c.className = {<% last = item.literals.last(); item.literals.each { lit -> %>
  $lit.underscored: new $classNameLit($lit.init)${lit == last ? '' : ','}<% } %>
}''')
    }
  }
}