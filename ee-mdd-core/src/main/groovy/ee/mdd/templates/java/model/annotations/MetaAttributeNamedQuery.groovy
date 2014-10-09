/*
 * Controlguide
 * Copyright (c) Siemens AG 2014, All Rights Reserved, Confidential
 */
package ee.mdd.templates.java.model.annotations

import static java.lang.annotation.RetentionPolicy.*
import ee.mdd.generator.Context
import ee.mdd.model.component.MetaAttribute

class MetaAttributeNamedQuery extends MetaAttribute {
  String query;
  def hints

  String toStringJava(Context c) {
  }
}
