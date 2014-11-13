/*
 * Controlguide
 * Copyright (c) Siemens AG 2014, All Rights Reserved, Confidential
 */
package ee.mdd.model.component

import ee.mdd.model.Composite

class Index extends Composite {
  List<Prop> props = []

  def add(Prop item) {
    props << super.add(item)
  }
}
