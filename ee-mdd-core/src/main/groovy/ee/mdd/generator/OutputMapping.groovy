/*
 * Controlguide
 * Copyright (c) Siemens AG 2015, All Rights Reserved, Confidential
 */
package ee.mdd.generator


/**
 *
 * @author Eugen Eisler
 */
class OutputMapping {
  OutputPurpose purpose
  OutputType type
  String facet
  String sourceModule

  /** Must not be null */
  String outputModule
}
