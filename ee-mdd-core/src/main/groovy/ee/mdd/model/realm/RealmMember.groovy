package ee.mdd.model.realm

import ee.mdd.model.Composite


class RealmMember extends Composite {
  List<RealmGroup> _groups

  List<String> groupRefs

  List<RealmGroup> getGroups() {
    if(!_groups) {
      _groups = groupRefs.collect { parent.resolve(it, RealmGroup.class, true) }
    }; _groups
  }

  String getQuotedGroupNames() {
    def groups = ''
    groupRefs.each{name ->
      groups += "'" + name + "',"
    }
    groups = groups.length()>0?groups[0..-2]:groups
    return groups
  }
}