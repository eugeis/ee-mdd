/*
 * Controlguide
 * Copyright (c) Siemens AG 2014, All Rights Reserved, Confidential
 */
package ee.mdd.model.component

import ee.mdd.model.Element

/**
 *
 * @author Niklas Cappelmann
 */
class Finder extends Controller {

  List<Find> getFinders() {
    operations?.findAll { Find.isInstance(it) }
  }

  List<Count> getCounters() {
    operations?.findAll { Count.isInstance(it) }
  }

  List<Exist> getExists() {
    operations?.findAll { Exist.isInstance(it) }
  }

  void fillReference(Map<String, Element> refToMe) {
    super.fillReference(refToMe)
    refToMe['finder'] = this
  }
}
