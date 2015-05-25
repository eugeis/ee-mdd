

/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ee.mdd.builder

import org.codehaus.groovy.runtime.InvokerHelper

import ee.mdd.model.Base
import ee.mdd.model.component.Namespace
import groovy.util.logging.Slf4j


/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class AbstractFactoryBuilder extends FactoryBuilderSupport {
  GroovyShell shell = new GroovyShell()
  protected  Map<String, Object> storedContinuationData
  TypeResolver typeResolver
  AttributeToObject attributeToObject

  AbstractFactoryBuilder(Closure postInstantiateDelegate = null, boolean init = true) {
    super(init)

    attributeToObject = new AttributeToObject()
    attributeToObject.add('namespace', new MddFactory(beanClass: Namespace))
    addAttributeDelegate(attributeToObject.attributteDelegate)

    typeResolver = new TypeResolver()
    addAttributeDelegate(typeResolver.attributteDelegate)
    addPostInstantiateDelegate(typeResolver.postInstantiateDelegate)
    addPostNodeCompletionDelegate(typeResolver.postNodeCompletionDelegate)

    if(postInstantiateDelegate) {
      addPostInstantiateDelegate(postInstantiateDelegate)
    }
  }

  def propertyMissing(String name) {
    throw new MissingPropertyException("Unrecognized property: ${name}", name, this.class)
  }

  public void registerBeanFactory(String theName, String groupName, final Class beanClass) {
    getProxyBuilder().registerFactory(theName, new MddFactory() {
          public Object newInstance(FactoryBuilderSupport builder, Object name, Object value,
              Map properties) throws InstantiationException, IllegalAccessException {
            if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, beanClass)) {
              return value;
            } else {
              return beanClass.newInstance();
            }
          }
        });
    getRegistrationGroup(groupName).add(theName);
  }

  protected Factory resolveFactory(Object name, Map attributes, Object value) {
    checkFactoryAllowed(name)
    super.resolveFactory(name, attributes, value)
  }

  @Override
  protected Object createNode(Object name, Map attributes, Object value) {
    def node
    if(attributes == null && value != null) {
      node = super.createNode(name, [:], value)
    } else {
      node = super.createNode(name, attributes, value)
    }

    if (node instanceof Base) {
      node.checkAndInit(parent)
    }
    node
  }

  Object getParent() {
    getProxyBuilder().getCurrent()
  }

  boolean checkFactoryAllowed(Object name) {
    MddFactory parent = getParentFactory()
    if(parent != null && !parent.isChildAllowed(name)) {
      String msg = "Child element '$name' in not allowed for parent '$parent'."
      log.error msg
      throw new RuntimeException(msg)
    }
    true
  }

  protected Object dispatchNodeCall(Object name, Object args) {
    Object node;
    Closure closure = null;
    List list = InvokerHelper.asList(args);

    final boolean needToPopContext;
    if (getProxyBuilder().getContexts().isEmpty()) {
      // should be called on first build method only
      getProxyBuilder().newContext();
      needToPopContext = true;
    } else {
      needToPopContext = false;
    }

    try {
      Map namedArgs = Collections.EMPTY_MAP;

      // the arguments come in like [named_args?, args..., closure?]
      // so peel off a hashmap from the front, and a closure from the
      // end and presume that is what they meant, since there is
      // no way to distinguish node(a:b,c,d) {..} from
      // node([a:b],[c,d], {..}), i.e. the user can deliberately confuse
      // the builder and there is nothing we can really do to prevent
      // that

      if ((list.size() > 0)
      && (list.get(0) instanceof LinkedHashMap)) {
        namedArgs = (Map) list.get(0);
        list = list.subList(1, list.size());
      }
      if ((list.size() > 0)
      && (list.get(list.size() - 1) instanceof Closure)) {
        closure = (Closure) list.get(list.size() - 1);
        list = list.subList(0, list.size() - 1);
      }
      Object arg;
      if (list.size() == 0) {
        arg = null;
      } else if (list.size() == 1) {
        arg = list.get(0);
      } else {
        arg = list;
      }
      node = getProxyBuilder().createNode(name, namedArgs, arg);

      Object current = getProxyBuilder().getCurrent();
      if (current != null) {
        getProxyBuilder().setParent(current, node);
      }

      Factory parentFactory = getProxyBuilder().getCurrentFactory();
      Closure childClosure = parentFactory.childClosure(getProxyBuilder().getChildBuilder(), node)
      if (closure != null || childClosure != null) {
        if (parentFactory.isLeaf()) {
          throw new RuntimeException("'" + name + "' doesn't support nesting.");
        }

        if(closure) {
          createChildNodes(node, closure, current, parentFactory)
        }

        if(childClosure) {
          createChildNodes(node, childClosure, current, parentFactory)
        }
      }

      getProxyBuilder().nodeCompleted(current, node);
      node = getProxyBuilder().postNodeCompletion(current, node);
    } finally {
      if (needToPopContext) {
        // pop the first context
        getProxyBuilder().popContext();
      }
    }
    return node;
  }

  public createChildNodes(node, String closureAsText) {
    Closure closure = evaluate(closureAsText)
    //closure.@owner = node
    createChildNodes(node, closure)
  }

  public createChildNodes(node, Closure closure) {
    Object current = getProxyBuilder().getCurrent()
    Factory parentFactory = getProxyBuilder().getCurrentFactory();

    createChildNodes(node, closure, current, parentFactory)
  }

  protected createChildNodes(node, Closure closure, current, Factory parentFactory) {
    boolean processContent = true;
    if (parentFactory.isHandlesNodeChildren()) {
      processContent = parentFactory.onNodeChildren(this, node, closure);
    }
    if (processContent) {
      // push new node on stack
      String parentName = getProxyBuilder().getCurrentName();
      Map parentContext = getProxyBuilder().getContext();
      getProxyBuilder().newContext();
      try {
        getProxyBuilder().getContext().put(OWNER, closure.getOwner());
        getProxyBuilder().getContext().put(CURRENT_NODE, node);
        getProxyBuilder().getContext().put(PARENT_FACTORY, parentFactory);
        getProxyBuilder().getContext().put(PARENT_NODE, current);
        getProxyBuilder().getContext().put(PARENT_CONTEXT, parentContext);
        getProxyBuilder().getContext().put(PARENT_NAME, parentName);
        getProxyBuilder().getContext().put(PARENT_BUILDER, parentContext.get(CURRENT_BUILDER));
        getProxyBuilder().getContext().put(CURRENT_BUILDER, parentContext.get(CHILD_BUILDER));
        // lets register the builder as the delegate
        getProxyBuilder().setClosureDelegate(closure, node);
        closure.call();
      } finally {
        getProxyBuilder().popContext();
      }
    }
  }

  Object build(File file) {
    if(file.exists()) {
      build(file.text)
    } else {
      log.error "Could not find file '$file'"
    }
  }

  Object buildFromClasspath(String path) {
    URL resource = getClass().getResource(path)
    if(resource) {
      build(resource)
    } else {
      log.error "Could not find path '$path'"
    }
  }

  Object build(URL resource) {
    build(resource.text)
  }

  Object build(String source) {
    build(source, new GroovyClassLoader())
  }

  Closure evaluate(String text) {
    Closure closure =shell.evaluate("{ it -> $text }")
  }
}

