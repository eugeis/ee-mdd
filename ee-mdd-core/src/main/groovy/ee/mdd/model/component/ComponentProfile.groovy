package ee.mdd.model.component


/**
 *
 * @author Niklas Cappelmann
 */
class ComponentProfile extends Profile {
  String getBaseName() { "${parent.capShortName}ComponentProfile" }
  String getCfgFile() { "${parent.uncapShortName}ComponentProfile.json" }
}
