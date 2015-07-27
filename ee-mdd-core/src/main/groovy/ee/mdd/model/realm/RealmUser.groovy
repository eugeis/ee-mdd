package ee.mdd.model.realm

class RealmUser extends RealmMember {
  String pass

  String getUserDescription() {
    description?: name
  }
  String getPass() {
    if (!pass) {
      pass = "${name}1${name}"
    }; pass
  }

  List<RealmRole> getRoles() {
    Set<String> groupNamesAsSet = groupRefs as Set
    //user roles = corelation between roles of group and roles of uses
    parent.roles.findAll { role -> role.groups.find { group -> groupNamesAsSet.contains(group.name) } }.sort { it.name }
  }

}