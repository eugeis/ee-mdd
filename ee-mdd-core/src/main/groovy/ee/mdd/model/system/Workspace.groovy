package ee.mdd.model.system

import ee.mdd.model.Composite

class Workspace extends Composite {
  String folder
  List<Service> services = []
  List<Content> modules = []

  def add(Service item) {
    services << item; super.add(item)
  }
  def add(Content item) {
    modules << item; super.add(item)
  }
}