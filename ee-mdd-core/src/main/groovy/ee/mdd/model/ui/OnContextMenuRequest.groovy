package ee.mdd.model.ui

class OnContextMenuRequest extends Listener {
  boolean init() {
    super.init()
    eventType = "ContextMenuRequestEvent"
    callbackBaseName = "${parent.cap}ContextMenuRequested"
    eventValueType = 'String'
    signatureValue = 'String value'
    true
  }

  void buildMe() {
    super.buildMe()
    receiverCallValue = "$receiverName(event.getItem())"
  }
}