
function UmEntity {
  // inherit from base class
  UmEntity.prototype = new UmEntityBase();

  // correct the constructor pointer because it points to base class
  UmEntity.prototype.constructor = UmEntity;
}