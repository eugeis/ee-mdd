package ee.mdd.model.component;
class ConfigController extends Controller {
  boolean addDefaultOperations = true

  boolean init() {
    super.init()
    if (addDefaultOperations) {
      def op = new Operation(name: 'update', ret: (Type) parent)
      op.add(new Param(name: parent.uncap, type: (Type) parent))
      add(op)
      op = new Operation(name: 'load', ret: (Type) parent)
      add(op)
    }
  }
}