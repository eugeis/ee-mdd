package com.moowork.gradle.mdd

import nebula.test.IntegrationSpec

class NpmInstallTest
    extends IntegrationSpec
{
    def 'install packages with npm'()
    {
        when:
        writeEmptyPackageJson()
        this.buildFile << applyPlugin( MddPlugin )
        this.buildFile << '''
            mdd {
                version = "0.10.33"
                npmVersion = "2.1.6"
                download = true
                workDir = file('build/mdd')
            }
        '''.stripIndent()

        def result = runTasksSuccessfully( 'npmInstall' )

        then:
        !result.wasUpToDate( 'npmInstall' )
        fileExists( 'mdd_modules' )

        when:
        result = runTasksSuccessfully( 'npmInstall' )

        then:
        result.wasUpToDate( 'npmInstall' )
    }

    def 'install packages with npm in different directory'()
    {
        when:
        def packageJson = createFile( 'subdirectory/package.json' )
        packageJson << """{
            "name": "example",
            "dependencies": {
            }
        }""".stripIndent()

        this.buildFile << applyPlugin( MddPlugin )
        this.buildFile << '''
            mdd {
                version = "0.10.33"
                npmVersion = "2.1.6"
                download = true
                workDir = file('build/mdd')
                mddModulesDir = file('subdirectory')
            }
        '''.stripIndent()

        def result = runTasksSuccessfully( 'npmInstall' )

        then:
        !result.wasUpToDate( 'npmInstall' )
        fileExists( 'subdirectory/mdd_modules' )

        when:
        result = runTasksSuccessfully( 'npmInstall' )

        then:
        result.wasUpToDate( 'npmInstall' )
    }

    def writeEmptyPackageJson()
    {
        def packageJson = createFile( 'package.json', this.projectDir )
        packageJson << """{
            "name": "example",
            "dependencies": {
            }
        }""".stripIndent()
    }
}
