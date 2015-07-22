package ee.mdd.model.ui;
class Table extends Control {
  List<Column> columns = []
  OnContextMenuRequest onContextMenuRequest
  OnSelect onSelect
  OnItemEditorItemSelect onItemEditorItemSelect

  void add(Column item) { super.add(item); columns << item }
  void add(OnSelect item) { super.add(item); onSelect = item }
  void add(OnAction item) { super.add(item); onAction = item }
  void add(OnItemEditorItemSelect item) {onItemEditorItemSelect = item; super.add(item)}
}