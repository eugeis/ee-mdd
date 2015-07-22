package ee.mdd.model.ui

class OnAction extends Listener {
  boolean init() {
    super.init()
    eventType = 'ActionEvent'
    callbackBaseName = "${parent.capName}"
    signatureValue = ''
    true
  }

  void buildMe() {
    super.buildMe()
    handlerCall = "$handlerName()"
    observerCall = "$observerName()"
  }
}