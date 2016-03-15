package ee.mdd.model.realm

import ee.mdd.model.Composite


class RealmMember extends Composite {
  List<RealmGroup> _groups
  List<RealmWorkstationType> _workstationTypes

  List<String> groupRefs
  List<String> workstationTypeRefs

  List<RealmGroup> getGroups() {
    if(!_groups) {
      _groups = groupRefs.collect { parent.resolve(it, RealmGroup.class, true) }
    }; _groups
  }
  
  List<RealmWorkstationType> getWorkstationTypes() { 
    if(!_workstationTypes) { 
      _workstationTypes = workstationTypeRefs.collect { parent.resolve(it, RealmWorkstationType.class, true) }
    }; _workstationTypes }

  String getQuotedGroupNames() {
    def groups = ''
    groupRefs.each{name ->
      groups += "'" + name + "',"
    }
    groups = groups.length()>0?groups[0..-2]:groups
    return groups
  }
}