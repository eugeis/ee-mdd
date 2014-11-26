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

  static Model build(ModelBuilder builder) {
    def ret =  builder.

        model ('Controlguide', key: 'cg', namespace: 'com.siemens.ra.cg') {

          model ('Platform', key: 'pl') {
            //constr
            component('User Management', key: 'um') {
              facet( )

              facet('common')

              module('shared') {

                enumType('TaskType', defaultLiteral: 'Unknown', desc: '''Defines the type of a task''') {
                  prop('code', type: 'int')

                  constr { param(prop: 'code') }

                  lit('Unknown', body: '-1')
                  lit('Open', body: '1')
                  lit('Closed', body: '2')
                }

                entity('UmEntity', virtual: true, meta: [
                  'ApplicationScoped'
                ]) {

                }

                entity('Comment', superUnit: 'UmEntity') {
                  prop('testTask', type: 'Task', opposite: 'comment')
                  prop('testProp', type: 'Task', multi: true)
                  prop('dateOfCreation', type: 'Date')
                }

                entity('Task', superUnit: 'UmEntity') {
                  prop('id', type: 'Long', unique: true, primaryKey: true)
                  prop('comment', type: 'Comment', opposite: 'testTask')
                  prop('created', type: 'Date', unique: true)
                  prop('closed', type: 'Date', index: true)


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

                  index( props: [
                    'comment',
                    'created'
                  ])

                  manager {
                    findBy { param(prop: 'comment' ) }
                    count { param(prop: 'created') }
                    exist {
                      param(prop: 'created')
                      param(prop: 'closed')
                    }
                    delete { param(prop: 'closed') }

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
