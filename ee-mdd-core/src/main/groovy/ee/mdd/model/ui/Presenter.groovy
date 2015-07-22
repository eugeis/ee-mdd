package ee.mdd.model.ui;
class Presenter extends PresentationLogicUnit {
  boolean withMediator = false

  String deriveName() { "${view.domainName}Presenter" }
}