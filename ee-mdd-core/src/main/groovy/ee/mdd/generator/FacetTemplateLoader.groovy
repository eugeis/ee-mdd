package ee.mdd.generator

import ee.mdd.TemplatesBuilder
import ee.mdd.model.component.Facet
import ee.mdd.model.component.Model
import groovy.util.logging.Slf4j

@Slf4j
class FacetTemplateLoader {
  String path = '/templates'
  TemplatesBuilder builder = new TemplatesBuilder()

  TemplateGroup loadFacetTemplates(Model model) {
    TemplateGroup ret = new TemplateGroup(name: model.name)

    Map<String, TemplateGroup> fullNameToLoadedTemplates = [:]

    def facetTemplateLoader
    facetTemplateLoader = { TemplateGroup facetTemplates, Map<String, Facet> currentFacets ->
      if(currentFacets) {
        currentFacets.each { facetName, Facet facet ->
          TemplateGroup templateGroup = load(facetName, "$facet.path", fullNameToLoadedTemplates)
          if(templateGroup) {

            if(templateGroup.onlyIf == null) {
              templateGroup.onlyIf = { Context c ->
                def su = c.get('module')
                if(!su) {
                  su = c.get('component')
                }
                if(!su) {
                  su = c.get('model')
                }
                su?.isFacetEnabled(facet)
              }
            }

            facetTemplates.add(templateGroup)
            facetTemplateLoader templateGroup, facet.facets
          }
        }
      }
    }
    facetTemplateLoader ret, model.facets

    ret
  }

  TemplateGroup load(String name, String templatePath, Map<String, TemplateGroup> fillFullNameToLoadedTemplates) {
    TemplateGroup ret

    String fullName = "$templatePath$name"

    if(fillFullNameToLoadedTemplates.containsKey(fullName)) {
      ret = fillFullNameToLoadedTemplates[fullName]
    } else {
      def fileFullName = "$path${fullName}.groovy"

      URL resource = getClass().getResource(fileFullName)
      if(resource) {
        ret = builder.build(resource.text)
        ret.path = templatePath

        //load macros
        loadMacros(ret, fillFullNameToLoadedTemplates)
      } else {
        log.info("Templates not found '{}'", fileFullName)
      }

      //we need empty facet template group for structure facets like e.g. java, js
      if(!ret) {
        ret = new TemplateGroup(name: name, path: fullName)
      }
      fillFullNameToLoadedTemplates[fullName] = ret
    }

    return ret
  }

  private loadMacros(TemplateGroup fillTemplates, Map fillFullNameToLoadedTemplates) {
    fillTemplates.aliasToMacrosFullName.each { alias, macroFullName ->
      TemplateGroup macros
      if(macroFullName) {
        if(fillFullNameToLoadedTemplates.containsKey(macroFullName)) {
          macros = fillFullNameToLoadedTemplates[macroFullName]
        } else {
          List nameParts = macroFullName.tokenize('/')
          if(nameParts.size() > 1) {
            String path = nameParts[0..nameParts.size()-2].join('/')
            String macroName = nameParts[nameParts.size() -1]
            macros = load(macroName, "/$path/", fillFullNameToLoadedTemplates)
          } else if(nameParts) {
            macros = load(nameParts[0], '/', fillFullNameToLoadedTemplates)
          }
        }
      } else {
        macros = load(alias, fillTemplates.path, fillFullNameToLoadedTemplates)
      }

      if(macros) {
        fillTemplates.aliasToMacros[alias] = macros
      }
    }
  }
}
