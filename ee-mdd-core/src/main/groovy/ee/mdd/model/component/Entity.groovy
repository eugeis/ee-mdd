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
package ee.mdd.model.component




/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Entity extends DataType {
  Cache cache
  DeltaCache deltaCache
  boolean manualId = false
  boolean ordered = false
  boolean clientCache = false
  String idGeneratorName
  String labelBody
  
  protected boolean init() {
    if(deltaCache == null) { add( new DeltaCache(base: true)) }
    super.init()
  }
  
  def add(Cache item) {
    if(virtual) { item.virtual = true }
    cache = super.add(item)
  }
  
  void add(DeltaCache item) {
    if(virtual) { item.virtual = true }
    deltaCache = super.add(item)
  }
}
