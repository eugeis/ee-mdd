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

import groovy.util.logging.Slf4j

import java.util.concurrent.Callable

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class Generator extends AbstractGenerator {
  Map<String, TemplateGroup> templateGroups = [:]

  void init(Context globalContext) {
    if (isToInit()) {
      log.debug "$name: Init context '$globalContext'."
      templateGroups.each { groupName, TemplateGroup templateGroup ->
        templateGroup*.init(globalContext)
      }
    }
  }

  void generate(Context context) {
    log.info "$name: Generate for context '$context'"
    Executor executor = new Executor(5)
    //    Executor executor = new Executor(1)

    templateGroups.each { groupName, TemplateGroup templates ->
      //call generate(template, executor closure)
      templates.generate(context) { template, templateContext, generator ->

        executor.submit ( {
          before(templateContext)
          generator(template, templateContext)
          after(templateContext)
        } as Callable )
      }
    }
    executor.shutdownAndAwaitTermination()
  }


  def generate(String groupName, String template, Context c) {
    def ret = ''
    if(templateGroups.containsKey(groupName)){
      before(c)
      ret = templateGroups[groupName].generate(template, c)
      after(c)
    } else {
      c.error = new IllegalStateException("$name: Generation of '${groupName}.$template is not possible, because templates '$groupName' does not exists.")
      log.error c.error.message
    }
    ret
  }

  def add(TemplateGroup child) {
    templateGroups[child.name] = super.add(child); child
  }
}