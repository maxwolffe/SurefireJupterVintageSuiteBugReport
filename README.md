# Background
Junit 5 (Jupiter) has the ability to run Junit4 tests using a Junit Vintage Test Engine (https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4-running). There appears to be a discrepancy in how configuration failures are handled for Suites in the Surefire Junit47 provier and the Surefire-Junit-Platform provider using the Jupter Vintage Test Engine.

TLDR - `@BeforeClass` annotations in Suites cause build failures when using the Surefire-Junit47 provider but don't when using the Surefire-Junit-Platform provider with Junit-Vintage-Test-Engine.

# Test Setup

1. Fresh maven app created following the maven gettings started guides - https://maven.apache.org/guides/getting-started/ . 
2. Create a `TestSuite` with two tests `ATestClass` and `BTestClass`, with some `@BeforeClass` setup method. This might be used to set up an expensive database or something.
3. Throw an exception in that `@BeforeClass` method.
4. Set the appropriate surefire test provider - (commented out in pom.xml). 
4. `mvn test`. 

# Surefire-Junit47 Behaviour (Expected Behaviour)

One can explicitly set the surefire runner - http://maven.apache.org/surefire/maven-surefire-plugin/examples/providers.html - if we explicitly set the surefire-junit47 provider (see pom.xml for a commented out example)  we get the expected behaviour:

```
$ mvn test
[INFO] Scanning for projects...
[INFO]
[INFO] ----------------------< com.mycompany.app:my-app >----------------------
[INFO] Building my-app 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-resources-plugin:3.0.2:resources (default-resources) @ my-app ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/src/main/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:compile (default-compile) @ my-app ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-resources-plugin:3.0.2:testResources (default-testResources) @ my-app ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ my-app ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-surefire-plugin:3.0.0-M4:test (default-test) @ my-app ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
Running setup
[INFO] Running com.mycompany.app.TestSuite
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.008 s <<< FAILURE! - in com.mycompany.app.TestSuite
[ERROR] com.mycompany.app.TestSuite  Time elapsed: 0.004 s  <<< ERROR!
java.lang.RuntimeException: ex
	at com.mycompany.app.TestSuite.setUp(TestSuite.java:15)

[INFO]
[INFO] Results:
[INFO]
[ERROR] Errors:
[ERROR]   TestSuite.setUp:15 Runtime ex
[INFO]
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.966 s
[INFO] Finished at: 2020-03-31T15:49:50-07:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M4:test (default-test) on project my-app: There are test failures.
[ERROR]
[ERROR] Please refer to /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/target/surefire-reports for the individual test results.
[ERROR] Please refer to dump files (if any exist) [date].dump, [date]-jvmRun[N].dump and [date].dumpstream.
[ERROR] -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
```

# Surefire-Junit-Platform Vintage Engine Behaviour

When we explicitly use the surefire-junit-platform provider (which is running junit4 tests using the vintage engine) - no tests are run and the build succeeds. 

```
$ mvn test
[INFO] Scanning for projects...
[INFO]
[INFO] ----------------------< com.mycompany.app:my-app >----------------------
[INFO] Building my-app 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-resources-plugin:3.0.2:resources (default-resources) @ my-app ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/src/main/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:compile (default-compile) @ my-app ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 1 source file to /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/target/classes
[INFO]
[INFO] --- maven-resources-plugin:3.0.2:testResources (default-testResources) @ my-app ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ my-app ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 3 source files to /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/target/test-classes
[INFO]
[INFO] --- maven-surefire-plugin:3.0.0-M4:test (default-test) @ my-app ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.mycompany.app.TestSuite
Running setup
[INFO] Tests run: 0, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.007 s - in com.mycompany.app.TestSuite
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 0, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.428 s
[INFO] Finished at: 2020-03-31T16:11:40-07:00
[INFO] ------------------------------------------------------------------------
```

# Happy Path

For both runners, if the exception in @BeforeClass is removed, the tests work fine:

## With Surefire-Junit-Platform

```
F-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ my-app ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 3 source files to /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/target/test-classes
[INFO]
[INFO] --- maven-surefire-plugin:3.0.0-M4:test (default-test) @ my-app ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.mycompany.app.TestSuite
Running setup
[INFO] Running com.mycompany.app.ATestClass
TestA
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.004 s - in com.mycompany.app.ATestClass
[INFO] Running com.mycompany.app.BTestClass
TestB
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s - in com.mycompany.app.BTestClass
[INFO] Tests run: 0, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.02 s - in com.mycompany.app.TestSuite
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.199 s
[INFO] Finished at: 2020-03-31T16:14:21-07:00
[INFO] ------------------------------------------------------------------------~
```

## With Surefire-Junit47
```
F-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ my-app ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 3 source files to /Users/mwolffe/Workspace/bugZone/mavenJupiterVintageBug/my-app/target/test-classes
[INFO]
[INFO] --- maven-surefire-plugin:3.0.0-M4:test (default-test) @ my-app ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.mycompany.app.TestSuite
Running setup
[INFO] Running com.mycompany.app.ATestClass
TestA
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.004 s - in com.mycompany.app.ATestClass
[INFO] Running com.mycompany.app.BTestClass
TestB
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s - in com.mycompany.app.BTestClass
[INFO] Tests run: 0, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.02 s - in com.mycompany.app.TestSuite
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.199 s
[INFO] Finished at: 2020-03-31T16:14:21-07:00
[INFO] ------------------------------------------------------------------------~
```

# Conclusion

There is certainly a difference between the Junit Vintage engine behaviour and the Junit47 behaviour - is this intended or a bug? If the former, does anyone have pointers to documentation?
