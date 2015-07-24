package ee.mdd.gradle.task

import org.junit.Test

import ee.mdd.generator.js.GeneratorForJs
import ee.mdd.model.component.Facet
import ee.mdd.model.component.Model

class GenerateJsTest {
  GeneratorForJs generator = new GeneratorForJs()

  @Test
  void testExtendModel() {

    Facet facet = generator.builder.build { js { common() } }

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
            prop('id', type: 'Long', primaryKey: true, unique: true)
            prop('comment', type: 'Comment', opposite: 'testTask')
            prop('created', type: 'Date', unique: true)
            prop('closed', type: 'Date', index: true)
            prop('actions', type: 'TaskAction', multi: true )
            prop('size', type: 'int')
            prop('order', type: 'Long', xml: 'false')

            constr {}

            constr {
              param(prop: 'id')
              param(prop: 'created', value: '#newDate')
            }

            constr {
              param(prop: 'actions')
              param(prop: 'created')
              param(prop: 'closed')
            }

            op('hello', body: '#testBody') {
              param('Test', type: 'String')
              param('countdown', type: 'int')
            }

            index( props: ['comment', 'created'])

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
        }

        module('ui', namespace: 'ui') {

          view ('TaskEditor', main: true) {
            viewRef(view: 'TaskExplorerView') {}
            viewRef(view: 'TaskDetailsView') {}
            presenter {}
            //model {}
            button('accept') { onAction(['TaskEditorView.model']) }
            button('discard') { onAction(['TaskEditorView.model']) }
          }

          view ('TaskExplorer') {
            button('addTask') { onAction(['TaskEditorView.model']) }
            button('deleteTask') { onAction(['TaskEditorView.model']) }
            table('tasks', type: 'Task') { onSelect(['TaskEditorView.model'], observerRefs:['TaskDetailsView.presenter']) }
            presenter {}
          }

          view ('TaskDetails') {
            textField('taskName') { onChange(['TaskEditorView.model']) }
            table('actions', type: 'TaskAction') {onSelect()}
            contextMenu('actionsManagement') {}
            presenter {}
          }

          view ('TaskSearch') {
            textField('name') { onChange() }
            textField('comment') { onChange() }
            button('search') { onAction() }
            presenter {}
            dialog {}
          }
        }
      }
    }

    model.add(facet)
    facet.extendModel(model)

    generator.builder.typeResolver.printNotResolved()

    generator.generate(model, new File('temp'))

    model.extend {
      component('Foo') {
      }
    }

    println model
  }
}

