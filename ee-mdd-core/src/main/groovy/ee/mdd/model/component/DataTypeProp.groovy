/*
 * Controlguide
 * Copyright (c) Siemens AG 2014, All Rights Reserved, Confidential
 */
package ee.mdd.model.component;

import ee.mdd.model.Element

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class DataTypeProp extends Prop {
  boolean unique, primaryKey, owner, index, mm
  boolean lob
  boolean ignoreInChangeFlag = false
  DataTypeProp opposite
}
