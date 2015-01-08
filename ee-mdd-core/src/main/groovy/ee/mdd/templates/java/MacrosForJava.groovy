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

      template('propsMember', body: '''<% item.props.each { prop -> c.prop = prop %>
  protected ${prop.computedType} $prop.uncap;<% } %>
''')

      template('jpaPropsMember', body: '''<% item.props.each { prop -> c.prop = prop; if(!prop.primaryKey) { %>${macros.generate('metaAtrributesProp', c)}
  protected ${prop.computedTypeEjbMember} $prop.uncap;
  <% } } %>
''')

      template('idProp', body: '''<% def idProp = c.item.idProp; if(idProp && !c.item.virtual) { c.prop = idProp%>${macros.generate('metaAtrributesProp', c)}
  protected ${idProp.computedTypeEjb} $idProp.uncap;<% } %>
''')

      template('multiSuperProps', body: '''<% def props = c.item.multiSuperProps; if(props) { props.each { prop -> c.prop = prop%>${macros.generate('metaAtrributesProp', c)}
  protected<% if(prop.isEjbProp(c)) { %> ${c.name('List')}<${prop.type.n.cap.entity}><% } else  { %> ${c.name('List')}<${prop.type.cap}><% } %> $prop.uncap;<% } } %>
''')

      template('propGettersIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.readable ) { %>

  ${c.name(prop.type)} $prop.getter;<% } } %>''')


      template('propGetters', body: '''<% item.props.each { prop -> %>
  public ${prop.computedType} $prop.getter {
    return $prop.uncap;
  }<% } %>
 ''')

      template('jpaPropGetters', body: '''<% item.props.each { prop -> if (prop.readable && !prop.primaryKey) {%><% if(!c.enumType) { %>
  @Override<% } %><% if(prop.multi && prop.isBasicTypeProp(c)) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${prop.computedTypeEjb} $prop.getter { <% if(prop.multi) { %>
    if($prop.name == null) {
      $prop.name = new ArrayList<>();
    }<% } else if (prop.type.name.startsWith('Map<')) { %>
    if ($prop.name == null) {
      $prop.name = new HashMap<>();
    }
    <% } %>
    return <% if(prop.multi && prop.isBasicTypeProp(c)) {%>(List)<% } %>$prop.uncap; 
  }
<% } } %>''')

      template('jpaMultiSuperPropGetters', body: '''<% item.multiSuperProps.each { prop -> if(prop.readable) { if(!c.enumType) { %>
  @Override<% } %><% if(prop.isBasicTypeProp(c)) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${prop.computedTypeEjb} $prop.getter {
    if($prop.name == null) {
      $prop.name = new ArrayList<>();
    }
    return <% if(prop.isBasicTypeProp(c)) {%>(List)<% } %>$prop.uncap;
  }
<% } } %>''')

      template('idPropGetter', body : '''<% def idProp = c.item.idProp; if(idProp) { %>
  //@Override
  public ${idProp.computedTypeEjb} $idProp.getter {
    return $idProp.uncap;
  }<% } %>
''')

      template('propsSetterIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.writable) { %>
  
  void $prop.setter;<% } } %>''')

      template('propsSetter', body: '''<% item.props.each { prop -> if (prop.writable) {%>
  @Override
  public void $prop.setter {
    this.$prop.uncap = $prop.uncap; 
  }<% } } %>''')

      //      template('jpaMultiSuperPropSetters', body: '''<% item.multiSuperProps.each { prop -> if (prop.writable) { if(prop.opposite) { %>
      //  @Override
      //
      //''')

      template('idPropSetter', body: '''<% def idProp = c.item.idProp; if(idProp) { %>
  //@Override
  public void set${idProp.cap}(${idProp.computedTypeEjb} $idProp.uncap) {
    this.$idProp.uncap = $idProp.uncap;
  }<% } %>
''')

      template('defaultConstructor', body:'''
  public $className() {
  }''')

      template('baseConstructor', body: '''<% item.constructors.each { constr -> %>
  
  public $className(${constr.signature(c)}) {<% constr.params.each { param -> if (param.value!=null) { %>
    ${param.resolveValue(c)}<% } else if (param.prop!=null) { %>
    this.$param.prop.uncap = $param.prop.uncap;<% } }%>
  }<% } %>''')

      template('superConstructor', body: ''' <% item.constructors.each { constr -> %>

  public $className(${constr.signature(c)}) {
    super($constr.call);
  }<% } %>''')

      template('enumConstructor', body: ''' <% item.constructors.each { constr -> %>

  private $className(${constr.signature(c)}) {<% constr.params.each { if(it.prop!=null) { if (it.value!=null) { %>
    this.$it.prop.uncap = $it.value;<% } else { %>
    this.$it.prop.uncap = $it.prop.uncap;<% } } } %>
  }<% } %>
''')

      template('methods', body: '''<% def separator = ', '; c.item.operations.each { op -> String ret = ''; if (op.ret) { %>
  
  @Override
  public ${op.ret.cap}<%} else {%>
  @Override 
  public void <% } %>$op.cap(<% op.params.each {ret += separator+it.type.name+' '+it.uncap}%>${ret-separator}) {
  ${op.resolveBody(c)}
  }<% } %> ''')

      template('ifcMethods', body: '''
  
<% def separator = ', '; c.item.operations.each { op -> String ret = ''; if (op.ret) {%>
public ${op.ret.cap} <%} else {%>  public void <% } %>$op.cap(<% op.params.each { ret += separator+"${c.name(it.type)}"+' '+it.uncap}%>${ret-separator}); 
<% } %>''')

      template('ifc', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
public interface $c.className<% if (c.serializable) { %> extends ${c.name('Serializable')}<% } %> {${macros.generate('propGettersIfc', c)}${macros.generate('propsSetterIfc', c)}
}
//ifc''')

      template('ifcExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %><% if (!c.metas) { c.metas = item.metas } %><% c.src = true %>{{imports}}
public interface $c.className extends <% if (item.superUnit) {%>$item.superUnit.cap<% } else { %>${c.className}Base<% } %> { <% if (item.superUnit) { %>
${macros.generate('propGettersIfc', c)}${macros.generate('propsSetterIfc', c)}<% } %>${macros.generate('ifcMethods', c)}
}
//ifcExtends''')

      template('implEntity', body: '''<% if (!c.className) { c.className = item.cap.implBase } %>{{imports}}${macros.generate('metaAttributesEntity', c)}
public ${c.virtual || c.base ? 'abstract ' : ''}class $c.className<% if(c.item.superUnit) { %> extends $c.item.superUnit.n.cap.impl <% } %> implements ${c.name(c.item)} {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
  ${macros.generate('propsMember', c)}${macros.generate('baseConstructor', c)}${macros.generate('jpaPropGetters', c)}${macros.generate('propsSetter', c)}${macros.generate('methods', c)}
}
//implEntity''')

      template('implEntityExtends', body: '''<% c.src = true; c.virtual = false; %><% if (!c.className) { c.className = item.n.cap.impl } %>{{imports}}
public ${c.item.virtual?'abstract':''} class $c.className extends ${c.className}Base {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
}
//implEntityExtends''')

      template('ejbEntity', body: '''<% def superUnit = c.item.superUnit; if(!c.className) { c.className = item.n.cap.entity } %>{{imports}}${macros.generate('metaAttributesEntity', c)}${macros.generate('jpaMetasEntity', c)}
public ${c.virtual || c.base ? 'abstract' : ''} class $c.className<% if(superUnit) { %> extends ${superUnit.n.cap.entity}<% } %> implements ${c.name(c.item.cap)} {
  private static final long serialVersionUID = 1L;
  <% if(c.item.attributeChangeFlag) {%>@Transient
  private transient boolean attributesChanged = false;<% } %>
  ${c.item.jpaConstants(c)}${macros.generate('idProp', c)}${macros.generate('multiSuperProps', c)}${macros.generate('jpaPropsMember', c)}${macros.generate('baseConstructor', c)}${macros.generate('idPropGetter', c)}${macros.generate('idPropSetter', c)}${macros.generate('jpaMultiSuperPropGetters', c)}${macros.generate('jpaPropGetters', c)}
  
}
//ejbEntity''')


      //
      //      template('ejbEntityExtends', body: '''<% if(!c.className) { c.className = item.n.cap.bean
      //public ${c.item.virtual?'abstract':''} class $c.className extends ${c.className}
      //''')
      //
      //      template('implBasicType', body: '''
      //''')
      //
      //      template('implBasicTypeExtends', body: '''
      //''')



      template('enum', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
public enum $c.className {<% def last = item.literals.last(); item.literals.each { lit -> %><% if(!lit.body) { %>
  $lit.underscored${lit == last ? ';' : ','}<% } else { %>$lit.underscored($lit.body)${lit == last ? ';' : ','}<% } } %>
  ${macros.generate('propsMember', c)}${macros.generate('enumConstructor', c)}${macros.generate('propGetters', c)}<% item.literals.each { lit -> %>
  public boolean $lit.is {
    return this == $lit.underscored; 
  }<% } %>
}''')

      template('testProperties', body: '''
  @${c.name('Test')}
  public void testProperties() {<% item.props.each { prop -> %><% if (prop.testable) { %>
    ${c.name(prop.type)} $prop.uncap = $prop.testValue;<% } else { %> 
    ${c.name(prop.type)} $prop.uncap = new ${prop.type.n.cap.impl}();<% } } %><% item.props.each { prop -> %>
    item.$prop.call;<% } %>
    <% item.props.each { prop -> %>
    ${c.name('assertEquals')}($prop.uncap, item.$prop.getter);<% } %>
  }''')

      template('testExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %>{{imports}}
public class $c.className extends ${c.className}Base {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
}''')

      template('test', body: '''<% c.scope='test' %><% if (!c.className) { c.className = item.cap } %><% if (!c.itemInit) { c.itemInit="new $item.n.cap.impl()" } %>{{imports}}
public ${c.virtual ? 'abstract ' : ''}class $c.className {
  protected $item.n.cap.impl item;
  
  @${c.name('Before')}
  public void before$c.className() {
    item = $c.itemInit;
  }
  ${macros.generate('testProperties', c)}${macros.generate('testConstructors', c)}
}''')

      template('testConstructors', body: '''<% item.constructors.each { constr -> %><% def className = item.n.cap.impl %>

  @${c.name('Test')}
  public void testConstructor${constr.paramsName}() { <% def customParams = constr.params.findAll { !it.value && it.prop }; customParams.each { param -> %><% def instance; if (param.prop.testable) { instance = param.prop.testValue } else { instance = 'new '+param.prop.type.n.cap.impl+'()' } %>
     ${c.name(param.type)} $param.uncap = $instance;<% } %><% if (item.superUnit) { %>
     $item.n.cap.impl instance = new $className(${constr.call});<% } else { %>
     ${c.name(item)} instance = new $className(${constr.call});<% } %><% customParams.each { param -> def prop = param.prop; %>
     ${c.name('assertSame')}($param.uncap, instance.$prop.getter);<% } %>
  }<% } %>''')

      template('testEnum', body : ''' <% c.scope='test' %><% if (!c.className) { c.className = item.n.cap.test } %><% def lastLit = '' %>{{imports}}
public class $c.className {
  
  @${c.name('Test')}
  public void testVal() { <% item.literals.each { lit -> lastLit = lit.cap %><% item.props.each { prop -> %>
      ${c.name('assertNotNull')}($c.item.cap.${lit.underscored}.get${prop.cap}());    <% } } %>
  }

  @${c.name('Test')}
  public void testIsLiteral() { <% item.literals.eachWithIndex { lit, i -> %>
    ${c.name('assertTrue')}($c.item.cap.${lit.underscored}.is${lit.cap}()); <% if(lit.cap != lastLit) { %>
    ${c.name('assertFalse')}($c.item.cap.${lit.underscored}.is${item.literals[i+1].cap}());<% } else { %>
    ${c.name('assertFalse')}($c.item.cap.${lit.underscored}.is${item.literals[0].cap}());<% } } %>
  }
}
 ''')

      template('metaAttributesEntity', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.metasForEntity(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
$ret''')

      template('jpaMetasEntity', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.jpaMetasForEntity(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
${ret-newLine}''')

      template('metaAtrributesProp', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.prop.propMapping(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
${ret-newLine}''')

      template('newDate', body: '''<% def ret = 'new Date();' %>$ret''')

      template('testBody', body: '''  int counter = countdown;
    while (counter!=0) {
      System.out.println(counter+"...");
      counter--;
    }
    System.out.println(test);''')
    }
  }
}
