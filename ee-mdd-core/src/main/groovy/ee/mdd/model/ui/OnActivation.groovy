package ee.mdd.model.ui;

class OnActivation extends Listener {
  void init() {
    super.init()
    eventType = "ActivationEvent"
    callbackBaseName = "${parent.capName}Activated"
    eventValueType = 'String'
    signatureValue = 'String value'
  }

  void buildMe() {
    super.buildMe()
    receiverCallValue = "$receiverName(event.getItemId())"
  }
}