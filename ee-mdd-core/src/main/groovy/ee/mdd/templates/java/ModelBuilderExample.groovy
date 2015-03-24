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

        model ('Controlguide', key: 'cg', namespace: 'com.siemens.ra.cg', uri: 'cg.test') {

          java()
          jms()
          cdi()
          jpa()
          test()
          cg()

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

                container('TaskContainer') {
                  prop('Signal', type: 'Signal', cache: true)
                  prop('Um', type: 'Um', cache: true)
                  prop('MotherStation', type: 'MotherStation')
                  prop('ProtectionRequirement', type: 'ProtectionRequirement')
                  prop('Trophy', type: 'Trophy')

                  controller(cache: true) {}
                }

                entity('Trophy') {
                  prop('id', type: 'Long', unique: true, primaryKey: true)
                  prop('value', type: 'int')
                }



                entity('ProtectionRequirement', superUnit: 'ElementLink', sqlName: 'PR',
                description: '''Protection requirements are element references which extend the reference by an element specific value They are used for possession areas to specify eg the locked position of a switch They can be specified in an ElementRefs collection instead of the ElementRef tag''') {
                  prop('protectionKey', type: 'ProtectionKeyType', description: '''The key of the attribute which must have for the referenced element a particular value''')
                  prop('protectionValue', type: 'ProtectionValueType', description: '''This is a global text field which can be used by the several protection requirements to specify restrictions Eg the locked position of a switch''')
                }

                entity('MotherStation', superUnit: 'Element', description: '''A mother station groups different stations in the sense of a track group In Iltis the mother station SG has stations like SGX SG8x etc ''') {
                }

                enumType('ProtectionKeyType',
                description: '''Possible keys for a Protection Requirement property''') {
                  lit('BlockState')
                  lit('SwitchPosition')
                }

                enumType('ProtectionValueType',
                description: '''Possible values for a Protection Requirement property''') {
                  lit('Blocked')
                  lit('Left')
                  lit('Right')
                  lit('Undef')
                }

                entity('ElementLink', virtual: true, base: true, description: '''A reference to an Element object''') {
                  prop('id', type: 'Long', unique: true, primaryKey: true, xml: false, hashCode: true)
                  prop('desc', type: 'String', sqlName: 'DSC', description: '''This is a description text which identifies the referenced element by a readable name Used only to make the XML file readable''')
                  prop('topologyId', type: 'Long', sqlName: 'T_ID', hashCode: true, index: true, description: '''The topology Id of the referenced Element''')
                }


                basicType('Coordinate', base: true,
                description: '''The coordinates of the item in the internal planning tool for the topography''') {
                  prop('x', type: 'Long', description: '''The xvalue of this location''')
                  prop('y', type: 'Long', description: '''The yvalue of this location ''')
                }

                entity('Um', virtual: true, meta: []) {
                  prop('testMultiProp', type: 'Element', multi: true, unique:true, primaryKey: true)
                  prop('zweitesMulti', type: 'Task', opposite: 'multiTest',  multi: true)
                }

                entity('Signal', base: true) {
                  prop('id', type: 'Long', primaryKey: true, unique: true)
                  prop('state', type: 'int')
                  prop('position', type: 'Long')

                  op('testOperation') {
                    param('size', type: 'int')
                  }
                }

                entity('Element',
                description: '''An element can be any general topological item which can be identified by a a topological Id and a name An Element can be assigned a ControlArea''') {
                  prop('id', type: "Long", unique: true, primaryKey: true, xml: false, hashCode: true, multi: true)
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


                entity('AllowedConnection',
                description: '''Describes an allowed connection by type which can be attached in the timetable to this location''') {
                  prop('id', type: "Long", unique: true, primaryKey: true, xml: false, hashCode: true)
                  prop('direction', type: 'int', hashCode: true, index: true, description: '''The direction in which the connection is allowed''')
                  prop('stationTrack', type: 'StationTrack', opposite: 'allowedConnections')
                  prop('type', type: 'String', description: '''The type of the connection''')
                }

                entity('StationTrack', superUnit: 'Element',
                description: '''A station track is a track belonging to a station The name of the station track is often shown in passenger timetables<br/><br/>A station track without platform track can only be used for technical stops ''') {
                  prop('allowedConnections', type: 'AllowedConnection', multi: true, opposite: 'stationTrack', description: '''The list of allowed connections''')
                  prop('isVirtual', type: 'Boolean', description: '''Marks this station track as virtual<br/><br/>Such a station track can be used to express relations from routes to stations on this route''')
                  prop('position', type: 'Integer', description: '''The position of this station track''')
                  prop('tgmtNumber', type: 'Integer', description: '''The platform number used in TGMT''')
                }


                entity('Comment', superUnit: 'Um', attributeChangeFlag: true) {
                  prop('id', type: 'Task',  unique: true, primaryKey: true, multi: true)
                  prop('testTask', type: 'Task', opposite: 'comment')
                  prop('testProp', type: 'Task', multi: true)
                  prop('dateOfCreation', type: 'Date')
                  prop('newTask', type: 'Task')

                  constr {}

                  commands {
                    delete { param(prop: 'dateOfCreation') }
                  }
                }

                entity('Task', attributeChangeFlag: true, ordered: true) {
                  prop('id', type: 'Long', primaryKey: true, unique: true)
                  prop('comment', type: 'Comment', opposite: 'testTask')
                  prop('created', type: 'Date', unique: true)
                  prop('closed', type: 'Date', index: true)
                  prop('actions', type: 'String' )
                  prop('size', type: 'int')
                  prop('multiTest', type:'Um', opposite: 'zweitesMulti', multi: true)

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
                    param('countdown', type: 'int')
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

                entity('TaskAction', description: '''The entity that represents the action''') {
                  prop('id', type:'Long', unique:true, primaryKey:true)
                  prop('task', type:'Task', opposite:'actions', description: '')
                  prop('name', type:'String')
                }

                channel('NotificationTopic') {
                  message(ref: 'TaskContainer')
                  message(ref: 'TaskAction')
                  message(ref: 'Task')
                }

              }

              module('backend') {

                entity('Area') {
                  prop('id', type: 'Long', unique: true, primaryKey: true)
                  prop('age', type: 'int')
                  prop('size', type: 'int')
                }

                controller('TaskAgregator') {
                  op('hello', ret: 'String', body: '#testBody') {
                    param('test', type: 'String')
                  }
                }

                service('CommandService') {    delegate(ref: 'TaskAgregator.hello')    }
              }

              module('ui', namespace: 'ui') {
              }
            }
          }
        }
    ret
  }
}
