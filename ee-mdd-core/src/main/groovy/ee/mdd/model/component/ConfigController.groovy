package ee.mdd.model.component;
class ConfigController extends Controller {
  boolean addDefaultOperations = true

  protected boolean init() {
    if (addDefaultOperations) {
      def op = new Operation(name: 'update', nameExternal: "update${parent.cap}", ret: (Type) parent)
      op.add(new Param(name: parent.uncap, type: (Type) parent))
      add(op)
      op = new Operation(name: 'load', nameExternal: "load${parent.cap}", ret: (Type) parent)
      add(op)
    }
    super.init()
  }
}