import ee.mdd.generator.OutputType
import ee.mdd.model.component.Component

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

/**
 *
 * @author Eugen Eisler
 */

templates('wildfly') {

    useMacros('commonMacros', '/common/macros')
    useMacros('macros')

    templates('config', type: OutputType.RESOURCE) {
        template('datasources', body: '''<% c.path = "wildlfy-datasources-${c.model.key}.txt" %>
                <% def deployable = ['ear','war'] as Set; c.model.findAllDown(ee.mdd.model.component.Component).findAll{ comp-> comp.modules.find { deployable.contains(it.name) } }.each { comp -> %>
                <datasource jta="true" jndi-name="jdbc:mysql://serv1:3306/$comp.key|jdbc:mysql://serv2:3307/$comp.key" pool-name="$comp.key" enabled="true" use-ccm="true">
                    <connection-url>jdbc:mysql://serv1:3306/$comp.key|jdbc:mysql://serv2:3307/$comp.key</connection-url>
                    <driver-class>com.mysql.jdbc.Driver</driver-class>
                    <driver>mysql</driver>
                    <url-delimiter>|</url-delimiter>
                     <pool>
                        <min-pool-size>1</min-pool-size>
                        <max-pool-size>200</max-pool-size>
                        <prefill>false</prefill>
                        <flush-strategy>EntirePool</flush-strategy>
                    </pool>
                    <security>
                        <user-name>$comp.key</user-name>
                        <password>\\${VAULT::database::$comp.key::1}</password>
                    </security>
                    <validation>
                        <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker"/>
                        <check-valid-connection-sql>call cg.assertMaster();</check-valid-connection-sql>
                        <background-validation>true</background-validation>
                        <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"/>
                    </validation>
                    <timeout>
                        <set-tx-query-timeout>false</set-tx-query-timeout>
                        <blocking-timeout-millis>0</blocking-timeout-millis>
                        <idle-timeout-minutes>1</idle-timeout-minutes>
                        <query-timeout>0</query-timeout>
                        <use-try-lock>0</use-try-lock>
                        <allocation-retry>0</allocation-retry>
                        <allocation-retry-wait-millis>0</allocation-retry-wait-millis>
                    </timeout>
                    <statement>
                        <share-prepared-statements>false</share-prepared-statements>
                    </statement>
                </datasource><% } %>''')
    }
}