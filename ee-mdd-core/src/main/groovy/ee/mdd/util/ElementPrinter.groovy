/*
 * Copyright 2003-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ee.mdd.util

import org.codehaus.groovy.runtime.InvokerHelper

import ee.mdd.model.Composite
import ee.mdd.model.Element

/**
 * @author Eugen Eisler
 */
class ElementPrinter {

  protected final IndentPrinter out

  ElementPrinter() {
    this(new IndentPrinter(new PrintWriter(new OutputStreamWriter(System.out))))
  }

  ElementPrinter(PrintWriter out) {
    this(new IndentPrinter(out))
  }

  ElementPrinter(IndentPrinter out) {
    if (out == null) {
      throw new NullPointerException('IndentPrinter "out" must not be null!')
    }
    this.out = out
  }

  void print(Element item, Closure filter = { true }) {
    if(filter(item)) {
      out.printIndent()
      printNameAndAttributes(item)

      if(Composite.isInstance(item)) {
        Composite composite = (Composite)item
        if(composite.children) {
          printList(composite.children, filter)
        } else {
          out.println()
        }
      } else {
        out.println()
      }
      out.flush()
    }
  }

  private printNameAndAttributes(Element item) {
    out.print(item.name)
    Map attributes = item.attributes()
    boolean hasAttributes = attributes != null && !attributes.isEmpty()
    if (hasAttributes) {
      printAttributes(attributes)
    }
  }

  protected void printList(List<Element> list, Closure filter) {

    def itemsWithoutChildren = list.findAll { filter && (!Composite.isInstance(it) || !it.children) }
    def itemsWithChildren = list.findAll { filter && (Composite.isInstance(it) && it.children) }

    if(itemsWithChildren) {
      out.println(' {')
      out.incrementIndent()
      if(itemsWithoutChildren) {
        out.printIndent()
        printListWithoutChildren(itemsWithoutChildren)
        out.println('')
      }
      itemsWithChildren.each { print(it, filter) }

      out.decrementIndent()
      out.printIndent()
      out.println('}')
    } else if(itemsWithoutChildren){
      out.print(' { ')
      printListWithoutChildren(itemsWithoutChildren)
      out.println(' }')
    }
  }

  protected void printListWithoutChildren(List<Element> list) {
    boolean first = true
    list.each {
      if(first) {
        first = false
      } else {
        out.print(', ')
      }
      printNameAndAttributes(it)
    }
  }

  protected void printAttributes(Map attributes) {
    out.print('(')
    boolean first = true
    for (Object o : attributes.entrySet()) {
      Map.Entry entry = (Map.Entry) o
      if (first) {
        first = false
      } else {
        out.print(', ')
      }
      out.print(entry.getKey().toString())
      out.print(':')
      if (entry.getValue() instanceof String) {
        out.print(''' + entry.getValue() + ''')
      } else {
        out.print(InvokerHelper.toString(entry.getValue()))
      }
    }
    out.print(')')
  }
}
