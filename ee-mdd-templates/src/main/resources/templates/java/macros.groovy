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

import static ee.mdd.generator.OutputPurpose.*
import static ee.mdd.generator.OutputType.*

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

templates ('macros') {

  useMacros('commonMacros', '/common/macros')

  template('header', body: '''/* EE Software */''')

  template('props', body: '''<% item.props.each { prop -> if(!prop.typeEntity) { %><% if(prop.multi) { %>
  protected ${c.name('List')}<${prop.type.name}> $prop.uncap; <% } else { %>
  protected ${c.name(prop.type.name)} $prop.uncap;<% } } else if (prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %><% if(relationIdProp) { %><% if(relationIdProp.multi) { %>
  protected ${c.name('List')}<${relationIdProp.type.name}> ${prop.uncap}${relationIdProp.cap};<% } else { %>
  protected ${relationIdProp.type.name} ${prop.uncap}${relationIdProp.cap};<% } } } } %>''')
  
  template('propMembers', body:'''<% item.props.each { prop-> %>
  protected ${prop.computedType(c)} $prop.uncap<% if (prop.defaultValue != null) { %> = ${prop.defaultLiteral}<% if (prop.type.name == 'Long' || prop.type.name == 'long') { %>L<% } %><% } %>;<% } %>''')

  template('jpaPropsMember', body: '''<% item.props.each { prop -> c.prop = prop; if(!prop.primaryKey) { %>${macros.generate('metaAttributesProp', c)}<% if (prop.multi) { %>
  protected ${c.name('List')}<${prop.typeEjbMember(c)}> $prop.uncap;<% } else { %>
  protected ${prop.typeEjbMember(c)} $prop.uncap;<% } } } %>''')

  template('refsMember', body: '''<% def members = [] %><% item.operations.each { delegate -> if(!members.contains(delegate.ref.parent)) { %>
  protected ${c.name(delegate.ref.parent.name)} $delegate.ref.parent.uncap;<% } %><% members.add(delegate.ref.parent) %><% } %>''')

  template('idProp', body: '''<% def idProp = c.item.idProp; if(idProp && !c.item.virtual) { c.prop = idProp%>${macros.generate('metaAttributesProp', c)}<% if (idProp.multi) { %>
  protected ${c.name('List')}<${idProp.typeEjbMember(c)}> $idProp.uncap;<% } else { %>
  protected ${idProp.typeEjbMember(c)} $idProp.uncap;<% } }%>''')

  template('multiSuperProps', body: '''<% def props = c.item.multiSuperProps; if(props) { props.each { prop -> if(!prop.primaryKey) { c.prop = prop%>${macros.generate('metaAttributesProp', c)}
  protected<% if(prop.typeEjb) { %> ${c.name('List')}<${prop.type.n.cap.entity}><% } else  { %> ${c.name('List')}<${prop.type.cap}><% } %> $prop.uncap;<% } } } %>''')
  
  template('propsUpdate', body: '''
  public void update($item.cap $item.uncap) {<% item.props.each { prop-> %><% if (item.propSetters) { %>
    $prop.setterMethodName(${item.uncap}.${prop.getter});<% } else { %>
    $prop.uncap = ${item.uncap}.${prop.getter};<% } %><% } %>
  }''')

  template('versionMember', body: '''<% if (!item.superUnit) { %>
  @${c.name('Version')}
  @${c.name('Column')}(name = "VERSION")
  protected Long version;<% } %>''')
  
  template('initFullConstructor', body: '''<% item.props.each { prop -> %>
    this.$prop.name = $prop.name;<% } %>''')

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
  
  template('superclassConstructor', body: '''public $className() {
  super();
 }''')

  template('enumConstructor', body: ''' <% item.constructors.each { constr -> %>

  private $className(${constr.signature(c)}) {<% constr.params.each { if(it.prop!=null) { if (it.value!=null) { %>
    this.$it.prop.uncap = $it.value;<% } else { %>
    this.$it.prop.uncap = $it.prop.uncap;<% } } } %>
  }<% } %>''')

  template('propGettersIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.readable) { %><% if (prop.description) { %>
  /** $prop.description */<% } %>
  <% if(prop.multi) { %>${c.name('List')}<${c.name(prop.type)}><% } else { %>${c.name(prop.type)}<% } %> $prop.getter;<% } } %> ''')

  template('propGettersEntityIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.readable && !prop.typeEntity && prop.name != 'id' ) { %>
  <% if (prop.description) { %>/** $prop.description */<% } %>
  <% if(prop.multi) { %>${c.name('List')}<${c.name(prop.type.name)}><% } else { %>${c.name(prop.type.name)}<% } %> $prop.getter;<% } } %>
''')

  template('propSettersIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.writable) { %>

  void $prop.setter;<% } } %>''')

  template('propsSettersEntityIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.writable && !prop.typeEntity && prop.name != 'id') { %>
  void $prop.setter;<% } } %>''')

  template('relationIdPropGetterIfc', body: '''<% item.props.each { prop -> if(prop.readable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>

  <% if (relationIdProp.multi) { %>${c.name('List')}<$relationIdProp.type.name><% } else { %>$relationIdProp.type.name<% } %> get${prop.cap}${relationIdProp.cap}();<% } } %>''')

  template('relationIdPropSetterIfc', body: '''<% item.props.each { prop -> if(prop.writable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>

  void set${prop.cap}${relationIdProp.cap}<% if(relationIdProp.multi) { %>(${c.name('List')}<$relationIdProp.type.name><% } else { %>($relationIdProp.type.name<% } %> ${prop.uncap}${relationIdProp.cap});<% } } %>''')

  template('propGetters', body: '''<% item.props.each { prop -> if (prop.readable && !prop.typeEntity) { %>

  <% if (!item.typeEnum) { %>@Override<% } %>
  public <% if(prop.multi) { %>${c.name('List')}<${prop.relTypeEjb(c)}><% } else { %>${prop.relTypeEjb(c)}<% } %> $prop.getter {
    return $prop.uncap;
  }<% } else if(prop.readable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %><% if (relationIdProp) { %>

  <% if (!item.typeEnum) { %>@Override<% } %>
  public <% if(relationIdProp.multi) { %>${c.name('List')}<${c.name(relationIdProp.relTypeEjb(c))}><% } else { %>${c.name(relationIdProp.relTypeEjb(c))}<% } %> get${prop.cap}${relationIdProp.cap}() {
    return ${prop.name}${relationIdProp.cap};
  }<% } } } %>''')

  template('propsSetter', body: '''<% item.props.each { prop -> if (prop.writable && !prop.typeEntity) { %>

  ${prop.primaryKey && !item.superUnit ? '':'@Override'}
  public void set${prop.cap}(<% if (prop.multi) { %>${c.name('List')}<${prop.relTypeEjb(c)}><% } else { %>${prop.relTypeEjb(c)}<% } %> $prop.name) {
    this.$prop.uncap = $prop.uncap;
  }<% } else if (prop.writable && prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %><% if (relationIdProp) { %>

  @Override
  public void set${prop.cap}${relationIdProp.cap}(<% if (relationIdProp.multi) { %>${c.name('List')}<${c.name(relationIdProp.relTypeEjb(c))}><% } else { %>${c.name(relationIdProp.relTypeEjb(c))}<% } %> ${prop.name}${relationIdProp.cap}) {
    this.${prop.name}${relationIdProp.cap} = ${prop.name}${relationIdProp.cap};
  }
<% } } } %>''')

  template('propGettersBasicType', body: ''' <% item.props.each { prop -> if (prop.readable) { %>
  @Override<% if (prop.multi && prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public <% if (prop.multi) { %>${c.name('List')}<${prop.relTypeEjb(c)}><% } else { %>${prop.relTypeEjb(c)}<% } %> $prop.getter {
    return <% if (prop.multi && prop.typeBasicType) { %>(List)<% } %>$prop.name;
  }<% } } %>''')

  template('propSettersBasicType', body: ''' <% item.props.each { prop -> if (prop.writable) { %>
  @Override <% if (prop.multi && prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public void set${prop.cap}(<% if (prop.multi) { %>${c.name('List')}<$prop.type.name><% } else { %>$prop.type.name<% } %> $prop.name) {
    this.$prop.name = <% if (prop.multi && prop.typeBasicType) { %>(List)<% } else if (prop.typeBasicType) { %>($prop.typeEjbMember(c))<% } %>$prop.name;
  }<% } } %>''')


  template('jpaPropGetters', body: '''<% item.props.each { prop -> if (!item.virtual || (item.virtual && !prop.elementCollection)) { if (prop.readable && !prop.primaryKey) {%>
  ${!prop.typeEntity?'@Override':''}<% if(prop.multi && prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %><% if(item.virtual && prop.multi) { %>
  public abstract ${c.name('List')}<${prop.relTypeEjb(c)}> $prop.getter;<% } else { %>
  public <% if(prop.multi) { %>${c.name('List')}<${prop.relTypeEjb(c)}><% } else { %>${prop.relTypeEjb(c)}<% } %> $prop.getter { <% if(prop.multi) { %>
    if($prop.name == null) {
      $prop.name = new ${c.name('ArrayList')}<>();
    }<% } else if (prop.type.name.startsWith('Map<')) { %>
    if ($prop.name == null) {
      $prop.name = new ${c.name('HashMap')}<>();
    }<% } %>
    return <% if(prop.multi && prop.typeBasicType) {%>(List)<% } %>$prop.uncap;
  }<% } } } }%>''')

  template('jpaPropSetters', body: '''<% item.props.each { prop -> if (!item.virtual || (item.virtual && !prop.elementCollection)) { if (prop.writable && !prop.primaryKey) {  %><% if(item.virtual && prop.multi) { %>public abstract void set${prop.cap}(${c.name('List')}<${prop.relTypeEjb(c)}> $prop.uncap);<% } else if (!prop.multi) { %>
  ${!prop.typeEntity?'@Override':''}
  public void set${prop.cap}(${prop.relTypeEjb(c)} $prop.uncap) { <% if(item.attributeChangeFlag && !prop.ignoreInChangeFlag) { %>
    if (${c.name('ComparisonUtils')}.areNotEquals(this.$prop.uncap, $prop.uncap)) {
      this.$prop.name = <% if (prop.typeBasicType) { %>(${prop.typeEjbMember(c)})<% } %>$prop.name;
      this.attributesChanged = true;
    }<% } else { %>
  this.$prop.uncap = <% if (prop.typeBasicType) { %>(${prop.typeEjbMember(c)})<% } %>$prop.uncap;<% } %>
  }<% } else { %>
  <% if (prop.typeBasicType) { %>

  @Override<% if (prop.multi) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } } %>
  public void set${prop.cap}<% if(prop.multi) { %>(${c.name('List')}<${prop.relTypeEjb(c)}><% } else { %>(${prop.relTypeEjb(c)}<% } %> $prop.uncap) {
    this.$prop.uncap = <% if (prop.multi && prop.typeBasicType) { %>(List)<% } else if (prop.typeBasicType) { %>(${prop.typeEjbMember(c)})<% } %>$prop.uncap;<% if (prop.typeEl && prop.type.ordered || (prop.opposite && !prop.opposite.multi)) { %>
    if ($prop.name != null) {<% if (prop.type.ordered) { %>
      long order = 1;
      for (${prop.relTypeEjb(c)} child : $prop.name) {
        child.setOrder(order++);<% if(prop.opposite && !prop.opposite.multi) { %>
        child.set$prop.opposite.cap(${item.base ? "($item.n.cap.Entity)" : ''}this);<% } %>
      }<% } else if (prop.opposite && !prop.opposite.multi) { %>
      for (${prop.relTypeEjb(c)} child : $prop.name) {
        child.set${prop.opposite.cap}(${item.base ? "($item.n.cap.Entity)" : ''}this);
      }<% } %>
    }<% } %>
  }<% } %><% if (prop.typeEl && prop.multi) { %>

  public boolean addTo${prop.cap}(${prop.relTypeEjb(c)} child) {<% if (prop.opposite) { if (!prop.opposite.multi) { %>
    child.set${prop.opposite.cap}(${item.base ? "($item.n.cap.Entity)" : ''}this);<% } else { %>
    child.get${prop.opposite.cap}.add(${item.base ? "($item.n.cap.Entity)" : ''}this);<% } } %><% if (prop.typeEl && prop.type.ordered) { %>
    child.setOrder(Long.valueOf(${prop.getter}.size() + 1));<% } %>
    return ${prop.getter}.add(child);
 }

  public boolean removeFrom${prop.cap}(${prop.relTypeEjb(c)} child) {<% if(prop.opposite) { if(!prop.opposite.multi) { %>
    child.set${prop.opposite.cap}(null);<% } else { %>
    child.get${prop.opposite.cap}.remove(${item.base ? "($item.n.cap.Entity)" : ''}this);<% } } %>
    return ${prop.getter}.remove(child);
  }<% } } } } %>''')

  template('jpaMultiSuperPropGetters', body: '''<% item.multiSuperProps.each { prop -> if(prop.readable && !prop.primaryKey) { %>
  <% if(!c.enumType && !item.superUnit.virtual) { %>
  @Override<% } %><% if(prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${c.name('List')}<${prop.relTypeEjb(c)}> $prop.getter {
    if($prop.name == null) {
      $prop.name = new ArrayList<>();
    }
    return <% if(prop.typeBasicType) {%>(List)<% } %>$prop.uncap;
  }<% } } %>''')

  template('jpaMultiSuperPropSetters', body: '''<% item.multiSuperProps.each { prop -> if (prop.writable && !prop.primaryKey) { if(!prop.opposite) { %>
  <% if(!item.superUnit.virtual) { %>
  @Override<% } %><% if(prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public void set$prop.cap(${c.name('List')}<${prop.relTypeEjb(c)}> $prop.uncap) {
    this.$prop.uncap = <%if(prop.typeBasicType) { %>(List)<% } %> $prop.uncap;
  }
  <% } else { %><% if(!item.superUnit.virtual) { %>
  @Override<% } %>
  public void set$prop.cap(${c.name('List')}<${prop.relTypeEjb(c)}> $prop.uncap) {
    this.$prop.uncap = $prop.uncap;
    if ($prop.uncap != null) {
      for ($prop.relTypeEjb(c) child : $prop.uncap) {
        child.set$prop.opposite.cap(${c.item.base ? "($item.n.cap.entity)" : ''}this);
      }
    }
  }<% } } } %>''')

  template('idPropGetter', body : '''<% def idProp = c.item.idProp; if(idProp && !item.virtual) { %>
  @Override
  public <% if(idProp.multi) { %>${c.name('List')}<$idProp.relTypeEjb(c)><% } else { %>${idProp.relTypeEjb(c)}<% } %> $idProp.getter {
    return $idProp.uncap;
  }<% } %>''')

  template('idPropSetter', body: '''<% def idProp = c.item.idProp; if(idProp && !item.virtual) { %>
  ${item.superUnit && !item.superUnit.virtual ? '@Override' : ''}
  public void set${idProp.cap}(<% if(idProp.multi) { %>${c.name('List')}<$idProp.relTypeEjb(c)><% } else { %>${idProp.relTypeEjb(c)}<% } %> $idProp.uncap) {
    this.$idProp.uncap = $idProp.uncap;
  }<% } %>''')

  template('relationIdPropGetter', body: '''<% item.props.each { prop -> if(prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>
  @Override
  public <% if(relationIdProp.multi) { %>${c.name('List')}<$relationIdProp.relTypeEjb(c)><% } else { %>${relationIdProp.relTypeEjb(c)}<% } %> get${prop.cap}${relationIdProp.cap}() {
    return ${prop.uncap} != null ? ${prop.uncap}.get${relationIdProp.cap}() : null;
  }<% } } %>''')

  template('relationIdPropSetter', body: '''<% item.props.each { prop -> if(prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %>
  @Override
  public void set${prop.cap}${relationIdProp.cap}(<% if(relationIdProp.multi) { %>${c.name('List')}<$relationIdProp.relTypeEjb(c)><% } else { %>${relationIdProp.relTypeEjb(c)}<% } %> ${prop.uncap}${relationIdProp.cap}) {
    //nothing, because object based;
  }<% } } %>
''')

  template('getSetVersion', body: '''
  @Override
  public Long getVersion() {
    return version;
  }
  ${item.superUnit ? '@Override':''}
  public void setVersion(Long version) {
    this.version = version;
  }''')
  
  template('initWidgets', body: '''
  @Override
  protected void initWidgets() {
    super.initWidgets();
  }''')
  
  template('onEventSuper', body: '''
  @Override
  public void onEvent(@${c.name('Observes')}(during = ${c.name('AFTER_COMPLETION')}, notifyObserver = ${c.name('IF_EXISTS')})${item.clientCache?' @Internal':''} ${item.cap}Event event) {
    super.onEvent(event);
  }''')
  
  


  template('methods', body: '''<% item.operations.each { op -> String ret = '' %>
  <% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  @Override
  public ${op.ret ? op.ret.name : 'void'} $op.name($op.signature) {
  ${op.resolveBody(c)}
  }<% } %>''')
  
  template('propMethods', body: '''<% item.props.each { prop-> %>
  <% if (prop.description) { %>
  /*** $prop.description */<% } %>
  public $prop.computedType(c) $prop.getter {
    return $prop.uncap;
  }<% if (item.propSetters) { %>
  public void ${prop.getSetter()} {
    this.$prop.uncap= $prop.uncap;
  }<% } %><% } %>''')
  
  template('operationRawType', body: '''<% def op = c.op %>public $op.ret ${op.name}($op.signature) {
    $op.body
  }
''')

  template('interfaceBody', body: '''<% item.operations.each { op -> if (!op.override) { %>
  ${op.description?"   /** $op.description */":''}<% if (op.transactional) { %>@${c.name('Transactional')}<% } %>
  ${op.return} $op.name(${op.signature(c)});<% } } %>''')

  template('interfaceBodyExternal', body: '''<% item.operations.each { op -> if(!op.delegateOp) { %>
  ${op.description?"   /** $op.description */":''}
  ${op.return} ${op.name}(${op.signature(c)});<% } }%><% item.operations.each { op -> if(op.delegateOp) { %>
  ${op.description?"   /** $op.description */":''}
  ${op.ref.return} ${op.ref.name}(${op.ref.signature(c)});<% } } %>''')

  template('implOperations', body: ''' <% item.operations.each { op -> if (!op.body && !op.provided && !op.delegateOp) { %>

  @Override<% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${op.ret ? op.ret.name : 'void'} $op.name($op.signature) {
    //TODO to implement <% if (op.returnTypeBoolean) { %>
    return false;<% } else if (op.ret) { %>
    return null; <% } %>
  }<% } } %>''')

  template('implOperationsAndDelegates', body: ''' <% item.operations.each { op -> if(op.body) { %>
  <% if (c.override) { %>
  @Override<% } %><% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public $op.ret ${op.name}($op.signature) {
    $op.body
  }<% } } %>''')

  template('implControlInjects', body: '''<% item.controls.each { ref -> def uncapName = ref.uncap%>
  @${c.name('Inject')}
  public void set${ref.cap}($ref.cap $uncapName) {
    this.$uncapName = $uncapName;
  }<% } %>''')


  template('ifcMethods', body: '''<% def separator = ', '; c.item.operations.each { op -> String ret = ''; if (op.ret) { %>
  public ${op.ret.cap}<% } else { %>public void<% } %> $op.cap(<% op.params.each { ret += separator+"${c.name(it.type)}"+' '+it.uncap }%>${ret-separator});<% } %>''')


  //ifcs


  template('ifcService', body: '''<% if (!c.className) { c.className = item.n.cap.base } %>{{imports}}<% if (!item.base) { %>
/**
* The service provides public operations for '$module.name'.<% if (item.description) { %>
* <p>
* $item.description
* </p><% } %>
*/<% } else { %>
/** Base interface of {@link $item.name} */<% } %>
public interface $className {
  ${macros.generate('interfaceBodyExternal', c)}
}''')

  template('ifcServiceExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %>
/**
* The service provides public operations for '$module.name'.<% if (item.description) { %>
* <p>
* $item.description
* </p><% } %>
*/
public interface $c.className extends $item.n.cap.base {
}''')

  template('ifcBasicType', body: '''<% if(!c.className) { c.className = item.cap } %> {{imports}}
${item.description?"/*** $item.description */":''}
public interface $className extends <% if (item.superUnit) {%>$superUnit.name<% } else { %>${c.name('Serializable')}<% } %> {
${macros.generate('propGettersIfc', c)}${macros.generate('propSettersIfc', c)}${macros.generate('interfaceBody', c)}
}''')

  template('ifcContainer', body: '''<% if (!c.className) { c.className = item.n.cap.base } %><% def entityNames = item.entities.collect { it.name } as Set; def oneToManyNoOppositeProps = [:]; def manyToOneProps = [:] %>
<% item.props.each { entityProp -> %><% def entity = entityProp.type; oneToManyNoOppositeProps[entity] = []; manyToOneProps[entity] = []; entity.propsRecursive.each { prop -> if(prop.type) { %><% if (prop.oneToMany && !prop.opposite && entityNames.contains(prop.type.name)) { oneToManyNoOppositeProps[entity] << prop } %>
<% if (prop.manyToOne && entityNames.contains(prop.type.name)) { manyToOneProps[entity] << prop } %><% } } } %>{{imports}} 
import ee.mdd.example.model.${item.name}; // TODO: c.name does not yet resolve items in sub packages like .model
<% if (!item.base) { %>
/**
* The container is used to transfer bundled data between between server and client.
* <p>
* ${item.description?item.description:''}
* </p>
*/<% } else { %>/** Base interface of {@link ${c.name(item.name)}} */<% } %>

public interface $c.className extends ${c.name('Serializable')} {
  /** A unique URI prefix for RESTful services and multi-language support */
  public static final String URI_PREFIX = "${item.uri}";

  /** Source of object builder. E.g. server/node name. */
  String getSource();

  void setSource(String source);

  /** Time point of data fetching */
  ${c.name('Date')} getTimestamp();

  void setTimestamp(Date timestamp);

  /** Reset temporary ids of new entities */
  void resetTempIds();

  /**
  * Applies all changes from the passed container. In effect, all removes are applied.
  *
  * Use this method to update a container which contains all information with a container carrying delta information.
  *
  * @param container Delta information (new, updated and deleted deadlocks)
  */
  void synchronize(${item.cap} container);

  /**
   * Applies all changes and addition returns a delta cache containing all changes. This method's logic is more
   * complex as the pure synchronize method, so it should only be used if the returned delta cache is needed for
   * further processing.
   *
   * @param container Delta information (new, updated and deleted deadlocks)
   * @return Delta container with all changes
   */
   $item.n.cap.delta synchronizeWithDelta($item.cap container);

   /**
   * Synchronizes only the removes.
   */
   void synchronize($item.n.cap.removes removes);

   /**
   * Merges the changes from the passed container. In effect, all removes are merged as well.
   *
   * Use this method to merge containers containing delta information.
   *
   * @param container Delta information (new, updated and deleted deadlocks)
   */
   void merge($item.cap container);

   $item.n.cap.removes get${item.cap}Removes();

   /**
   * Clears all data (caches and removes)
   */
   void clear();

   /**
   * Returns whether the container is empty, i.e. that it contains neither container entries nor removes.
   */
   boolean isEmpty();

   $item.name buildChangeSet();<% item.props.each { entityProp -> def entity = entityProp.type %>
   
   $entity.cap get${entity.n.cap.entity}s();<% } %><% oneToManyNoOppositeProps.each { entity, linkedProps -> linkedProps.each { prop -> def relationIdProp = prop.type.idProp %>
   
   ${c.name('LinkedObjectCache')}<${entity.idProp.computedType(c)}, ${relationIdProp.computedType(c)}, $prop.type.name> get${entity.name}${prop.cap}();<% } } %>

   ${macros.generate('interfaceBody', c)}<% item.props.each { prop -> %>

   ${prop.computedType(c)} $prop.getter;<% } %>
   ${macros.generate('propSettersIfc', c)}
}''')

  template('ifcContainerDelta', body: ''' <% if (!c.className) { c.className = item.n.cap.deltaBase } %>{{imports}}
  public interface $c.className extends ${c.name('LogStringProvider')} {
    <% item.props.each { entityProp -> def entity = entityProp.type %>
      ${entity.n.cap.deltaCache} get${entity.n.cap.delta}();<% } %>
  }''')

  template('ifcContainerDeltaExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.n.cap.delta } %><% def superClassName = item.n.cap.deltaBase %>{{imports}}
  public interface $c.className extends $superClassName {
  }''')

  template('ifcController', body: '''<% if (!c.className) { c.className = item.n.cap.base } %>{{imports}}
  <% if (!item.base) { %>
    /**
     * The controller $item.name provides internal logic operations for '$module.name'.<% if (item.description) { %>
     * <p>
     * $item.description
     * </p><% } %>
     */<% } else { %>/** Base interface of {@link $item.name} */<% } %>
  public interface $className<% if (item.superUnit) { %> extends ${item.superUnit.cap}<% } %> {
    ${macros.generate('interfaceBody', c)}
  }''')

  template('ifcEntity', body: '''{{imports}}
  ${item.description?"/*** $item.description */":''}
  public interface $item.baseGenericName extends<% if (item.superUnit) { %> ${c.name(item.superUnit.name)}${item.superGenericSgn} <% } else { %> ${c.name('EntityIfc')}<${item.idProp.type.name}><% } %>{
    /** A unique URI prefix for RESTful services and multi-language support */
    public static final String URI_PREFIX = "${item.getUri()}";
    ${macros.generate('propGettersEntityIfc', c)}${macros.generate('propsSettersEntityIfc', c)}${macros.generate('relationIdPropGetterIfc', c)}${macros.generate('relationIdPropSetterIfc', c)}${macros.generate('interfaceBody', c)}
  }''')

  template('ifcExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %><% if (!c.metas) { c.metas = item.metas } %>{{imports}}
  /** Base interface for {@link $item.n.cap.base} */
  public interface $c.className extends $item.n.cap.base {
  }''')

  template('ifcContainerExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %>{{imports}}
  /**
   * The container is used to transfer bundled data between server and client.
   * <p>
   * ${item.description?item.description:''}
   * </p>
   */
  public interface $c.className extends $item.n.cap.base {
  }''')

  template('ifcControllerExtends', body: '''<% c.src = true %><% if(!c.className) { c.className = item.cap } %>{{imports}}
  /**
   * The $item.name controller provides internal logic operations for '$module.name'.<% if (item.description) { %>
   * <p>
   * $item.description
   * </p><% } %>
   */
  public interface $className extends $item.n.cap.base {
  }''')

  template('ifcFinders', body: '''<% if(!c.className) { c.className = item.n.cap.finders } %><% def finders = item.finders; def idProp = item.idProp; %>{{imports}}
  <% if(finders.description) { %>/**
     * $finders.description
     */<% } else { %>/** The finders provide Find, Count, Exist operations for entity {@link $item.cap}.*/<% } %>
  public interface $className extends ${c.name('Manager')}<${idProp.type.name}, ${c.name(item.cap)}> {  <% finders.counters.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %><% finders.finders.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.returnTypeExternal(c)} ${op.name}(${op.signature(c)});
      
      ${op.returnTypeExternal(c)} ${op.name}Strict(${op.signature(c)});
      <% } %><% finders.exists.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %>
}''')

  template('ifcCommands', body: '''<% if(!c.className) { c.className = item.n.cap.commands } %><% def commands = item.commands; def idProp = item.idProp; %>{{imports}}
  <% if(commands.description) { %>/**
     * $commands.description
     */<% } else { %>/** The commands provide Delete, Update, Create operations for entity {@link $item.cap}.*/<% } %>
  public interface $className extends ${c.name('Manager')}<${idProp.type.name}, ${c.name(item.cap)}> {<% commands.deleters.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %><% commands.creates.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %><% commands.updates.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${idProp.computedType(c)} $idProp.name, ${op.signature(c)});
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}($item.cap entity, ${op.signature(c)});
      <% if (op.fireEventProp) { %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${idProp.computedType(c)} $idProp.name, ${op.signature(c)}, boolean fireEvent);
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}($item.cap entity, ${op.signature(c)}, boolean fireEvent);
<% } } %>
  }''')
  
  template('ifcCommandsExtends', body: '''<% def commands = item.commands %>
<% if (commands.description) { %>/**
* $commands.description
*/<% } else { %>/** The commands provide CRUD operations for entity {@link $item.cap} */<% } %>
public interface $className extends $item.n.cap.commandsBase {
}''')
  
  template('ifcFindersExtends', body: '''<% def finders = item.finders %>
<% if (finders.description) { %>/**
* $finders.description
*/<% } else { %>/** The finders provides CRUD operations for entity {@link $item.cap} */<% } %>
public interface $className extends $item.n.cap.findersBase {
}''')

  template('ifcCache', body: '''<% def superUnit = item.superUnit; def idProp = item.idProp; def type = item.virtual?'E':c.name(item.cap); def cacheSuper %>{{imports}}
  <% if (superUnit) { cacheSuper = "${superUnit.n.cap.cache}<$type>" } else if (idProp.typeLong || idProp.typeInteger) { cacheSuper = "${c.name('Cache')}<${c.name(idProp.type.name)}, $type>, ${c.name('TempIdCache')}" } else { cacheSuper = "Cache<${c.name(idProp.type.name)}, $type>" } %>
  <% def singlePropIndexes = item.props.findAll {!it.primaryKey && ( it.index || it.unique )}; def relationIdPropIndexes = item.props.findAll { it.typeEl && it.manyToOne }; def keyNameToIndex = [:]; item.indexes.collect { def index -> String keyName = index.props.collect { it.cap }.join ('And')[0].toLowerCase(); keyNameToIndex[keyName] = index; } %>
  public interface <% if(item.virtual) { %>$className<$item.simpleGenericSgn E extends ${c.name(item.cap)}${item.genericSgn}> extends $cacheSuper<% } else { %>$className extends $cacheSuper<% } %> {<% if (item.finders && item.finders.finders) { item.finders.finders.each { op -> %><% String finderKeyName = op.params.collect { it.prop.cap }.join('And')[0].toLowerCase(); if ((op.params.size() == 1 && singlePropIndexes.contains(op.params.get(0).prop)) || (keyNameToIndex.containsKey(finderKeyName) && !op.params.find { it.multi })) { %>

  <% if(op.unique) { %> $idProp.type <% } else { %>${c.name('Set')}<$idProp.type.name><% } %> ${op.name}AsId($op.signature);<% } %>

  <% if(op.unique) { %> $type <% } else { %> ${c.name('List')}<$type> <% } %> ${op.name}($op.signature);

  <% if(op.unique) { %> $type <% } else { %> ${c.name('List')}<$type> <% } %> ${op.name}Strict($op.signature);<% } } %><% item.props.each { prop -> if(prop.type && prop.manyToOne && prop.type.idProp) { def relationIdProp = prop.type.idProp %>

    ${c.name('Set')} <${c.name(idProp.type)}> findBy${prop.cap}${relationIdProp.cap}AsId(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap});

    ${c.name('List')}<$type> findBy${prop.cap}${relationIdProp.cap}(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap});

    List<$type> findBy${prop.cap}${relationIdProp.cap}Strict(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap});

    List<$type> findBy${prop.cap}${relationIdProp.cap}s(List<${relationIdProp.computedType(c)}> ${prop.uncap}${relationIdProp.cap}s);

    List<$type> findBy${prop.cap}${relationIdProp.cap}sStrict(List<${relationIdProp.computedType(c)}> ${prop.uncap}${relationIdProp.cap}s);<% } else if (prop.type && prop.oneToOne) { def relationIdProp = prop.type.idProp %>

    $type findBy${prop.cap}${relationIdProp.cap}(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap});

    $type findBy${prop.cap}${relationIdProp.cap}Strict(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap});<% } } %>

    String toStringAsIdAndVersion();

    ${macros.generate('interfaceBody', c)}

  }''')

  template('ifcCacheExtends', body: '''{{imports}}
  public interface <% if (item.virtual) { %>$className<${item.simpleGenericSgn}E extends ${c.name(item.cap)}${item.genericSgn}> extends ${className}Base<${item.simpleGenericSgn}E><% } else { %>$className extends ${className}Base<% } %> {
  }''')



  //classes

  template('implCache', body: '''<% def superUnit = item.superUnit; def idProp = item.idProp; def type = item.virtual?'E' : c.name(item.cap); def cacheSuper%>import static ee.common.util.ComparisonUtils.*;{{imports}}
  <% if (!c.override) { %><% if (superUnit) { cacheSuper = "${superUnit.n.cap.cacheImpl}<$type>" } else if (idProp.typeLong) { cacheSuper = "${c.name('LongEntityCache')}<$type>" } else if (idProp.typeInteger) { cacheSuper = "${c.name('IntegerEntityCache')}<$type>" } else if (idProp.typeString) { cacheSuper = "${c.name('StringEntityCache')}<$type>"} else { cacheSuper = "CacheImpl<$idProp.type, $type>" } %>
  <% } else { %><% if (superUnit) { cacheSuper = "${superUnit.n.cap.cacheOverride}<$type>" } else if (idProp.typeLong) { cacheSuper = "${c.name('LongCacheOverride')}<$type>" } else if (idProp.typeInteger) { cacheSuper = "${c.name('IntegerCacheOverride')}<$type>" } else if (idProp.typeString) { cacheSuper = "${c.name('StringCacheOverride')}<$type>" } else { cacheSuper = "CacheOverride<$idProp.type, $type>" } %>
  <% } %><% def singlePropIndexes = item.props.findAll { !it.primaryKey && (it.index || it.unique ) }; def relationIdPropIndexes = item.props.findAll { it.typeEl && it.manyToOne }; def keyNameToIndex = [:]; item.indexes.collect { def index -> String keyName = index.props.collect { it.cap }.join('And')[0].toLowerCase(); keyNameToIndex[keyName] = index; } %>
public abstract <% if (item.virtual) { %>class $className<${item.simpleGenericSgn}E extends ${c.name(item.cap)}${item.genericSgn}> extends $cacheSuper implements ${item.n.cap.cache}<${item.simpleGenericSgn}E><% } else { %>class $className extends $cacheSuper implements $item.n.cap.cache<% } %> {
  private static final long serialVersionUID = 1L;
  <% singlePropIndexes.each { prop -> if (prop.unique) { %>
  protected ${c.name('Map')}<${c.name(prop.type.name)}, ${c.name(idProp.type.name)}> ${prop.uncap}ToId = null;<% } else { %>
  protected ${c.name('LinkToSetCache')}<${c.name(prop.type.name)}, ${c.name(idProp.type.name)}> ${prop.uncap}ToIds = null;<% } %><% } %><% relationIdPropIndexes.each { prop -> def relationIdProp = prop.type.idProp; if(relationIdProp) { %>
  protected ${c.name('LinkToSetCache')}<${c.name(relationIdProp.type.name)}, ${c.name(idProp.type.name)}> ${prop.uncap}${relationIdProp.cap}ToIds = null;<% } %><% } %><% keyNameToIndex.each { key, index -> if (index.unique) { %>
  protected ${c.name('Map')}<String, $idProp.type.name> ${key}ToId = null;<% } else { %>
  protected ${c.name('LinkToSetCache')}<String, ${c.name(idProp.type.name)}> ${key}ToIds = null;<% } %><% } %><% if (c.override && item.virtual) { %>

  public $className(${item.n.cap.cache}<${item.simpleGenericSgn}E> parent) {
    super(parent);
  }

  public ${className}(${item.n.cap.cache}<${item.simpleGenericSgn}E> parent, boolean threadSafe) {
    super(parent, threadSafe);
  }<% } else if (c.override) { %>
  public ${className}($item.n.cap.cache parent) {
    super(parent);
  }

  public ${className}($item.n.cap.cache parent, boolean threadSafe) {
    super(parent, threadSafe);
  }<% } else { %>
  public $className() {
    super();
  }

  public $className(boolean threadSafe) {
    super(threadSafe);
  }<% } %><% if (item.finders && item.finders.finders) { item.finders.finders.each { op -> if(op.entity.name == item.name) { String finderKeyName = op.params.collect { it.prop?.uncap }.join('And'); %><% if (op.params.size() == 1 && singlePropIndexes.contains(op.params[0].prop)) { def param = op.params[0]; def prop = param.prop; %>

  @Override
  public ${op.unique ? "$idProp.type.name" : "Set<$idProp.type.name>"} ${op.uncap}AsId($op.signature) {
    checkAndInitPropertyBasedLazyCaches();<% if (param.multi) { %>
    ${c.name('HashSet')}<${c.name(idProp.type.name)}> ret = new ${c.name('HashSet')}<>();
    for($prop.type $prop.name : ${prop.name}s) {<% if (prop.unique) { %>
      if (${prop.uncap}ToId.containsKey($prop.name)) {
        ret.add(${prop.uncap}ToId.get($prop.name));
      }<% if (c.override) { %> else if (parent != null) {
        ${c.name('Set')}<${c.name(idProp.type.name)}> ids = (($item.n.cap.cache${item.virtual ? '<E>' : ''})parent).${op.name}AsId($op.signatureName);
        ids.removeAll(deleted);
        ret.addAll(ids);
      }<% } %><% } else { %><% if (op.unique) { %>
      ${c.name(idProp.type.name)} id = ${prop.uncap}ToId.get($propAttr.paramName);
      if (id != null) {
        ret.add(id);
      }<% } else { %>
      ${c.name('LinkToSet')}<?, ${c.name(idProp.type.name)}> ids = ${prop.uncap}ToIds.get($propAttr.paramName);
      if (ids != null) {
        ret.addAll(ids.getTo());
      }<% } %><% if (c.override) { %>
      ${macros.generate('propToIds', c)}<% } %><% } %>
    }
    return ret;<% } else { %><% if (prop.unique) { %>
    ${c.name(idProp.type.name)} ret = ${prop.uncap}ToId.get($prop.name);<% if (c.override) { c.op = op %>
    ${macros.generate('retNullOrDeleted', c)}
    <% } %><% } else { %>
    ${c.name('Set')}<${c.name(idProp.type.name)}> ret = null;
    ${c.name('LinkToSet')}<?, ${c.name(idProp.type.name)}> ids = ${prop.uncap}ToIds.get($prop.name);
    ${macros.generate('checkIdsNull', c)}<% if (c.override) { c.op = op; %>
    ${macros.generate('propToIds', c)}<% } %><% } %><% if (item.ordered) { %>
    sort${c.name(item.cap)}sByOrder(ret);<% } %><% if (op.unique && !prop.unique) { %>
    if (!ret.isEmpty()) {
      return ret.iterator().next();
    } else {
      return null;
    }<% } else { %>
      return ret;<% } %><% } %>
  }

  @Override
  public ${op.unique?"$type":"List<$type>"} ${op.name}($op.signature) {<% if (op.unique) { %>
      $idProp.type.name id = ${op.name}AsId($op.signatureName);
      $type ret = null;
      if (id != null) {
        ret = get(id);
      }<% } else { %>
      ${c.name('Set')}<${c.name(idProp.type.name)}> ids = ${op.name}AsId($op.signatureName);
      ${c.name('List')}<$type> ret = getAll(ids);<% } %>
    return ret;
  }

  @Override
  public ${op.unique?"$type":"List<$type>"} ${op.name}Strict($op.signature) {
    return strict(${op.name}($op.signatureNames), \"${op.name}\", $op.signatureNames);
  }

  <% } else if (keyNameToIndex.containsKey(finderKeyName) && !op.params.find { it.multi }) { def index = keyNameToIndex[finderKeyName]; %>

  @Override
  public ${op.unique ? "$idProp.type.name" : "Set<$idProp.type.name>"} ${op.name}AsId($op.signature) {
    checkAndInitPropertyBasedLazyCaches();<% if (index.unique) { %>
    ${c.name(idProp.type.name)} ret = ${finderKeyName}ToId.get(${finderKeyName}Key($op.signatureName));<% if (c.override) { c.op = op %>
    ${macros.generate('retNullOrDeleted', c)}
    <% } %><% } else { %>
    ${c.name('Set')}<$idProp.type.name> ret = null;
    ${c.name('LinkToSet')}<String, $idProp.type.name> ids = ${finderKeyName}ToIds.get(${finderKeyName}Key($op.signatureName));
    ${macros.generate('checkIdsNull', c)}<% if (c.override) { c.op = op; %>
    ${macros.generate('propToIds', c)}<% } %><% } %><% if (item.ordered) { %>
    sort${c.name(item.cap)}sByOrder(ret);<% } %><% if (op.unique && !index.unique) { %>
    if (!ret.isEmpty()) {
      return ret.iterator().next();
    } else {
      return null;
    }<% } else { %>
    return ret;<% } %>
  }

  @Override
  public ${op.unique?"$type":"List<$type>"} ${op.name}($op.signature) {<% if (op.unique) { %>
    $idProp.type id = ${op.name}AsId($op.signatureName);
    $type ret = null;
    if (id != null) {
      ret = get(id);
    }<% } else { %>
    ${c.name('Set')}<${c.name(idProp.type.name)}> ids = ${op.name}AsId($op.signatureName);
    ${c.name('List')}<$type> ret = getAll(ids);<% } %>
    return ret;
  }

  @Override
  public ${op.unique?"$type":"List<$type>"} ${op.name}Strict($op.signature) {
    return strict(${op.name}($op.signatureNames), \"${op.name}\", $op.signatureNames);
  }<% } else { %>

   @Override
  public<% if(op.unique) { %> $type <% } else { %> ${c.name('List')}<$type> <% } %> ${op.name}($op.signature) {
    <% if(op.unique) { %>$type ret = null;<% } else { %>${c.name('ArrayList')}<$type> ret = new ArrayList<>();<% } %><% if (!c.override) { %>
    for (${c.name('Map')}.Entry<${c.name(idProp.type.name)}, $type> entry : data.entrySet())<% } else { %>for (${c.name('Map')}.Entry<${c.name(idProp.type.name)}, $type> entry : merged().entrySet())<% } %> {
      $type entity = entry.getValue();
    }<% if (!op.unique && item.ordered) { %>
    sort${c.name(item.cap)}sByOrder(ret);<% } %>
    return ret;
  }

  @Override
  public ${op.unique?"$type":"List<$type>"} ${op.name}Strict($op.signature) {
    return strict(${op.name}($op.signatureName), "\${op.name}\", $op.signatureName);
  }<% } } } } %><% relationIdPropIndexes.each { prop-> def relationIdProp = prop.type.idProp; if(relationIdProp) { %>

   @Override
  public Set<${c.name(idProp.type.name)}> findBy${prop.cap}${relationIdProp.cap}AsId(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap}) {
    checkAndInitPropertyBasedLazyCaches();
    ${c.name('Set')}<${c.name(idProp.type.name)}> ret = null;
    ${c.name('LinkToSet')}<?, ${c.name(idProp.type.name)}> ids = ${prop.uncap}${relationIdProp.cap}ToIds.get(${prop.uncap}${relationIdProp.cap});
    ${macros.generate('checkIdsNull', c)}<% if (c.override) { %>
    if (parent != null) {
      ret = new ${c.name('HashSet')}<>(ret);
      ret.addAll((($item.n.cap.cache${item.virtual ? '<E>' : ''})parent).findBy${prop.cap}${relationIdProp.cap}AsId(${prop.uncap}${relationIdProp.cap}));
      ret.removeAll(deleted);
    }<% } %>
    return ret;
  }

  @Override
  public ${c.name('List')}<$type> findBy${prop.cap}${relationIdProp.cap}(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap}) {
    ${c.name('Set')}<${c.name(idProp.type.name)}> ids = findBy${prop.cap}${relationIdProp.cap}AsId(${prop.uncap}${relationIdProp.cap});
    ${c.name('List')}<$type> ret = getAll(ids);<% if (item.ordered) { %>
    sort${c.name(item.cap)}sByOrder(ret);<% } %>
    return ret;
  }

  @Override
  public ${c.name('List')}<$type> findBy${prop.cap}${relationIdProp.cap}Strict(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap}) {
    return strict(findBy${prop.cap}${relationIdProp.cap}(${prop.uncap}${relationIdProp.cap}), \"findBy${prop.cap}${relationIdProp.cap}\" , ${prop.uncap}${relationIdProp.cap});
  }

  @Override
  public ${c.name('List')}<$type> findBy${prop.cap}${relationIdProp.cap}s(${c.name('List')}<${relationIdProp.computedType(c)}> ${prop.uncap}${relationIdProp.cap}s) {
    ${c.name('ArrayList')}<$type> ret = new ArrayList<>();
    for(${relationIdProp.computedType(c)} relationId : ${prop.uncap}${relationIdProp.cap}s) {
      ret.addAll(findBy${prop.cap}${relationIdProp.cap}(relationId));
    }<% if (item.ordered) { %>
    sort${c.name(item.cap)}sByOrder(ret);<% } %>
    return ret;
  }

  @Override
  public ${c.name('List')}<$type> findBy${prop.cap}${relationIdProp.cap}sStrict(List<${relationIdProp.computedType(c)}> ${prop.uncap}${relationIdProp.cap}s) {
    return strict(findBy${prop.cap}${relationIdProp.cap}s(${prop.uncap}${relationIdProp.cap}s), \"findBy${prop.cap}${relationIdProp.cap}s\", ${prop.uncap}${relationIdProp.cap}s);
  }<% } } %><% item.props.each { prop -> if (prop.typeEl && prop.oneToOne) { def relationIdProp = prop.type.idProp %>

   @Override
  public $type findBy${prop.cap}${relationIdProp.cap}(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap}) {
    $type ret = null;<% if (!c.override) { %>
    for (${c.name('Map')}.Entry<${c.name(idProp.type.name)}, $type> entry : data.entrySet())<% } else { %>for (${c.name('Map')}.Entry<${c.name(idProp.type.name)}, $type> entry : merged().entrySet())<% } %> {
      $type entity = entry.getValue();
      if (areEquals(entity.get${prop.cap}${relationIdProp.cap}(), ${prop.uncap}${relationIdProp.cap})) {
        ret = entity;
        break;
      }
    }
    return ret;
  }

  @Override
  public $type findBy${prop.cap}${relationIdProp.cap}Strict(${relationIdProp.computedType(c)} ${prop.uncap}${relationIdProp.cap}) {
    return strict(findBy${prop.cap}${relationIdProp.cap}(${prop.uncap}${relationIdProp.cap}), \"findBy${prop.cap}${relationIdProp.cap}\", ${prop.uncap}${relationIdProp.cap});
  } <% } } %><% item.operations.each { op -> if (op.body) { %>

  @Override<% if(op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public $op.ret ${op.name}($op.signature) {
    $op.body
  }<% } %><% } %><% if (c.override && !item.virtual) { %>

  @Override
  public $item.n.cap.cache getParent() {
    return ($item.n.cap.cache) parent;
  }<% } %>

  @Override
  public String toStringAsIdAndVersion() {
    String content = ${c.name('CollectionUtils')}.resolveAndJoinIdAndNaturalKeyAndVersion(data.values());
    return toString(content);
  }

  @Override
  public String toStingAsIdAndNaturalKeyAndVersion() {
    String content = CollectionUtils.resolveAndJoinIdAndNaturalKeyAndVersion(data.values());
    return toString(content);
  }<% if (item.ordered) { %>

  protected void sort${item.cap}sByOrder(List<$item.cap> items) {
    Collections.sort(items, new ${c.name('Comparator')}<$item.cap>() {
      @Override
      public int compare($item.cap o1, $item.name o2) {
        return o1.getOrder().compareTo(o2.getOrder());
      }
    });
  }<% } %><% if (singlePropIndexes || relationIdPropIndexes|| item.indexes) { %>

    //local indexes
  @Override
  public void clearIndexes() {<% singlePropIndexes.each { prop -> if (prop.unique) { %>
    ${prop.uncap}ToId = null;<% } else { %>
    ${prop.uncap}ToIds = null;<% } %><% } %><% relationIdPropIndexes.each { prop -> def relationIdProp = prop.type.idProp; if(relationIdProp) { %>
    ${prop.uncap}${relationIdProp.cap}ToIds = null;<% } } %><% keyNameToIndex.each { key, index -> if (index.unique) { %>
    ${key}ToId = null;<% } else { %>
    ${key}ToIds = null;<% } %><% } %>
    super.clearIndexes();
  }

  @Override
  protected void preInitPropertyBasedLazyCaches() {
    super.preInitPropertyBasedLazyCaches();<% singlePropIndexes.each { prop -> if (prop.unique) { %>
    ${prop.uncap}ToId = !threadSafe ? new ${c.name('HashMap')}<$prop.type, $idProp.type>() : new ${c.name('ConcurrentHashMap')}<$prop.type, $idProp.type>();<% } else { %>
    ${prop.uncap}ToIds = new ${c.name('LinkToSetCache')}<>(threadSafe);<% } %><% } %><% relationIdPropIndexes.each { prop -> def relationIdProp = prop.type.idProp; if(relationIdProp) { %>
    ${prop.uncap}${relationIdProp.cap}ToIds = new ${c.name('LinkToSetCache')}<>(threadSafe);<% } } %><% keyNameToIndex.each { key, index -> if (index.unique) { %>
    ${key}ToId = !threadSafe ? ${c.name('HashMap')}<String, $idProp.type>() : new ${c.name('ConcurrentHashMap')}<String, $idProp.type>();<% } else { %>
    ${key}ToIds = new ${c.name('LinkToSetCache')}<>(threadSafe);<% } %><% } %>
  }

  @Override
  protected void addToPropertyBasedLazyCaches($idProp.type.name id, $type entity, $type oldEntity) {
    super.addToPropertyBasedLazyCaches(id, entity, oldEntity);<% singlePropIndexes.each { prop -> if (prop.unique) { %>
    ${prop.uncap}ToId.put(entity.$prop.getter, id);<% } else { %>
    ${prop.uncap}ToIds.add(entity.$prop.getter, id);<% } %><% } %><% relationIdPropIndexes.each { prop -> def relationIdProp = prop.type.idProp; if(relationIdProp) { %>
    ${prop.uncap}${relationIdProp.cap}ToIds.add(entity.get${prop.cap}${relationIdProp.cap}(), id);<% } } %><% keyNameToIndex.each { key, index -> def call = index.props.collect { prop -> (prop.typeEl && prop.manyToOne) ? "entity.get${prop.cap}${relationIdProp.cap}()" : "entity.$prop.getter" }.join (', '); if (index.unique) { %>
    ${key}ToId.put(${key}Key($call), id);<% } else { %>
    ${key}ToIds.add(${key}Key($call), id);<% } %><% } %>
  }

  @Override
  protected void removeFromPropertyBasedLazyCaches($idProp.type.name id, $type entity) {
    super.removeFromPropertyBasedLazyCaches(id, entity);<% singlePropIndexes.each { prop -> if (prop.unique) { %>
    ${prop.uncap}ToId.remove(entity.$prop.getter);<% } else { %>
    ${prop.uncap}ToIds.removeTo(entity.$prop.getter, id);<% } %><% } %><% relationIdPropIndexes.each { prop -> def relationIdProp = prop.type.idProp; if(relationIdProp) { %>
    ${prop.uncap}${relationIdProp.cap}ToIds.removeTo(entity.get${prop.cap}${relationIdProp.cap}(), id);<% } } %><% keyNameToIndex.each { key, index -> def call = index.props.collect { prop -> (prop.typeEl && prop.manyToOne) ? "entity.get${prop.cap}${relationIdProp.cap}()" : "entity.$prop.getter" }.join (', '); if (index.unique) { %>
    ${key}ToId.remove(${key}Key($call));<% } else { %>
    ${key}ToIds.removeTo(${key}Key($call), id);<% } %><% } %>
  }

<% keyNameToIndex.each { key, index -> def signature = index.props.collect { prop -> (prop.typeEl && prop.manyToOne) ? "$relationIdProp.type ${prop.uncap}${relationIdProp.cap}" : "$prop.type $prop.uncap" }.join (', '); %>
<% def ret = index.props.collect { prop -> if (prop.typeEl && prop.manyToOne) { "${prop.uncap}${relationIdProp.cap}" } else if (prop.typeDate) { "${prop.uncap}.getTime()" } else { "$prop.uncap" } }.join (' + SEPARATOR_FOR_PROPERTY_KEYS + '); %>

  protected String ${key}Key($signature) {
    return $ret;
  }<% } %><% } %>

  @Override
  public ${item.deltaCache.cap} synchronizeWithDelta(Cache<$idProp.type.name, $type> update, Collection<$idProp.type.name> removedKeys) {
    return (${item.deltaCache.cap}) super.synchronizeWithDelta(update, removedKeys);
  }

  @Override
  public ${item.deltaCache.cap} synchronizeWithDelta(Cache<$idProp.type.name, $type> update) {
    return (${item.deltaCache.cap}) super.synchronizeWithDelta(update);
  }
}''')

  template('implCacheExtends', body: '''{{imports}}
/** Cache implementation for {@link ${c.name(item.name)}} */
@${c.name('Alternative')}<% def type = item.virtual ? 'E':item.cap; def idProp = item.idProp; if (!c.override) { %>
<% if (item.virtual) { %>@SuppressWarnings("unchecked")
public abstract class $className<${item.simpleGenericSgn}E extends ${item.cap}${item.genericSgn}> extends ${item.n.cap.cacheBaseImpl}<${item.simpleGenericSgn}E> <% } else { %> public class $className extends ${item.n.cap.cacheBaseImpl}<% } } else { %>
<% if (item.virtual) { %>@SuppressWarnings("unchecked")
public abstract class $className<${item.simpleGenericSgn}E extends ${item.cap}${item.genericSgn}> extends ${item.n.cap.cacheOverrideBase}<${item.simpleGenericSgn}E><% } else { %> public class $className extends ${item.n.cap.cacheOverrideBase}<% } } %> {
  private static final long serialVersionUID = 1L;<% if(c.override && item.virtual) { %>

  public ${className}(${item.n.cap.cache}<${item.simpleGenericSgn}E> parent) {
    super(parent);
  }

  public ${className}(${item.n.cap.cache}<${item.simpleGenericSgn}E> parent, boolean threadSafe) {
    super(parent, threadSafe);
  }<% } else if (c.override) { %>

  public $className() {
    this(new $item.n.cap.cacheImpl());
  }

  public $className(boolean threadSafe) {
    this(new $item.n.cap.cacheImpl(threadSafe), threadSafe);
  }

  public $className($item.n.cap.cache parent) {
    super(parent);
  }

  public $className($item.n.cap.cache parent, boolean threadSafe) {
    super(parent, threadSafe);
  }<% } else { %>

  public $className() {
    super();
  }

  public $className(boolean threadSafe) {
    super(threadSafe);
  }<% } %>
  ${macros.generate('implOperations', c)}
}''')

  template('containerIdsBase', body: '''<% if (!c.className) { c.className = item.n.cap.idsBase } %>{{imports}}
public class $c.className extends ${c.name('Base')} {
  private static final long serialVersionUID = 1L;<% item.props.each { entityProp -> def entity = entityProp.type %>
  protected ${c.name('HashSet')}<$entity.idProp.type.name> ${entity.instancesName} = new ${c.name('HashSet')}<>();<% } %><% item.props.each { entityProp -> def entity = entityProp.type %>

  public ${c.name('Set')}<$entity.idProp.type.name> get${entity.cap}s() {
    return ${entity.instancesName};
  }<% } %>

  public void synchronize($c.className removed) {<% item.props.each { entityProp -> def entity = entityProp.type %>
    ${entity.instancesName}.addAll(removed.get${entity.cap}s());<% } %>
  }

  public void clear() {<% item.props.each { entityProp -> def entity = entityProp.type %>
    ${entity.instancesName}.clear();<% } %>
  }

  @Override
  public void fillToString(StringBuffer b) {
    super.fillToString(b);<% item.props.each { entityProp -> def entity = entityProp.type %>
    b.append("${entity.instancesName}=").append(${entity.instancesName}).append(SEP);<% } %>
  }
}''')

  template('containerIds', body: '''<% if (!c.className) { c.className = item.n.cap.ids } %>{{imports}}
public class $c.className extends $item.n.cap.idsBase {
  private static final long serialVersionUID = 1L;
}''')

  template('implContainerDelta', body: '''<% if (!c.className) { c.className = item.n.cap.deltaBaseImpl } %><% def signature = item.props.collect { entityProp -> "${entityProp.type.n.cap.deltaCache} ${entityProp.type.uncap}DeltaCache" }.join(", ")
def newInstances = item.props.collect { entityProp -> "new ${entityProp.type.n.cap.deltaCacheImpl}()" }.join(", ") %>{{imports}}
public class $c.className implements $item.n.cap.deltaImpl {
  <% item.props.each { entityProp -> def entity = entityProp.type %>private final ${entity.n.cap.deltaCache} ${entity.uncap}DeltaCache;<% } %>

  public $c.className(${signature}) {
    <% item.props.each { entityProp -> def entity = entityProp.type %>this.${entity.uncap}DeltaCache = ${c.name('AssertionUtils')}.assertNotNull(${entity.uncap}DeltaCache);<% } %>
  }

  public $c.className() {
    this(${newInstances});
  }
  <% item.props.each { entityProp -> def entity = entityProp.type %>
  @Override
  public ${entity.n.cap.deltaCache} get${entity.n.cap.delta}() {
    return ${entity.uncap}DeltaCache;
  }<% } %>

  @Override
  public void fillToLogString(${c.name('LogStringBuilder')} tf) {
    <% item.props.each { entityProp -> def entity = entityProp.type %>tf.append("${entity.uncap}Delta", ${entity.uncap}DeltaCache);<% } %>
  }
}''')

  template('implContainerDeltaExtends', body: '''<% if (!c.className) { c.className = item.n.cap.deltaImpl } %><% def signature = item.props.collect { entityProp -> "${entityProp.type.n.cap.deltaCache} ${entityProp.type.uncap}DeltaCache" }.join(", ")
def params = item.props.collect { entityProp -> "new ${entityProp.type.uncap}deltaCache()" }.join(", ") %>
public class $c.className extends $item.n.cap.deltaBaseImpl {

  public $c.className(${signature}) {
    super(${params});
  }

  public $c.className() {
    super();
  }
}''')

  template('implEntity', body: '''<% if (!c.className) { c.className = item.cap.baseImpl} %>{{imports}}
public ${item.virtual || item.base ? 'abstract ' : ''}class $c.className extends<% if(c.item.superUnit) { %> $c.item.superUnit.n.cap.impl <% } else { %> ${c.name('EntityImpl')}<${item.idProp.type.name}> <% } %>implements ${c.name(c.item)} {
  private static final long serialVersionUID = 1L;
  ${macros.generate('props', c)}${macros.generate('propGetters', c)}${macros.generate('propsSetter', c)}${macros.generate('methods', c)}${macros.generate('propsToString', c)}${macros.generate('hashCodeAndEqualsEntity', c)}

}''')

  template('implEntityExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.n.cap.impl } %>{{imports}}
public ${c.item.virtual?'abstract ':''}class $c.className extends ${item.cap}BaseImpl {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
}''')

  template('entityBaseBean', body: '''<% def superUnit = c.item.superUnit; if(!c.className) { c.className = item.n.cap.entity } %>{{imports}}${macros.generate('metaAttributesEntity', c)}${macros.generate('jpaMetasEntity', c)}
public ${item.virtual || item.base ? 'abstract ':''}class ${item.genericsName} extends<% if(item.superUnit) { %> ${superUnit.n.cap.entity}<% } else { %> ${c.name('EntityJpa')}<${item.idProp.type.name}><% } %> implements ${c.name(c.item.cap)} {
  private static final long serialVersionUID = 1L;
  <% if(c.item.attributeChangeFlag) {%>@${c.name('Transient')}
  private transient boolean attributesChanged = false;<% } %>
  ${c.item.jpaConstants(c)}${macros.generate('idProp', c)}${macros.generate('versionMember', c)}${macros.generate('multiSuperProps', c)}${macros.generate('jpaPropsMember', c)}${macros.generate('baseConstructor', c)}
  ${macros.generate('idPropGetter', c)}${macros.generate('idPropSetter', c)}
  ${macros.generate('getSetVersion', c)}${macros.generate('jpaMultiSuperPropGetters', c)}${macros.generate('jpaMultiSuperPropSetters', c)}
  ${macros.generate('jpaPropGetters', c)}${macros.generate('jpaPropSetters', c)}
  ${macros.generate('relationIdPropGetter', c)}${macros.generate('relationIdPropSetter', c)}
  ${macros.generate('labelBody',c)}${macros.generate('attributesChanged', c)}
  ${macros.generate('methods', c)}${macros.generate('propsToString', c)}
  ${macros.generate('hashCodeAndEqualsEntity', c)}
}''')

  template('entityBean', body: ''' <% c.src = true %><% if(!c.className) { c.className = item.n.cap.entity } %>{{imports}}${macros.generate('metaAttributesEntity', c)}
public ${c.item.virtual?'abstract':''} class $c.className extends ${item.n.cap.baseEntity} {
  private static final long serialVersionUID = 1L;
  ${macros.generate('superConstructor', c)}${macros.generate('implOperations', c)}
}''')

  template('basicTypeBaseBean', body: '''<% def superUnit = c.item.superUnit %><% if (!c.className) { c.className = item.beanName } %>{{imports}}
/** JPA representation of {@link $item.name} */${macros.generate('metaAttributesBasicType', c)}
public ${item.base || item.virtual ? 'abstract':''} class $c.className<% if (superUnit) { %> extends superUnit.cap<% } %> implements ${c.name(item.name)} {
  private static final long serialVersionUID = 1L;
  ${c.item.jpaConstants(c)}${macros.generate('idProp', c)}${macros.generate('jpaPropsMember', c)}${macros.generate('baseConstructor', c)}
  ${macros.generate('idPropGetter', c)}${macros.generate('propGettersBasicType', c)}${macros.generate('propSettersBasicType', c)}
  ${macros.generate('implOperationsAndDelegates', c)}${macros.generate('hashCodeAndEqualsBasicType', c)}
}''')

  template('basicTypeBean', body: '''<% c.src = true %><% def superUnit = c.item.superUnit %><% if (!c.className) { c.className = item.beanName } %>{{imports}}
/** JPA representation of {@link ${c.name(item.name)}} */
@${c.name('Embeddable')}
public class $className extends ${item.n.cap.baseEmbeddable} {
  private static final long serialVersionUID = 1L;
  ${macros.generate('superConstructor', c)}${macros.generate('implOperations', c)}
}''')

  template('serviceBaseBean', body: ''' <% if (!c.className) { c.className = item.n.cap.serviceBaseBean } %>{{imports}}
/** Ejb implementation of {@link $item.name} */
${macros.generate('metaAttributesService', c)}
public ${item.base?'abstract ':''}class $className implements ${c.name(item.name)} {<% if (item.useConverter) { %>
  protected $module.n.cap.converter converter;<% } %>
  ${macros.generate('refsMember', c)}
<% item.operations.each { op -> if(!op.delegateOp && op.body) { %>

  @Override<% if(op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${op.return} $op.name($op.signature) {
    ${op.resolveBody(c)}
  }<% } } %><% item.operations.each { op -> if(op.delegateOp) { %><% def ref = op.ref; def raw = op.rawType || (ref.resultExpression && ref.ret.multi && ref.ret.typeEntity) %>

  @Override<% if(raw) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public ${ref.return} $ref.name(${ref.signature(c)}) {<% if(ref.void) { %>
    ${ref.parent.uncap}.${ref.name}($ref.signatureName);<% } else { %><% if (ref.resultExpression) { %>
    ${ref.return} ret = ${ref.parent.uncap}.${ref.name}($ref.signatureName);
    if (ret !=null) {
      $ref.ret.name entity = ($ref.ret.name) ret;<% if (raw) { %>
      //load LAZY loading
      entity.${ref.ret.getter}.size();
      return (${c.name('List')})entity.${ref.ret.getter};<% } else { if (item.useConverter && (ref.ret.typeEntity || ref.ret.typeBasicType)) { %>
      return converter.toExternal(entity.${ref.ret.getter});<% } else { %>
      return entity.${ref.ret.getter};<% } } %>
    } else {
      return null;
    }<% } else { %>
    ${c.name(ref.return)} ret = ${ref.parent.uncap}.${ref.name}($ref.signatureName);<% if (item.useConverter && ref.returnTypeEjb) { %>
    ret = converter.toExternal(ret);<% } %>
    return ret;<% } %><% } %>
  }<% } %><% } %>
  ${macros.generate('implInjects', c)}<% if (item.useConverter) { %>

  @${c.name('Inject')}
  public void set${module.n.cap.converter}(${module.n.cap.converter} converter) {
    this.converter = converter;
  }<% } %>
}
''')

  template('serviceBean', body: '''<% if(!c.className) { c.className = item.n.cap.serviceBean } %>
import static ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${c.item.component.n.cap.constantsBase}.*;{{imports}}
/** Ejb implementation of {@link $item.name} */
${macros.generate('metaAttributesService', c)}
public class $className extends $item.n.cap.baseBean {
${macros.generate('implOperations', c)}
}''')

  template('implContainer', body: '''<% if(!c.className) { c.className = item.n.cap.containerBaseImpl }%><% def className = c.className; def entityNames = item.props.collect { it.name } as Set %><%  def oneToManyNoOppositeProps = [:]; def manyToOneProps = [:]; item.props.each { entityProp -> %>
<% def entity = entityProp.type; oneToManyNoOppositeProps[entity] = []; manyToOneProps[entity] = []; entity.propsRecursive.each { prop -> if(prop.type) {
   if (((prop.oneToMany && !prop.opposite) || (prop.mm)) && entityNames.contains(prop.type.name)) { oneToManyNoOppositeProps[entity] << prop }
   if (prop.manyToOne && entityNames.contains(prop.type.name)) { manyToOneProps[entity] << prop } } } } %>import ee.mdd.example.model.${item.n.cap.ids};
import ee.mdd.example.model.${item.name}; // TODO: c.name does not yet resolve items in sub packages like .model {{imports}}
@${c.name('Alternative')}
public class $className extends ${c.name('Base')} implements $item.name {
  private static final long serialVersionUID = 1L;

  protected ${item.n.cap.ids} removed = new ${item.n.cap.ids}();

  protected String source;
  protected ${c.name('Date')} timestamp;
  <% c.item.props.each { prop -> %>
  protected ${c.name(prop.type.name)} ${prop.type.instancesName}; <% } %><% oneToManyNoOppositeProps.each { entity, linkedProps -> linkedProps.each { prop -> def relationIdProp = prop.type.idProp %>
  protected ${c.name('LinkedObjectCache')}< <% if (entity.idProp.multi) {%>${c.name('List')}<entity.idProp.type><% } else {%>entity.idProp.type<% } %>, <% if (relationIdProp.multi) {%>${c.name('List')}<relationIdProp.type><% } else {%>relationIdProp.type<% } %>, prop.type.cap> ${entity.uncap}${prop.cap};<% } } %>

  public $className(boolean override, boolean threadSafe) {
    super();
    this.timestamp = ${c.name('TimeUtils')}.now();
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
    this.timestamp = ${c.name('TimeUtils')}.now(); <% item.props.each { prop -> %>
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
  public LinkedObjectCache<${entity.idProp.computedType(c)}, ${relationIdProp.computedType(c)}, $prop.type.cap> get${entity.name}${prop.cap}() {
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
}''')

  template('implContainerExtends', body: '''<% if(!c.item.name.endsWith("Container")) { c.className = c.item.n.cap.containerImpl } else { c.className = c.item.n.cap.impl } %>{{imports}}
@${c.name('Alternative')}
public class $className extends $item.n.cap.baseImpl {
  private static final long serialVersionUID = 1L;

  public $className() {
    super(false, false);
  }

  public $className(String source) {
    super(source, false, false);
  }

  public $className(boolean override) {
    super(override, false);
  }

  public $className(String source, boolean override) {
    super(source, override, false);
  }

  public $className(String source, boolean override, boolean threadSafe) {
    super(source, override, threadSafe);
  }

  public $className($item.name sourceContainer) {
    super(sourceContainer, false);
  }

  public $className($item.name sourceContainer, boolean threadSafe) {
    super(sourceContainer, threadSafe);
  }

  public $className(String source, $item.name sourceContainer) {
    super(source, sourceContainer, false);
  }

  public $className(String source, $item.name sourceContainer, boolean threadSafe) {
    super(source, sourceContainer, threadSafe);
  }
  ${macros.generate('implOperations', c)}
}

''')

  template('enum', body: '''<% if (!c.className) { c.className = item.cap } %>
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${c.item.component.name}Ml;{{imports}}
${item.description?"/*** $item.description */":''}
public enum $c.className implements ${c.name('Labeled')}, ${c.name('MlKeyBuilder')} {<% def last = item.literals.last(); item.literals.each { lit -> %>
  ${lit.definition}${lit == last ? ';' : ','}<% } %>
  ${macros.generate('props', c)}${macros.generate('enumConstructor', c)}${macros.generate('propGetters', c)}<% item.literals.each { lit -> %>

  public boolean $lit.is {
    return this == $lit.underscored;
  }<% } %><% item.operations.each { op -> if(op.body) { c.op = op %>
  <% if (op.override) { %>@Override<% } %><% if(op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  ${macros.generate('operationRawType', c)}<% } } %>
  ${macros.generate('buildMlKey', c)}

  @Override
  public String getLabel() {
    return name();
  }

  public static $className findByOrdinal(int ordinal) {
    if (ordinal < values().length) {
      return values()[ordinal];
    } else {
      throw new ${c.name('NotFoundException')}("$className(ordinal)", ordinal);
    }
  }<% if(item.defaultLiteral) { %>

  public static $className findByName(String name) {
    return findByName(name, $item.defaultLiteral.underscored);
  }<% } %>

  public static $className findByName(String name, $className defaultValue) {
    $className ret = defaultValue;
    if (name != null) {
      for ($className literal : values()) {
        if(literal.name().equalsIgnoreCase(name)) {
          ret = literal;
          break;
        }
      }
    }
    return ret;
  }
}''')

  template('jmsToCdi', body: '''<% if (!c.className) { c.className = item.n.cap.jmsToCdi } %>{{imports}}
/** Jms to Cdi bridge for '$module.name' */${macros.generate('metaAttributesBridge', c)}
public class $className extends ${c.name('JmsToEventListener')} {

  @Override
  @${c.name('Inject')}
  public void setDestinationConfig(@$component.name ${c.name('JmsDestinationConfig')} destinationConfig) {
    super.setDestinationConfig(destinationConfig);
  }

  ${macros.generate('setEventListener', c)}

  public void onChangeServiceLocator(@${c.name('Observes')}(notifyObserver = ${c.name('Reception')}.IF_EXISTS) ${c.name('ServiceLocator')} serviceLocator) {
    setServiceLocator(serviceLocator);
  }
}''')

  template('jmsToCdiMdb', body: '''<% if (!c.className) { c.className = item.n.cap.jmsToCdiMdb } %><% def cachedEntities = []; def cachedContainers = module.containers.findAll { it.controller.cache };
cachedContainers.each { cachedContainer -> cachedContainer.props.each { prop -> if(!cachedEntities.contains(prop.type)) { cachedEntities.add(prop.type) } } };
c.messageSelectors = cachedContainers.collect { "JMS_MSG_PROPERTY_TYPE_OF_OBJECT + \\"= '$it.cap'\\"" }; module.configs.each { messageSelectors << "JMS_MSG_PROPERTY_TYPE_OF_OBJECT + \\" = '$it.cap'\\"" }; cachedEntities.each { entity -> messageSelectors << "JMS_MSG_PROPERTY_TYPE_OF_OBJECT + \\" = '$entity.cap'\\"" } %>{{imports}}
/** Jms to Cdi MDB for '$module.name' for containers and config objects*/
${macros.generate('metaAttributesBridge', c)}
public class $c.className extends ${c.name('SingleTypeEventListenerBridgeByJms')}<Object> {

  ${macros.generate('setEventListenerExternal', c)}
}''')

  template('cdiToJms', body: '''<% if (!c.className) { c.className = item.n.cap.cdiToJms } %>{{imports}}
/** Cdi to Jms bridge for '$module.name' */
${macros.generate('metaAttributesBridge', c)}
public class $className extends ${c.name('JmsSender')} {

  @${c.name('Inject')}
  protected ${module.n.cap.moduleFactory} modelFactory;

  public $className () {
    super(JMS_NOTIFICATION_TOPIC, JMS_CONNECTION_FACTORY);
  }

  @SuppressWarnings("unchecked")
  protected <T> ${c.name('Event')}<T> perpareBeforeSent(Event<T> event) {
    if (${c.name('CollectionUtils')}.isNotEmpty(event.getObjectList())) {
      ${c.name('Factory')}<T> factory = (Factory<T>) modelFactory.findFactoryByType(event.getObjectType());
      if (factory != null) {
        ((${c.name('EventImpl')}<T>) event).setObjectList(factory.convertList(event.getObjectList()));
      }
    }
    return event;
  }<% module.entities.each { entity -> if(entity.event && !entity.virtual) { %>

  public void on${entity.n.cap.event}(@${c.name('Observes')} @$component.cap @${c.name('Backend')} ${entity.n.cap.event} event) {
    send(event);
  }<% } } %><% module.configs.each { config -> if(config.event) { %>

  public void on${config.n.cap.event}(@${c.name('Observes')} @$component.cap @${c.name('Backend')} ${config.n.cap.event} event) {
    send(event);
  }<% } } %><% module.containers.each { container -> %>

  public void on${container.n.cap.event}(@${c.name('Observes')} @$component.cap @${c.name('Backend')} ${container.n.cap.event} event) {
    send(event);
  }<% } %>

  @Override
  @${c.name('Resource')}(mappedName = JMS_CONNECTION_FACTORY)
  public void setConnectionFactory(${c.name('ConnectionFactory')} connectionFactory) {
    super.setConnectionFactory(connectionFactory);
  }

  @Override
  @Resource(mappedName = JMS_NOTIFICATION_TOPIC, type = ${c.name('Topic')}.class)
  public void setDestination(${c.name('Destination')} destination) {
    super.setDestination(destination);
  }
}''')

  template('eventToCdi', body: '''<% if(!c.className) { c.className = item.n.cap.eventToCdiBase } %>{{imports}}
/** Event Listener to Cdi for '$module.name' */
public abstract class $className extends ${c.name('MultiTypeCdiEventListener')} {

  @${c.name('Inject')}
  @${component.cap}
  protected Event<${c.name('ConnectionMetaEvent')}> connectionMetaEventPublisher;<% module.entities.each { entity -> if(entity.event && !entity.virtual) { %>

  @${c.name('Inject')}
  @${component.cap}
  protected Event<${entity.n.cap.event}> ${entity.uncap}Publisher;<% } } %><% module.configs.each { config-> if (config.event) { %>

  @${c.name('Inject')}
  @${component.cap}
  @${c.name('Backend')}
  protected Event<${config.n.cap.event}> ${config.uncap}Publisher;<% } } %><% module.containers.each { container-> %>

  @${c.name('Inject')}
  @${component.cap}
  @${c.name('Backend')}
  protected Event<${container.n.cap.event}> ${container.uncap}Publisher;<% } %>

  @${c.name('PostConstruct')}
  protected void postConstruct() {
    registerEventPublisher(ConnectionMetaEvent.class, connectionMetaEventPublisher);<% module.entities.each { entity-> if (entity.event && !entity.virtual) { %>
    registerEventPublisher(${entity.n.cap.event}.class, ${entity.uncap}Publisher);<% } } %><% module.configs.each { config -> if (config.event) { %>
    registerEventPublisher(${config.n.cap.event}.class, ${config.uncap}Publisher);<% } } %><% module.containers.each { container -> %>
    registerEventPublisher(${container.n.cap.event}.class, ${container.uncap}Publisher);<% } %>
  }
}''')

  template('eventToCdiExtends', body: '''<% if(!c.className) { c.className = item.n.cap.eventToCdi } %>{{imports}}
/** Listener for Cdi to Jms bridge for '$module.name' */${macros.generate('metaAttributesBridge', c)}
public class $className extends ${className}Base {
}''')

  template('eventToCdiExternal', body: '''<% if(!c.className) { c.className = item.n.cap.eventToCdiExternal } %>{{imports}}
/** Event Listener to Cdi for '$module.name' */
public abstract class $className extends ${c.name('MultiTypeCdiEventListener')} {<% module.entities.each { entity-> if (entity.event && !entity.virtual) { %>

  @${c.name('Inject')}
  @${component.cap}
  @${c.name('External')}
  protected Event<${entity.n.cap.event}> ${entity.uncap}Publisher;<% } } %><% module.configs.each { config -> if(config.event) { %>

  @${c.name('Inject')}
  @${component.cap}
  @${c.name('External')}
  protected Event<${config.n.cap.event}> ${config.uncap}Publisher;<% } } %><% module.containers.each { container -> %>

  @${c.name('Inject')}
  @${component.cap}
  @${c.name('External')}
  protected Event<${container.n.cap.event}> ${container.uncap}Publisher;<% } %>

  @${c.name('PostConstruct')}
  protected void postConstruct() {<% module.entities.each { entity -> if(entity.event && !entity.virtual) { %>
    registerEventPublisher(${entity.n.cap.event}.class, ${entity.uncap}Publisher);<% } } %><% module.configs.each { config -> if (config.event) { %>
    registerEventPublisher(${config.n.cap.event}.class, ${config.uncap}Publisher);<% } } %><% module.containers.each { container -> %>
    registerEventPublisher(${container.n.cap.event}.class, ${container.uncap}Publisher);<% } %>
  }
}''')

  template('eventToCdiExternalExtends', body: '''<% if(!c.className) { c.className = item.n.cap.eventToCdiExternal } %>{{imports}}
/** Listener for event to Cdi bridges for '$module.name' with 'External' qualifier. */${macros.generate('metaAttributesBridge', c)}
public class $className extends ${className}Base {
}''')

  template('notificationPlugin', body: '''<% if (!c.className) { c.className = component.cap+"NotificationPlugin" } %><% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %>{{imports}}
${macros.generate('metaAttributesBridge', c)}
public class $className extends PluginActivator {

  public static final String ID = ${className}.class.getName();
  <% modules.each { m-> %><% if(m.name == 'backend') { %>
  private ${m.parent.key.capitalize()}JmsToCdi ${m.parent.key}JmsToCdi;<% } else { %>
  private ${m.n.cap.jmsToCdi} ${m.name}JmsToCdi;<% } } %>

  public $className() {
    super(ID);
  }

  @Override
  protected void initialize(${c.name('LifecycleEvent')} event) {<% modules.each { m -> %><% if(m.name == 'backend') { %>
    ${m.parent.key}JmsToCdi.initialize();<% } else { %>
    ${m.name}JmsToCdi.initialize();<% } } %>
  }

  @Override
  protected void shutdown(LifecycleEvent event) {<% modules.each { m -> %><% if(m.name == 'backend') { %>
    ${m.parent.key}JmsToCdi.close();<% } else { %>
    ${m.name}JmsToCdi.close();<% } } %>
  }<% modules.each { m -> %><% if(m.name == 'backend') {%>

  @${c.name('Inject')}
  public void set${m.parent.key.capitalize()}JmsToCdi(${m.parent.key.capitalize()}JmsToCdi ${m.parent.key}JmsToCdi) {
    this.${m.parent.key}JmsToCdi = ${m.parent.key}JmsToCdi;
  }<% } else { %>
  @${c.name('Inject')}
  public void set${m.nam.capitalize()}JmsToCdi(${m.parent.key.capitalize()}JmsToCdi ${m.parent.key}JmsToCdi) {
    this.${m.parent.key}JmsToCdi = ${m.parent.key}JmsToCdi;
  }<% } } %>
}//TODO: Adapt to a future implementation of Backend and Shared modules''')

  template('constants', body: '''<% if (!c.className) { c.className = item.n.cap.constantsBase } %>{{imports}}
/** Constants for '${c.item.name}' */
public class $className {
  public static final String JMS_CONNECTION_FACTORY = com.siemens.ra.cg.pl.common.base.integ.CommonConstants.JMS_CONNECTION_FACTORY;
  public static final String JMS_CONNECTION_FACTORY_NOT_XA = com.siemens.ra.cg.pl.common.base.integ.CommonConstants.JMS_CONNECTION_FACTORY_NOT_XA;
  public static final String JMS_NOTIFICATION_TOPIC = "java:global/jms/cg/${component.key}/NotificationTopic";
  public static final String JMS_IMPORT_QUEUE = "java:global/jms/cg/${component.key}/ImportQueue";
<% item.modules.each { depModule -> %><% if(depModule.name != 'shared') { %>
  public static final String MODULE_${depModule.underscored} = "$depModule.uncap";<% depModule.services.each { service -> %>
  public static final String SERVICE_${service.underscored} = "${service.name}";<% } %><% depModule.containers.each { container -> %>
  public static final String JMS_MESSAGE_SELECTOR_${container.underscored} = "$container.uncap";
  public static final String JMS_MESSAGE_SELECTOR_${container.underscored}_DATA = "${container.uncap}_data";<% } %>
<% } } %>
}''')

  template('constantsExtends', body: '''<% if (!c.className) { c.className = item.n.cap.constants } %>
/** Constants for '$item.name' */
public class $className extends ${item.n.cap.constantsBase} {
}''')

  template('constantsMl', body: '''<% if (!c.className) { c.className = "${item.name}MlBase" } %>
/** Multi language constants for '${c.item.name}' */
public class $className {
  //base name for '$item.name' resource bundle
  public static final String ML_BASE = "${component.artifact}.ml_${component.artifact}";<% item.modules.each { depModule -> if(depModule.name != 'shared') { depModule.entities.each { def entity -> def finders = entity.finders; def commands = entity.commands;  %>
  public static final String $entity.underscored = "${entity.underscored.toLowerCase()}";<% if ( (commands || finders) && !entity.virtual ) { commands.updates.each { def op -> %>
  public static final String $op.underscored = "${op.underscored.toLowerCase()}";<% } } } %><% depModule.containers.each { def container -> %>
  public static final String $container.underscored = "${container.underscored.toLowerCase()}";
  public static final String ${container.underscored}_IMPORTED = "${container.underscored.toLowerCase()}_imported";
  public static final String ${container.underscored}_IMPORT_FAILED = "${container.underscored.toLowerCase()}_import_failed";
  public static final String ${container.underscored}_DELETED = "${container.underscored.toLowerCase()}_deleted";<% } } %>
  <% depModule.configs.each { def config -> %>
  public static final String $config.underscored = "${config.underscored.toLowerCase()};
  public static final String ${config.underscored}_UPDATED = "${config.underscored.toLowerCase()}_updated";<% } } %>
//StateMachine constants 
  <% item.modules.each { depModule -> depModule.stateMachines.each { sm -> sm.conditions.collect { cond -> cond.underscored+"_FAIL" }.each { failMl -> %> 
  public static final String ${failMl} = "${failMl.toLowerCase()}";<% } } }%>
}''')

  template('constantsMlExtends', body: '''<% if (!c.className) { c.className = "${item.key.capitalize()}Ml" } %>
/** Multi language constants for '${c.item.name}' */
public class $className extends ${className}Base {
}''')
  
  template('constantsRealm', body: '''<% if (item.realm && !item.realm.empty) { %>
/** Role related constants for '$item.name' */
public class $className {
  // users<% component.realm.users.each { user -> %>
  public static final String USER_${user.underscored} = "${user.uncap}";
  public static final String PASS_${user.underscored} = "${user.pass}";<% } %>
  // groups<% component.realm.groups.each { group -> %>
  public static final String GROUP_${group.underscored} = "${group.cap}";<% } %>
  // roles<% component.realm.roles.each { role -> %>
  public static final String ROLE_${role.underscored} = "${item.uncapShortName}_${role.name}";<% } %>
  // roles generated out from state machines<% def sm = []; item.children.each { it.children.each { if (it.generatePermissionsForEvents) { sm.add(it) }}} %><% if (sm != []) { %>
  <% sm.each { stateMachine -> def eventsMap = [:]; stateMachine.events.flatten().each{ event-> eventsMap.put(event.name, event)}; eventsMap.each { eventEntry-> %>
  public static final String ROLE_${stateMachine.underscored}_${eventEntry.value.underscored} = "${component.uncapShortName}_${stateMachine.uncapShortName}_${eventEntry.key}";<% } } } %>
}<% }%>''')

  template('implInjects', body: ''' <% def op = []; item.operations.each { opRef -> def ref = opRef.ref.parent %><% if (!op.contains(ref)) { %>

  @${c.name('Inject')}
  public void set${ref.cap}($ref.name $ref.uncap) {
    this.$ref.uncap = $ref.uncap;
  }<% op << ref %><% } } %>''')



  //tests


  template('beforeClass', body: '''
  public void before$className() {
    resetMocks();
  }''')

  template('afterClass', body: '''
  public void after$className() {
    verifyNoMoreInteractions();
  }''')

  template('testProperties', body: '''<% def list = [] %>
  @${c.name('Test')}
  public void testProperties() {<% item.props.each { prop -> %><% if (prop.testable) { %>
    ${c.name(prop.type)} $prop.uncap = $prop.testValue;<% } else if (prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { %><% if(prop.type.idProp!=null) { if(!((prop.type) in list)) { list << (prop.type); def relationIdProp = prop.type.idProp %><% if(relationIdProp.multi) { %>
    ${c.name('List')}<${relationIdProp.type.name}> ${prop.uncap}${relationIdProp.cap};<% } else { %>
    ${relationIdProp.type.name} ${prop.uncap}${relationIdProp.cap} = $relationIdProp.testValue;<% } } } %><% } else if (!prop.type.typeEnum && !prop.typeEntity) { %>
    ${c.name(prop.type)} $prop.uncap = new ${prop.type.n.cap.impl}();<% } } %>
    <% list = [] %><% item.props.each { prop -> if(prop.typeEntity && (prop.manyToOne || prop.oneToOne) && !((prop.type) in list)) {  list << prop.type %>
    item.$prop.call;<% } %><% if (!prop.type.typeEnum && !prop.typeEntity) { %>
    item.$prop.call;<% } }%>
    <% list = [] %><% item.props.each { prop -> if (!prop.type.typeEnum && !prop.typeEntity ) { %>
    ${c.name('assertEquals')}($prop.uncap, item.$prop.getter); <% } else if (prop.typeEntity && (prop.manyToOne || prop.oneToOne) && !((prop.type) in list)) { list << prop.type; def relationIdProp = prop.type.idProp %>
    ${c.name('assertEquals')}(${prop.uncap}${relationIdProp.cap}, item.get${prop.cap}${relationIdProp.cap}());<% } } %>
  }''')

  template('testExtends', purpose: UNIT_TEST, body: '''<% c.src = true %><% if (!c.className) { c.className = item.cap } %>{{imports}}
public class $c.className extends ${c.className}Base {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
}''')

  template('test', purpose: UNIT_TEST, body: '''<% if (!c.className) { c.className = item.cap } %><% if (!c.itemInit) { c.itemInit="new $item.n.cap.impl()" } %>
import static org.junit.Assert.*;{{imports}}
public abstract class $c.className {
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

  template('testEnum', purpose: UNIT_TEST, body: '''<% if (!c.className) { c.className = item.n.cap.test } %><% def lastLit = '' %>
import static org.junit.Assert.*;{{imports}}
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
}''')

  template('notificationPluginTest', purpose: UNIT_TEST, body: '''<% if(!c.className) { c.className = c.item.n.cap.notificationPluginTest } %><% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %>{{imports}}
//CHECKSTYLE_OFF: MethodName
//'_' allowed in test method names for better readability
@${c.name('RunWith')}(${c.name('MockitoJUnitRunner')}.class)
public class $className extends ${c.name('BaseTestCase')} {

  private ${component.n.cap.notificationPlugin} notificationPlugin;
  <% modules.each { m -> %>
  @${c.name('Mock')}
  private $m.n.cap.jmsToCdi ${m.uncap}JmsToCdi};<% } %>

  @${c.name('Before')}
  public void before() {
    notificationPlugin = new ${component.n.cap.notificationPlugin}();<% modules.each { m -> %>
    notificationPlugin.set${m.n.cap.jmsToCdi}(${m.n.cap.jmsToCdi});<% } %>
  }

  @Test
  public void initialize_forwardsTo_JmsToCdi() throws Exception {
    // given
    ${c.name('LifecycleEvent')} event = mock(LifecycleEvent.class);

    // when
    notificationPlugin.initialize(event);

    // then<% modules.each { m -> %>
    verify(${m.uncap}JmsToCdi}).initialize();<% } %>
  }

  @Test
  public void shutdown_forwardsTo_JmsToCdi() throws Exception {
    // given
    LifecycleEvent event = mock(LifecycleEvent.class);

    // when
    notificationPlugin.shutdown(event);

    // then<% modules.each { m -> %>
    verify(${m.uncap}JmsToCdi}).close();<% } %>
  }

}''')

  template('jmsToCdiTest', purpose: UNIT_TEST, body: '''<% if(!c.className) { c.className = item.n.cap.jmsToCdiTest } %>{{imports}}
//CHECKSTYLE_OFF: MethodName
//'_' allowed in test method names for better readability
public class $className extends ${c.name('JmsMessagingAdapterTestCase')} {

  @Override
  protected Class<? extends ${c.name('JmsToEventListener')}> getAdapterUnderTest() {
    return ${module.n.cap.jmsToCdi}.class;
  }

  @Test
  public void setEventListener_updatesInterServiceLocator() {
    // given
    ${module.n.cap.jmsToCdi} jmsToEventListener = new ${module.n.cap.jmsToCdi}();
    ${module.n.cap.eventToCdi} eventListener = mock(${module.n.cap.eventToCdi}.class);
    // when
    jmsToEventListener.setEventListener(eventListener);
    // then
    eventListenerIsUpdated(jmsToEventListener, eventListener);
  }
}''')

  template('cdiToJmsTest', purpose: UNIT_TEST, body: '''<% if(!c.className) { c.className = item.n.cap.cdiToJmsTest } %>{{imports}}
public class $className {

  protected static $item.n.cap.cdiToJms cdiToJms;
  protected static ${c.name('JmsSendExecutor')} executor;
  protected static ${c.name('Topic')} destination;
  protected static ${c.name('ConnectionFactory')} connectionFactory;


  @${c.name('BeforeClass')}
  public static void beforeClass$className() {
    cdiToJms = new $module.n.cap.cdiToJms();
    cdiToJms.setConnectionFactory(connectionFactory = mock(ConnectionFactory.class));
    cdiToJms.setDestination(destination = mock(Topic.class));
    cdiToJms.setJmsSendExecutor(executor = mock(JmsSendExecutor.class));
  }

  @${c.name('After')}
  ${macros.generate('afterClass', c)}

  @${c.name('Before')}
  ${macros.generate('beforeClass', c)}

  protected void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(executor);
  }

  protected void resetMocks() {
    MockitoCg.resetMocks(executor);
  }<% module.entities.each { entity-> if (entity.event && !entity.virtual) { %>

  public void testOn${entity.n.cap.event}() {
    ${entity.n.cap.event} event = mock(${entity.n.cap.event}.class);
    cdiToJms.on${entity.n.cap.event}(event);
    verify(executor).send(event, destination, connectionFactory);
  }<% } } %><% module.containers.each { container-> %>

  public void testOn${container.n.cap.event}() {
    ${container.n.cap.event} event = mock(${container.n.cap.event}.class);
    cdiToJms.on${container.n.cap.event}(event);
    verify(executor).send(event, destination, connectionFactory);
  }<% } %>
}''')

  template('eventToCdiTest', purpose: UNIT_TEST, body: '''<% if(!c.className) { c.className = item.n.cap.eventToCdiTest } %>{{imports}}
public class ${className} extends BaseTestCase {
  @${c.name('Test')}
  @Override
  public void testConstructorsForCoverage() throws Exception {
    constructorTester.verifyDefaultConstructor(${item.n.cap.eventToCdi}.class);
  }
}''')
  
  template('stateMachineControllerBaseTest', purpose: UNIT_TEST, body: '''<% def controller = item.controller; def idProp = item.entity.idProp %>{{imports}}
@${c.name('ApplicationScoped')}
public abstract class $className {
  protected final ${c.name('XLogger')} log = ${c.name('XLoggerFactory')}.getXLogger(getClass());
  protected final static int MAX_STATE_OCCURENCES = 3;

  protected $item.entity.finders.cap $item.entity.finders.uncap;
  protected $item.entity.commands.cap $item.entity.commands.uncap;
  protected $controller.cap $controller.uncap;
  protected ${item.entity.module.capShortName}DataFactoryBase dataFactory;

  protected $item.entity.cap $item.entity.uncap;
  protected ${item.capShortName}StateMetaModel stateMetaModel;
  protected ${item.capShortName}EventFactory eventFactory;<% if (item.history) { %>
  protected UmTestUtils umTestUtils;<% } %>

  protected HashMap<$item.stateProp.type.name, Integer> stateOccurences = new HashMap<>();

  @Before
  public void before() {
    $item.entity.uncap = ${item.entity.commands.uncap}.create(dataFactory.new${item.entity.cap}(1));
    assertNotNull("$item.entity.cap object is null.", $item.entity.uncap);
    stateMetaModel = ${controller.uncap}.findStateMetaModel();
    assertNotNull("State meta model is null.", stateMetaModel);<% if (item.history) { %>
    assertNotNull(umTestUtils.loginUserWithAllGroups(UmTestUtils.ADMINISTRATOR_NAME));<% } %>

    for ($item.stateProp.type.name state : ${item.stateProp.type.name}.values()) {
      stateOccurences.put(state, 0);
    }
  }

  @After
  public void after() {<% if (item.history) { %>
    umTestUtils.logout();<% } %>
    try {
      ${item.entity.commands.uncap}.delete(${item.entity.uncap}.$idProp.getter);
    } catch (Exception e) {
      log.error("Exception in after {}", e);
    }
  }

  @Test
  @Ignore
  public void testProcessNormalFlow() {
    testProcess(StateFlowType.NORMAL);
  }

    public void testProcess(StateFlowType flowType) {
    boolean run = true;<% if (item.history) { %>
    long count = 0;<% } %>
    while (run) {
      $item.stateProp.type.name startState = ${item.entity.uncap}.$item.stateProp.getter;
      log.info("Processing with state {} and $item.entity.uncap {}", startState, ${item.entity.uncap});
      ${item.capShortName}MetaState metaState = stateMetaModel.findMetaState(startState);
      if (metaState != null && metaState.getEvents().size() > 0) {

        for (${item.capShortName}StateEventType eventType : metaState.getEvents()) {
          if (flowType.equals(eventType.getFlowType())) {
            ${item.capShortName}StateEvent event = eventFactory.new${item.capShortName}StateEvent(eventType, ${item.entity.uncap}.getId()<%if (item.stateEvent) { item.stateEvent.props.size().times {%>, null<% } } %>, metaState.getState());
            fillEvent(event);
            log.info("Send event {}", event);

            ${item.entity.uncap} = ${controller.uncap}.process(event);<% if (item.history) { %>
            count++;
            assertEquals(((${item.entity.cap}Bean) $item.entity.uncap).getHistoryEntries().size(), count);<% } %>

            log.info("Result state {} and $item.entity.uncap {}", ${item.entity.uncap}.getState(), $item.entity.uncap);
            if (!startState.equals(${item.entity.uncap}.getState())) {
              break;
            }
          }
        }

        $item.stateProp.type.name currentState = ${item.entity.uncap}.getState();
        int stateOccurence = stateOccurences.get(currentState) + 1;
        stateOccurences.put(currentState, stateOccurence);

        //check if transitions for given flow type changed the state, if not exit.
        if (startState.equals(currentState)) {
          fail(String.format("Exit after state %s, because the state did not change after transition. Workflow might not be fully tested.", currentState));
        }

        //to avoid a infinite loop in workflow a certain state can only be passed a 'MAX_STATE_OCCURENCES' times
        if (stateOccurence > MAX_STATE_OCCURENCES) {
          String msg = String.format("Exit after %s times for same state %s. Following states are tested %s.",
              stateOccurence, currentState, stateOccurences);
          log.info(msg);
          if (stateMetaModel.getMetaStates().length > stateOccurences.size()) {
            fail("Count of tested states is %s, but there are %s states available. Workflow might not be fully tested.");
          } else {
            run = false;
          }
        }

      } else {
        run = false;
      }
    }
    log.info("End processing with state {} and $item.entity.uncap {}", ${item.entity.uncap}.getState(), $item.entity.uncap);
  }

  /** Fill event event in sub class with additional event parameters */
  protected void fillEvent(${item.capShortName}StateEvent event) {
    log.debug("fillEvent({})", event);
  }

  @${c.name('Inject')}
  public void set$controller.cap($controller.cap $controller.uncap) {
    this.$controller.uncap = $controller.uncap;
  }

  @${c.name('Inject')}
  public void set$item.entity.commands.name($item.entity.commands.cap $item.entity.commands.uncap) {
    this.$item.entity.commands.uncap = $item.entity.commands.uncap;
  }

  @${c.name('Inject')}
  public void set$item.entity.finders.name($item.entity.finders.cap $item.entity.finders.uncap) {
    this.$item.entity.finders.uncap = $item.entity.finders.uncap;
  }

  @${c.name('Inject')}
  public void setDataFactory(@Internal ${item.entity.module.capShortName}DataFactoryBase dataFactory) {
    this.dataFactory = dataFactory;
  }

  @${c.name('Inject')}
  public void setEventFactory(${item.capShortName}EventFactory eventFactory) {
    this.eventFactory = eventFactory;
  }<% if (item.history) { %>

  @${c.name('Inject')}
  public void setUmTestUtils(UmTestUtils umTestUtils) {
    this.umTestUtils = umTestUtils;
  }<% } %>
}''')
  
  template('controllerLocalTestInteg', purpose: UNIT_TEST, body: '''<% def controller = item.controller %>{{imports}}
// TODO: Migrate Weld test classes
// @${c.name('RunWith')}(LocalWeldRunner.class)
@${c.name('SupportsEnvironments')}(@${c.name('Environment')}(executions = { ${c.name('LOCAL')} }))
public class $className extends ${controller.cap}TestImpl {
}''')
  
  template('controllerMemoryTestInteg', purpose: UNIT_TEST, body: '''<% def controller = item.controller %>{{imports}}
// TODO: Migrate Weld test classes
// @${c.name('RunWith')}(MemoryWeldRunner.class)
@${c.name('SupportsEnvironments')}(@${c.name('Environment')}(executions = { ${c.name('MEMORY')} }))
public class $className extends ${controller.cap}TestImpl {
}''')
  
  template('controllerTest', purpose: UNIT_TEST, body: '''<% def controller = item.controller %>{{imports}}
public abstract class $className extends ${controller.cap}BaseTestImpl {
}''')
  
  template('conditionHandlerTest', purpose: UNIT_TEST, body: '''
public abstract class $className {

  protected ${item.cap}VerifierImpl verifier;

  public void setUp() {
    verifier = new ${item.cap}Impl();
  }

  @Test
  public void testEvaluateConditionStrictForEmptyContext() {
    try {
      verifier.evaluateConditionStrict(($item.stateMachine.context.name)null);
      fail("Exception expected.");
    } catch(Exception e) {
      //ignore
    }
  }
}''')
  
  template('conditionHandlerTestExtends', purpose: UNIT_TEST, body: '''
//CHECKSTYLE_OFF: MethodName
//'_' allowed in test method names for better readability
@${c.name('RunWith')}(MockitoJUnitRunner.class)
public class $className extends ${item.cap}TestBase {

  @Override
  @Before
  public void setUp() {
    super.setUp();
    // additional setup
  }

  // tests
}''')
  


  //metaAttributes


  template('metaAttributesEntity', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.metasForEntity(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
$ret''')

  template('metaAttributesBasicType', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.metasForBasicType(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
$ret''')

  template('metaAttributesService', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.metasForService(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
$ret''')

  template('metaAttributesBridge', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.metasForBridge(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
$ret''')

  template('jpaMetasEntity', body: '''<% if(!item.virtual) { %><% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.item.jpaMetasForEntity(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
${ret-newLine}<% } %>''')

  template('metaAttributesProp', body: '''<% def ret = ''; String newLine = System.properties['line.separator']; def annotations = c.prop.propMapping(c); if(annotations) { annotations.each { ret += newLine+it.annotation(c) } } %>
${ret-newLine}''')


  //logic


  template('setEventListener', body: '''@${c.name('Inject')}
  public void setEventListener(${module.cap}EventToCdi eventListener) {
    super.setEventListener(eventListener);
  }''')

  template('setEventListenerExternal', body: '''@${c.name('Inject')}
  public void setEventListener(${module.cap}EventToCdiExternal eventListener) {
    super.setEventListener(eventListener);
  }''')

  template('labelBody', body: '''<% if(item.labelBody) { %>
  @Override
  public String naturalKey() {
    return $item.labelBody;
  }<% } %>''')
  template('attributesChanged', body: '''<% if(item.attributeChangeFlag) { %>
  public boolean attributesChanged() {
    return this.attributesChanged;
  }

  public void clearAttributesChanged() {
    this.attributesChanged = false;
  }<% } %>''')

  template('propsToString', body: '''<% def idProp = item.idProp; def props = item.props.findAll{!it.primaryKey}; %>

  @Override
  protected void fillToString(StringBuffer b) {
    super.fillToString(b);<% if (idProp && !item.virtual) { %>
    b.append("$idProp.name=").append($idProp.name).append(SEP);<% } %><% props.each { prop -> if(!prop.typeEntity && prop.type.cap.matches('(String|Boolean|Long|Integer)')) { %><% if (prop.multi) { %>
    b.append("$prop.name=").append($prop.getter).append(SEP);<% } else { %>
    b.append("$prop.name=").append($prop.name).append(SEP);<% } %><% } }%>
  }''')
  
  template('hashCodeAndEquals', body: '''<% if (item.propsForHashCode) { %>

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ${item.superUnit ? 'super.hashCode()' : '1'};<% item.propsForHashCode.each { prop-> %>
    result = prime * result + <% if (prop.typeRef.primitive) { %>$prop.name;<% } else { %>(($prop.name == null) ? 0 : ${prop.name}.hashCode());<% } } %>
    return result;
  }

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
    $className other = (${className}) obj;<% item.propsForHashCode.each { prop-> if (!prop.primitive) { %>
    if (${prop.name} == null) {
      if (other.${prop.name} != null)
        return false;
    } else if (!${prop.name}.equals(other.${prop.name}))
      return false;<% } else { %>
    if (${prop.name} != other.${prop.name})
      return false;<% } %>
    <% } %>
    return true;
  }<% } %>''')

  template('hashCodeAndEqualsEntity', body: '''<% if(item.propsForHashCode) { %>

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ${item.superUnit ? 'super.hashCode()' : '1'};<% item.propsForHashCode.each { prop -> def propAccess = prop.primaryKey ? 'getId()' : prop.name; %>
    result = prime * result + (($propAccess == null) ? 0 : ${propAccess}.hashCode());<% } %>
    return result;
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
    $c.className other = (${c.className}) obj;<% item.propsForHashCode.each { prop -> def propAccess = prop.primaryKey ? 'getId()' : prop.name; %>
    if (${propAccess} == null) {
      if (other.${propAccess} != null)
        return false;
    } else if (!${propAccess}.equals(other.${propAccess}))
      return false;<% } %>
    return true;
  }<% } %>''')

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

  template('buildMlKey', body: '''
  public ${c.name('MlKey')} buildMlKey() {
    return new ${c.name('MlKeyImpl')}(${component.capShortName}Ml.ML_BASE, name());
  }''')

  template('propToIds', body: '''<% def op = c.op %>if (parent != null) {<% if (op.unique) { %>
        $idProp.type.name id = (($item.n.cap.cache${item.virtual ? '<E>' : ''})parent).${op.name}AsId($op.signatureName);
        if (id != null) {
          ret.add(id);
        }<% } else { %>
        ret = new ${c.name('HashSet')}<>(ret);
        ret.addAll((($item.n.cap.cache${item.virtual ? '<E>' : ''})parent).${op.name}AsId($op.signatureName));<% } %>
        ret.removeAll(deleted);
      }''')

  template('retNullOrDeleted', body: '''<% def op = c.op %>if (ret == null && parent != null) {
      ret = (($item.n.cap.cache${item.virtual ? '<E>' : ''})parent).${op.name}AsId($op.signatureName);
      if (ret != null && deleted.contains(ret)) {
        ret = null;
      }
}''')

  template('checkIdsNull', body: '''if (ids != null) {
      ret = ids.getTo();
    } else {
      ret = ${c.name('Collections')}.emptySet();
    }''')


  template('newDate', body: '''<% def ret = 'new Date();' %>$ret''')

  template('testBody', body: '''  int counter = countdown;
	while (counter!=0) {
	  System.out.println(counter+"...");
	  counter--;
	}
	System.out.println(test);''')


  // UI

  template('viewInterface', body: '''
/** Interface of ${item.name}. */
public interface $className extends ${className}Base {
}''')


  template('viewInterfaceBase', body: '''<% def baseClass = item.dialog ? 'DialogViewInterface' : 'ViewInterface' %>{{imports}}
/** Base interface of ${item.name}. */
public interface $className extends ${c.name(baseClass)} {<% item.controls.each { def control-> if ((control != null) && !control.static) { %>
  ${c.name(control.widgetInterface)} ${control.getter};
  <% } } %>
}''')

  template('viewModel', body: '''<% def model = item.model %>{{imports}}
/** View model implementation for ${item.name}. */
@${c.name('RootScoped')}
@${c.name('Model')}
public class $className extends $model.n.cap.base {
  ${macros.generate('implOperations', c)}
}''')

  template('viewModelBase', body: '''<% def model = item.model %>{{imports}}
/** Base view model implementation for ${item.name}. */
public abstract class $className extends ${c.name('BaseModel')} { <% def listProps = model.props.findAll { prop -> prop.multi } %>
  protected $model.n.cap.events forward;
  ${macros.generate('refsMember', c)}
  <% model.props.each { prop -> %>protected $prop.computedType(c) $prop.name;<% } %> <% if (listProps) { %>
  private ObservableFactory observableFactory;

  @PostConstruct
  void postConstruct() { <% listProps.each { prop -> %>
    $prop.name = observableFactory.newObservableList();<% } %>
  } <% } %>
  <% model.props.each { prop ->  %><% if (prop.multi && prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public $prop.computedType(c) $prop.getter {
    return <% if (prop.multi && prop.typeBasicType) { %>(List)<% } %>$prop.name;
  }<% if (prop.multi && prop.typeBasicType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public void $prop.setter<% if (prop.multi && prop.typeBasicType) { %>(List)<% } %> {
    this.$prop.name = $prop.name;
  } <% } %>
  <% model.handlers.each { def op -> if (op.forward) { %>
  public abstract void $op.handlerName($op.signatureValue);
  <% } } %>
  @${c.name('Inject')}
  public void set$model.n.cap.events($model.n.cap.events forward) {
    this.forward = forward;
    forward.set$model.cap(($model.cap) this);
  }
  <% if (listProps) { %>
  @${c.name('Inject')}
  public void setObservableFactory(ObservableFactory observableFactory) {
    this.observableFactory = observableFactory;
  } <% } %>
  ${macros.generate('implOperationsAndDelegates', c)}
}''')
  
  template('modelEventForwarderBase', body: '''<% def model = item.model %><% if(!c.className) { c.className = item.n.cap.eventsBase } %>
public interface $c.className {
  <% model.handlers.each { def op -> if (op.forward) { %>
  void $op.observerName($op.signatureValue);
  <% } } %>
  void set$model.cap($model.cap $model.uncap);
}''')
  
  template('modelEventForwarder', body: '''<% def model = item.model %><% if(!c.className) { c.className = item.n.cap.events } %>
public interface $className extends ${className}Base {
}''')
  
  template('presenterBase', body: '''<% def presenter = item.presenter %>{{imports}}
/** Base presenter implementation for ${item.name}. */
public abstract class $className extends ${c.name('Presenter')}<${item.cap}> {<% if (presenter.withMediator) { %>
  protected $item.n.cap.presenterEvents forward; <% } %>
  <% item.controls.each { def control-> control.operations.each { def op -> if (presenter.withMediator && op.forward) { %>
  <% if (!op.eventValueType) { %>@SuppressWarnings("unused")<% } %>
  public void $op.receiverName($op.signatureEvent) {
    forward.$op.receiverCallValue;
  } <% } else { %>
  public abstract void $op.receiverName($op.signatureEvent);<% } } } %>
  <% presenter.handlers.each { def op -> if (presenter.withMediator && op.forward) { %>
  public abstract void $op.handlerName($op.signatureValue);
  <% } } %> <% presenter.observers.each { def op -> if (op.forward) { %>
  public abstract void $op.observerName($op.signatureValue);
  <% } } %>
  <% if (presenter.withMediator) { %>@${c.name('Inject')}
  public void set$item.n.cap.presenterEvents($item.n.cap.presenterEvents forward) {
    this.forward = forward;
    forward.set$item.n.cap.presenterEvents(($presenter.cap) this);
  } <% } %>
}''')
  
  template('presenter', body: '''<% def presenter = item.presenter %>{{imports}}
/** Presenter implementation for ${item.name}. */
@${c.name('RootScoped')}
public class $className extends ${presenter.cap}Base {
}''')
  
  template('presenterEventForwarderBase', body: '''<% def view = item; def presenter = item.presenter %>
public interface $className {
  <% view.controls.each { def control-> control.operations.each { def op -> if (op.forward) { %>
  void $op.receiverName($op.signatureValue);
  <% } } } %>
  void set$presenter.cap($presenter.cap $presenter.uncap);
}''')
  
  template('presenterEventForwarder', body: '''<% def presenter = item.presenter %>
public interface $className extends ${className}Base {
}''')
  
  template('mediatorBase', body: '''<% def view = item.view %>{{imports}}
public abstract class $className implements $view.mediatorImplements { <% view.mediatorDelegates.each{ delegate -> %>
  protected $delegate.cap $delegate.uncap; <% } %>
  <% view.mediatorViews.each{ def presenter = it.presenter; it.controls.each { def control -> control.operations.each { def op -> if (op.forward) { %>
  @Override
  public void $op.receiverName($op.signatureValue) {<% op.handlers.each{ def handler -> %>
    $handler.uncap.$op.handlerCall;<% } %>
  }
  <% } } } } %><% def model = view.model; if (model) { model.handlers.each { def op -> if (op.forward) { %>
  @Override
  public void $op.observerName($op.signatureValue) {<% op.observers.each{ def observer -> %>
    $observer.uncap.$op.observerCall;<% } %>
  }
  <% } } } %><% view.mediatorDelegates.each{ delegate -> %>
  @Override
  public void set$delegate.cap($delegate.cap $delegate.uncap) {
    this.$delegate.uncap= $delegate.uncap;
  }<% } %>
}''')
  
  template('mediator', body: '''<% def view = item.view %>
@RootScoped
public class $className extends ${view.n.cap.mediatorBase-"View"} {
}
''')
  
  template('dialogGuidoBase', body: '''<% def dialog = item.dialog; def contentViewClassName = "${item.cap}Guido"-"View" %>
/** Base Guido implementation of ${item.name}. */
public abstract class $c.className extends DialogView {
  public static final String ID = ${item.dialog.cap}Guido.class.getName();

  protected $contentViewClassName contentView;

  ${macros.generate('superclassConstructor', c)}

  @Override
  protected void postViewCreated() {
    super.postViewCreated();
    initWidgets();
    initEventHandling();
  }

  protected void initEventHandling() {
  }

  @${c.name('Inject')}
  public void setContentView($contentViewClassName contentView) {
    this.contentView = contentView;
  }
}''')
  
  template('dialogGuido', body: '''<% def dialog = item.dialog %>{{imports}}
/** Guido implementation of ${item.name}. */
@${c.name('RootScoped')}(RootType.NEW)
@View
public class $className extends ${dialog.cap}GuidoBase {

  ${macros.generate('superclassConstructor', c)}

  ${macros.generate('initWidgets', c)}

  @Override
  protected void initEventHandling() {
    super.initEventHandling();
  }
}''')
  
  
  //StateMachine
  
  template('eventStateMachine', body: '''<% def entity = item.entity; def idProp = entity.idProp %>{{imports}}
/** Base state event interface for all state events of state machine $item.name */
public interface $className extends ${c.name('Serializable')} {

  /**
  * Expected state defines a kind of a limitation of the event. If the value is null, than the limitation will be ignored.
  * If it is not null, then the event is valid only if the current state of $item.entity.cap is same.
  * If the expected value is not equal to the current state, then the event will be rejected.
  */
  ${c.name(item.stateProp.type.name)} getExpectedState();

  $idProp.type.name get$idProp.capFullName();

  void set${idProp.capFullName}($idProp.type.name $idProp.uncapFullName);

  void setExpectedState($item.stateProp.type.name expectedState);

  $item.n.cap.stateEventType getType();

  void setActor(String actor);

  String getActor();
}''')
  
  template('eventType', body: '''import static ee.common.statemachine.StateFlowType.*;
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${component.capShortName}Ml;
{{imports}}
/** Events of state machine '$item.name' */
public enum $className implements ${c.name('MlKeyBuilder')} { <% def literals = item.events.collect {
  if (it.description) { "   /** it.description */   it.alternative? it.underscored(ALTERNATIVE):it.underscored(NORMAL)" }else { it.alternative ? it.underscored+"(ALTERNATIVE)":it.underscored+"(NORMAL)"} }.join(',   ') %>
  $literals;

  private ${c.name('StateFlowType')} flowType;

  private $className(StateFlowType flowType) {
    this.flowType = flowType;
  }<% item.events.each { event-> %>

  public boolean is${event.cap}() {
    return this == $event.underscored;
  }<% } %>
  
  @Override
  ${macros.generate('buildMlKey', c)}

  public StateFlowType getFlowType() {
    return flowType;
  }
}''')
  
  template('implEventStateMachine', body: '''<% def idProp = item.entity.idProp %>{{imports}}
/** Base implementation of {@link ${item.capShortName}StateEvent} */
public abstract class $className extends ${c.name('Base')} implements ${item.key.capitalize()}StateEvent {<% extraArgs = item.stateEvent ? ', ' + item.stateEvent.signatureFullConstr(c) : '' %>
  private static final long serialVersionUID = 1L;

  protected final ${item.capShortName}StateEventType type;
  protected ${c.name(item.stateProp.type.name)} expectedState;
  protected $idProp.type.name $idProp.uncapFullName;
  protected String actor;<% if (module.stateEvent) { module.stateEvent.props.each { prop ->%>
  protected $prop.type.name $prop.uncap;<% } } %>

  protected $className(${item.capShortName}StateEventType type) {
    this.type = type;
  }

  protected $className(${item.capShortName}StateEventType type, $idProp.type.name $idProp.uncapFullName$extraArgs) {
    this.type = type;
    this.$idProp.uncapFullName = $idProp.uncapFullName;<% if (item.stateEvent) { item.stateEvent.props.each { prop ->%>
    this.$prop.uncap = $prop.uncap;
    <% } } %>
  }

  protected $className(${item.capShortName}StateEventType type, $idProp.type.name $idProp.uncapFullName$extraArgs, $item.stateProp.type.name expectedState) {
    this.type = type;
    this.$idProp.uncapFullName = $idProp.uncapFullName;
    this.expectedState = expectedState;<% if (item.stateEvent) { item.stateEvent.props.each { prop ->%>
    this.$prop.uncap = $prop.uncap;
    <% } } %>
  }

  @Override
  public $item.stateProp.type.name getExpectedState() {
    return expectedState;
  }

  @Override
  public String getActor() {
    return actor;
  }

  @Override
  public $idProp.type.name get${idProp.capFullName}() {
    return $idProp.uncapFullName;
  }

  @Override
  public void set$idProp.capFullName($idProp.type.name $idProp.uncapFullName) {
    this.$idProp.uncapFullName = $idProp.uncapFullName;
  }

  @Override
  public void setExpectedState($item.stateProp.type.name expectedState) {
    this.expectedState = expectedState;
  }

  @Override
  public void setActor(String actor) {
    this.actor = actor;
  }

  @Override
  public ${item.capShortName}StateEventType getType() {
    return type;
  }<% if (item.stateEvent) { item.stateEvent.props.each { prop ->%>

  @Override
  public $prop.type.name get$prop.cap() {
    return $prop.uncap;
  }

  @Override
  public void set$prop.cap($prop.type.name $prop.uncap) {
    this.$prop.uncap = $prop.uncap;
  }<% } } %>

  @Override
  protected void fillToString(StringBuffer buffer) {
    super.fillToString(buffer);
    buffer.append("type=").append(type).append(SEP);
    buffer.append("expectedState=").append(expectedState).append(SEP);
    buffer.append("actor=").append(actor).append(SEP);
    buffer.append("${idProp.uncapFullName}=").append($idProp.uncapFullName);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expectedState == null) ? 0 : expectedState.hashCode());
    result = prime * result + ((actor == null) ? 0 : actor.hashCode());
    result = prime * result + ((${idProp.uncapFullName} == null) ? 0 : ${idProp.uncapFullName}.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    $className other = ($className) obj;
    if (expectedState != other.expectedState)
      return false;
    if (actor == null) {
      if (other.actor != null)
        return false;
    } else if (!actor.equals(other.actor))
      return false;
    if (${idProp.uncapFullName} == null) {
      if (other.${idProp.uncapFullName} != null)
        return false;
    } else if (!${idProp.uncapFullName}.equals(other.${idProp.uncapFullName}))
      return false;
    if (type != other.type)
      return false;
    return true;
  }
}''')
  
  template('eventFactory', body: '''<% def idProp = item.entity.idProp %>{{imports}}
/** Factory for all state events of state machine $item.name */
public interface $className {<% extraArgs = item.stateEvent ? ', ' + item.stateEvent.signatureFullConstr(c) : '' %>
  ${item.capShortName}StateEvent new${item.capShortName}StateEvent(${item.capShortName}StateEventType type, $idProp.type.name $idProp.uncapFullName$extraArgs);

  ${item.capShortName}StateEvent new${item.capShortName}StateEvent(${item.capShortName}StateEventType type, $idProp.type.name $idProp.uncapFullName$extraArgs, ${c.name(item.stateProp.type.name)} expectedState);<% item.events.each { event -> %><% if (!event.props) { %>

  ${event.cap}Event new${event.cap}Event($idProp.type.name $idProp.uncapFullName$extraArgs);<% } %>

  ${event.cap}Event new${event.cap}Event($idProp.type.name $idProp.uncapFullName$extraArgs, $item.stateProp.type.name expectedState);<% if (event.props) { %>

  ${event.cap}Event new${event.cap}Event($idProp.type.name $idProp.uncapFullName$extraArgs, ${event.signatureFullConstr(c)});

  ${event.cap}Event new${event.cap}Event($idProp.type.name $idProp.uncapFullName$extraArgs, $item.stateProp.type.name expectedState, ${event.signatureFullConstr(c)});<% } %><% } %>
}''')
  
  template('implEventFactory', body: '''<% def idProp = item.entity.idProp %>import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${component.key.capitalize()}Ml;
{{imports}}
public abstract class $className implements ${item.capShortName}EventFactory {<% argsConstr = item.stateEvent ? ', ' + item.stateEvent.signatureFullConstr(c) : ''; args = item.stateEvent ? ', ' + item.stateEvent.signatureNamesFullConstr(c) : ''; %>

  @Override
  public ${item.capShortName}StateEvent new${item.capShortName}StateEvent(${item.capShortName}StateEventType type, $idProp.type.name $idProp.uncapFullName$argsConstr) {
    return new${item.capShortName}StateEvent(type, $idProp.uncapFullName$args, null);
  }

  @Override
  public ${item.capShortName}StateEvent new${item.capShortName}StateEvent(${item.capShortName}StateEventType type, $idProp.type.name $idProp.uncapFullName$argsConstr, $item.stateProp.type.name expectedState) {
    ${item.capShortName}StateEvent ret;

    <% item.events.each { def event -> %>if (type.is${event.cap}()) {
      ret = new${event.cap}Event($idProp.uncapFullName$args, expectedState);
    } else <% } %>{
       throw new ${c.name('IllegalStateException')}(${component.capShortName}Ml.ML_BASE, "no_state_event", type);
    }
    return ret;
  }<% item.events.each { event-> %><% if (!event.props) { %>

  @Override
  public ${event.n.cap.event} new${event.n.cap.event}($idProp.type.name $idProp.uncapFullName$argsConstr) {
    return new ${event.n.cap.eventImpl}($idProp.uncapFullName$args);
  }<% } %>

  @Override
  public ${event.n.cap.event} new${event.n.cap.event}($idProp.type.name $idProp.uncapFullName$argsConstr, ${c.name(item.stateProp.type.name)} expectedState) {
    return new ${event.n.cap.eventImpl}($idProp.uncapFullName$args, expectedState);
  }<% if (event.props) { %>

  @Override
  public ${event.n.cap.event} new${event.n.cap.event}($idProp.type.name $idProp.uncapFullName$argsConstr, ${event.signatureFullConstr(c)}) {
    return new ${event.n.cap.eventImpl}($idProp.uncapFullName$args, ${event.signatureNamesFullConstr(c)});
  }

  @Override
  public ${event.n.cap.event} new${event.n.cap.event}($idProp.type.name $idProp.uncapFullName$argsConstr, ${c.name(item.stateProp.type.name)} expectedState, ${event.signatureFullConstr(c)}) {
    return new ${event.n.cap.eventImpl}($idProp.uncapFullName$args, expectedState, ${event.signatureNamesFullConstr(c)});
  }<% } %><% } %>
}''')
  
  template('implEventFactoryExtends', body: '''<% def idProp = item.entity.idProp %>{{imports}}
@${c.name('ApplicationScoped')}
@${c.name('Traceable')}
public class $className extends ${item.capShortName}EventFactoryBaseImpl {
}''')
  
  template('actionType', body: '''import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${component.key.capitalize()}Ml;
{{imports}}
/** Actions Enum of state machine $item.name */
public enum $className { <% def literals = item.actions.collect {
  if (it.description) { "   /** $it.description */   $it.underscored" } else { it.underscored} }.join(',   ') %>
  $literals;<% item.actions.each { action -> %>

  public boolean is${action.cap}() {
    return this == $action.underscored;
  }<% } %>

  ${macros.generate('buildMlKey', c)}
}''')
  
  template('conditionType', body: '''import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${component.key.capitalize()}Ml;
{{imports}}
/** Conditions Enum of state machine $module.name */
public enum $className { <% def literals = item.conditions.collect {
  if (it.description) { "   /** $it.description */   $it.name" }else { it.underscored } }.join(',   ') %>
  $literals;<% item.conditions.each { cond -> %>

  public boolean is${cond.cap}() {
    return this == $cond.underscored;
  }<% } %>

  ${macros.generate('buildMlKey', c)}
}''')
  
  template('stateMachineController', body: '''<% def controller = item.controller %>{{imports}}
<% if (item.description) { %>/**
* $item.description
*/<% } else { %>/** Event processor for state machine $item.name */<% } %>
public interface $className {
  ${c.name(item.entity.cap)} process(${item.capShortName}StateEvent event);
  ${item.capShortName}StateMetaModel findStateMetaModel();
  ${c.name(item.stateProp.type.name)} findCurrentState($item.entity.idProp.type.name $item.entity.idProp.uncap);<% controller.operations.each { op -> if(!op.override) { %>
  ${op.description?"   /** $op.description */":''}<% if (op.transactional) { %>
  @${c.name('Transactional')}<% } %>
  $op.ret ${op.name}($op.signature(c));<% } %><% } %>
}''')
  
  template('stateMachineControllerExtends', body: '''
<% if (item.description) { %>/**
* $item.description
*/<% } else { %>/** The controller is the entry point for state machine $item.name */<% } %>
public interface $className extends ${item.capShortName}ControllerBase {
}''')
  
  template('implStateMachineController', body: '''<% def controller = item.controller; def idProp = item.entity.idProp; def members = [] %>{{imports}}
@${c.name('Alternative')}
public class $className implements ${item.capShortName}Controller {
  protected final ${c.name('XLogger')} log = ${c.name('XLoggerFactory')}.getXLogger(getClass());
  <% item.states.each { def state -> %>
  protected ${item.capShortName}${state.cap}EventProcessor ${state.uncap}EventProcessor;<% } %>
  protected ${item.capShortName}StateMetaModel stateMetaModel;
  protected ${c.name('Provider')}<${item.capShortName}ContextManager> contextManagerDef;<% controller.operations.each { delegate -> if(delegate.ref) { if(!members.contains(delegate.ref.parent)) { %>
  protected ${c.name(delegate.ref.parent.name)} $delegate.ref.parent.uncap;<% } %><% members.add(delegate.ref.parent) %><% } } %>

  @Override
  public ${c.name(item.entity.cap)} process(${item.capShortName}StateEvent event) {
    log.${item.logLevel}("process({})", event);
    ${item.capShortName}ContextManager contextManager = contextManager();
    ${item.capShortName}Context context = contextManager.loadContext(event);
    validateExpectedAndCurrentState(context);
    process(context);
    log.${item.logLevel}("completed transition '{}')", context.getCurrentTransition());
    while (context.redirectEvent()) {
      log.${item.logLevel}("process redirect event '{}'", context.getEvent());
      process(context);
    }
    return contextManager.storeContext(context);
  }

  protected void process(${item.capShortName}Context context) {
    ${item.capShortName}StateEventProcessor stateEventProcessor = findEventProcessorNotNull(context);
    stateEventProcessor.process(context);
  }

  @Override
  public ${item.capShortName}StateMetaModel findStateMetaModel() {
    return stateMetaModel;
  }

  @Override
  public ${c.name(item.stateProp.type.name)} findCurrentState($item.entity.idProp.type.name $item.entity.idProp.uncapFullName) {
    return contextManager().findCurrentState($item.entity.idProp.uncapFullName);
  }

  protected ${item.capShortName}StateEventProcessor findEventProcessorNotNull(${item.capShortName}Context context) {
    ${item.capShortName}StateEventProcessor ret;
    ${item.stateProp.type.name} $item.stateProp.name = context.getState();

    <% item.states.each { def state -> %>if (${item.stateProp.name}.is${state.cap}()) {
      ret = ${state.uncap}EventProcessor;
    } else <% } %>{
      throw new ${c.name('IllegalStateException')}(${c.name('CommonConstants')}.ML_BASE, CommonConstants.ML_KEY_UNEXPECTED_EVENT_FOR_STATE, context.getEvent().getType(), context.getState());
    }
    return ret;
  }

  protected void validateExpectedAndCurrentState(${item.capShortName}Context context) {
    //check if expected state and current state are same
    ${item.capShortName}StateEvent event = context.getEvent();
    if (event.getExpectedState() != null) {
      if (!event.getExpectedState().equals(context.getState())) {
        throw new ${c.name('IllegalStateException')}(${c.name('CommonConstants')}.ML_BASE, CommonConstants.ML_KEY_EXPECTED_AND_CURRENT_STATE_DIFFERENT,
            event.getExpectedState(), context.getState());
      }
    }
  }

  protected ${item.capShortName}ContextManager contextManager() {
    return contextManagerDef.get();
  }

  @${c.name('Inject')}
  public void set${item.capShortName}StateMetaModel(${item.capShortName}StateMetaModel stateMetaModel) {
    this.stateMetaModel = stateMetaModel;
  }

  @${c.name('Inject')}
  public void setContextManagerDef(Provider<${item.capShortName}ContextManager> contextManagerDef) {
    this.contextManagerDef = contextManagerDef;
  }<% item.states.each { def state-> %>

  @${c.name('Inject')}
  public void set${item.capShortName}${state.cap}EventProcessor(${item.capShortName}${state.cap}EventProcessor ${state.uncap}EventProcessor) {
    this.${state.uncap}EventProcessor = ${state.uncap}EventProcessor;
  }<% } %>

}''')

  template('implStateMachineControllerExtends', body: '''<% def controller = item.controller %>{{imports}}
@${c.name('ApplicationScoped')}
@${c.name('Controller')}
@${c.name('SupportsEnvironments')}({
    @${c.name('Environment')}(executions = { ${c.name('PRODUCTIVE')} }, runtimes = { ${c.name('SERVER')} }),
    @${c.name('Environment')}(executions = { ${c.name('LOCAL')}, ${c.name('MEMORY')} }, runtimes = { ${c.name('CLIENT')} }) })
public class $className extends ${item.capShortName}ControllerBaseImpl {
}''')
  
  template('metaModel', body: '''{{imports}}
/** Meta model for state machine $item.name provides static information of available states, events and actions. */
@${c.name('ApplicationScoped')}
@${c.name('SupportsEnvironments')}({
    @${c.name('Environment')}(executions = { ${c.name('PRODUCTIVE')} }, runtimes = { ${c.name('SERVER')} }),
    @Environment(executions = { LOCAL, MEMORY, PRODUCTIVE }, runtimes = { CLIENT }) })
public class $className extends ${c.name('Base')} {
  private static final long serialVersionUID = 1L;
  <% item.states.each { state -> %>
  private ${item.capShortName}${state.cap}MetaState ${state.uncap}MetaState;<% } %>

  private ${item.capShortName}MetaState[] metaStates;

  ${macros.generate('superclassConstructor', c)}

  protected $className(${item.capShortName}MetaState[] metaStates) {
    super();
    this.metaStates = metaStates;
  }

  @${c.name('PostConstruct')}
  public void postConstruct() {
    metaStates = new ${item.capShortName}MetaState[] {   <% def metaStates = item.states.collect { state -> "${state.uncap}MetaState" }.join(',\\n      ') %>
      $metaStates
    };
  }

  public $className copy() {
    return new $className(metaStates);
  }

  public ${item.capShortName}MetaState[] getMetaStates() {
    return metaStates;
  }

  public ${item.capShortName}MetaState findMetaState(${c.name(item.stateProp.type.name)} state) {
    ${item.capShortName}MetaState ret = null;
    if (state != null) {
      for (${item.capShortName}MetaState metaState : metaStates) {
        if (state.equals(metaState.getState())) {
          ret = metaState;
          break;
        }
      }
    }
    return ret;
  }

  @Override
  protected void fillToString(StringBuffer buffer) {
    super.fillToString(buffer);
    buffer.append("metaStates=").append(${c.name('Arrays')}.toString(metaStates));
  }<% item.states.each { state -> %>

  @${c.name('Inject')} 
  public void set${item.capShortName}${state.cap}MetaState(${item.capShortName}${state.cap}MetaState ${state.uncap}MetaState) {
    this.${state.uncap}MetaState = ${state.uncap}MetaState;
  }<% } %>
}''')
  
  template('metaState', body: '''{{imports}}
/** Static information about events and actions for a state of state machine $module.name */
@${c.name('Alternative')}
public abstract class $className extends ${c.name('Base')} {
  private static final long serialVersionUID = 1L;

  protected ${c.name(item.stateProp.type.name)} state;
  protected ${c.name('List')}<${item.capShortName}StateEventType> events;<% module.notifiables.each { toBeNotified -> %>
  protected boolean ${toBeNotified}ToBeNotified = false;<% } %>
  protected ${c.name('Provider')}<UserInRoleConditionVerifier> userInRoleConditionVerifierDef;
  protected ${c.name('UserInRoleConditionVerifier')} userInRoleConditionVerifier;

  // required to be proxyable
  protected $className() {
    super();
  }

  public $className(${item.stateProp.type.name} state, List<${item.capShortName}StateEventType> events) {
    super();
    this.state = state;
    this.events = events;
  }

  public $item.stateProp.type.name getState() {
    return state;
  }

  public List<${item.capShortName}StateEventType> getEvents() {
    return events;
  }<% item.notifiables.each { toBeNotified -> %>

  public boolean is${toBeNotified.capitalize()}ToBeNotified() {
    return ${toBeNotified}ToBeNotified;
  }<% } %>

  public abstract List<${item.capShortName}StateEventType> findPossibleEvents(${item.context.cap} context);

  @Override
  protected void fillToString(StringBuffer buffer) {
    super.fillToString(buffer);
    buffer.append("state=").append(state).append(SEP);
    buffer.append("events=").append(events);
  }

  public void setUserInRoleConditionVerifier(UserInRoleConditionVerifier userInRoleConditionVerifier) {
    this.userInRoleConditionVerifier = userInRoleConditionVerifier;
  }

  @${c.name('Inject')}
  public void setUserInRoleConditionVerifierDef(Provider<UserInRoleConditionVerifier> userInRoleConditionVerifierDef) {
    this.userInRoleConditionVerifierDef = userInRoleConditionVerifierDef;
  }

  protected UserInRoleConditionVerifier userInRoleConditionVerifier() {
    if(userInRoleConditionVerifier == null) {
        userInRoleConditionVerifier = userInRoleConditionVerifierDef.get();
    }
    return userInRoleConditionVerifier;
  }
}''')
  
  template('contextManager', body: '''{{imports}}
public interface $className {

  ${c.name(item.context.cap)} loadContext(${item.capShortName}StateEvent event);

  ${c.name(item.entity.cap)} storeContext(${item.context.cap} context);

  ${c.name(item.stateProp.type.name)} findCurrentState($item.entity.idProp.type.name $item.entity.idProp.uncapFullName);

  ${c.name('List')}<${item.entity.cap}> findExpired${item.entity.instancesName.capitalize()}();

}''')
  
  template('contextManagerExtends', body: '''{{imports}}
public interface $className extends ${className}Base {
}''')
  
  template('implContextManager', body: '''{{imports}}
public abstract class $className implements ${item.capShortName}ContextManager {
  protected final ${c.name('XLogger')} log = ${c.name('XLoggerFactory')}.getXLogger(getClass());

  protected ${c.name('SessionPrincipal')} sessionPrincipal;
  protected ${c.name('UserInRoleConditionVerifier')} userInRoleConditionVerifier;

  @Override
  public ${item.capShortName}Context loadContext( ${item.capShortName}StateEvent event) {
    log.$item.logLevel("loadContext({})", event);
    ${item.capShortName}Context context = new ${item.capShortName}Context();
    context.setEvent(event);
    context.setSessionPrincipal(sessionPrincipal);
    context.setUserInRoleConditionVerifier(userInRoleConditionVerifier);

    return fillContext(context);
  }

  protected abstract ${item.capShortName}Context fillContext(${item.capShortName}Context context);

  @${c.name('Inject')}
  public void setSessionPrincipal(SessionPrincipal sessionPrincipal) {
    this.sessionPrincipal = sessionPrincipal;
  }

  @${c.name('Inject')}
  public void setUserInRoleConditionVerifier(UserInRoleConditionVerifier userInRoleConditionVerifier) {
    this.userInRoleConditionVerifier = userInRoleConditionVerifier;
  }
}''')
  
  template('implContextManagerExtends', body: '''{{imports}}//Manual imports because c.name() does not resolve these classes
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.${item.entity.n.cap.commands};
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.${item.entity.n.cap.finders};
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.${item.history.entity.n.cap.commands};
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.${item.history.entity.n.cap.finders};
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.ejb.${item.history.entity.n.cap.entity};

@${c.name('Controller')}
@${c.name('SupportsEnvironments')}({
    @${c.name('Environment')}(executions = { ${c.name('PRODUCTIVE')} }, runtimes = { ${c.name('SERVER')} }),
    @${c.name('Environment')}(executions = { LOCAL, MEMORY }, runtimes = { CLIENT }) })
public class $className extends ${item.capShortName}ContextManagerBaseImpl {

  protected ${item.entity.n.cap.commands} ${item.entity.uncap}Commands;
  protected ${item.entity.n.cap.finders} ${item.entity.uncap}Finders;<% if (item.history) { %>
  protected ${item.history.entity.n.cap.commands} ${item.history.entity.uncap}Commands;
  protected ${item.history.entity.n.cap.finders} ${item.history.entity.uncap}Finders;<% } %>

  @Override
  protected ${item.capShortName}Context fillContext(${item.capShortName}Context context) {
    ${c.name(item.entity.cap)} entity = ${item.entity.uncap}Finders.findByIdStrict(context.getEvent().get${item.entity.idProp.capFullName}());
    context.set${item.entity.cap}(entity);
    context.set${item.stateProp.cap}(entity.get${item.stateProp.cap}());
    return context;
  }

  @Override
  public ${item.entity.cap} storeContext(${item.capShortName}Context context) {
    log.$item.logLevel("storeContext({})", context);
    ${item.entity.cap} ${item.entity.uncap} = context.get${item.entity.cap}();
    if (context.getNewState() != null) {
      <% if (item.timeoutEnabled) { %>${item.entity.uncap}.${item.stateTimeoutProp.setterMethodName}(context.getNewTimeout());<% } %>
      ${item.entity.uncap} = ${item.entity.uncap}Commands.update${item.stateProp.cap}($item.entity.uncap, context.getNewState(), true);
      context.set${item.entity.cap}(${item.entity.uncap});<% if (item.history) { %>
      create${item.history.entity.name}(context);<% } %>
    }
    return ${item.entity.uncap};
  }

  @Override
  public ${c.name(item.stateProp.type.name)} findCurrentState($item.entity.idProp.type.cap $item.entity.idProp.uncapFullName) {
    ${item.entity.cap} entity = ${item.entity.uncap}Finders.findByIdStrict($item.entity.idProp.uncapFullName);
    $item.stateProp.type.name ret = entity.get${item.stateProp.cap}();
    return ret;
  }

  @Override
  public ${c.name('List')}<$item.entity.cap> findExpired${item.entity.instancesName.capitalize()}() {
    return ${item.entity.uncap}Finders.findBy$item.stateTimeoutProp.cap(${c.name('TimeUtils')}.now());
  }<% if (item.history) { def history = item.history; %>

  protected void create$history.entity.name(${item.capShortName}Context context) {
    log.$item.logLevel("Creating history entry for state change");
    $item.entity.cap $item.entity.uncap = context.get${item.entity.cap}();
    ${history.entity.n.cap.entity} ret = new ${history.entity.cap}Entity();
    /** TODO: Generic types must be resolvable in model in order to make this part compilable. We replaced generic types by String for now.
    *<% if (history.oldState) { %>ret.set$history.oldState.cap(context.getState());
    *<% }; if (history.newState) { %>ret.set$history.newState.cap(context.getNewState());
    *<% }; if (history.actor) { %>ret.set$history.actor.cap(context.getSessionPrincipal().getUser());
    *<% }; if (history.action) { %>ret.set$history.action.cap(context.getEvent().getType().buildMlKey());
    *<% }; if (history.dateOfOccurrence) { %>ret.set$history.dateOfOccurrence.cap(${c.name('now')}());
    *<% }; if (history.reason) { %>ret.set$history.reason.cap(${item.entity.uncap}.get$history.reason.cap());
    *<% }; if (history.stateMachineEntityHistoryEntries) { %>((${item.entity.cao}Entity)${item.entity.uncap}).addTo$history.stateMachineEntityHistoryEntries.cap(ret);
    <% } %>*/
    ${history.entity.uncap}Commands.create(ret, true);
  }

  @${c.name('Inject')}
  public void set${item.history.entity.cap}Commands(${item.history.entity.cap}Commands ${item.history.entity.uncap}Commands) {
    this.${item.history.entity.uncap}Commands = ${item.history.entity.uncap}Commands;
  }

  @${c.name('Inject')}
  public void set${item.history.entity.cap}Finders(${item.history.entity.cap}Finders ${item.history.entity.uncap}Finders) {
    this.${item.history.entity.uncap}Finders = ${item.history.entity.uncap}Finders;
  }<% } %>

  @${c.name('Inject')}
  public void set${item.entity.cap}Commands(${item.entity.cap}Commands ${item.entity.uncap}Commands) {
    this.${item.entity.uncap}Commands = ${item.entity.uncap}Commands;
  }

  @${c.name('Inject')}
  public void set${item.entity.cap}Finders(${item.entity.cap}Finders ${item.entity.uncap}Finders) {
    this.${item.entity.uncap}Finders = ${item.entity.uncap}Finders;
  }
}''')
  
  template('stateEventProcessor', body: '''{{imports}}
/** Event processor for single state of the state machine $item.name */
public interface $className {
  void process(${item.capShortName}Context context);
}''')
  
  template('implStateEventProcessor', body: '''{{imports}}
@${c.name('Traceable')}
public class $className implements ${item.capShortName}StateEventProcessor {
  protected final ${c.name('XLogger')} log = ${c.name('XLoggerFactory')}.getXLogger(getClass());
  protected final String source = ${c.name('StringUtils')}.formatSource(this);<% if (item.timeoutEnabled) { %>

  protected ${item.capShortName}Timeouts stateTimeouts;<% } %>

  @Override
  public void process(${item.capShortName}Context context) {
    log.warn("Unexpected event type '{}' for current state '{}' - process({})", context.getEvent().getType(), context.getState(), context);
    throw new ${c.name('IllegalStateException')}(${c.name('CommonConstants')}.ML_BASE, CommonConstants.ML_KEY_UNEXPECTED_EVENT_FOR_STATE,
        context.getEvent().getType(), context.getState());
  }

  protected void processNoValidFlow(${item.capShortName}Context context) {
    log.info("processNoValidFlow({})", context);
    ${item.capShortName}TransitionExecutionResult<?> lastTransition = context.getCurrentTransition();
    throw new ${c.name('IllegalStateException')}(${c.name('CommonConstants')}.ML_BASE, CommonConstants.ML_KEY_NO_VALID_FLOW, lastTransition.getFromStateAsMlKey(), lastTransition.getToStateAsMlKey(), lastTransition.getEventTypeAsMlKey(), lastTransition.getFailedConditionAsMlKey());
  }<% if (item.timeoutEnabled) { %>

  @${c.name('Inject')}
  public void setStateTimeouts(${item.capShortName}Timeouts stateTimeouts) {
    this.stateTimeouts = stateTimeouts;
  }<% } %>
}''')
  
  template('implStateEventProcessorExtends', body: '''{{imports}}
@${c.name('Traceable')}
public class $className extends ${item.capShortName}StateEventProcessorBaseImpl {
}''')
  
  template('context', body: '''<% def entity = item.entity; def idProp = entity.idProp; def context = item.context %>import static com.siemens.ra.cg.pl.common.base.util.TimeUtils.*;
{{imports}}
${context.description?"/*** $context.description */":''}
public class $className extends ${c.name('Base')} {
  private static final long serialVersionUID = 1L;
  protected transient ${c.name('UserInRoleConditionVerifier')} userInRoleConditionVerifier;
  // DEFAULT props for Context cannot be set dynamically yet because type resolution is missing
  protected ${c.name('SessionPrincipal')} sessionPrincipal;
  protected ${item.capShortName}StateEvent event;
  protected ${item.capShortName}StateEvent redirectEvent;
  protected ${c.name('List')}<${item.capShortName}TransitionExecutionResult<?>> transitions = new ${c.name('ArrayList')}<>();
  protected ${item.capShortName}TransitionExecutionResult<?> currentTransition;
  protected ${c.name('Date')} timeout;
  protected ${c.name('Date')} newTimeout;
  protected ${c.name(entity.cap)} $entity.uncap;
  protected ${c.name(item.stateProp.type.name)} state;
  protected ${c.name(item.stateProp.type.name)} newState;<% context.props.each { prop -> %>
  protected ${prop.computedType(c)} $prop.uncap<% if (prop.defaultValue != null) { %> = ${prop.defaultLiteral}<% if (prop.type.name == 'Long' || prop.type.name == 'long') { %>L<% } %><% } %>;<% } %>
  //cached conditions<% item.conditions.findAll { it.cachedInContext }.each { con -> %>
  protected Boolean $con.uncap;<% } %><% context.props.each { prop-> %><% if (prop.description) { %>

  /*** $prop.description */<% } %>
  public ${prop.computedType(c)} $prop.getter {
    return $prop.uncap;
  }<% if (context.propSetters) { %>
  public void ${prop.setter} {
    this.$prop.uncap = $prop.uncap;
  }<% } %><% } %>public SessionPrincipal getSessionPrincipal() {
    return sessionPrincipal;
  }
  
  public void setSessionPrincipal(SessionPrincipal sessionPrincipal) {
    this.sessionPrincipal = sessionPrincipal;
  }

  public ${item.capShortName}StateEvent getEvent() {
    return event;
  }

  public void setEvent(${item.capShortName}StateEvent redirectEvent) {
    this.redirectEvent = redirectEvent;
  }

  public ${item.capShortName}StateEvent getRedirectEvent() {
    return redirectEvent;
  }

  public void setRedirectEvent(${item.capShortName}StateEvent redirectEvent) {
    this.redirectEvent = redirectEvent;
  }

  public Date getTimeout() {
    return timeout;
  }

  public void setTimeout(Date timeout) {
    this.timeout = timeout;
  }

  public Date getNewTimeout() {
    return newTimeout;
  }

  public void setNewTimeout(Date newTimeout) {
    this.newTimeout = newTimeout;
  }
  
  public ${item.stateProp.type.name} getState() {
    return state;
  }

  public void setState(${item.stateProp.type.name} state) {
    this.state = state;
  }

  public ${item.stateProp.type.name} getNewState() {
    return newState;
  }

  public void setNewState(${item.stateProp.type.name} newState) {
    this.newState = newState;
  }

  public ${entity.cap} get${entity.cap}() {
    return $entity.uncap;
  }

  public void set${entity.cap}($entity.cap $entity.uncap) {
    this.$entity.uncap = $entity.uncap;
  }

  public ${item.capShortName}TransitionExecutionResult<?> getCurrentTransition() {
    return currentTransition;
  }

  public void setCurrentTransition(${item.capShortName}TransitionExecutionResult<?> currentTransition) {
    this.currentTransition = currentTransition;
  }
  
  <% context.operations.each { op -> if(op.body) { %><% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  public $op.ret ${op.name}($op.signature) {
    $op.body
  }<% } } %><% if(context.propsUpdate) { %>
  public void update($context.cap $context.uncap) {<% context.props.each { prop-> %><% if (context.propSetters) { %>
    $prop.setterMethodName(${context.uncap}.${prop.getter});<% } else { %>
    $prop.uncap = ${context.uncap}.${prop.getter};<% } %><% } %>
  }<% } %>

  public <E extends ${item.capShortName}StateEvent> ${item.capShortName}TransitionExecutionResult<E> startTransitionTo($item.stateProp.type.name newState) {
    return startTransitionTo(newState, null);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <E extends ${item.capShortName}StateEvent> ${item.capShortName}TransitionExecutionResult<E> startTransitionTo($item.stateProp.type.name newState, ${item.capShortName}StateEvent redirectEvent) {
    transitions.add(currentTransition = new ${item.capShortName}TransitionExecutionResult<>(state, newState, event, redirectEvent));
    return (${item.capShortName}TransitionExecutionResult)currentTransition;
  }

  public boolean redirectEvent() {
    boolean ret = false;
    if (redirectEvent != null) {
      ret = true;
      event = redirectEvent;
      redirectEvent = null;
      state = newState;
      newState = null;<% if (item.timeoutEnabled) { %>
      newTimeout = null;<% } %>
    }
    return ret;
  }

  public void completeTransition() {<% if (item.timeoutEnabled) { %>
    newTimeout = null;<% } %>
    newState = currentTransition.getToState();
  }<% if (item.timeoutEnabled) { %>

  public void completeTransition(int timeoutInMillis) {
    if (timeoutInMillis > 0) {
      this.newTimeout = createDateAddMillis(now(), timeoutInMillis);
    } else {
      this.newTimeout = null;
    }
    newState = currentTransition.getToState();
  }<% } %>

  public void completeTransition(${item.capShortName}StateEvent redirectEvent) {
    this.redirectEvent = redirectEvent;
    completeTransition();
  }

  //handling for cache condition values<% item.conditions.findAll { it.cachedInContext }.each { con -> %>

  /** Set the verification value of the condition $con.name and return it in order to use the method in if statements */
  public boolean change$con.cap(boolean $con.uncap) {
    this.$con.uncap = $con.uncap;
    return $con.uncap;
  }

  public Boolean is$con.cap() {
    return $con.uncap;
  }<% } %>

  public void evaluateUserInRoleStrict(String role) {
    if(userInRoleConditionVerifier != null) {
        userInRoleConditionVerifier.evaluateConditionStrict(role);
    } else {
        throw new ${c.name('IllegalStateException')}("userInRoleConditionVerifier", "not_null", "null");
    }
  }

  public void setUserInRoleConditionVerifier(UserInRoleConditionVerifier userInRoleConditionVerifier) {
    this.userInRoleConditionVerifier = userInRoleConditionVerifier;
  }

  @Override
  protected void fillToString(StringBuffer b) {
    super.fillToString(b);<% item.props.each { prop-> if (!prop.multi && prop.type.name.matches('(String|Boolean|boolean|Long|long|Integer|int)')) { %>
    b.append("$prop.name=").append($prop.name).append(SEP);<% } } %>
    b.append($entity.uncap != null ? ${entity.uncap}.${idProp.getter} : null).append(SEP);
    if (state != null) {
      b.append(state.name());
    }
    if (newState != null) {
      b.append("->");
      b.append(newState.name());
    }
  }
}''')
  
  template('contextExtends', body: '''<% def context = item.context %>{{imports}}<% if(context.description) { %>
/*** @see ${item.capShortName}ContextBase*/<% } %>
public class $className extends ${item.capShortName}ContextBase {
  private static final long serialVersionUID = 1L;
}''')
  
  template('actionExecutor', body: '''
/**
* An action handler is responsible to execute a transition or state action of for state machine $item.name.
* The execution result is a boolean value, what is handled as transition condition.
* If it is false, then the transition can not be applied and the state event handler will proceed with next possible transitions or abort the event processing.
*/
public interface $className {

  void execute(${item.capShortName}Context context);
}''')
  
  template('actionEvent', body: '''<% def sm = item.stateMachine %>{{imports}}
/** Event object for action $item.name */
public class $className extends ${c.name('EventImpl')}<${sm.entity.cap}> {
  private static final long serialVersionUID = 1L;
  private ${sm.capShortName}StateEvent stateEvent;
  private ${c.name(sm.stateProp.type.name)} oldState;
  private ${c.name(sm.stateProp.type.name)} newState;

  public ${item.cap}Event(${c.name(sm.entity.cap)} object, ${c.name('ActionType')} type, String source, ${sm.capShortName}StateEvent stateEvent, $sm.stateProp.type.name oldState, $sm.stateProp.type.name newState) {
    super(object, type, source, ${sm.entity.cap}.class);
    this.stateEvent = stateEvent;
    this.oldState =  oldState;
    this.newState = newState;
  }

  public ${sm.capShortName}StateActionType getStateAction() {
    return ${sm.capShortName}StateActionType.${item.underscored};
  }

  public $sm.stateProp.type.name getState() {
    return oldState;
  }

  public $sm.stateProp.type.name getNewState() {
    return newState;
  }

  public ${sm.capShortName}StateEvent getStateEvent() {
    return stateEvent;
  }

  @Override
  protected void fillToString(StringBuffer b) {
    super.fillToString(b);
    b.append(SEPARATOR);
    b.append("stateEvent=").append(stateEvent).append(SEPARATOR);
    b.append("oldState=").append(oldState).append(SEPARATOR);
    b.append("newState=").append(newState);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((newState == null) ? 0 : newState.hashCode());
    result = prime * result + ((oldState == null) ? 0 : oldState.hashCode());
    result = prime * result + ((stateEvent == null) ? 0 : stateEvent.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    $className other = ($className) obj;
    if (newState != other.newState)
      return false;
    if (oldState != other.oldState)
      return false;
    if (stateEvent == null) {
      if (other.stateEvent != null)
        return false;
    } else if (!stateEvent.equals(other.stateEvent))
      return false;
    return true;
  }
}''')
  
  template('actionEventReceiver', body: '''{{imports}}
/** Event receiver for JSE environment only of {@link ${item.cap}Event} */
@${c.name('ApplicationScoped')}
public class $className extends ${c.name('Receiver')}<${item.cap}Event> {
  ${macros.generate('onEventSuper', c)}
}''')

  template('executorIfc', body: '''{{imports}}
/** Executor for action $item.name of state machine $item.stateMachine.name */
public interface $className extends ${item.stateMachine.capShortName}ActionExecutor {
}''')
  
  template('implExecutor', body: '''<% def sm = item.stateMachine %>{{imports}}
@${c.name('Controller')}
@${c.name('SupportsEnvironments')}({
    @${c.name('Environment')}(executions = { ${c.name('PRODUCTIVE')} }, runtimes = { ${c.name('SERVER')} }),
    @Environment(executions = { LOCAL, MEMORY }, runtimes = { CLIENT }) })
public class $className implements ${item.cap}Executor {
  protected final ${c.name('XLogger')} log = ${c.name('XLoggerFactory')}.getXLogger(getClass());

  @Override
  public void execute(${sm.capShortName}Context context) {
    log.${sm.logLevel}("execute({})", context);
    //TODO to implement
  }
}''')
  
  template('eventIfc', body: '''<% def sm = item.stateMachine %>{{imports}}
/** $item.cap event interface of state machine $sm.name */
public interface $className extends ${sm.capShortName}StateEvent {
  ${macros.generate('propGettersIfc', c)}
  ${macros.generate('propSettersIfc', c)}
}''')
  
  template('implEvent', body: '''<% def sm = item.stateMachine; def idProp = sm.entity.idProp %>{{imports}}
public class $className extends ${sm.capShortName}StateEventImpl implements ${item.cap}Event { <% def argsConstr = sm.stateEvent ? ', ' + sm.stateEvent.signatureFullConstr(c) : ''; %><% def args = sm.stateEvent ? ', ' + sm.stateEvent.signatureNamesFullConstr(c) : ''; %>
  private static final long serialVersionUID = 1L;<% item.props.each { prop-> %>

  protected ${prop.computedType(c)} $prop.name;<% } %>

  public $className() {
    super(${sm.capShortName}StateEventType.${item.underscored});
  }<% if (!item.props) { %>

  public $className($idProp.type.name $idProp.uncapFullName$argsConstr) {
    super(${sm.capShortName}StateEventType.${item.underscored}, $idProp.uncapFullName$args);
  }<% } %>

  public $className($idProp.type.name $idProp.uncapFullName$argsConstr, ${c.name(sm.stateProp.type.name)} expectedState) {
    super(${sm.capShortName}StateEventType.${item.underscored}, $idProp.uncapFullName$args, expectedState);
  }<% if (item.props) { %>

  public $className($idProp.type.name $idProp.uncapFullName$argsConstr, ${item.signatureFullConstr(c)}) {
    super(${sm.capShortName}StateEventType.${item.underscored}, $idProp.uncapFullName$args);${macros.generate('initFullConstructor', c)}
  }

  public $className($idProp.type.name $idProp.uncapFullName$argsConstr, $sm.stateProp.type.name expectedState, ${item.signatureFullConstr(c)}) {
    super(${sm.capShortName}StateEventType.${item.underscored}, $idProp.uncapFullName$args, expectedState);${macros.generate('initFullConstructor', c)}
  }<% item.props.each { prop-> %>

  @Override
  public ${prop.computedType(c)} get${prop.cap}() {
    return $prop.name;
  }

  @Override
  public void set${prop.cap}(${prop.computedType(c)} $prop.name) {
    this.$prop.name = $prop.name;
  }<% } }%>

  @Override
  protected void fillToString(StringBuffer b) {
    super.fillToString(b);
    b.append(SEP);<% item.props.each { prop -> if (prop.type.name.matches('(String|Boolean|Long|Integer)')) { %>
    b.append("$prop.name=").append($prop.name).append(SEP);<% } } %>
  }
  ${macros.generate('hashCodeAndEquals', c)}
}''')
  
  template('eventProcessor', body: '''<% def sm = item.stateMachine %>
/** Event processor for state '$item.name' of '$sm.name'. */
public interface $className extends ${sm.capShortName}StateEventProcessor {<% item.eventTransitions.each { etrs -> def event = etrs.event; %>

  void on$event.cap(${event.cap}Event event, ${sm.capShortName}Context context);<% } %>
}''')
  
  template('implEventProcessor', body: '''<% def sm = item.stateMachine %>
import static ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.${sm.stateProp.type.name}.*;
import static ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.statemachine.${sm.capShortName}StateConditionType.*;{{imports}}
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${c.item.component.n.cap.realmConstants};
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${component.key.capitalize()};
import javax.enterprise.event.Event;
/** Event processor for state '$item.name' of '$sm.name'. */
@${c.name('Controller')}
@${c.name('ApplicationScoped')}
@${c.name('SupportsEnvironments')}({
    @${c.name('Environment')}(executions = { ${c.name('PRODUCTIVE')} }, runtimes = { ${c.name('SERVER')} }),
    @Environment(executions = { LOCAL, MEMORY }, runtimes = { CLIENT }) })
public class $className extends ${sm.capShortName}StateEventProcessorImpl implements ${sm.capShortName}${item.cap}EventProcessor {<% item.actions.each { def action-> if (!action.body) { if (action.async) { %>
  protected Event<${action.cap}Event> ${action.uncap}Publisher;<% } else { %>
  protected ${action.cap}Executor ${action.uncap};<% } %>
  <% } } %><% item.conditions.each { cond -> %>
  protected $cond.n.cap.verifier $cond.uncap;<% } %><% if (item.transitions) { %>

  @Override
  public void process(${sm.capShortName}Context context) {
    log.${sm.logLevel}("process({})", context);
    ${sm.capShortName}StateEventType eventType = context.getEvent().getType();
    <% item.eventTransitions.each { etrs-> def event = etrs.event; %>if (eventType.is${event.cap}()) {
      on${event.cap}((${event.cap}Event)context.getEvent(), context);
    } else <% } %>{
      super.process(context);
    }
  }<% } %><% item.eventTransitions.each { etrs-> %>

  @Override
  public void on${etrs.event.cap}(${etrs.event.cap}Event event, ${sm.capShortName}Context context) {
    log.${sm.logLevel}("${etrs.event.uncap}({}, {})", event, context);<%if (sm.generatePermissionsForEvents) { %>
    context.evaluateUserInRoleStrict(${component.capShortName}RealmConstants.ROLE_${sm.underscored}_${etrs.event.underscored});<% } %>
    <% if (etrs.conditions) { def elseCase = false; %>
    ${sm.capShortName}TransitionExecutionResult<${etrs.event.cap}Event> transition;
    <% etrs.transitions.each { tr -> if (tr.conditionObjs || tr.notConditionObjs) { def expr; def exprs = []
      if (tr.conditionObjs) { exprs.addAll( tr.conditionObjs.collect { cond -> "${cond.uncap}(transition, context)" } ) }
      if (tr.notConditionObjs) { exprs.addAll( tr.notConditionObjs.collect { cond -> "!${cond.uncap}(transition, context)" } ) }
      expr = exprs.join(' &&\\n         ')
    %>if ((transition = context.startTransitionTo($tr.state.underscored)) != null &&
        $expr) {<% tr.allActions.collect { def action -> if (action.body || action.async) { %>
      $action.uncap(context);<% } else { %>
      ${action.uncap}.execute(context);<% } } %><% if (tr.redirect) { %>
      context.completeTransition(new ${tr.redirect.cap}Impl(context.getEvent().get${sm.entity.idProp.capFullName}()));<% } else { %><% if (tr.state.timeoutEnabled) { %>
      context.completeTransition(stateTimeouts.get${tr.state.cap}Timeout());<% } else { %>
      context.completeTransition();<% } } %>
    } else <% } else { elseCase = true; %>{
      context.startTransitionTo($tr.state.underscored);<% tr.allActions.collect { def action -> if (action.body || action.async) { %>
      $action.uncap(context);<% } else { %>
      ${action.uncap}.execute(context);<% } } %><% if (tr.redirect) { %>
      context.completeTransition(new ${tr.redirect.cap}EventImpl(context.getEvent().get${sm.entity.idProp.capFullname}()));<% } else { %><% if (tr.state.timeoutEnabled) { %>
      context.completeTransition(stateTimeouts.get${tr.state.cap}Timeout());<% } else { %>
      context.completeTransition();<% } } %>
    }<% } %><% } %><% if (!elseCase) { %>{
      super.processNoValidFlow(context);
    }<% } } else { def tr = etrs.transition %>context.startTransitionTo($tr.state.underscored);<% tr.allActions.collect { def action -> if (action.body || action.async) { %>
    $action.uncap(context);<% } else { %>
    ${action.uncap}.execute(context);<% } } %><% if (tr.redirect) { %>
    context.completeTransition(new ${tr.redirect.uncap}Impl(context.getEvent().get${sm.entity.idProp.capFullname}()));<% } else { %><% if (tr.state.timeoutEnabled) { %>
    context.completeTransition(stateTimeouts.get${tr.state.cap}Timeout());<% } else { %>
    context.completeTransition();<% } } } %>
  }<% } %><% item.conditions.each { cond -> %>

  protected boolean $cond.uncap(${sm.capShortName}TransitionExecutionResult<?> transition, ${sm.capShortName}Context context) {<% if (cond.cachedInContext) { %>
    boolean ret = false;
    if (context.is${cond.cap}() != null) {
      ret = context.is${cond.cap}();
    } else {<% if (cond.body) { %>
      ret = context.change${cond.cap}(${cond.uncap}(context));<% } else { %>
      ret = context.change${cond.cap}(${cond.uncap}.evaluateCondition(context));<% } %>
      log.${sm.logLevel}("${cond.uncap}({}) = {}", context, ret);
    }<% } else { %><% if (cond.body) { %>
    boolean ret = context.change${cond.cap}(${cond.uncap}(context));<% } else { %>
    boolean ret = ${cond.uncap}.evaluateCondition(context);<% } %>
    log.${sm.logLevel}("${cond.uncap}.evaluateCondition({}) = {}", context, ret);<% } %>
    return transition.add($cond.underscored, ret);
  }<% } %><% item.actions.each { def action-> if (action.body) { %>

  protected void $action.uncap(${sm.capShortName}Context context) {
    log.${sm.logLevel}("${action.uncap}({})", context);
    $action.body
  }<% } else if (action.async) { %>

  protected void $action.uncap(${sm.capShortName}Context context) {
    log.${sm.logLevel}("${action.uncap}({})", context);
    ${action.uncap}Publisher.fire(new ${action.cap}Event(context.get${sm.entity.cap}(), ${c.name('ActionType')}.TRIGGER, source, context.getEvent(), context.getState(), context.getCurrentTransition().getToState()));
  }<% } } %><% item.actions.each { def action-> if (!action.body) { if (action.async) { %>

  @${c.name('Inject')}
  public void set${action.cap}Publisher(@${component.capShortName} @${c.name('Backend')} Event<${action.cap}Event> ${action.uncap}Publisher) {
    this.${action.uncap}Publisher = ${action.uncap}Publisher;
  }<% } else { %>

  @${c.name('Inject')}
  public void set${action.cap}Executor(${action.cap}Executor $action.uncap) {
    this.$action.uncap = $action.uncap;
  }<% } } } %><% item.conditions.each { cond -> %>

  @${c.name('Inject')}
  public void set${cond.cap}Verifier(${cond.cap}Verifier $cond.uncap) {
    this.$cond.uncap = $cond.uncap;
  }<% } %>
}
''')
  
  template('stateMetaState', body: '''<% def sm = item.stateMachine %>import static ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.statemachine.${sm.capShortName}StateEventType.*;
{{imports}}
/** Static information of state $item.name of state machine $sm.name */
@${c.name('ApplicationScoped')}
public class $className extends ${sm.capShortName}MetaState {
  private static final long serialVersionUID = 1L;
  <% def conditions = item.eventTransitions.collect { etrs -> etrs.transition.conditionObjs }.flatten() as Set
  conditions.each { cond -> if (cond.toShared) { %>
  private transient ${cond.cap}Verifier ${cond.uncap}Verifier;<% } } %>

  public $className() {
    super(${c.name(sm.stateProp.type.name)}.$item.underscored, findStateEvents());<% item.toBeNotified.each { toBeNotified ->%>
    ${toBeNotified}ToBeNotified = true;<% } %>
  }

  private static ${c.name('List')}<${sm.capShortName}StateEventType> findStateEvents() {
    ${c.name('List')}<${sm.capShortName}StateEventType> ret = new ${c.name('ArrayList')}<${sm.capShortName}StateEventType>();
    <% item.eventTransitions.each { %>
    ret.add($it.event.underscored);<% } %>
    return ret;
  }

  @Override
  public ${c.name('List')}<${sm.capShortName}StateEventType> findPossibleEvents(${sm.capShortName}Context context) {
    ${c.name('List')}<${sm.capShortName}StateEventType> ret = new ArrayList<${sm.capShortName}StateEventType>();
    <% item.eventTransitions.each { etrs ->
      def condStr
      def groupStr
      if (etrs.transition.conditionObjs) {
        condStr = etrs.transition.conditionObjs.findAll { it.toShared }.collect { cond -> "${cond.uncap}Verifier.evaluateCondition(context)" }.unique().join(' &&\\n        ')
      }
      if (etrs.transition.groupObjs) {
        groupStr = etrs.transition.groupObjs.collect { group -> "userInRoleConditionVerifier().evaluateCondition(${component.capShortName}RealmConstants.ROLE_${item.parent.underscoredName}_${etrs.event.underscored})" }.unique().join(' &&\\n        ')
      }

      def strList = [groupStr, condStr]
      def str = strList.findAll().join(' &&\\n        ')

      if (str) {
    %>
    if ($str) {
      ret.add($etrs.event.underscored);
    }<% } else { %>
    ret.add($etrs.event.underscored);<% } } %>
    return ret;
  }<% conditions.each { cond -> if (cond.toShared) { %>

  @${c.name('Inject')}
  public void set${cond.cap}Verifier(${cond.cap}Verifier ${cond.uncap}Verifier) {
    this.${cond.uncap}Verifier = ${cond.uncap}Verifier;
  }<% } } %>
}''')
  
  template('controllerBootstrapBase', body: '''<% def controller = item.controller; def idProp = item.entity.idProp %>{{imports}}
public class $className implements ${c.name('Closeable')} {
  protected final ${c.name('XLogger')} log = ${c.name('XLoggerFactory')}.getXLogger(getClass());
  protected boolean wrapForThreadBound = true;
  protected String threadName;
  protected ${controller.cap}FactoryBase factory;
  protected ${controller.cap} instance;
  protected ${c.name('ThreadBoundProxyHandler')}<$controller.cap> threadBoundHandler;<% if (item.timeoutEnabled) { %>
  protected ${item.capShortName}StateTimeoutHandler timeoutHandler;<% } %>

  protected $className() {
  }

  protected $className(${controller.cap}FactoryBase factory, boolean wrapForThreadBound, String threadName) {
    this.factory = factory;
    this.wrapForThreadBound = wrapForThreadBound;
    this.threadName = threadName;
  }

  public $controller.cap start() {
    log.info("start()");
    if (instance == null) {
      $controller.cap controller = factory.get$controller.cap();
      if (wrapForThreadBound) {
        instance = wrapForThreadBound(controller);
      } else {
        instance = controller;
      }<% if (module.timeoutEnabled) { %>
      timeoutHandler = get${item.capShortName}StateTimeoutHandler();
      timeoutHandler.registerTimer();<% } %>
    }
    ${c.name('JmxUtils')}.deployMBean(this);
    return instance;
  }

  protected $controller.cap wrapForThreadBound($controller.cap controller) {
    threadBoundHandler = new ThreadBoundProxyHandler<$controller.cap>(controller, 1, ${c.name('SingletonContainer')}.getSingleton(${c.name('NamedThreadFactoryHolderByPrefix')}.class).getNamedThreadFactory(threadName)) {
      @Override
      protected boolean isThreadBound(final ${c.name('Method')} method) throws NoSuchMethodException {
        return method.getName() == "process";
      }
    };
    return ($controller.cap) ${c.name('Proxy')}.newProxyInstance(controller.getClass().getClassLoader(), new Class[] { ${controller.cap}.class }, threadBoundHandler);
  }<% if (module.timeoutEnabled) { %>

  protected ${item.capShortName}StateTimeoutHandler get${item.capShortName}StateTimeoutHandler() {
    ${item.capShortName}StateTimeoutHandlerImpl ret = new ${item.capShortName}StateTimeoutHandlerMem();
    return wire(ret);
  }

  protected $module.names.stateTimeoutHandler wire(${item.capShortName}StateTimeoutHandlerImpl item) {
    item.setStateTimeouts(factory.getStateTimeouts());
    item.setContextManager(factory.getContextManager());
    item.set$controller.cap(instance);
    item.setEventFactory(factory.getEventFactory());
    return item;
  }<% } %>

  @Override
  public void close() {
    log.info("close()");<% if (item.timeoutEnabled) { %>
    if (timeoutHandler instanceof Closeable) {
      ((Closeable)timeoutHandler).close();
    }<% } %>

    if (threadBoundHandler != null) {
      threadBoundHandler.close();
    }
    JmxUtils.undeployMBean(this);
  }
}''')
  
  template('controllerFactoryBase', body: '''<% def controller = item.controller; def idProp = item.entity.idProp %>{{imports}}
public abstract class $className {
  protected final ${c.name('XLogger')} log = ${c.name('XLoggerFactory')}.getXLogger(getClass());
  protected boolean wrapForThreadBound = true;
  protected ${controller.cap}FactoryBase delegate;

  protected $className() {
    super();
    this.delegate = this;
  }

  protected $className(${controller.cap}FactoryBase delegate) {
    this.delegate = delegate;
  }

  protected ${controller.cap} get${controller.cap}() {
    ${controller.cap}Impl ret = new ${controller.cap}Impl();
    return wire(ret);
  }

  protected ${controller.cap} wire(${controller.cap}Impl item) {
    item.set${item.capShortName}StateMetaModel(delegate.getStateMetaModel());
    item.setContextManagerDef(new ${c.name('HolderImpl')}(delegate.getContextManager()));<% item.states.each { def state-> %>
    item.set${item.capShortName}${state.cap}EventProcessor(delegate.get${item.capShortName}${state.cap}EventProcessor());<% } %><% controller.operations.each { ref-> def uncapName = ref.uncap %>
    item.set${ref.name}(delegate.get$ref.name());<% } %><% controller.containers.each { ref -> %>
    item.set${ref.names.clazz}(delegate.get$ref.cap());<% } %>
    return item;
  }

  protected ${item.capShortName}EventFactory getEventFactory() {
    ${item.capShortName}EventFactoryImpl ret = new ${item.capShortName}EventFactoryImpl();
    return wire(ret);
  }

  protected ${item.capShortName}EventFactory wire(${item.capShortName}EventFactoryImpl item) {
    return item;
  }<% if (item.timeoutEnabled) { %>

  protected ${item.capShortName}Timeouts getStateTimeouts() {
    ${item.capShortName}Timeouts ret = new ${item.capShortName}Timeouts();
    return wire(ret);
  }

  protected ${item.capShortName}Timeouts wire(${item.capShortName}Timeouts item) {
    return item;
  }<% } %><% item.states.each { def state-> %>

  protected ${item.capShortName}${state.cap}EventProcessor get${item.capShortName}${state.cap}EventProcessor() {
    ${item.capShortName}${state.cap}EventProcessorImpl ret = new ${item.capShortName}${state.cap}EventProcessorImpl();
    return wire(ret);
  }

  protected ${item.capShortName}${state.cap}EventProcessor wire(${item.capShortName}${state.cap}EventProcessorImpl item) {<% state.actions.each { def action-> if (!action.body) { if (action.async) { %>
    item.set${action.cap}Publisher(delegate.get${action.cap}Publisher());<% } else { %>
    item.set${action.cap}Executor(delegate.get${action.cap}Executor());<% } } } %><% state.conditions.each { cond -> %>
    item.set${cond.cap}Verifier(delegate.get${cond.cap}Verifier());<% } %><% if (module.timeoutEnabled) { %>
    item.setStateTimeouts(delegate.getStateTimeouts());<% } %>
    return item;
  }<% } %>

  protected ${item.capShortName}ContextManager getContextManager() {
    ${item.capShortName}ContextManager ret = new ${item.capShortName}ContextManagerImpl();
    return wire(ret);
  }

  protected ${item.capShortName}ContextManager wire(${item.capShortName}ContextManager item) {
    return item;
  }<% controller.operations.each { ref -> def uncap = ref.uncap %>

  protected $ref.cap get$ref.cap() {
    ${ref.cap}Impl ret = ${ref.cap}Impl();
    return wire(ret);
  }

  protected ${ref.cap} wire(${ref.cap}Impl item) {
    return item;
  }<% } %>

  protected ${item.capShortName}StateMetaModel getStateMetaModel() {
    ${item.capShortName}StateMetaModel ret = new ${item.capShortName}StateMetaModel();
    return wire(ret);
  }

  protected ${item.capShortName}StateMetaModel wire(${item.capShortName}StateMetaModel item) {<% item.states.each { state -> %>
    item.set${item.capShortName}${state.cap}MetaState(delegate.get${item.capShortName}${state.cap}MetaState());<% } %>
    return item;
  }<% item.states.each { state -> %>

  protected ${item.capShortName}${state.cap}MetaState get${item.capShortName}${state.cap}MetaState() {
    ${item.capShortName}${state.cap}MetaState ret = new ${item.capShortName}${state.cap}MetaState();
    return wire(ret);
  }

  protected ${item.capShortName}${state.cap}MetaState wire(${item.capShortName}${state.cap}MetaState item) {
    return item;
  }<% } %><% item.actions.each { def action-> if (!action.body) { if (action.async) { %>

  protected ${c.name('Event')}<${action.cap}Event> get${action.cap}Publisher() {
    return new ${c.name('PublisherEmpty')}<${action.cap}Event>() {

      @Override
      public void fire(${action.cap}${c.name('Event')} event) {
        fire${action.cap}Event(event);
      }
    };
  }

  protected abstract void fire${action.cap}${c.name('Event')}(${action.cap}Event event);
  <% } else { %>

  protected abstract ${action.cap}Executor get${action.cap}Executor();<% } } } %><% item.conditions.each { cond -> %>

  protected abstract ${cond.cap}Verifier get${cond.cap}Verifier();<% } %>

  protected abstract ${c.name('SessionPrincipal')} getSessionPrincipal();
}''')
  
  template('stateTimeoutHandler', body: '''
public interface $className {

  void registerTimer();

  void unregisterTimer();

  void processExpired${item.entity.instancesName.capitalize()}();
}''')
  
  template('stateTimeoutHandlerBean', body: '''{{imports}}
import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${c.item.component.n.cap.realmConstants};
@${c.name('Stateless')}
@${c.name('SupportsEnvironments')}(@${c.name('Environment')}(executions = { ${c.name('PRODUCTIVE')} }, runtimes = { ${c.name('SERVER')} }))
@${c.name('Controller')}<% if (item.generatePermissionsForEvents) { %>
@${c.name('RunAs')}(${component.capShortName}RealmConstants.ROLE_${item.underscored}_TIMEOUT)<%}%>
@${c.name('Local')}(${item.capShortName}StateTimeoutHandler.class)
public class $className extends ${item.capShortName}StateTimeoutHandlerImpl {
  @${c.name('Resource')}
  protected ${c.name('TimerService')} timerService;

  @Override
  public void registerTimer() {
    ${c.name('Timer')} existingTimer = findExistingTimer();
    if (existingTimer == null) {
      ${c.name('TimerConfig')} timerConfig = new TimerConfig(source, false);
      timerConfig.setPersistent(false);
      timerService.createIntervalTimer(0, stateTimeouts.getTimeoutCheckInterval(), timerConfig);
    }
  }

  @Override
  public void unregisterTimer() {
    Timer existingTimer = findExistingTimer();
    if (existingTimer != null) {
      existingTimer.cancel();
    }
  }

  protected Timer findExistingTimer() {
    Timer ret = null;
    if (timerService.getTimers() != null) {
      for (Timer timer : timerService.getTimers()) {
        if (source.equals(timer.getInfo())) {
          ret = timer;
          break;
        }
      }
    }
    return ret;
  }

  @Override
  @${c.name('Timeout')}
  public void processExpired${item.entity.instancesName.capitalize()}() {
    super.processExpired${item.entity.instancesName.capitalize()}();
  }
}''')
  
  template('stateTimeoutHandlerImpl', body: '''<% def controller = item.controller %>{{imports}}
public abstract class $className implements ${item.capShortName}StateTimeoutHandler {<% extraArgs = ''; if (item.stateEvent) { item.stateEvent.props.each { prop -> extraArgs += ", ${item.entity.uncap}.${prop.getter}" } }; %>
  protected final ${c.name('XLogger')} log = ${c.name('XLoggerFactory')}.getXLogger(getClass());
  protected final String source = ${c.name('StringUtils')}.formatSource(${className}.class);
  protected ${item.capShortName}Timeouts stateTimeouts;
  protected ${item.capShortName}ContextManager contextManager;
  protected $controller.cap $controller.uncap;
  protected ${item.capShortName}EventFactory eventFactory;

  @Override
  public void processExpired${item.entity.instancesName.capitalize()}() {
    ${c.name('List')}<${c.name(item.entity.cap)}> expired${item.entity.instancesName.capitalize()} = contextManager.findExpired${item.entity.instancesName.capitalize()}();
    for ($item.entity.cap $item.entity.uncap : expired${item.entity.instancesName.capitalize()}) {
      ${controller.uncap}.process(eventFactory.newTimeoutEvent(${item.entity.uncap}.${item.entity.idProp.getter}$extraArgs, ${item.entity.uncap}.${item.stateProp.getter}));
    }
  }

  @${c.name('Inject')}
  public void setStateTimeouts(${item.capShortName}Timeouts stateTimeouts) {
    this.stateTimeouts = stateTimeouts;
  }

  @${c.name('Inject')}
  public void setContextManager(${item.capShortName}ContextManager contextManager) {
    this.contextManager = contextManager;
  }

  @${c.name('Inject')}
  public void set$controller.cap($controller.cap $controller.uncap) {
    this.$controller.uncap = $controller.uncap;
  }

  @${c.name('Inject')}
  public void setEventFactory(${item.capShortName}EventFactory eventFactory) {
    this.eventFactory = eventFactory;
  }
}''')
  
  template('stateTimeoutHandlerMem', body: '''{{imports}}
@${c.name('ApplicationScoped')}
@${c.name('SupportsEnvironments')}(@${c.name('Environment')}(executions = { ${c.name('LOCAL')}, ${c.name('MEMORY')} }))
@${c.name('Controller')}
public class $className extends ${item.capShortName}StateTimeoutHandlerImpl implements ${c.name('Closeable')} {
  protected ${c.name('ScheduledExecutorService')} scheduler;
  protected ${c.name('ScheduledFuture')}<?> timer;

  @Override
  public void registerTimer() {
    close();
    final Runnable timeout = new Runnable() {
      @Override
      public void run() {
        try {
          processExpired${item.entity.instancesName.capitalize()}();
        } catch(Exception e) {
          log.error("Exception occured durring precessing of expired timers: {}", ${c.name('StringUtils')}.formatExceptionCauseHierarchy(e));
        }
      }
    };
    scheduler = ${c.name('Executors')}.newScheduledThreadPool(1, ${c.name('SingletonContainer')}.getSingleton(${c.name('NamedThreadFactoryHolderByPrefix')}.class).getNamedThreadFactory("$className"));
    timer = scheduler.scheduleAtFixedRate(timeout, 0, stateTimeouts.getTimeoutCheckInterval(), ${c.name('TimeUnit')}.MILLISECONDS);
  }

  @Override
  public void unregisterTimer() {
    if (timer != null) {
      timer.cancel(true);
    }
  }

  @Override
  @${c.name('PreDestroy')}
  public void close() {
    if (scheduler != null) {
      scheduler.shutdownNow();
    }
  }
}''')
 
  template('transitionExecutionResult', body: '''{{imports}}
/** Result object for procession of a transition in state machine $item.name. The object provides especially results action executions bound to a transition. */
public class $className<EVENT extends ${item.capShortName}StateEvent> extends ${c.name('Base')} {
  private static final long serialVersionUID = 1L;

  protected ${c.name(item.stateProp.type.name)} fromState;
  protected ${c.name(item.stateProp.type.name)} toState;
  protected EVENT event;
  protected ${item.capShortName}StateEvent redirectEvent;
  protected ${c.name('ArrayList')}<${c.name('Link')}<${item.capShortName}StateConditionType, Boolean>> conditionResults = new ArrayList<>();
  protected ${item.capShortName}StateConditionType failedCondition = null;

  public $className($item.stateProp.type.name fromState, $item.stateProp.type.name toState, EVENT event, ${item.capShortName}StateEvent redirectEvent) {
    this.fromState = fromState;
    this.toState = toState;
    this.event = event;
    this.redirectEvent = redirectEvent;
  }

  /** Add condition result and return boolean result value in order to use the method in condition line */
  public boolean add(${item.capShortName}StateConditionType conditionType, boolean successfully) {
    conditionResults.add(new Link<${item.capShortName}StateConditionType, Boolean>(conditionType, successfully));
    if (!successfully) {
      this.failedCondition = conditionType;
    }
    return successfully;
  }

  public $item.stateProp.type.name getFromState() {
    return fromState;
  }

  public ${c.name('MlKey')} getFromStateAsMlKey() {
    return fromState != null ? fromState.buildMlKey() : null;
  }

  public $item.stateProp.type.name getToState() {
    return toState;
  }

  public MlKey getToStateAsMlKey() {
    return toState != null ? toState.buildMlKey() : null;
  }

  public EVENT getEvent() {
    return event;
  }

  public MlKey getEventTypeAsMlKey() {
    return event != null ? event.getType().buildMlKey() : null;
  }

  public ${c.name('List')}<Link<${item.capShortName}StateConditionType, Boolean>> getConditionResults() {
    return conditionResults;
  }

  public boolean isSuccessfully() {
    return failedCondition == null;
  }

  public ${item.capShortName}StateConditionType getFailedCondition() {
    return failedCondition;
  }

  public MlKey getFailedConditionAsMlKey() {
    return failedCondition != null ? failedCondition.buildMlKey() : null;
  }

  @Override
  protected void fillToString(StringBuffer b) {
    super.fillToString(b);
    b.append("event=").append(event).append(SEP);
    b.append("fromState=").append(fromState).append(SEP);
    b.append("toState=").append(toState);
    if (redirectEvent != null) {
      b.append(SEP).append("redirectEvent=").append(redirectEvent);
    }
    if (failedCondition != null) {
      b.append(SEP).append("failedCondition=").append(failedCondition);
    }
  }
}''')
  
  template('condVerifier', body: '''import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${component.key.capitalize()}Ml;
{{imports}}
/**
* A condition verifier is responsible to evaluate a transition condition for state machine $item.name.
* The logic must not change any state, but shall be 'readonly'
* If it is false, then the transition can not be applied and the state event handler will proceed with next possible transitions or abort the event processing.
*/
@${c.name('Controller')}
public abstract class $className extends ${c.name('ConditionVerifierAbstract')}<${item.capShortName}Context > {

  @Override
  protected String getMlBase() {
    return ${component.capShortName}Ml.ML_BASE;
  }
}''')
  
  template('conditionVerifierIfc', body: '''<% def sm = item.stateMachine %>{{imports}}
public interface $className extends ${c.name('ConditionVerifier')}<${sm.capShortName}Context> {
}''')
  
  template('conditionVerifier', body: '''<% def sm = item.stateMachine %>import ${c.item.component.parent.ns.name}.${c.item.component.ns.name}.integ.${component.key.capitalize()}Ml;
${item.description?"/*** $item.description */":''}
public abstract class $className extends ${sm.capShortName}ConditionVerifier implements ${item.cap}Verifier {

  @Override
  protected String getExceptionKey() {
    return ${component.capShortName}Ml.${item.underscored}_FAIL;
  }
}''')
  
  template('implConditionVerifier', body: '''<% def sm = item.stateMachine %>{{imports}}
@${c.name('ApplicationScoped')}
@${c.name('Controller')}
public class $className extends ${item.cap}VerifierBase {

  @Override
  public boolean evaluateCondition(${sm.capShortName}Context context) {
    log.debug("evaluateCondition({})", context);

    // TODO implement me
    boolean ret = false;
    if (context != null) {
      ret = true;
    }
    return ret;
  }
}''')
  
  template('timeoutsConfig', body: '''<% def props = []; item.states.each { if(it.timeoutInMillis) { props.add(it) } } %> {{imports}}
@${c.name('ApplicationScoped')}
@${c.name('Config')}<% if (item.onlyInClient) { %>
@SupportsEnvironments(@Environment(executions = { PRODUCTIVE }, runtimes = { CLIENT }))<% } %>
public<% if (item.base) { %> abstract<% } %> class $className extends ${c.name('Base')} {
  private static final long serialVersionUID = 1L;
  /** A unique URI prefix for RESTful services and multi-language support */
  public static final String URI_PREFIX = "$item.uri";
  <% if(item.timeoutCheckIntervalInMillis) { %>
  protected int timeoutCheckInterval = $item.timeoutCheckIntervalInMillis;<% } %>
  <% props.each { prop -> %>  <% if (prop.description) { %>/*** $prop.description */<% } %>
  protected int ${prop.uncap}Timeout = ${prop.timeoutInMillis};<% } %>
  ${macros.generate('baseConstructor', c)}  <% if(item.timeoutCheckIntervalInMillis) { %>
  public int getTimeoutCheckInterval() {
    return timeoutCheckInterval;
  }

  public void setTimeoutCheckInterval(int timeoutCheckInterval)  {
    this.timeoutCheckInterval = timeoutCheckInterval;
  }<% } %><% props.each { prop-> %>
  <% if (prop.description) { %>
  /*** $prop.description */<% } %>
  public int get${prop.cap}Timeout() {
    return ${prop.uncap}Timeout;
  }
  public void set${prop.cap}Timeout(int ${prop.uncap}Timeout) {
    this.${prop.uncap}Timeout = ${prop.uncap}Timeout;
  }<% } %>
  ${macros.generate('implOperationsAndDelegates', c)}
  public void update(${item.capShortName}Timeouts ${item.uncapShortName}Timeouts) {<% if(item.timeoutCheckIntervalInMillis) { %>
    setTimeoutCheckInterval(${item.uncapShortName}Timeouts.getTimeoutCheckInterval());<% } %><% props.each { prop-> %>
    set${prop.cap}Timeout(${item.uncapShortName}Timeouts.get${prop.cap}Timeout());<% } %>
  }
  ${macros.generate('hashCodeAndEquals', c)}
}''')
  
 template('qualifier', body: '''{{imports}}
/**
* The qualifier of '$item.name' what can be used in artifacts at dependency management and event messaging.
* The qualifier allows to select correct implementation of the common/generic interfaces.
*/
@${c.name('Qualifier')}
@${c.name('Retention')}(${c.name('RetentionPolicy')}.RUNTIME)
@${c.name('Target')}({ ${c.name('ElementType')}.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR })
@${c.name('DependsOnExecutionType')}
public @interface $className {
}''')
  
  
  
}
