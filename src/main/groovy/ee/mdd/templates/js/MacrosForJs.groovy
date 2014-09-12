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

import ee.mdd.builder.GeneratorBuilder
import ee.mdd.generator.CategoryGenerator
import ee.mdd.generator.Context
import ee.mdd.generator.Generator
import ee.mdd.model.component.Entity



/**
 *
 * @author Eugen Eisler
 */
class MacrosForJs {

  static CategoryGenerator build() {
    new GeneratorBuilder().category ('macros') {

      template('header', body: '''/* Eugeis Software */''')

      template('propsMember', body: '''<% item.props.each { prop -> %>
  this.$prop.uncap;<% } %>''')

      template('model', body: '''<% if(!c.className) { c.className=item.name } %>
function $c.className {
  ${macros.generate('propsMember', c)}
}''')

      template('implExtends', body: '''<% c.src=true %><% if(!c.className) { c.className=item.name } %>
function $c.className {
}''')
      
      template('enum', body: '''<% if(!c.className) { c.className=item.name }; def classNameLit = "${c.className}Lit" %>
function $classNameLit(name) {
  this.name = name;
  ${macros.generate('propsMember', c)}<% item.literals.each { lit -> %>
  
  public boolean $lit.is {
    return this == $lit.underscored; 
  }<% } %>
}

var $c.className = {<% def last = item.literals.last(); item.literals.each { lit -> %>
  $lit.underscored: new $classNameLit('$lit.uncap')${lit == last ? '' : ','}<% } %>
}''')
    }
  }
}