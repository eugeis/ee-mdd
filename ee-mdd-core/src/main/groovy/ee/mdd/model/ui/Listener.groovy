package ee.mdd.model.ui

import ee.mdd.model.component.Operation

class Listener extends Operation {
  boolean forward = true
  String eventType
  String eventTypeRawType
  String eventValueType
  String eventDomainType
  String eventDefaultValue
  String signatureEvent
  String signatureValue
  String callbackBaseName
  String receiverName
  String receiverCallValue
  String handlerName
  String handlerCall
  String observerName
  String observerCall
  List<String> handlerRefs = []
  List<PresentationLogicUnit> handlers = []
  List<String> observerRefs = []
  List<PresentationLogicUnit> observers = []

  String deriveName() {
    getClass().simpleName
  }

  void buildMe() {
    super.buildMe()
    receiverName = "on$callbackBaseName"
    receiverCallValue = "$receiverName()"
    handlerName = "handle$callbackBaseName"
    handlerCall = "$handlerName(value)"
    observerName = "after$callbackBaseName"
    observerCall = "$observerName(value)"
    signatureEvent = "$eventType event"
    if (widget.domainType) {
      eventDomainType = widget.domainType.capName
      signatureValue = "$eventDomainType value"
      eventValueType = "$eventDomainType"
    } else {
      eventDomainType = eventValueType
    }
    eventDefaultValue = "defaultValue(${eventDomainType}.class)"
    if (!eventTypeRawType) {
      eventTypeRawType = eventType
    }
    handlers = handlerRefs.collect { new Reference().build(module(), it, true, module()).presentationLogicUnit }
    if (handlers.isEmpty()) {
      handlers << presenter
    }
    handlers.each{ it.addHandlerFor(this) }
    observers = observerRefs.collect { new Reference().build(module(), it, true, module()).presentationLogicUnit }
    observers.each{ it.addObserverFor(this) }
  }

  Widget getWidget() {
    parent
  }
  Presenter getPresenter() {
    widget.view.presenter
  }
}