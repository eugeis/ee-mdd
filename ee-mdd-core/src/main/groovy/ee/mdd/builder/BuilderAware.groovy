/*
 * Controlguide
 * Copyright (c) Siemens AG 2014, All Rights Reserved, Confidential
 */
package ee.mdd.builder

import groovy.lang.Closure;


interface BuilderAware {
  void setBuilder(ModelBuilder builder)
  Closure childBuilder()
}
