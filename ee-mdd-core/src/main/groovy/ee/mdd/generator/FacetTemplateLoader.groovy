package ee.mdd.generator

import ee.mdd.TemplatesBuilder
import ee.mdd.model.component.Facet
import ee.mdd.model.component.Model

class FacetTemplateLoader {
  String path = '/templates'
  TemplatesBuilder builder = new TemplatesBuilder()

  TemplateGroup loadFacetTemplates(Model model) {
    TemplateGroup ret = new TemplateGroup(name: model.name)

    def facetTemplateLoader
    facetTemplateLoader = { TemplateGroup facetTemplates, Map<String, Facet> currentFacets ->
      if(currentFacets) {
        currentFacets.each { facetName, Facet facet ->
          TemplateGroup templateGroup = load(facet.path, facetName)
          if(templateGroup) {
            facetTemplates.add(templateGroup)
            facetTemplateLoader templateGroup, facet.facets
          }
        }
      }
    }
    facetTemplateLoader ret, model.facets

    ret
  }

  TemplateGroup load(String facetPath, String facetName) {
    TemplateGroup ret

    def absolutPath = "$path$facetPath${facetName}.groovy"

    URL resource = getClass().getResource(absolutPath)
    if(resource) {
      ret = builder.build(resource.text)
    }
    
    if(!ret) {
      ret = new TemplateGroup(name: facetName)
    }
    return ret
  }
}
