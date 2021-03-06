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

import ee.mdd.builder.*
import ee.mdd.model.Base
import ee.mdd.model.Body
import ee.mdd.model.Element
import ee.mdd.model.Namespace
import ee.mdd.model.component.*
import ee.mdd.model.realm.*
import ee.mdd.model.statemachine.*
import ee.mdd.model.ui.*

/**
 *
 * @author Eugen Eisler
 */
class ModelBuilder extends AbstractFactoryBuilder {
    private Facets facets = new Facets()
    private def type = new CompositeFactory(beanClass: Type, childFactories: ['meta'])
    private def cache = new CompositeFactory(beanClass: Cache, childFactories: ['op'], parent: cu)
    private def channel = new CompositeFactory(beanClass: Channel, childFactories: ['meta', 'message'])
    private
    def cu = new CompositeFactory(beanClass: CompilationUnit, childFactories: ['constr', 'prop', 'op', 'delegate', 'cache'], parent: type)
    private
    def dataType = new CompositeFactory(beanClass: DataType, childFactories: ['finder', 'commands', 'index'], parent: cu)
    private def interfType = new CompositeFactory(beanClass: InterfType, parent: cu)
    private def typeRef = new CompositeFactory(beanClass: TypeRef)
    private def message = new CompositeFactory(beanClass: Message, childFactories: ['meta'], parent: typeRef)
    private def basicType = new CompositeFactory(beanClass: BasicType, parent: dataType)
    private def body = new CompositeFactory(beanClass: Body, childFactories: ['type'])
    private def pojo = new CompositeFactory(beanClass: Pojo, parent: cu)
    private def lu = new CompositeFactory(beanClass: LogicUnit, childFactories: ['meta', 'param'], parent: body)
    private def attr = new CompositeFactory(beanClass: Attribute, childFactories: ['meta'])
    private def param = new CompositeFactory(beanClass: Param, parent: attr)
    private def operation = new CompositeFactory(beanClass: Operation, parent: lu)
    private def operationRef = new CompositeFactory(beanClass: OperationRef, valueProperty: 'ref', parent: operation)
    private def facet = new FacetFactory(beanClass: Facet, childFactories: ['dependencies', 'extModule', 'facet'], facets: facets)
    private
    def su = new FacetAwareFactory(beanClass: StructureUnit, childFactories: ['facet', 'namespace'], facets: facets)
    private def command = new CompositeFactory(beanClass: Command, childFactories: ['prop'], parent: dataType)
    private def commandFactory = new CompositeFactory(beanClass: CommandFactory, parent: dataType)
    private
    def commands = new CompositeFactory(beanClass: Commands, childFactories: ['create', 'delete', 'update', 'prop', 'op'], parent: controller)
    private def moduleGroup = new CompositeFactory(beanClass: ModuleGroup)
    private
    def component = new CompositeFactory(beanClass: Component, childFactories: ['moduleGroup', 'module', 'realm'], parent: su)
    private def condition = new CompositeFactory(beanClass: ConditionParam, parent: param)
    private def config = new CompositeFactory(beanClass: Config, parent: dataType, childFactories: ['configController'])
    private def constructor = new CompositeFactory(beanClass: Constructor, parent: lu)
    private
    def container = new CompositeFactory(beanClass: Container, childFactories: ['controller', 'xmlController'], parent: dataType)
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
    private
    def finder = new CompositeFactory(beanClass: Finders, childFactories: ['exist', 'count', 'findBy'], parent: controller)
    private ModelFactory model = new ModelFactory(childFactories: ['model', 'component'], parent: su)
    private def metaAttribute = new CompositeFactory(beanClass: MetaAttribute, parent: attr)

    private def profile = new CompositeFactory(beanClass: Profile)
    private def componentProfile = new CompositeFactory(beanClass: ComponentProfile)
    private def userProfile = new CompositeFactory(beanClass: UserProfile)

    private
    def realm = new CompositeFactory(beanClass: Realm, childFactories: ['group', 'role', 'user', 'workstationType'])
    private def realmGroup = new CompositeFactory(beanClass: RealmGroup)
    private def realmRole = new CompositeFactory(beanClass: RealmRole)
    private def realmUser = new CompositeFactory(beanClass: RealmUser)
    private def realmWorkstationType = new CompositeFactory(beanClass: RealmWorkstationType)

    private
    def externalModule = new CompositeFactory(beanClass: ExternalModule, childFactories: ['extType'], parent: module)
    private def prop = new PropFactory(parent: attr)
    private def literal = new CompositeFactory(beanClass: Literal)
    private def facade = new CompositeFactory(beanClass: Facade, parent: cu)
    private def namespace = new MddFactory(beanClass: Namespace)
    private def dependencies = new MddFactory(beanClass: Dependencies)

    //UI
    private def factoryWidget = new MddFactory(beanClass: Widget)
    private def factoryControl = new MddFactory(beanClass: Control, parent: factoryWidget)
    private def factoryListener = new MddFactory(beanClass: Listener, parent: operation)

    private def factoryPresenter = new MddFactory(beanClass: Presenter, parent: factoryControl)
    private def factoryViewRef = new MddFactory(beanClass: ViewRef)
    private def factoryDialog = new MddFactory(beanClass: Dialog, parent: factoryWidget)
    private
    def factoryComboBox = new MddFactory(beanClass: ComboBox, childFactories: ['onSelect'], parent: factoryControl)
    private
    def factoryGroupContentFrame = new MddFactory(beanClass: GroupContentFrame, childFactories: ['onSelect'], parent: factoryControl)
    private
    def factoryContextMenu = new MddFactory(beanClass: ContextMenu, childFactories: ['OnActivation'], parent: factoryControl)
    private
    def factoryCheckBox = new MddFactory(beanClass: CheckBox, childFactories: ['onChange'], parent: factoryControl)
    private def factoryLabel = new MddFactory(beanClass: Label, childFactories: ['onSelect'], parent: factoryControl)
    private def factoryHeader = new MddFactory(beanClass: Header, childFactories: ['onSelect'], parent: factoryControl)
    private def factoryPanel = new MddFactory(beanClass: Panel, childFactories: ['onSelect'], parent: factoryControl)
    private
    def factorySpinner = new MddFactory(beanClass: Spinner, childFactories: ['onSelect', 'onChange'], parent: factoryControl)
    private
    def factoryTextField = new MddFactory(beanClass: TextField, childFactories: ['onChange'], parent: factoryControl)
    private
    def factoryGroupBoxHeader = new MddFactory(beanClass: GroupBoxHeader, childFactories: ['onSelect'], parent: factoryControl)
    private
    def factoryTimeField = new MddFactory(beanClass: TimeField, childFactories: ['onChange'], parent: factoryControl)
    private
    def factoryDateField = new MddFactory(beanClass: DateField, childFactories: ['onChange'], parent: factoryControl)
    private def factoryViewModel = new MddFactory(beanClass: ViewModel, parent: factoryControl)
    private def factoryTable = new MddFactory(beanClass: Table, childFactories: ['onSelect'], parent: factoryControl)
    private def factoryColumn = new MddFactory(beanClass: Column, childFactories: ['onSelect'], parent: factoryControl)
    private def factoryButton = new MddFactory(beanClass: Button, childFactories: ['onAction'], parent: factoryControl)
    private def factoryOnAction = new MddFactory(beanClass: OnAction, childFactories: [], parent: factoryListener)
    private
    def factoryOnActivation = new MddFactory(beanClass: OnActivation, childFactories: [], parent: factoryListener)
    private def factoryOnChange = new MddFactory(beanClass: OnChange, childFactories: [], parent: factoryListener)
    private
    def factoryOnContextMenuRequest = new MddFactory(beanClass: OnContextMenuRequest, childFactories: [], parent: factoryListener)
    private
    def factoryOnItemEditorItemSelect = new MddFactory(beanClass: OnItemEditorItemSelect, childFactories: [], parent: factoryListener)
    private def factoryOnSelect = new MddFactory(beanClass: OnSelect, childFactories: [], parent: factoryListener)

    //StateMachine
    private
    def factoryStateMachine = new CompositeFactory(beanClass: StateMachine, childFactories: ['controller', 'action', 'condition', 'enumType', 'entity', 'event', 'state', 'stateEvent', 'history', 'stateMachineController', 'context'])
    private def factoryAction = new MddFactory(beanClass: Action, childFactories: [], parent: cu)
    private def factoryCondition = new MddFactory(beanClass: Condition, childFactories: [], parent: cu)
    private def factoryEvent = new MddFactory(beanClass: Event, childFactories: [], parent: cu)
    private def factoryState = new MddFactory(beanClass: State, childFactories: ['on'], parent: lu)
    private def factoryStateEvent = new MddFactory(beanClass: StateEvent, childFactories: [], parent: cu)
    private def factoryTransition = new MddFactory(beanClass: Transition, childFactories: [])
    private def factoryHistory = new MddFactory(beanClass: History, childFactories: [], parent: lu)
    private
    def factoryStateMachineController = new MddFactory(beanClass: StateMachineController, childFactories: [], parent: controller)
    private def factoryContext = new MddFactory(beanClass: Context, childFactories: [], parent: pojo)

    private def factoryView = new MddFactory(beanClass: View, valueProperty: 'domainName',
            childFactories: ['dialog', 'header', 'viewRef', 'viewModel', 'presenter', 'button', 'comboBox', 'contextMenu', 'checkBox', 'groupContentFrame', 'groupBoxHeader', 'label', 'panel', 'spinner', 'textField', 'timeField', 'dateField', 'table'], parent: factoryWidget)
    private CompositeFactory module = new CompositeFactory(beanClass: Module,
            childFactories: ['entity', 'basicType', 'enumType', 'pojo', 'config', 'command', 'commandFactory', 'controller', 'facade', 'container', 'channel', 'dependencies', 'view', 'stateMachine', 'interf'], parent: su)


    ModelBuilder(Closure postInstantiateDelegate = null) {
        super(postInstantiateDelegate)

        typeResolver.addGlobalType(Model, Module, Type)

        OppositeResolveHandler oppositeResolver = typeResolver.addResolver(new OppositeResolveHandler(name: 'opposite'))
        typeResolver.addResolver(oppositeResolver)

        typeResolver.addGlobalResolverByParentRoot('type', Type, Component, pathResolver(), null, false, { prop, resolved ->
            if (DataTypeProp.isInstance(prop) && DataType.isInstance(resolved) && !prop.opposite) {
                oppositeResolver.onDataTypeProp(prop)
            }
        })
        typeResolver.addGlobalResolver('type', ExternalType, pathResolver(), [Object] as Set)
        typeResolver.addGlobalResolver('component', Component, pathResolver(), [Model] as Set)

        typeResolver.addGlobalResolverByParentRoot('ret', Type, Component, pathResolver())
        typeResolver.addGlobalResolver('ret', ExternalType, pathResolver(), [Object] as Set)

        typeResolver.addGlobalResolverByParentRoot('ref', Element, Component, pathResolver())

        typeResolver.addParentResolver('prop', Prop, 2)
        typeResolver.addGlobalResolverByParentRoot('module', Module, Component, pathResolver())
        typeResolver.addGlobalResolverByParentRoot('superUnit', CompilationUnit, Component, pathResolver())

        MetaAttributeHolder metaAttributeHolder = new MetaAttributeHolder()
        typeResolver.addGlobalResolverByParentRoot('meta', Type, Component, pathResolver(), metaAttributeHolder.&forType, true)
        typeResolver.addParentResolver('props', Prop, 2, null, true)

        typeResolver.addGlobalResolverByParentRoot('modules', Module, Component, pathResolver(), null, true)
        typeResolver.addGlobalResolverByParentRoot('dependencies', Module, Component, pathResolver(), null, true)

        typeResolver.addGlobalResolverByParentRoot('view', View, Component, pathResolver())

        facets.names.each { facetName -> registerFactory facetName, new FacetFactory(facetName: facetName, facets: facets, parent: facet) }

        reg()
    }

    protected PathResolveHandler pathResolver() {
        new PathResolveHandler(
                rootFindParentMatcher: { String part, Base el, Base parent ->
                    el.findUp(parent) { Base item ->
                        part.equalsIgnoreCase(item.name) ||
                                (StructureUnit.isInstance(item) && part.equalsIgnoreCase(((StructureUnit) item).key))
                    }
                },
                midPartFindMatcher: { String part, Component el, Base parent ->
                    el.find { Base item ->
                        part.equalsIgnoreCase(item.name) ||
                                (StructureUnit.isInstance(item) && part.equalsIgnoreCase(((StructureUnit) item).key))
                    }
                },
                lastPartFindMatcher: { String part, Component el, Base parent ->
                    el.find { Base item ->
                        part.equalsIgnoreCase(item.name) ||
                                (StructureUnit.isInstance(item) && part.equalsIgnoreCase(((StructureUnit) item).key))
                    }
                }
        )
    }

    void reg() {

        registerFactory 'basicType', basicType
        registerFactory 'body', body
        registerFactory 'cache', cache
        registerFactory 'cond', condition
        registerFactory 'command', command
        registerFactory 'commandFactory', commandFactory
        registerFactory 'component', component
        registerFactory 'moduleGroup', moduleGroup
        registerFactory 'config', config
        registerFactory 'constr', constructor
        registerFactory 'container', container
        registerFactory 'controller', controller
        registerFactory 'xmlController', xmlController
        registerFactory 'configController', configController
        registerFactory 'delegate', operationRef
        registerFactory 'index', index
        registerFactory 'initializer', initializer
        registerFactory 'interf', interfType
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
        registerFactory 'dependencies', dependencies
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

        registerFactory 'profile', profile
        registerFactory 'componentProfile', componentProfile
        registerFactory 'userProfile', userProfile

        registerFactory 'realm', realm
        registerFactory 'group', realmGroup
        registerFactory 'role', realmRole
        registerFactory 'user', realmUser
        registerFactory 'workstationType', realmWorkstationType

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
        registerFactory 'stateEvent', factoryStateEvent
        registerFactory 'on', factoryTransition
        registerFactory 'history', factoryHistory
        registerFactory 'stateMachineController', factoryStateMachineController
        registerFactory 'context', factoryContext
    }
}

