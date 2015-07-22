package ee.mdd.gradle.task

import org.junit.Test

import ee.mdd.generator.java.GeneratorForJava;
import ee.mdd.model.component.Facet
import ee.mdd.model.component.Model

class GenerateJsTest {
	GeneratorForJava generator = new GeneratorForJava()

	@Test
	void testExtendModel() {

		Facet facet = generator.builder.build {
			java {
				common()
				//        cdi()
				//        ejb()
				//        jms()
				//        jpa()
				//        test()
				//        ee()
				//        cg()
			}
		}

		Model model = generator.builder.model ('MddExample', key: 'cg', namespace: 'ee.mdd', uri: 'cg.test') {

			component('MddExampleComponent', key: 'me', namespace: 'example', artifact: 'ee-mdd_example') {

				module('shared') {

					enumType('TaskType', defaultLiteral: 'Unknown', desc: '''Defines the type of a task''') {
						prop('code', type: 'int')

						constr { param(prop: 'code') }

						lit('Unknown', body: '-1')
						lit('Open', body: '1')
						lit('Closed', body: '2')
					}

					entity('Comment', attributeChangeFlag: true) {
						prop('id', type: 'Long',  unique: true, primaryKey: true)
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
						//List<Prop> props (CompilationUnit)
						prop('id', type: 'Long', primaryKey: true, unique: true)
						prop('comment', type: 'Comment', opposite: 'testTask')
						prop('created', type: 'Date', unique: true)
						prop('closed', type: 'Date', index: true)
						prop('actions', type: 'TaskAction', multi: true )
						prop('size', type: 'int')
						prop('order', type: 'Long', xml: 'false')

						//List<Constructor> constructors (CompilationUnit)
						constr {}

						constr {
							//List<Param> params (LogicUnit)
							param(prop: 'id')
							param(prop: 'created', value: '#newDate')
						}

						constr {
							//List<Param> params (LogicUnit)
							param(prop: 'actions')
							param(prop: 'created')
							param(prop: 'closed')
						}

						//List<Operation> operations (CompilationUnit)
						op('hello', body: '#testBody') {
							//List<Param> params (LogicUnit)
							param('Test', type: 'String')
							param('countdown', type: 'int')
						}

						//List<Index> indexes (DataType)
						index( props: ['comment', 'created'])

						//Finders finders (DataType)
						finder {
							findBy { param(prop: 'comment' ) }
							count { param(prop: 'created') }
							exist {
								param(prop: 'created')
								param(prop: 'closed')
							}
						}

						//Commands commands (DataType)
						commands {
							delete { param(prop: 'closed') }
						}
					}

					entity('TaskAction', description: '''The entity that represents the action''') {
						prop('id', type:'Long', unique:true, primaryKey:true)
						prop('task', type:'Task', opposite:'actions', description: '')
						prop('name', type:'String')
					}
				}

				module('ui', namespace: 'ui') {
				}
			}
		}

		model.add(facet)
		facet.extendModel(model)

		generator.generate(model, new File('temp'))

		model.extend {
			component('Foo') {
			}
		}

		println model
	}
}
