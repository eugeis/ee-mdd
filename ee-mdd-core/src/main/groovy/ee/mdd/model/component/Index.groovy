/*
 * Controlguide
 * Copyright (c) Siemens AG 2014, All Rights Reserved, Confidential
 */
package ee.mdd.model.component

import ee.mdd.model.Composite

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Index extends Composite {
  List<Prop> props = []
  boolean index = false
  boolean unique = false

  def add(Prop item) {
    props << super.add(item)
  }
}
