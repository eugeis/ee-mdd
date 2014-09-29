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

import ee.mdd.builder.GeneratorBuilder
import ee.mdd.generator.CategoryGenerator


/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class MacrosForJava {

  static CategoryGenerator build() {
    new GeneratorBuilder().category ('macros') {

      template('header', body: '''/* EE Software */''')

      template('propsMember', body: '''<% item.props.each { prop -> %>
  protected ${c.name(prop.type)} $prop.uncap;<% } %>''')

      template('propsGetterIfc', body: '''<% item.props.each { prop -> %>

  ${c.name(prop.type)} $prop.getter;<% } %>''')

      template('propsGetter', body: '''<% item.props.each { prop -> %>
  
  public ${c.name(prop.type)} $prop.getter {
    return $prop.uncap; 
  }<% } %>''')

      template('testProperties', body: '''
  @${c.name('Test')}
  public void testProperties() {<% def testableProps = item.props.findAll { it.testable } %><% testableProps.each { prop -> %>
    ${c.name(prop.type)} $prop.uncap = $prop.testValue;<% } %><% testableProps.each { prop -> %>
    item.$prop.call;<% } %>
    <% testableProps.each { prop -> %>
    ${c.name('assertEquals')}($prop.uncap, item.$prop.getter);<% } %>
  }''')

      template('propsSetterIfc', body: '''<% item.props.each { prop -> %>
  
  void $prop.setter;<% } %>''')

      template('propsSetter', body: '''<% item.props.each { prop -> %>

  public void $prop.setter {
    this.$prop.uncap = $prop.uncap; 
  }<% } %>''')

      template('defaultConstructor', body:'''
  public $className() {
  }''')

      template('baseConstructor', body: '''<% item.constructors.each { constr -> %>
  
  public $className(${constr.signature(c)}) {<% constr.params.each { param -> if (param.value!=null) { %>
    ${param.resolveValue(c)}<% } else if (param.prop!=null) { %>
    this.$param.prop.uncap = $param.prop.uncap;<% } } %>
  }<% } %>''')

      template('superConstructor', body: ''' <% item.constructors.each { constr -> %>

  public $className(${constr.signature(c)}) {
    super($constr.call);
  }<% } %>''')

      template('enumConstructor', body: ''' <% item.constructors.each { constr -> %>

    private $className(${constr.signature(c)}) {<% constr.params.each { if(it.prop!=null) { if (it.value!=null) { %>
      this.$it.prop.uncap = $it.value;<% } else { %>
      this.$it.prop.uncap = $it.prop.uncap;<% } } } %>
    }<% } %>''')

      template('ifc', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
public interface $c.className<% if (c.serializable) { %> extends ${c.name('Serializable')}<% } %> {${macros.generate('propsGetterIfc', c)}${macros.generate('propsSetterIfc', c)}
}''')

      template('ifcExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %><% c.src = true %>{{imports}}
public interface $c.className extends ${c.className}Base {
}''')

      template('impl', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
public ${c.virtual ? 'abstract ' : ''}class $c.className implements ${c.name(c.item)} {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
  ${macros.generate('propsMember', c)}${macros.generate('baseConstructor', c)}${macros.generate('propsGetter', c)}${macros.generate('propsSetter', c)}
}''')

      template('implExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %>{{imports}}
public class $c.className extends ${c.className}Base {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
  ${macros.generate('superConstructor', c)}
}''')

      template('testExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %>{{imports}}
public class $c.className extends ${c.className}Base {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
}''')

      template('test', body: '''<% c.scope='test' %><% if (!c.className) { c.className = item.cap } %><% if (!c.itemInit) { c.itemInit="new ${c.name(item)}()" } %>{{imports}}
public ${c.virtual ? 'abstract ' : ''}class $c.className {
  protected ${c.name(item)} item;
  
  @${c.name('Before')}
  public void before$c.className() {
    item = $c.itemInit;
  }
  ${macros.generate('testProperties', c)}${macros.generate('testConstructors', c)}
}''')

      template('testConstructors', body: '''<% item.constructors.each { constr -> %><% def className = item.n.cap.impl %>

  @${c.name('Test')}
  public void testConstructor${constr.paramsName}() { <% def customParams = constr.params.findAll { !it.value && it.prop }; customParams.each { param -> %> 
     ${c.name(param.type)} $param.uncap = ${param.prop.testable ? param.prop.testValue : param.prop.type.n.cap.impl(c.nameRegister)};<% } %>
     ${c.name(item)} instance = new $className(${constr.call});
     <% customParams.each { param -> def prop = param.prop; %>
     ${c.name('assertSame')}($param.uncap, instance.$prop.getter);<% } %>
  }<% } %>''')

      template('enum', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
public enum $c.className {<% def last = item.literals.last(); item.literals.each { lit -> %>
  $lit.underscored($lit.body)${lit == last ? ';' : ','}<% } %>
  ${macros.generate('propsMember', c)}${macros.generate('enumConstructor', c)}${macros.generate('propsGetter', c)}<% item.literals.each { lit -> %>
  
  public boolean $lit.is {
    return this == $lit.underscored; 
  }<% } %>
}''')

      template('newDate', body: '''<% def ret = 'new Date();' %>$ret''')
    }
  }
}
