/*
 * Controlguide
 * Copyright (c) Siemens AG 2014, All Rights Reserved, Confidential
 */
package ee.mdd.builder

import ee.mdd.ModelBuilder


interface BuilderAware {
  void setBuilder(AbstractFactoryBuilder builder)
  void setFactory(MddFactory factory)
}
