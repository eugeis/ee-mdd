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
enum OutputPurpose {
  PRODUCTION, SIMULATION, UNIT_TEST, INTEG_TEST, ACCEPTANCE_TEST

  boolean isProduction() {
    PRODUCTION == this
  }

  boolean isSimulation() {
    SIMULATION == this
  }

  boolean isUnitTest() {
    UNIT_TEST == this
  }

  boolean isIntegTest() {
    INTEG_TEST == this
  }

  boolean isAcceptanceTest() {
    ACCEPTANCE_TEST == this
  }

  boolean isTest() {
    isUnitTest() || isIntegTest() || isAcceptanceTest()
  }
}
