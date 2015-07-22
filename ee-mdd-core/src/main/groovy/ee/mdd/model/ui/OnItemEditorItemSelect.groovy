package ee.mdd.model.ui
class OnItemEditorItemSelect extends Listener {
  String editorType

  boolean init() {
    super.init()
    forward = false
    eventType = "ItemEditorItemSelectionEvent"
    callbackBaseName = "${parent.capName}ItemSelected"
    true
  }

  void buildMe() {
    super.buildMe()
  }
}