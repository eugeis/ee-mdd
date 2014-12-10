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
class Command extends Controller {

  List<Delete> getDeleters() {
    operations?.findAll { Delete.isInstance(it) }
  }

  List<Delete> getUpdates() {
    operations?.findAll { Update.isInstance(it) }
  }

  List<Delete> getCreates() {
    operations?.findAll { Create.isInstance(it) }
  }

  void fillReference(Map<String, Element> refToMe) {
    super.fillReference(refToMe)
    refToMe['commands'] = this
  }
}
