package ee.mdd.model.ui;

class OnChange extends Listener {
  void init() {
    super.init()
    eventTypeRawType = "ChangeEvent"
    eventType = "$eventTypeRawType<$eventValueType>"
    signatureValue = "$eventValueType value"
    callbackBaseName = "${parent.capName}Changed"
  }

  void buildMe() {
    super.buildMe()
    receiverCallValue = "$receiverName(event.getNewValue())"
  }
}