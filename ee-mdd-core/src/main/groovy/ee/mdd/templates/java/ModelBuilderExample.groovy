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

          commonJava()
          cdi()
          jpa()
          test()

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

                enumType('AreaLocation',
                description: '''Definition where an area is located see chapter 347''') {
                  lit('BothSides')
                  lit('LeftSide')
                  lit('OnTrack')
                  lit('RightSide')
                }


                basicType('Coordinate',
                description: '''The coordinates of the item in the internal planning tool for the topography''') {
                  prop('x', type: 'Long', description: '''The xvalue of this location''')
                  prop('y', type: 'Long', description: '''The yvalue of this location ''')
                }

                entity('Um', virtual: true, meta: [
                  'ApplicationScoped'
                ]) {

                }

                entity('Element',
                description: '''An element can be any general topological item which can be identified by a a topological Id and a name An Element can be assigned a ControlArea''') {
                  prop('id', type: "Long", unique: true, primaryKey: true, xml: false, hashCode: true)
                  //                  prop('controlArea', description: '''The assigned ControlArea for this Element''')
                  prop('longName', type: "String", index: true, description: '''Long name of the element''')
                  prop('shortName', type: "String", hashCode: true, index: true, description: '''Short name of the element''')
                  prop('topologyId', type: "int", sqlName: 'T_ID', hashCode: true, index: true, description: '''Unique Id assigned by engineering''')
                  prop('type', type: 'Element', description: '''The type classification of the Element''')
                  //
                  //                  //                  cache {}
                  //
                  commands {
                    delete { param(prop: 'topologyId') }
                  }

                  finder {
                    exist {  param(prop: 'shortName')  }
                    exist {  param(prop: 'topologyId')  }
                    //                    //                    findBy(unique: true) {  param(prop: 'longName')  }
                    findBy {  param(prop: 'shortName')  }
                    findBy {  param(prop: 'topologyId') }
                  }
                }


                entity('Comment', superUnit: 'Um') {
                  prop('id', type: 'Long',  unique: true, primaryKey: true)
                  prop('testTask', type: 'Task', opposite: 'comment')
                  prop('testProp', type: 'Task', multi: true)
                  prop('dateOfCreation', type: 'Date')

                  commands {
                    delete { param(prop: 'dateOfCreation') }
                  }
                }

                entity('Task', attributeChangeFlag: true) {
                  prop('id', type: 'Long', primaryKey: true, unique: true)
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

                  op('hello', body: '#testBody') {
                    param('Test', type: 'String')
                    param('counter', type: 'int')
                  }

                  index( props: [
                    'comment',
                    'created'
                  ])

                  finder {
                    findBy { param(prop: 'comment' ) }
                    count { param(prop: 'created') }
                    exist {
                      param(prop: 'created')
                      param(prop: 'closed')
                    }
                  }

                  commands {
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
