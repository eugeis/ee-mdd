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
package ee.mdd

import ee.mdd.builder.AbstractFactoryBuilder
import ee.mdd.builder.CompositeFactory
import ee.mdd.builder.EnumTypeFactory
import ee.mdd.builder.FacetAwareFactory
import ee.mdd.builder.FacetFactory
import ee.mdd.builder.Facets
import ee.mdd.builder.MddFactory
import ee.mdd.builder.MetaAttributeHolder
import ee.mdd.builder.ModelFactory
import ee.mdd.builder.OppositeResolveHandler
import ee.mdd.builder.PropFactory
import ee.mdd.model.Body
import ee.mdd.model.Element
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Cache
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Commands
import ee.mdd.model.component.CompilationUnit
import ee.mdd.model.component.Component
import ee.mdd.model.component.ConditionParam
import ee.mdd.model.component.Config
import ee.mdd.model.component.ConfigController
import ee.mdd.model.component.Constructor
import ee.mdd.model.component.Container
import ee.mdd.model.component.Controller
import ee.mdd.model.component.Count
import ee.mdd.model.component.Create
import ee.mdd.model.component.DataType
import ee.mdd.model.component.DataTypeOperation
import ee.mdd.model.component.DataTypeProp
import ee.mdd.model.component.Delete
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Exist
import ee.mdd.model.component.ExternalModule
import ee.mdd.model.component.ExternalType
import ee.mdd.model.component.Facade
import ee.mdd.model.component.Facet
import ee.mdd.model.component.Find
import ee.mdd.model.component.Finders
import ee.mdd.model.component.Index
import ee.mdd.model.component.Initializer
import ee.mdd.model.component.Literal
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Message
import ee.mdd.model.component.MetaAttribute
import ee.mdd.model.component.Model
import ee.mdd.model.component.Module
import ee.mdd.model.component.Namespace
import ee.mdd.model.component.Operation
import ee.mdd.model.component.OperationRef
import ee.mdd.model.component.Param
import ee.mdd.model.component.Pojo
import ee.mdd.model.component.Prop
import ee.mdd.model.component.StructureUnit
import ee.mdd.model.component.Type
import ee.mdd.model.component.TypeRef
import ee.mdd.model.component.Update
import ee.mdd.model.component.XmlController
import ee.mdd.model.realm.Realm
import ee.mdd.model.realm.RealmGroup
import ee.mdd.model.realm.RealmRole
import ee.mdd.model.realm.RealmUser
import ee.mdd.model.statemachine.Action
import ee.mdd.model.statemachine.Condition
import ee.mdd.model.statemachine.Context
import ee.mdd.model.statemachine.Event
import ee.mdd.model.statemachine.History
import ee.mdd.model.statemachine.State
import ee.mdd.model.statemachine.StateMachine
import ee.mdd.model.statemachine.StateMachineController
import ee.mdd.model.statemachine.Transition
import ee.mdd.model.ui.Button
import ee.mdd.model.ui.CheckBox
import ee.mdd.model.ui.Column
import ee.mdd.model.ui.ComboBox
import ee.mdd.model.ui.ContextMenu
import ee.mdd.model.ui.Control
import ee.mdd.model.ui.DateField
import ee.mdd.model.ui.Dialog
import ee.mdd.model.ui.GroupBoxHeader
import ee.mdd.model.ui.GroupContentFrame
import ee.mdd.model.ui.Header
import ee.mdd.model.ui.Label
import ee.mdd.model.ui.Listener
import ee.mdd.model.ui.OnAction
import ee.mdd.model.ui.OnActivation
import ee.mdd.model.ui.OnChange
import ee.mdd.model.ui.OnContextMenuRequest
import ee.mdd.model.ui.OnItemEditorItemSelect
import ee.mdd.model.ui.OnSelect
import ee.mdd.model.ui.Panel
import ee.mdd.model.ui.Presenter
import ee.mdd.model.ui.Spinner
import ee.mdd.model.ui.Table
import ee.mdd.model.ui.TextField
import ee.mdd.model.ui.TimeField
import ee.mdd.model.ui.View
import ee.mdd.model.ui.ViewModel
import ee.mdd.model.ui.ViewRef
import ee.mdd.model.ui.Widget

/**
 *
 * @author Eugen Eisler
 */
class ModelBuilder extends AbstractFactoryBuilder {
  private Facets facets = new Facets()
  private def type = new CompositeFactory(beanClass: Type, childFactories: ['meta'])
  private def cache = new CompositeFactory(beanClass: Cache, parent: cu)
  private def channel = new CompositeFactory(beanClass: Channel, childFactories: ['meta', 'message'])
  private def cu = new CompositeFactory(beanClass: CompilationUnit, childFactories: ['constr', 'prop', 'op', 'delegate', 'cache'], parent: type)
  private def dataType = new CompositeFactory(beanClass: DataType, childFactories: ['finder', 'commands', 'index'], parent: cu)
  private def typeRef = new CompositeFactory(beanClass: TypeRef)
  private def message = new CompositeFactory(beanClass: Message,  childFactories: ['meta'], parent: typeRef)
  private def basicType = new CompositeFactory(beanClass: BasicType, parent: dataType)
  private def body = new CompositeFactory(beanClass: Body, childFactories: ['type'])
  private def pojo = new CompositeFactory(beanClass: Pojo, parent: cu)
  private def lu = new CompositeFactory(beanClass: LogicUnit, childFactories: ['meta', 'param'], parent: body)
  private def attr = new CompositeFactory(beanClass: Attribute, childFactories: ['meta'])
  private def param = new CompositeFactory(beanClass: Param, parent: attr)
  private def operation = new CompositeFactory(beanClass: Operation, parent: lu)
  private def operationRef = new CompositeFactory(beanClass: OperationRef, valueProperty: 'ref', parent: operation)
  private def facet = new FacetFactory(beanClass: Facet, childFactories: ['extModule', 'facet'], facets: facets)
  private def su = new FacetAwareFactory(beanClass: StructureUnit, childFactories: ['facet', 'namespace'], facets: facets)
  private def commands = new CompositeFactory(beanClass: Commands, childFactories: ['create', 'delete', 'update', 'prop'], parent: controller)
  private def component = new CompositeFactory(beanClass: Component, childFactories: ['module', 'realm'], parent: su)
  private def condition = new CompositeFactory(beanClass: ConditionParam, parent: param)
  private def config = new CompositeFactory(beanClass: Config, parent: dataType, childFactories: ['configController'])
  private def constructor = new CompositeFactory(beanClass: Constructor, parent: lu)
  private def container = new CompositeFactory(beanClass: Container, childFactories: ['controller', 'xmlController'], parent: dataType)
  private def controller = new CompositeFactory(beanClass: Controller, parent: cu)
  private def xmlController = new CompositeFactory(beanClass: XmlController, parent: controller)
  private def configController = new CompositeFactory(beanClass: ConfigController, parent: controller)
  private def index = new CompositeFactory(beanClass: Index)
  private def initializer = new CompositeFactory(beanClass: Initializer, parent: controller)
  private def dataTypeOperation = new CompositeFactory(beanClass: DataTypeOperation, parent: operation)
  private def counter = new CompositeFactory(beanClass: Count, parent: dataTypeOperation)
  private def create = new CompositeFactory(beanClass: Create, parent: dataTypeOperation)
  private def delete = new CompositeFactory(beanClass: Delete, parent: dataTypeOperation)
  private def update = new CompositeFactory(beanClass: Update, parent: dataTypeOperation)
  private def entity = new CompositeFactory(beanClass: Entity, parent: dataType)
  private def enumType = new EnumTypeFactory(beanClass: EnumType, childFactories: ['lit'], parent: dataType)
  private def exist = new CompositeFactory(beanClass: Exist, parent: dataTypeOperation)
  private def externalType = new CompositeFactory(beanClass: ExternalType, parent: type)
  private def find = new CompositeFactory(beanClass: Find, parent: dataTypeOperation)
  private def finder = new CompositeFactory(beanClass: Finders, childFactories: ['exist', 'count', 'findBy'], parent: controller)
  private ModelFactory model = new ModelFactory(childFactories: ['model', 'component'], parent: su)
  private def metaAttribute = new CompositeFactory(beanClass: MetaAttribute, parent: attr)
  
  private def realm = new CompositeFactory(beanClass: Realm, childFactories: ['group', 'role', 'user'])
  private def realmGroup = new CompositeFactory(beanClass: RealmGroup)
  private def realmRole = new CompositeFactory(beanClass: RealmRole)
  private def realmUser = new CompositeFactory(beanClass: RealmUser)

  private def externalModule = new CompositeFactory(beanClass: ExternalModule, childFactories: ['extType'], parent: module)
  private def prop = new PropFactory(parent: attr)
  private def literal = new CompositeFactory(beanClass: Literal)
  private def facade = new CompositeFactory(beanClass: Facade, parent: cu)
  private def namespace = new MddFactory(beanClass: Namespace)

  //UI
  private def factoryWidget = new MddFactory(beanClass: Widget)
  private def factoryControl = new MddFactory(beanClass: Control, parent: factoryWidget)
  private def factoryListener = new MddFactory(beanClass: Listener, parent: operation)

  private def factoryPresenter = new MddFactory(beanClass: Presenter, parent: factoryControl)
  private def factoryViewRef = new MddFactory(beanClass: ViewRef)
  private def factoryDialog = new MddFactory(beanClass: Dialog, parent: factoryWidget)
  private def factoryComboBox = new MddFactory(beanClass: ComboBox, childFactories: ['onSelect'], parent: factoryControl)
  private def factoryGroupContentFrame = new MddFactory(beanClass: GroupContentFrame, childFactories: ['onSelect'], parent: factoryControl)
  private def factoryContextMenu = new MddFactory(beanClass: ContextMenu, childFactories: ['OnActivation'], parent: factoryControl)
  private def factoryCheckBox = new MddFactory(beanClass: CheckBox, childFactories: ['onChange'], parent: factoryControl)
  private def factoryLabel = new MddFactory(beanClass: Label, childFactories: ['onSelect'], parent: factoryControl)
  private def factoryHeader = new MddFactory(beanClass: Header, childFactories: ['onSelect'], parent: factoryControl)
  private def factoryPanel = new MddFactory(beanClass: Panel, childFactories: ['onSelect'], parent: factoryControl)
  private def factorySpinner = new MddFactory(beanClass: Spinner, childFactories: ['onSelect'], parent: factoryControl)
  private def factoryTextField = new MddFactory(beanClass: TextField, childFactories: ['onChange'], parent: factoryControl)
  private def factoryGroupBoxHeader = new MddFactory(beanClass: GroupBoxHeader, childFactories: ['onSelect'], parent: factoryControl)
  private def factoryTimeField = new MddFactory(beanClass: TimeField, childFactories: ['onChange'], parent: factoryControl)
  private def factoryDateField = new MddFactory(beanClass: DateField, childFactories: ['onChange'], parent: factoryControl)
  private def factoryViewModel = new MddFactory(beanClass: ViewModel, parent: factoryControl)
  private def factoryTable = new MddFactory(beanClass: Table, childFactories: ['onSelect'], parent: factoryControl)
  private def factoryColumn = new MddFactory(beanClass: Column, childFactories: ['onSelect'], parent: factoryControl)
  private def factoryButton = new MddFactory(beanClass: Button, childFactories: ['onAction'], parent: factoryControl)
  private def factoryOnAction = new MddFactory(beanClass: OnAction, childFactories: [], parent: factoryListener)
  private def factoryOnActivation = new MddFactory(beanClass: OnActivation, childFactories: [], parent: factoryListener)
  private def factoryOnChange = new MddFactory(beanClass: OnChange, childFactories: [], parent: factoryListener)
  private def factoryOnContextMenuRequest = new MddFactory(beanClass: OnContextMenuRequest, childFactories: [], parent: factoryListener)
  private def factoryOnItemEditorItemSelect = new MddFactory(beanClass: OnItemEditorItemSelect, childFactories: [], parent: factoryListener)
  private def factoryOnSelect = new MddFactory(beanClass: OnSelect, childFactories: [], parent: factoryListener)
  
  //StateMachine
  private def factoryStateMachine = new CompositeFactory(beanClass: StateMachine, childFactories: ['controller', 'action', 'condition', 'event', 'state', 'history', 'stateMachineController', 'context'])
  private def factoryAction = new MddFactory(beanClass: Action, childFactories:[], parent: cu)
  private def factoryCondition = new MddFactory(beanClass: Condition, childFactories:[], parent: cu)
  private def factoryEvent = new MddFactory(beanClass: Event, childFactories: [], parent: cu)
  private def factoryState = new MddFactory(beanClass: State, childFactories: ['on'], parent: lu)
  private def factoryTransition = new MddFactory(beanClass: Transition, childFactories: [])
  private def factoryHistory = new MddFactory(beanClass: History, childFactories: [], parent: lu)
  private def factoryStateMachineController = new MddFactory(beanClass: StateMachineController, childFactories: [], parent: controller)
  private def factoryContext = new MddFactory(beanClass: Context, childFactories: [], parent: pojo)
  
  private def factoryView = new MddFactory(beanClass: View, valueProperty: 'domainName',
  childFactories: ['dialog', 'viewRef', 'viewModel', 'presenter', 'button', 'comboBox', 'contextMenu', 'checkBox', 'label', 'panel', 'spinner', 'textField', 'timeField', 'dateField', 'table'], parent: factoryWidget)
  private CompositeFactory module = new CompositeFactory(beanClass: Module,
  childFactories: ['entity', 'basicType', 'enumType', 'pojo', 'config', 'controller', 'facade', 'container', 'channel', 'dependency', 'view', 'stateMachine'], parent: su)


  ModelBuilder(Closure postInstantiateDelegate = null) {
    super(postInstantiateDelegate)

    OppositeResolveHandler oppositeResolver = typeResolver.addResolver(new OppositeResolveHandler(name: 'opposite'))
    typeResolver.addResolver(oppositeResolver)

    typeResolver.addGlobalResolver('type', Type, null, false, { prop, resolved ->
      if(DataTypeProp.isInstance(prop) && DataType.isInstance(resolved) && !prop.opposite) {
        oppositeResolver.onDataTypeProp(prop)
      }
    })
    typeResolver.addGlobalResolver('ret', Type)
    typeResolver.addGlobalResolver('ref', Element)

    typeResolver.addParentResolver('prop', Prop, 2)
    typeResolver.addGlobalResolver('module', Module)
    typeResolver.addGlobalResolver('superUnit', CompilationUnit)

    MetaAttributeHolder metaAttributeHolder = new MetaAttributeHolder()
    typeResolver.addGlobalResolver('meta', Type, metaAttributeHolder.&forType, true)
    typeResolver.addParentResolver('props', Prop, 2, null, true)

    typeResolver.addGlobalTypes([Model, Module, Component, Type, CompilationUnit])

    typeResolver.addGlobalResolver('view', View)

    facets.names.each { facetName -> registerFactory facetName, new FacetFactory(facetName: facetName, facets: facets, parent: facet) }

    reg()
  }

  void reg() {

    registerFactory 'basicType', basicType
    registerFactory 'body', body
    registerFactory 'cache', cache
    registerFactory 'cond', condition
    registerFactory 'component', component
    registerFactory 'config', config
    registerFactory 'constr', constructor
    registerFactory 'container', container
    registerFactory 'controller', controller
    registerFactory 'xmlController', xmlController
    registerFactory 'configController', configController
    registerFactory 'delegate', operationRef
    registerFactory 'index', index
    registerFactory 'initializer', initializer
    registerFactory 'count', counter
    registerFactory 'create', create
    registerFactory 'delete', delete
    registerFactory 'entity', entity
    registerFactory 'enumType', enumType
    registerFactory 'exist', exist
    registerFactory 'extType', externalType
    registerFactory 'facet', facet
    registerFactory 'findBy', find
    registerFactory 'meta', metaAttribute
    registerFactory 'model', model
    registerFactory 'finder', finder
    registerFactory 'commands', commands
    registerFactory 'module', module
    registerFactory 'extModule', externalModule
    registerFactory 'op', operation
    registerFactory 'param', param
    registerFactory 'pojo', pojo
    registerFactory 'prop', prop
    registerFactory 'lit', literal
    registerFactory 'facade', facade
    registerFactory 'update', update
    registerFactory 'namespace', namespace
    registerFactory 'channel', channel
    registerFactory 'message', message
    
    registerFactory 'realm' , realm
    registerFactory 'group', realmGroup
    registerFactory 'role', realmRole
    registerFactory 'user', realmUser

    //UI
    registerFactory 'view', factoryView
    registerFactory 'viewRef', factoryViewRef
    registerFactory 'dialog', factoryDialog
    registerFactory 'button', factoryButton
    registerFactory 'comboBox', factoryComboBox
    registerFactory 'groupContentFrame', factoryGroupContentFrame
    registerFactory 'contextMenu', factoryContextMenu
    registerFactory 'checkBox', factoryCheckBox
    registerFactory 'label', factoryLabel
    registerFactory 'header', factoryHeader
    registerFactory 'panel', factoryPanel
    registerFactory 'spinner', factorySpinner
    registerFactory 'textField', factoryTextField
    registerFactory 'groupBoxHeader', factoryGroupBoxHeader
    registerFactory 'timeField', factoryTimeField
    registerFactory 'dateField', factoryDateField
    registerFactory 'presenter', factoryPresenter
    registerFactory 'viewModel', factoryViewModel
    registerFactory 'table', factoryTable
    registerFactory 'column', factoryColumn
    registerFactory 'onAction', factoryOnAction
    registerFactory 'onActivation', factoryOnActivation
    registerFactory 'onChange', factoryOnChange
    registerFactory 'onContextMenuRequest', factoryOnContextMenuRequest
    registerFactory 'onItemEditorItemSelect', factoryOnItemEditorItemSelect
    registerFactory 'onSelect', factoryOnSelect
    
    //StateMachine
    
    registerFactory 'stateMachine', factoryStateMachine
    registerFactory 'action', factoryAction
    registerFactory 'condition', factoryCondition
    registerFactory 'event', factoryEvent
    registerFactory 'state', factoryState
    registerFactory 'on', factoryTransition
    registerFactory 'history', factoryHistory
    registerFactory 'stateMachineController', factoryStateMachineController
    registerFactory 'context', factoryContext
  }
}

