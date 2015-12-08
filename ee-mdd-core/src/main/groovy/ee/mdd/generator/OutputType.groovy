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

/**
 * @author Eugen Eisler
 */
enum OutputType {
  API, API_IMPL, SHARED, LOGIC, INTEG, FRONTEND, CONFIG

  boolean isApi() {
    API == this
  }

  boolean isApiImpl() {
    API_IMPL == this
  }

  boolean isShared() {
    SHARED == this
  }

  boolean isLogic() {
    LOGIC == this
  }

  boolean isInteg() {
    INTEG == this
  }

  boolean isConfig() {
    CONFIG == this
  }

  boolean isFrontend() {
    FRONTEND == this
  }
}
