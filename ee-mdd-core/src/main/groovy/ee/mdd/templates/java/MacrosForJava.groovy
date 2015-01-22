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

      template('propsMember', body: '''<% item.props.each { prop -> if(!prop.typeEntity) { %><% if(prop.multi) { %>
  protected ${c.name('List')}<${prop.type.name}> $prop.uncap; <% } else { %>
  protected ${prop.type.name} $prop.uncap;<% } } else if (prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %><% if(relationIdProp) { %><% if(relationIdProp.multi) { %>
  protected ${c.name('List')}<${relationIdProp.type.name}< ${prop.uncap}${relationIdProp.cap};<% } else { %>
  protected ${relationIdProp.type.name} ${prop.uncap}${relationIdProp.cap};<% } } } } %>
''')

      template('jpaPropsMember', body: '''<% item.props.each { prop -> c.prop = prop; if(!prop.primaryKey) { %>${macros.generate('metaAtrributesProp', c)}<% if (prop.multi) { %>
  protected ${c.name('List')}<${prop.typeEjbMember}> $prop.uncap;<% } else { %>
  protected ${prop.typeEjbMember} $prop.uncap;<% } } } %>
''')

      template('idProp', body: '''<% def idProp = c.item.idProp; if(idProp && !c.item.virtual) { c.prop = idProp%>${macros.generate('metaAtrributesProp', c)}<% if (idProp.multi) { %>
  protected ${c.name('List')}<${idProp.typeEjbMember}> $idProp.uncap;<% } else { %>
  protected ${idProp.typeEjbMember} $idProp.uncap;<% } }%>
''')

      template('multiSuperProps', body: '''<% def props = c.item.multiSuperProps; if(props) { props.each { prop -> if(!prop.primaryKey) { c.prop = prop%>${macros.generate('metaAtrributesProp', c)}
  protected<% if(prop.typeEjb) { %> ${c.name('List')}<${prop.type.n.cap.entity}><% } else  { %> ${c.name('List')}<${prop.type.cap}><% } %> $prop.uncap;<% } } } %>
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

      template('propGettersIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.readable ) { %>

  ${c.name(prop.type)} $prop.getter;<% } } %>''')

      template('propsSetterIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.writable) { %>
  
  void $prop.setter;<% } } %>''')

      template('relationIdPropGetterIfc', body: ''' item.props.each { prop -> if(prop.readable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>
  <% if (relationIdProp.multi) { %>${c.name('List')}<$prop.relTypeEjb><% } else { %>$prop.relTypeEjb<% } %> ${prop.name}${relationIdProp.cap};
''')

      template('propGetters', body: '''<% item.props.each { prop -> if (prop.readable && !prop.typeEntity) { %>
  @Override
  public <% if(prop.multi) { %>${c.name('List')}<$prop.relTypeEjb><% } else { %>${prop.relTypeEjb}<% } %> $prop.getter {
    return $prop.uncap;
  }<% } else if(prop.readable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %><% if (relationIdProp) { %>

  @Override
  public <% if(relationIdProp.multi) { %>${c.name('List')}<relationIdProp.relTypeEjb><% } else { %>${relationIdProp.relTypeEjb}<% } %> get${prop.cap}${relationIdProp.cap}() {
    return ${prop.name}${relationIdProp.cap};
  }
<% } } }%>''')

      template('propsSetter', body: '''<% item.props.each { prop -> if (prop.writable && !prop.typeEntity) { %>
  @Override
  public void set${prop.cap}($prop.relTypeEjb $prop.name) {
    this.$prop.uncap = $prop.uncap; 
  }<% } else if (prop.writable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %><% if (relationIdProp) { %>

  @Override
  public void set${prop.cap}${relationIdProp.cap}($relationIdProp.relTypeEjb ${prop.name}${relationIdProp.cap}) {
    this.${prop.name}${relationIdProp.cap} = ${prop.name}${relationIdProp.cap};
  }
<% } } } %>''')

      template('jpaPropGetters', body: '''<% item.props.each { prop -> if (!item.virtual || (item.virtual && !prop.elementCollection)) { if (prop.readable && !prop.primaryKey) {%>
  ${!prop.typeEntity?'@Override':''}<% if(prop.multi && prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %><% if(item.virtual && prop.multi) { %>
  public abstract ${c.name('List')}<${prop.relTypeEjb}> $prop.getter;<% } else { %> 
  <% if(prop.multi) { %>${c.name('List')}<$prop.relTypeEjb><% } else { %>${prop.relTypeEjb}<% } %> $prop.getter { <% if(prop.multi) { %>
    if($prop.name == null) {
      $prop.name = new ${c.name('ArrayList')}<>();
    }<% } else if (prop.type.name.startsWith('Map<')) { %>
    if ($prop.name == null) {
      $prop.name = new ${c.name('HashMap')}<>();
    }<% } %>
    return <% if(prop.multi && prop.typeBasicType) {%>(List)<% } %>$prop.uncap; 
  }
  <% } } } }%>
''')

      template('jpaPropSetters', body: '''<% item.props.each { prop -> if (!item.virtual || (item.virtual && !prop.elementCollection)) { if (prop.writable && !prop.primaryKey) {  %><% if(item.virtual && prop.multi) { %>
  public abstract void set${prop.cap}(${c.name('List')}<${prop.relTypeEjb}> $prop.uncap);<% } else if (!prop.multi) { %>
  ${!prop.typeEntity?'@Override':''}
  public void set${prop.cap}(${prop.relTypeEjb} $prop.uncap) { <% if(item.attributeChangeFlag && !prop.ignoreInChangeFlag) { %>
    if (ComparisonUtils.areNotEquals(this.$prop.uncap, $prop.uncap)) {
      this.$prop.name = <% if (prop.typeBasicType) { %>(${prop.typeEjbMember})<% } %>$prop.name;
      this.attributesChanged = true;
    }<% } else { %>
  this.$prop.uncap = <% if (prop.typeBasicType) { %>(${prop.typeEjbMember})<% } %>$prop.uncap;<% } %>
  }<% } else { %>
  <% if (prop.typeBasicType) { %>
  @Override<% if (prop.multi) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } } %>
  public void set${prop.cap}<% if(prop.multi) { %>(${c.name('List')}<${prop.relTypeEjb}><% } else { %>(${prop.relTypeEjb}<% } %> $prop.uncap) {
    this.$prop.uncap = <% if (prop.multi && prop.typeBasicType) { %>(List)<% } else if (prop.typeBasicType) { %>(${prop.typeEjbMember})<% } %>$prop.uncap;<% if (prop.typeEl && prop.type.ordered || (prop.opposite && !prop.opposite.multi)) { %>
    if ($prop.name != null) {<% if (prop.type.ordered) { %>
      long order = 1;
      for ($prop.relTypeEjb child : $prop.name) {
        child.setOrder(order++);<% if(prop.opposite && !prop.opposite.multi) { %>
        child.set$prop.opposite.cap(${item.base ? "($item.n.cap.Entity)" : ''}this);<% } %>
      }<% } else if (prop.opposite && !prop.opposite.multi) { %>
      for ($prop.relTypeEjb child : $prop.name) {
        child.set${prop.opposite.cap}(${item.base ? "($item.n.cap.Entity)" : ''}this);
      }<% } %>
    }<% } %>
  }<% } %><% if (prop.typeEl && prop.multi) { %>

  public boolean addTo${prop.cap}($prop.relTypeEjb child) {<% if (prop.opposite) { if (!prop.opposite.multi) { %>
    child.set${prop.opposite.cap}(${item.base ? "($item.n.cap.Entity)" : ''}this);<% } else { %>
    child.get${prop.opposite.cap}.add(${item.base ? "($item.n.cap.Entity)" : ''}this);<% } } %><% if (prop.typeEl && prop.type.ordered) { %>
    child.setOrder(Long.valueOf(${prop.getter}.size() + 1));<% } %>
    return ${prop.getter}.add(child);
 }

  public boolean removeFrom${prop.cap}($prop.relTypeEjb child) {<% if(prop.opposite) { if(!prop.opposite.multi) { %>
    child.set${prop.opposite.cap}(null);<% } else { %>
    child.get${prop.opposite.cap}.remove(${item.base ? "($item.n.cap.Entity)" : ''}this);<% } } %>
    return ${prop.getter}.remove(child);
  }<% } } } } %>
''')

      template('jpaMultiSuperPropGetters', body: '''<% item.multiSuperProps.each { prop -> if(prop.readable && !prop.primaryKey) { if(!c.enumType) { %>
  @Override<% } %><% if(prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${c.name('List')}<${prop.relTypeEjb}> $prop.getter {
    if($prop.name == null) {
      $prop.name = new ArrayList<>();
    }
    return <% if(prop.typeBasicType) {%>(List)<% } %>$prop.uncap;
  }
<% } } %>''')

      template('jpaMultiSuperPropSetters', body: '''<% item.multiSuperProps.each { prop -> if (prop.writable && !prop.primaryKey) { if(!prop.opposite) { %>
  @Override<% if(prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public void set$prop.cap(${prop.relTypeEjb} $prop.uncap) {
    this.$prop.uncap = <%if(prop.typeBasicType) { %>(List)<% } %> $prop.uncap; 
  }
  <% } else { %>
  @Override
  public void set$prop.cap(${prop.relTypeEjb} $prop.uncap) {
    this.$prop.uncap = $prop.uncap;
    if ($prop.uncap != null) {
      for ($prop.relTypeEjb child : $prop.uncap) {
        child.set$prop.opposite.cap(${c.item.base ? "($item.n.cap.entity)" : ''}this);
      }
    }
  }<% } } } %>''')

      template('idPropGetter', body : '''<% def idProp = c.item.idProp; if(idProp) { %>
  @Override
  public <% if(idProp.multi) { %>${c.name('List')}<$idProp.relTypeEjb><% } else { %>${idProp.relTypeEjb}<% } %> $idProp.getter {
    return $idProp.uncap;//idPropGetter
  }<% } %>
''')

      template('idPropSetter', body: '''<% def idProp = c.item.idProp; if(idProp) { %>
  @Override
  public void set${idProp.cap}(<% if(idProp.multi) { %>${c.name('List')}<$idProp.relTypeEjb><% } else { %>${idProp.relTypeEjb}<% } %> $idProp.uncap) {
    this.$idProp.uncap = $idProp.uncap;
  }<% } %>''')

      template('relationIdPropGetter', body: '''<% item.props.each { prop -> if(prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>
  @Override
  public <% if(relationIdProp.multi) { %>${c.name('List')}<$relationIdProp.relTypeEjb><% } else { %>${relationIdProp.relTypeEjb}<% } %> get${prop.cap}${relationIdProp.cap}() {
    return ${prop.uncap} != null ? ${prop.uncap}.get${relationIdProp.cap}() : null;
  }<% } } %>
 ''')

      template('relationIdPropSetter', body: '''<% item.props.each { prop -> if(prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>
  @Override
  public void set${prop.cap}${relationIdProp.cap}(<% if(relationIdProp.multi) { %>${c.name('List')}<$relationIdProp.relTypeEjb><% } else { %>${relationIdProp.relTypeEjb}<% } %> ${prop.uncap}${relationIdProp.cap}) {
    //nothing, because object based;
  }<% } } %>
''')

      template('methods', body: '''<% item.operations.each { op -> String ret = ''; if (op.body) { %><% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  @Override
  public ${op.ret ? op.ret.name : 'void'} $op.name($op.signature) {
  ${op.resolveBody(c)}
  }<% } } %>
''')

      template('implOperations', body: ''' <% item.operations.each { op -> if (!op.body && !op.provided) { %>
  @Override<% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${op.ret ? op.ret.name : 'void'} $op.name($op.signature) {
    //TODO to implement<% if (op.typeBoolean) { %>
    return false;<% } else if (op.ret) { %>
    return null; <% } %>
  }<% } } %>''')

      template('ifcMethods', body: '''
  
<% def separator = ', '; c.item.operations.each { op -> String ret = ''; if (op.ret) {%>
public ${op.ret.cap} <%} else {%>  public void <% } %>$op.cap(<% op.params.each { ret += separator+"${c.name(it.type)}"+' '+it.uncap}%>${ret-separator}); 
<% } %>''')

      template('ifc', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
${item.description?"/*** $item.description */":''}
public interface $c.className extends<% if (item.superUnit) { %> ${item.superUnit.name} <% } else { %> ${c.name('Serializable')}<% } %> 
  /** A unique URI prefix for RESTful services and multi-language support */
  public static final string URI_PREFIX = "$item.uri";
{${macros.generate('propGettersIfc', c)}${macros.generate('propsSetterIfc', c)}
}
//ifc''')

      template('ifcExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %><% if (!c.metas) { c.metas = item.metas } %>{{imports}}
public interface $c.className extends <% if (item.superUnit) {%>$item.superUnit.cap<% } else { %>${c.className}Base<% } %> { <% if (item.superUnit) { %>
${macros.generate('propGettersIfc', c)}${macros.generate('propsSetterIfc', c)}<% } %>${macros.generate('ifcMethods', c)}
}
//ifcExtends''')


      template('implEntity', body: '''<% if (!c.className) { c.className = item.cap.implBase } %>{{imports}}
public ${c.virtual || c.base ? 'abstract ' : ''}class $c.className<% if(c.item.superUnit) { %> extends $c.item.superUnit.n.cap.impl <% } %> implements ${c.name(c.item)} {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>${macros.generate('propsMember', c)}${macros.generate('propGetters', c)}${macros.generate('propsSetter', c)}${macros.generate('methods', c)}${macros.generate('propsToString', c)}${macros.generate('hashCodeAndEqualsEntity', c)}
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
  ${c.item.jpaConstants(c)}${macros.generate('idProp', c)}${macros.generate('multiSuperProps', c)}${macros.generate('jpaPropsMember', c)}${macros.generate('baseConstructor', c)}
  ${macros.generate('idPropGetter', c)}${macros.generate('idPropSetter', c)}
  ${macros.generate('jpaMultiSuperPropGetters', c)}${macros.generate('jpaMultiSuperPropSetters', c)}
  ${macros.generate('jpaPropGetters', c)}${macros.generate('jpaPropSetters', c)}
  ${macros.generate('relationIdPropGetter', c)}${macros.generate('relationIdPropSetter', c)}
  ${macros.generate('labelBody',c)}${macros.generate('attributesChanged', c)}
  ${macros.generate('methods', c)}${macros.generate('propsToString', c)}
  ${macros.generate('hashCodeAndEqualsEntity', c)}
}
//ejbEntity''')

      template('ejbEntityExtends', body: ''' <% c.src = true %><% if(!c.className) { c.className = item.n.cap.entity } %>{{imports}}${macros.generate('metaAttributesEntity', c)}
public ${c.item.virtual?'abstract':''} class $c.className extends ${item.n.cap.baseEntity} {
  private static final long serialVersionUID = 1L;      
  ${macros.generate('superConstructor', c)}
  ${macros.generate('implOperations', c)}
}
//ejbEntityExtends''')




      //            template('implBasicType', body: '''
      //      ''')
      //
      //            template('implBasicTypeExtends', body: '''
      //      ''')

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

      template('labelBody', body: '''<% if(item.labelBody) { %>
  @Override
  public String naturalKey() {
    return $item.labelBody;
  }<% } %>
''')
      template('attributesChanged', body: '''<% if(item.attributeChangeFlag) { %>
  public boolean attributesChanged() {
    return this.attributesChanged;
  }

  public void clearAttributesChanged() {
    this.attributesChanged = false;
  }<% } %>
''')

      template('propsToString', body: '''<% def idProp = item.idProp; def props = item.props.findAll{!it.primaryKey}; %>
  @Override
  protected void fillToString(StringBuffer b) {
    super.fillToString(b);<% if (idProp && !item.virtual) { %>
    b.append("$idProp.name=").append($idProp.name).append(SEPARATOR);<% } %><% props.each { prop -> if(!prop.typeEntity && prop.type.cap.matches('(String|Boolean|Long|Integer)')) { %><% if (prop.multi) { %>
    b.append("$prop.name=").append($prop.getter).append(SEPARATOR);<% } else { %>
    b.append("$prop.name=").append($prop.name).append(SEPARATOR);<% } %><% } }%>
  }
''')

      template('hashCodeAndEqualsEntity', body: '''<% def className = item.genericsName; if(item.propsForHashCode) { %>
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ${item.superUnit ? 'super.hashCode()' : '1'};<% item.propsForHashCode.each { prop -> def propAccess = prop.primaryKey ? 'getId()' : prop.name; %>
    result = prime * result + (($propAccess == null) ? 0 : ${propAccess}.hashCode());<% } %>
  }

  @Override<% if (item.generic) { %>
  @SuppressWarnings("unchecked")<% } %>
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (this == obj)
      return true;<% if(item.virtual) { %>
    if (!super.equals(obj))
      return false;<% } %>
    if (getClass() != obj.getClass())
      return false;
    $className other = (${className}) obj;<% item.propsForHashCode.each { prop -> def propAccess = prop.primaryKey ? 'getId()' : prop.name; %>
    if (${propAccess} == null) {
      if (other.${propAccess} != null)
        return false;
    } else if (!${propAccess}.equals(other.${propAccess}))
      return false;<% } %>
    return true;
  }<% } %>
''')


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
