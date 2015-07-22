package ee.mdd.model.ui;
class OnItemEditorItemSelect extends Listener {
  String editorType

  void init() {
    super.init()
    forward = false
    eventType = "ItemEditorItemSelectionEvent"
    callbackBaseName = "${parent.capName}ItemSelected"
  }

  void buildMe() {
    super.buildMe()
  }
}