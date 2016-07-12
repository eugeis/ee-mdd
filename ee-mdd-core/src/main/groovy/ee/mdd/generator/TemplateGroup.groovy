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

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class TemplateGroup extends AbstractGenerator {
    String path
    Map<String, Template> templates = [:]
    Map<String, TemplateGroup> templateGroups = [:]
    Map<String, String> aliasToMacrosFullName = [:]
    Map<String, TemplateGroup> aliasToMacros = [:]

    Closure onlyIf
    Closure context

    void initialize(Context globalContext) {
        if (isToInit()) {
            log.debug "$name: Init context '$globalContext'."
            super.initialize(globalContext)
            templates.each { initialize(globalContext) }
            templateGroups.each { groupName, TemplateGroup templateGroup ->
                templateGroup*.initialize(globalContext)
            }
        }
    }

    void generate(Context globalContext, Closure executor) {
        if (enabled(globalContext)) {
            log.info "$name: Generate for context '$globalContext'."
            templates.each { templateName, Template template ->
                Context templateContext = globalContext.clone()
                extendContext(templateContext)
                generate(template, templateContext, executor)
            }

            templateGroups.each { groupName, TemplateGroup templateGroup ->
                Context templateContext = globalContext.clone()
                extendContext(templateContext)
                templateGroup.generate(templateContext, executor)
            }
        } else {
            log.debug "$name: The template group is disabled, will not generate '$globalContext'."
        }
    }

    def generate(Template template, Context templateContext, Closure executor) {
        def ret
        if (enabled(templateContext)) {
            executor(template, templateContext) { tmpl, c ->
                ret = generateTemplate(tmpl, c)
            }
        }
        ret
    }

    def generate(String template, Context c) {
        def ret = ''
        if (templates.containsKey(template)) {
            ret = generateTemplate(templates[template], c)
        } else {
            c.error = new IllegalStateException("$name: Generation of '$template' is not possible, because template '$template' does not exists.")
            log.error c.error.message
        }
        ret
    }

    protected def generateTemplate(Template template, Context c) {
        before(c)
        template.generate(c)
        after(c)
        c.output
    }

    protected void extendContext(Context c) {
        //add macros to context
        if (aliasToMacros) {
            c.putAll(aliasToMacros)
        }

        extendContextOutput(c)

        try {
            context?.call(c)
        } catch (e) {
            log.error "Context can not be extended.", e
        }
    }

    void useMacros(String alias, String fullName = null) {
        if (!fullName && path) {
            aliasToMacrosFullName[alias] = "$path$alias"
        } else {
            aliasToMacrosFullName[alias] = fullName
        }
    }

    def add(Template child) {
        templates[child.name] = super.add(child); child
    }

    def add(TemplateGroup child) {
        templateGroups[child.name] = super.add(child); child
    }

    def add(Processor child) {
        if (processors == null) {
            processors = []
        }
        processors << child; super.add(child)
    }

    boolean enabled(Context context) {
        !onlyIf || onlyIf.call(context)
    }
}
