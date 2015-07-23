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
package ee.mdd.model.ui


/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class View extends Widget {
  String domainName
  boolean main = false
  boolean withMediator = false
  Presenter presenter
  ViewModel model
  Dialog dialog
  List<Control> controls = []
  List<ViewRef> viewRefs = []

  boolean init() {
    super.init()
    true
  }

  void buildMe() {
    super.buildMe()
    if (!views.empty) {
      view.withMediator = main ; presenter.withMediator = true ; views.each {view -> view.presenter.withMediator = true}
    }
  }

  String deriveName() {
    "${domainName}View"
  }
  def getMediatorImplements() {
    mediatorDelegates.collect{it.names.eventForwarder}.join(', ')
  }
  List<Presenter> getMediatorPresenters() {
    mediatorViews*.presenter
  }
  List<ViewModel> getMediatorModels() {
    mediatorViews*.model - null
  }
  List<ViewModel> getMediatorDelegates() {
    mediatorPresenters + mediatorModels
  }
  List<View> getMediatorViews() {
    def ret = []; views.each{ret.addAll(it.mediatorViews)} ; ret << view ; ret
  }

  def getViews() {
    viewRefs.views
  }
  void add(ViewRef item) {
    if(!viewRefs) {
      viewRefs = []
    }; viewRefs << super.add(item)
  }
  void add(Control item) {
    if(!controls) {
      controls = []
    }; controls << super.add(item)
  }
  void add(Dialog item) {
    super.add(item); dialog = item
  }
  void add(Presenter item) {
    super.add(item); presenter = item
  }
  void add(ViewModel item) {
    super.add(item); model = item
  }
}
