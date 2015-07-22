package ee.mdd.model.ui;

class OnContextMenuRequest extends Listener {
  void init() {
    super.init()
    eventType = "ContextMenuRequestEvent"
    callbackBaseName = "${parent.capName}ContextMenuRequested"
    eventValueType = 'String'
    signatureValue = 'String value'
  }

  void buildMe() {
    super.buildMe()
    receiverCallValue = "$receiverName(event.getItem())"
  }
}