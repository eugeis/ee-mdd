package ee.mdd.builder

import groovy.json.JsonSlurper


class Facets {
  def facets
  Collection<String> names
  Map<String, String> facetFullNameToScript = [:]

  Facets() {
    load()
  }

  boolean isFacet(String name) {
    names.contains(name)
  }

  String loadFacetScript(String name, String facetPath = '/') {
    String ret
    URL url = resource("/facets$facetPath${name}.groovy")
    if(url) {
      ret = url.text
    }
    ret
  }

  private void load() {
    def jsonSlurper = new JsonSlurper()
    URL url = resource('/facets/facets.json')
    if(url) {
      facets = jsonSlurper.parse(url)
      loadFacetFiles()
    }
  }

  private loadFacetFiles() {
    names = [] as Set
    def facetParser
    facetParser = { name, item ->
      if(name) {
        names << name
      }
      if(Collection.isInstance(item)) {
        names.addAll(item)
      } else if(Map.Entry.isInstance(item)) {
        facetParser item.key, item.value
      } else if(Map.isInstance(item)) {
        item.each { cKey, cItem -> facetParser(cKey, cItem) }
      }
    }

    facetParser null, facets
  }

  private URL resource(String path) {
    getClass().getResource(path)
  }
}
