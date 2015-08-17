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

  template('propsMember', body: '''<% item.props.each { prop -> if(!prop.typeEntity) { %><% if(prop.multi) { %>
  protected ${c.name('List')}<${prop.type.name}> $prop.uncap; <% } else { %>
  protected ${c.name(prop.type.name)} $prop.uncap;<% } } else if (prop.typeEntity && (prop.manyToOne || prop.oneToOne)) { def relationIdProp = prop.type.idProp %><% if(relationIdProp) { %><% if(relationIdProp.multi) { %>
  protected ${c.name('List')}<${relationIdProp.type.name}> ${prop.uncap}${relationIdProp.cap};<% } else { %>
  protected ${relationIdProp.type.name} ${prop.uncap}${relationIdProp.cap};<% } } } } %>''')

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

  template('versionMember', body: '''<% if (!item.superUnit) { %>
  @${c.name('Version')}
  @${c.name('Column')}(name = "VERSION")
  protected Long version;<% } %>''')

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

  template('propGettersIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.readable) { %><% if (prop.description) { %>
  /** $prop.description */<% } %>
  <% if(prop.multi) { %>${c.name('List')}<${c.name(prop.type)}><% } else { %>${c.name(prop.type)}<% } %> $prop.getter;<% } } %> ''')

  template('propGettersEntityIfc', body: '''<% item.props.each { prop -> if (prop.api && prop.readable && !prop.typeEntity && prop.name != 'id' ) { %>
  <% if (prop.description) { %>/** $prop.description */<% } %>
  <% if(prop.multi) { %>${c.name('List')}<${c.name(prop.type)}><% } else { %>${c.name(prop.type)}<% } %> $prop.getter;<% } } %>
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


  template('methods', body: '''<% item.operations.each { op -> String ret = '' %>
  <% if (op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  @Override
  public ${op.ret ? op.ret.name : 'void'} $op.name($op.signature) {
  ${op.resolveBody(c)}
  }<% } %>''')

  template('interfaceBody', body: '''<% item.operations.each { op -> if (!op.override) { %>
  ${op.description?"   /** $op.description */":''}<% if (op.transactional) { %>@${c.name('Transactional')}<% } %>
  ${op.return} $op.name(${op.signature(c)});<% } } %>''')

  template('interfaceBodyExternal', body: '''<% item.operations.each { op -> if(!op.delegateOp) { %>
  ${op.description?"   /** $op.description */":''}
  $op.ret ${op.name}(${op.signature(c)});<% } }%><% item.operations.each { op -> if(op.delegateOp) { %>
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

  template('ifcEntity', body: '''<% if (!c.className) { c.className = item.cap } %>{{imports}}
  ${item.description?"/*** $item.description */":''}
  public interface $c.className extends<% if (item.superUnit) { %> ${item.superUnit.name} <% } else { %> ${c.name('EntityIfc')}<${item.idProp.type.name}><% } %>{
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
  public interface $className extends ${c.name('Manager')}<${idProp.type.name}, ${c.name(item.cap)}> { <% finders.counters.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %><% finders.finders.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %><% finders.exists.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %>
  }''')

  template('ifcCommands', body: '''<% if(!c.className) { c.className = item.n.cap.commands } %><% def commands = item.commands; def idProp = item.idProp; %>{{imports}}
  <% if(commands.description) { %>/**
     * $commands.description
     */<% } else { %>/** The commands provide Delete, Update, Create operations for entity {@link $item.cap}.*/<% } %>
  public interface $className extends ${c.name('Manager')}<${idProp.type.name}, ${c.name(item.cap)}> { <% commands.deleters.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %><% commands.updates.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %><% commands.creates.each { op -> %>
      ${op.description?"   /** $op.description */":''}
      ${op.return} ${op.name}(${op.signature(c)});<% } %>
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

  template('implCache', body: '''<% def superUnit = item.superUnit; def idProp = item.idProp; def type = item.virtual?'E':c.name(item.cap); def cacheSuper%>import static ee.common.util.ComparisonUtils.*;{{imports}}
  <% if (!c.override) { %><% if (superUnit) { cacheSuper = "${superUnit.n.cap.cacheImpl}<$type>" } else if (idProp.typeLong) { cacheSuper = "${c.name('LongEntityCache')}<$type>" } else if (idProp.typeInteger) { cacheSuper = "${c.name('IntegerEntityCache')}<$type>" } else if (idProp.typeString) { cacheSuper = "${c.name('StringEntityCache')}<$type>"} else { cacheSuper = "CacheImpl<$idProp.type, $type>" } %>
    <% } else { %><% if (superUnit) { cacheSuper = "${superUnit.n.cap.cacheOverride}<$type>" } else if (idProp.typeLong) { cacheSuper = "${c.name('LongCacheOverride')}<$type>" } else if (idProp.typeInteger) { cacheSuper = "${c.name('IntegerCacheOverride')}<$type>" } else if (idProp.typeString) { cacheSuper = "${c.name('StringCacheOverride')}<$type>" } else { cacheSuper = "CacheOverride<$idProp.type, $type>" } %>
    <% } %>
  <% def singlePropIndexes = item.props.findAll { !it.primaryKey && (it.index || it.unique ) }; def relationIdPropIndexes = item.props.findAll { it.typeEl && it.manyToOne }; def keyNameToIndex = [:]; item.indexes.collect { def index -> String keyName = index.props.collect { it.cap }.join('And')[0].toLowerCase(); keyNameToIndex[keyName] = index; } %>
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
      }<% } %><% if (item.finders && item.finders.finders) { item.finders.finders.each { op -> %>
        <% String finderKeyName = op.params.collect { it.prop.uncap }.join('And'); %>
        <% if (op.params.size() == 1 && singlePropIndexes.contains(op.params.get(0).prop)) { def param = op.params.get(0); def prop = param.prop; %>

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
                ${c.name(idProp.type.name)} ret = ${prop.uncap}ToId.get($prop.name);<% if (c.override) { %>
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
    ${c.name(idProp.type.name)} ret = ${finderKeyName}ToId.get(${finderKeyName}Key($op.signatureName));<% if (c.override) { %>
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
  }<% } } } %><% relationIdPropIndexes.each { prop-> def relationIdProp = prop.type.idProp; if(relationIdProp) { %>

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
  ${macros.generate('propsMember', c)}${macros.generate('propGetters', c)}${macros.generate('propsSetter', c)}${macros.generate('methods', c)}${macros.generate('propsToString', c)}${macros.generate('hashCodeAndEqualsEntity', c)}

}''')

  template('implEntityExtends', body: '''<% c.src = true %><% if (!c.className) { c.className = item.n.cap.impl } %>{{imports}}
public ${c.item.virtual?'abstract ':''}class $c.className extends ${item.cap}BaseImpl {<% if (c.serializable) { %>
  private static final long serialVersionUID = 1L;<% } %>
}''')

  template('entityBaseBean', body: '''<% def superUnit = c.item.superUnit; if(!c.className) { c.className = item.n.cap.entity } %>{{imports}}${macros.generate('metaAttributesEntity', c)}${macros.generate('jpaMetasEntity', c)}
public ${item.virtual || item.base ? 'abstract ':''}class $c.className extends<% if(item.superUnit) { %> ${superUnit.n.cap.entity}<% } else { %> ${c.name('EntityJpa')}<${item.idProp.type.name}><% } %> implements ${c.name(c.item.cap)} {
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
  ${macros.generate('propsMember', c)}${macros.generate('enumConstructor', c)}${macros.generate('propGetters', c)}<% item.literals.each { lit -> %>

  public boolean $lit.is {
    return this == $lit.underscored;
  }<% } %><% item.operations.each { op -> if(op.body) { %>
  <% if (op.override) { %>@Override<% } %><% if(op.rawType) { %>
  @SuppressWarnings({ "rawtypes", "unchecked" })<% } %>
  ${macros.generate('methods', c)}<% } } %>
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

  @Inject
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

  @Inject
  @${component.cap}
  protected Event<${entity.n.cap.event}> ${entity.uncap}Publisher;<% } } %><% module.configs.each { config-> if (config.event) { %>

  @Inject
  @${component.cap}
  @${c.name('Backend')}
  protected Event<${config.n.cap.event}> ${config.uncap}Publisher;<% } } %><% module.containers.each { container-> %>

  @Inject
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

  @Inject
  public void set${m.parent.key.capitalize()}JmsToCdi(${m.parent.key.capitalize()}JmsToCdi ${m.parent.key}JmsToCdi) {
    this.${m.parent.key}JmsToCdi = ${m.parent.key}JmsToCdi;
  }<% } else { %>
  @Inject
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
}
''')

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
}''')

  template('constantsMlExtends', body: '''<% if (!c.className) { c.className = "${item.name}Ml" } %>
/** Multi language constants for '${c.item.name}' */
public class $className extends ${className}Base {
}''')


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
  @Override
  public ${c.name('MlKey')} buildMlKey() {
    return new ${c.name('MlKeyImpl')}(${component.name}Ml.ML_BASE, name());
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
  <% model.props.each { prop -> %>protected $prop.computedType $prop.name;<% } %> <% if (listProps) { %>
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
  <% if (presenter.withMediator) { %>@Inject
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
    
}
