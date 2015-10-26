package ee.mdd.model.statemachine

import ee.mdd.model.Composite
import ee.mdd.model.component.Component
import ee.mdd.model.component.Config
import ee.mdd.model.component.ConfigController
import ee.mdd.model.component.Entity
import ee.mdd.model.component.Module
import ee.mdd.model.component.Names
import ee.mdd.model.component.Prop

class StateMachine extends Composite {
  Names n
  boolean generateDefaultImpl = true
  Boolean generatePermissionsForEvents = false
  String statePropRef, entityRef, stateTimeoutPropRef, logLevel='debug', key
  Integer timeoutCheckIntervalInMillis
  Entity _entity
  Context context
  Prop _stateProp, _histProp, _stateTimeoutProp
  List<String> notifiables = []

  List<Event> events = []
  List<Action> actions = []
  List<Condition> conditions = []
  List<State> states = []
  History history
  StateEvent stateEvent
  StateMachineController controller
  Config timeouts

  void add(StateMachineController item) {
    super.add(item); controller = item
  }
  
  Module getModule() {
    parent.module
  }

  Component getComponent() {
    parent.component
  }
  
  Names getN() {
    if (!n) {
      if(key)
        n = new Names(this, key)
      else
        n = new Names(this, name)
    }
    n
  }
  
  String getCapShortName() {
    underscoreToCamelCase(key).capitalize();
  }
  
  String getUncapShortName() {
    underscoreToCamelCase(key).toLowerCase();
  }

  Entity getEntity() {
    if(!_entity) {
      _entity = module.resolve(entityRef, Entity.class, true)
    }; _entity
  }
  Prop getStateProp() {
    if(!_stateProp) {
      _stateProp = entity.resolve(statePropRef, Prop.class, true)
      assert _stateProp.typeEl, "typeEl of the state property '$_stateProp.type' can not be resolved."
    }; _stateProp
  }
  Prop getHistProp() {
    if(!_histProp) {
      _histProp = findHistProp(); assert _histProp, "histProp is null in '$this'"
    }; _histProp
  }
  Prop getStateTimeoutProp() {
    if(!_stateTimeoutProp) {
      _stateTimeoutProp = entity.resolve(stateTimeoutPropRef, Prop.class, true)
    }; _stateTimeoutProp
  }

  def add(State item) {
    if(!states) {
      states = []
    }; states << super.add(item)
  }
  
  def add(Action item) {
    if(!actions) {
      actions = []
    }; actions << super.add(item)
  }
  
  def add(Event item) {
    if(!events) {
      events = []
    }; events << super.add(item)
  }
  
  def add(Condition item) {
    if(!conditions) {
      conditions = []
    }; conditions << super.add(item)
  }

  def add(Context item) {
    context = super.add(item)
  }
  
  def add(StateEvent item) {
    stateEvent = super.add(item);
  }
 
  def add(History item) {
    println "Enabling history tracking for entity $item.entityRef"; super.add(item); history = item
  }

  void setTimeoutCheckInterval(String timeoutCheckInterval) {
    timeoutCheckIntervalInMillis = timeoutCheckInterval.duration(timeoutCheckInterval)
  }
  
  String underscoreToCamelCase(String underscoreStr) {
    if(!underscoreStr || underscoreStr.isAllWhitespace()){
      return ''
    }
    return underscoreStr.replaceAll(/_\w/){ it[1].toUpperCase() }
  }

  protected Prop findHistProp() {
    if (history) {
      entity.propsRecursive.find { prop ->
        //println "$prop.type, $prop.typeEl == $history.entity";
        prop.typeEl == history.entity }
    }
  }

  boolean init() {
    boolean ret = super.init()

    if(isTimeoutEnabled()) {
      //createStatesTimeoutConfig()
      addTimeoutEvent()
    }

    ret
  }

  // Returns the groups associated with each event.
  // The groups are listed in a comma separated qoted strig lists. ('gr1','gr2')
  Map getEventGroupsMap(){
    def eventGroupsMap = new HashMap()
    states.transitions.flatten().each{ transition->
      if (eventGroupsMap.containsKey(transition.event.name)) {
        eventGroupsMap[transition.event.name].addAll(transition.groups as Set)
      } else {
        eventGroupsMap.put(transition.event.name, transition.groups as Set)
      }
    }
    eventGroupsMap.each{key, value ->
      def groups = ''
      value.each{ groups += "'" + it + "'," }
      groups = groups.length()>0?groups[0..-2]:groups
      eventGroupsMap.put(key, groups)
    }

    return eventGroupsMap
  }

  void addTimeoutEvent() {
    add(new Event(name: 'timeout', alternative: true))
  }

  void createStatesTimeoutConfig() {
    def description = "Stores the defined state timeouts (milliseconds) of the $name state machine"
    timeouts = new Config(name: "${capShortName}Timeouts", description: description, namespace: component.subPackages.model, event: true)
    add(timeouts)

    Prop prop = new Prop(name: "timeoutCheckInterval", type: 'int', defaultValue: timeoutCheckIntervalInMillis)
    timeouts.add(prop)
    //create prop entry for each state that has a timeout
    states.each { State item ->
      if(item.timeoutEnabled){
        prop = new Prop(name: "${item.uncapName}Timeout", type: 'int', defaultValue: item.timeoutInMillis)
        timeouts.add(prop)
      }
    }
    timeouts.add( new ConfigController(namespace: component.subPackages.core, base:true) )
  }

  boolean isTimeoutEnabled() { timeoutCheckIntervalInMillis }
}