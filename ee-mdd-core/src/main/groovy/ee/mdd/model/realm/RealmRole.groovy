package ee.mdd.model.realm

class RealmRole extends RealmMember {
  String getRoleDescription() {
    description ?: 'Role for ' + name
  }
}