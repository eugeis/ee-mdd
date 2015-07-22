package ee.mdd.model.ui

class OnChange extends Listener {
  boolean init() {
    super.init()
    eventTypeRawType = "ChangeEvent"
    eventType = "$eventTypeRawType<$eventValueType>"
    signatureValue = "$eventValueType value"
    callbackBaseName = "${parent.capName}Changed"
    true
  }

  void buildMe() {
    super.buildMe()
    receiverCallValue = "$receiverName(event.getNewValue())"
  }
}