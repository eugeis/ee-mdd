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
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Listener extends Operation {
  boolean forward = true
  String eventType
  String eventTypeRawType
  String eventValueType
  String eventDomainType
  String eventDefaultValue
  String signatureEvent
  String signatureValue
  String callbackBaseName
  String receiverName
  String receiverCallValue
  String handlerName
  String handlerCall
  String observerName
  String observerCall

  protected boolean init() {
    receiverName = "on$callbackBaseName"
    receiverCallValue = "$receiverName()"
    handlerName = "handle$callbackBaseName"
    handlerCall = "$handlerName(value)"
    observerName = "after$callbackBaseName"
    observerCall = "$observerName(value)"
    signatureEvent = "$eventType event"
    if (widget.domainType) {
      eventDomainType = widget.domainType.cap;
      signatureValue = "$eventDomainType value";
      eventValueType = "$eventDomainType";
    } else {
      eventDomainType = eventValueType;
    }
    eventDefaultValue = "defaultValue(${eventDomainType}.class)"
    if (!eventTypeRawType) {
      eventTypeRawType = eventType
    }
  }

  Widget getWidget() {
    parent
  }
}
