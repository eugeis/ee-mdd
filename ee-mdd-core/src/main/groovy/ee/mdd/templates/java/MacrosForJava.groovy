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
  }<% } %>
''')

      template('propGettersIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.readable) { %>
  <% if (prop.description) { %>
  /** $prop.description */<% } %>
  <% if(prop.multi) { %>${c.name('List')}<${c.name(prop.type)}><% } else { %>${c.name(prop.type)}<% } %> $prop.getter;<% } } %> 
''')

      template('propGettersEntityIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.readable && !prop.typeEntity && prop.name != 'id' ) { %>
  <% if (prop.description) { %>
  /** $prop.description */<% } %>
  <% if(prop.multi) { %>${c.name('List')}<${c.name(prop.type)}><% } else { %>${c.name(prop.type)}<% } %> $prop.getter;<% } } %>
''')

      template('propSettersIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.writable) { %>
  void $prop.setter;
<% } } %>''')

      template('propsSettersEntityIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.writable && !prop.typeEntity && prop.name != 'id') { %>
  void $prop.setter;
<% } } %>''')

      template('relationIdPropGetterIfc', body: '''<% item.props.each { prop -> if(prop.readable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>
  <% if (relationIdProp.multi) { %>${c.name('List')}<$relationIdProp.type.name><% } else { %>$relationIdProp.type.name<% } %> get${prop.cap}${relationIdProp.cap}();<% } } %>
''')

      template('relationIdPropSetterIfc', body: '''<% item.props.each { prop -> if(prop.writable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>
  void set${prop.cap}${relationIdProp.cap}<% if(relationIdProp.multi) { %>(${c.name('List')}<$relationIdProp.type.name><% } else { %>($relationIdProp.type.name<% } %> ${prop.uncap}${relationIdProp.cap});<% } } %>
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
<% } } } %>''')

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

      template('propGettersBasicType', body: ''' <% item.props.each { prop -> if (prop.readable) { %>
  @Override<% if (prop.multi && prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public <% if (prop.multi) { %>${c.name('List')}<$prop.relTypeEjb><% } else { %>${prop.relTypeEjb}<% } %> $prop.getter {
    return <% if (prop.multi && prop.typeBasicType) { %>(List)<% } %>$prop.name;
  }<% } } %>

''')

      template('propSettersBasicType', body: ''' <% item.props.each { prop -> if (prop.writable) { %>
  @Override <% if (prop.multi && prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public void set${prop.cap}(<% if (prop.multi) { %>${c.name('List')}<$prop.type.name><% } else { %>$prop.type.name<% } %> $prop.name) {
    this.$prop.name = <% if (prop.multi && prop.typeBasicType) { %>(List)<% } else if (prop.typeBasicType) { %>($prop.typeEjbMember)<% } %>$prop.name;
  }<% } } %>''')


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

      template('interfaceBody', body: '''<% item.operations.each { op -> if (!op.override) { %>
  ${op.description?"   /** $op.description */":''}<% if (op.transactional) { %>
  @${c.name('Transactional')}<% } %>
  ${op.ret ? op.ret.name : 'void'} $op.name($op.signature);
  <% } } %>''')



      template('implOperations', body: ''' <% item.operations.each { op -> if (!op.body && !op.provided && !op.delegateOp) { %>
  @Override<% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${op.ret ? op.ret.name : 'void'} $op.name($op.signature) {
    //TODO to implement <% if (op.typeBoolean) { %>
    return false;<% } else if (op.ret) { %>
    return null; <% } %>
  }<% } } %>''')

      template('implOperationsAndDelegates', body: ''' <% item.operations.each { op -> if(op.body) { %> 
  <% if (c.override) { %>
  @Override<% } %><% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public $op.ret ${op.name}($op.signature) {
    $op.body
  }<% } } %>
''')


      template('ifcMethods', body: '''
  
<% def separator = ', '; c.item.operations.each { op -> String ret = ''; if (op.ret) {%>
public ${op.ret.cap} <%} else {%>  public void <% } %>$op.cap(<% op.params.each { ret += separator+"${c.name(it.type)}"+' '+it.uncap}%>${ret-separator}); 
<% } %>''')

      template('ifc', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
${item.description?"/*** $item.description */":''}
public interface $c.className extends<% if (item.superUnit) { %> ${item.superUnit.name} <% } else { %> ${c.name('Serializable')}<% } %> { 
  /** A unique URI prefix for RESTful services and multi-language support */
  public static final String URI_PREFIX = "${item.getUri()}";
${macros.generate('propGettersEntityIfc', c)}${macros.generate('propsSettersEntityIfc', c)}${macros.generate('relationIdPropGetterIfc', c)}${macros.generate('relationIdPropSetterIfc', c)}${macros.generate('interfaceBody', c)}
}
//ifc''')

      template('ifcExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %><% if (!c.metas) { c.metas = item.metas } %>{{imports}}
/** Base interface for {@link $item.n.cap.base} */
public interface $c.className extends $item.n.cap.base {
}
//ifcExtends''')

      template('ifcBasicType', body: '''<% if(!c.className) { c.className = item.cap } %> {{imports}}
${item.description?"/*** $item.description */":''}
public interface $className extends <% if (item.superUnit) { %>$superUnit.name<% } else { %>${c.name('Serializable')}<% } %> {
${macros.generate('propGettersIfc', c)}${macros.generate('propSettersIfc', c)}${macros.generate('interfaceBody', c)}
}
//ifcBasicType''')

      template('ifcContainerExtends', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
/**
* The container is used to transfer bundled data between server and client.
* <p>
* ${item.description?item.description:''}
* </p>
*/
public interface $c.className extends $item.n.cap.base {
}
//ifcContainerExtends''')


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

      template('ejbBasicType', body: '''<% def superUnit = c.item.superUnit %><% if (!c.className) { c.className = item.beanName } %>
/** JPA representation of {@link $item.name} */${macros.generate('metaAttributesBasicType', c)}
public ${item.base || item.virtual ? 'abstract':''} class $c.className extends <% if (superUnit) { %>superUnit.cap<% } else { %>Base<% } %> implements ${item.name} {
  private static final long serialVersionUID = 1L;
  ${c.item.jpaConstants(c)}${macros.generate('idProp', c)}${macros.generate('jpaPropsMember', c)}${macros.generate('baseConstructor', c)}
  ${macros.generate('idPropGetter', c)}${macros.generate('propGettersBasicType', c)}${macros.generate('propSettersBasicType', c)}
  ${macros.generate('implOperationsAndDelegates', c)}${macros.generate('hashCodeAndEqualsBasicType', c)}
}
//ejbBasicType''')

      template('ejbBasicTypeExtends', body: '''<% c.src = true %><% def superUnit = c.item.superUnit %><% if (!c.className) { c.className = item.beanName } %>
/** JPA representation of {@link $item.name} */
@Embeddable
public class $className extends ${item.n.cap.baseEmbeddable} {
  private static final long serialVersionUID = 1L;
  ${macros.generate('superConstructor', c)}${macros.generate('implOperations', c)}
}
//ejbBasicTypeExtends''')

      template('ejbService', body: ''' <% if (!c.className) { c.className = item.n.cap.serviceBaseBean } %>
/** Ejb implementation of {@link $item.name} */
${macros.generate('metaAttributesService', c)}
//ejbService''')

      template('ejbServiceExtends', body: '''<% if(!c.className) { c.className = item.n.cap.serviceBean } %>
/** Ejb implementation of {@link $item.name} */
${macros.generate('metaAttributesService', c)}
public class $className extends $item.n.cap.baseBean {
${macros.generate('implOperations', c)}
}
//ejbServiceExtends''')

      template('implContainer', body: '''<% if(!c.className) { c.className = item.n.cap.containerBaseImpl }%><% def className = c.className; def entityNames = item.props.collect { it.name } as Set %><%  def oneToManyNoOppositeProps = [:]; def manyToOneProps = [:]; item.props.each { entityProp -> %>
<% def entity = entityProp.type; oneToManyNoOppositeProps[entity] = []; manyToOneProps[entity] = []; entity.propsRecursive.each { prop -> if(prop.type) {
   if (((prop.oneToMany && !prop.opposite) || (prop.mm)) && entityNames.contains(prop.type.name)) { oneToManyNoOppositeProps[entity] << prop }
   if (prop.manyToOne && entityNames.contains(prop.type.name)) { manyToOneProps[entity] << prop } } } } %>
@${c.name('Alternative')}
public class $className extends Base implements $item.name {
  private static final long serialVersionUID = 1L;

  protected ${className}Removes removes = new ${className}Removes();

  protected String source;
  protected Date timestamp; 
  <% c.item.props.each { prop -> %>
  protected $prop.type.name ${prop.type.instancesName}; <% } %><% oneToManyNoOppositeProps.each { entity, linkedProps -> linkedProps.each { prop -> def relationIdProp = prop.type.idProp %>
  protected ${c.name('LinkedObjectCache')}< <% if (entity.idProp.multi) {%>List<entity.idProp.type><% } else {%>entity.idProp.type<% } %>, <% if (relationIdProp.multi) {%>List<relationIdProp.type><% } else {%>relationIdProp.type<% } %>, prop.type.cap> ${entity.uncap}${prop.cap};<% } } %>

  public $className(boolean override, boolean threadSafe) {
    super();
    this.timestamp = TimeUtils.now();
    if(!override) {<% item.props.each { prop -> %>
      this.${prop.type.instancesName} = new ${prop.cap}CacheImpl(threadSafe); <% } %>
    } else {<% item.props.each { prop -> %>
      this.${prop.type.instancesName} = new ${prop.cap}CacheOverride(threadSafe); <% } %>
    }
    <% oneToManyNoOppositeProps.each { entity, linkedProps -> linkedProps.each { prop -> def relationIdProp = prop.type.idProp %>
    this.${entity.uncap}${prop.cap} = new ${c.name('LinkedObjectCache')}<>($prop.type.name);<% } } %>
  }

  public $className(String source, boolean override, boolean threadSafe) {
    this(override, threadSafe);
    this.source = source;
  }

  public $className($item.name sourceContainer, boolean threadSafe) {
    super();
    this.timestamp = TimeUtils.now(); <% item.props.each { prop -> %>
    this.${prop.type.instancesName} = new ${prop.type.n.cap.cacheOverride}(threadSafe);
    ((${prop.type.n.cap.cacheOverride}) this.{$prop.type.instancesName}.setParent(sourceContainer.get${prop.type.name}s());<% } %>
    <% oneToManyNoOppositeProps.each { entity, linkedProps -> linkedProps.each { prop -> def relationIdProp = prop.type.idProp %>
    this.${entity.uncap}${prop.cap} = new LinkedObjectCache<>($prop.type.instancesName);<% } } %>
  }

  public $className(String source, $item.name sourceContainer, boolean threadSafe) {
    this(sourceContainer, threadSafe);  
    this.source = source;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public void setSource(String source) {
    this.source = source;
  }

  @Override
  public Date getTimestamp() {
    return timestamp;
  }

  @Override
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public void resetTempIds() { <% item.props.each { prop -> 
    if(prop.type.idProp) { def type = prop.type.idProp.type; 
    if (type.name.equalsIgnoreCase('Long') || type.name.equalsIgnoreCase('Integer')) { %>
    ${prop.type.instancesName}.resetTempIds();<% } } }%>
  }

  @Override
  public void synchronize($item.name container) {<% item.props.each { prop -> %>
    ${prop.type.instancesName}.synchronize(container.get${prop.type.cap}s());<% } %>
  }

  @Override
  public void synchronize($item.n.cap.removes removes) {<% item.props.each { prop -> %>
    ${prop.type.instancesName}.synchronizeRemoves(removes.get${prop.type.cap}Ids());<% } %>
  }

  @Override
  public ${item.n.cap.delta} synchronizeWithDelta($item.cap container) {
    <% item.props.each { prop -> %>${prop.type.n.cap.deltaCache} ${prop.type.n.uncap.deltaCache} = ${prop.type.namesInstances}.synchronizeWithDelta(container.get${prop.type.cap}s(), container.get${item.n.cap.removes}().get${prop.type.cap}Ids());
    <% } %>
    <% def localDeltas = item.props.collect { prop -> "${prop.type.n.uncap.deltaCache}" }.join(", ") %>
     return new ${item.n.cap.deltaImpl}(${localDeltas});
  }

  @Override
  public void merge($item.cap container) {
    removes.synchronize(container.get${item.n.cap.removes}());

    synchronize(container);
    synchronize(removes);
  }

  @Override
  public ${item.n.cap.removes} get${item.n.cap.removes}() {
    return removes;
  }

  @Override
  public boolean isEmpty() {<%  def sizeQueries = (item.props.collect { prop -> "${prop.type.instancesName}.getSize() == 0" } + item.props.collect { prop -> "removes.get${prop.type.cap}Ids()" }).join(" && ") %>
    return ${sizeQueries};
  }

  @Override
  public void clear() { <% item.props.each { prop -> %>
    ${prop.type.instancesName}.clear();<% } %>
    removes.clear();
  }<% item.props.each { prop -> %>

  @Override
  public ${prop.type.n.cap.cache} get${prop.type.cap}s() {
    return ${prop.type.instancesName};
  }<% } %><% oneToManyNoOppositeProps.each { entity, linkedProps -> linkedProps.each { prop -> def relationIdProp = prop.type.idProp %>

  @Override
  public LinkedObjectCache<${entity.idProp.computedType}, $relationIdProp.computedType, $prop.type.cap> get${entity.name}${prop.cap}() {
    return ${entity.uncap}${prop.cap};
  }<% } } %>
 ${macros.generate('implOperationsAndDelegates', c)}

  @Override
  public $item.name buildChangeSet() {
    $item.name changeSet = new ${item.name}Impl(true);<% item.props.each { prop -> %>
    ((${prop.type.n.cap.cacheOverride}) changeSet.get${prop.type.cap}s()).fillChangeSetFrom(($prop.type.n.cap.cacheOverride) get${prop.type.cap}s());<% } %>
    return changeSet;
  }

  protected <T> T strict(T result, String method, Object... params) {
    return ExceptionUtils.checkIfFound(result, this, method, params);
  }

  @Override
  public void fillToLogString(LogStringBuilder b) {
    super.fillToLogString(b); <% item.props.each { prop -> %>
    b.append("${prop.type.instancesName}", ${prop.type.instancesName}); <% } %>
  }
}//implContainer
''')

      template('enum', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
public enum $c.className {<% def last = item.literals.last(); item.literals.each { lit -> %><% if(!lit.body) { %>
  $lit.underscored${lit == last ? ';' : ','}<% } else { %>$lit.underscored($lit.body)${lit == last ? ';' : ','}<% } } %>
  ${macros.generate('propsMember', c)}${macros.generate('enumConstructor', c)}${macros.generate('propGetters', c)}<% item.literals.each { lit -> %>
  public boolean $lit.is {
    return this == $lit.underscored; 
  }<% } %>
}''')

      template('jmsToCdi', body: '''<% if (!c.className) { c.className = item.n.cap.jmsToCdi } %>{{imports}}
/** Jms to Cdi bridge for '$module.name' */${macros.generate('metaAttributesBridge', c)}
public class $className extends JmsToEventListener {

}

''')

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

      template('metaAttributesBasicType', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.metasForBasicType(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
$ret''')

      template('metaAttributesService', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.metasForService(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
$ret''')

      template('metaAttributesBridge', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.metasForBridge(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
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

      template('hashCodeAndEqualsBasicType', body: '''<% def className = c.className %>
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ${item.superUnit ? 'super.hashCode()' : '1'};<% item.props.each { prop-> %>
    result = prime * result + <% if (prop.primitive && prop.type.name != 'boolean') { %>$prop.name;
        <% } else if (prop.type.name == 'boolean') { %>((${prop.name})?1:0); 
        <% } else { %>(($prop.name == null) ? 0 : ${prop.name}.hashCode());<% } } %>
    return result;
  }
  <% if (item.generic) { %>
  @SuppressWarnings("unchecked")<% } %>
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (this == obj)
      return true;<% if (item.virtual) { %>
    if (!super.equals(obj))
      return false;<% } %>
    if (getClass() != obj.getClass())
      return false;
    $className other = (${className}) obj;<% item.props.each { prop-> if (!prop.primitive) { %>
    if (${prop.name} == null) {
      if (other.${prop.name} != null)
        return false;
    } else if (!${prop.name}.equals(other.${prop.name}))
      return false;<% } else { %>
    if (${prop.name} != other.${prop.name})
      return false;<% } %>
    <% } %>
    return true;
  }''')


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