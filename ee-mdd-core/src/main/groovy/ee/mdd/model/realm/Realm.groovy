package ee.mdd.model.realm

import ee.mdd.model.Composite

class Realm extends Composite {
  List<RealmUser> users = []
  List<RealmGroup> groups = []
  List<RealmRole> roles = []
  List<RealmWorkstationType> workstationTypes = []

  void add(RealmUser item) {
    super.add(item); users << item
  }
  void add(RealmGroup item) {
    super.add(item); groups << item
  }
  void add(RealmRole item) {
    super.add(item); roles << item
  }
  void add(RealmWorkstationType item) {
    super.add(item); workstationTypes << item 
  }

  boolean isEmpty() {
    users.empty && groups.empty && roles.empty && workstationTypes.empty
  }
}