package ee.mdd.model.system

import ee.mdd.model.Element

class Machine extends Element {
  String host
  int port
  List<Workspace> workspaces = []

  def add(Workspace item) {
    workspaces << item; super.add(item)
  }
}