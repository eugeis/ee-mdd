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
package ee.mdd.generator

import ee.mdd.model.component.Model
import groovy.util.logging.Slf4j

import java.util.concurrent.Callable

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class Generator extends AbstractGenerator {
  Map<String, CategoryGenerator> categories = [:]

  void generate(Model model) {
    log.info "$name: Generate for model '$model.name'"
    //Executor executor = new Executor(5)
    Executor executor = new Executor(1)

    Context c = new Context()
    c.model = model

    categories.each { catName, CategoryGenerator category ->
      category.generate(c) { template, templateContext, generator ->

        executor.submit ( {
          before(templateContext)
          generator(template, templateContext)
          after(templateContext)
        } as Callable )
      }
    }
    executor.shutdownAndAwaitTermination()
  }


  def generate(String catName, String template, Context c) {
    def ret = ''
    if(categories.containsKey(catName)){
      before(c)
      ret = categories[catName].generate(template, c)
      after(c)
    } else {
      c.error = new IllegalStateException("$name: Generation of '${catName}.$template is not possible, because category '$catName' does not exists.")
      log.error c.error.message
    }
    ret
  }

  def add(CategoryGenerator child) {
    categories[child.name] = super.add(child); child
  }
}