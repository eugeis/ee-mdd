package ee.mdd.model.component

import ee.mdd.model.Element


/**
 *
 * @author Niklas Cappelmann
 */
class Profile extends Element {
  List<Prop> props = []
  
    void add(Prop item) { super.add(item); props<<item }
}
