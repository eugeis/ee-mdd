package ee.mdd.model.ui

class OnActivation extends Listener {
  boolean init() {
    super.init()
    eventType = "ActivationEvent"
    callbackBaseName = "${parent.cap}Activated"
    eventValueType = 'String'
    signatureValue = 'String value'
    true
  }

  void buildMe() {
    super.buildMe()
    receiverCallValue = "$receiverName(event.getItemId())"
  }
}