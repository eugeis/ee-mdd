package ee.mdd.model.component


/**
 *
 * @author Niklas Cappelmann
 */
class UserProfile extends Profile {
  String getBaseName() { "${parent.capShortName}UserProfile" }
  String getCfgFile() { "${parent.uncapShortName}DefaultUserProfile.json" }
}
