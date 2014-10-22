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
package ee.mdd.templates.java

import ee.mdd.builder.ModelBuilder
import ee.mdd.model.component.Model


class ModelBuilderExample {

  void testComponentChildren() {
    def model = build()
    assert model != null
  }

  static Model build(Closure postInstantiateDelegate = null) {
    def ret =  new ModelBuilder(postInstantiateDelegate).model ('Controlguide', key: 'cg', namespace: 'com.siemens.ra.cg') {

      model ('Platform', key: 'pl') {
        //constr
        component('User Management', key: 'um') {
          facet( )

          facet('common')

          module('shared') {

            enumType('TaskStatus', defaultLiteral: 'Unknown') {
              prop('code', type: 'int')

              constr { param(prop: 'code') }

              lit('Unknown', body: '-1')
              lit('Open', body: '1')
              lit('Closed', body: '2')
            }

            entity('UmEntity', virtual: true, meta: [
              'ApplicationScoped',
              'NamedQueries'
            ]) {

            }

            entity('Comment', superUnit: 'UmEntity') {

            }

            entity('Task', superUnit: 'UmEntity') {
              prop('comment', type: 'Comment')
              prop('created', type: 'Date')
              prop('closed', type: 'Date')


              constr {}

              constr {
                param(prop: 'comment')
                param(prop: 'created', value: '#newDate')
              }

              constr {
                param(prop: 'comment')
                param(prop: 'created')
                param(prop: 'closed')
              }

              op('hello', ret: 'String') {
                param('Test', type: 'String')
                param('counter', type: 'int')
              }

              manager {
                prop('testProp', type: 'String')
                prop('testCounter', type: 'int')
                find ('finder') {
                  param(prop: 'testProp' )
                  param(prop: 'testCounter')
                }
                //                counter('countByTestCounterAndTestProp')
                //                exist('ExistsByTestPropAndTestCounter')
                //                delete('DeleteByTestPropAndTestCounter')
              }
            }
          }

          module('backend') {
            controller('TaskAgregator') {
              op('hello', ret: 'String', body: '#testBody') {
                param('test', type: 'String')
              }
            }
            service('CommandService') {   delegate(ref: 'TaskAgregator.hello')   }
          }

          module('ui', namespace: 'ui') {
          }
        }
      }
    }
    ret
  }
}
