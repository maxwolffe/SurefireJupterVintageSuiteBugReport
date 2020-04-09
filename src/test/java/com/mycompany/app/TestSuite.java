package com.mycompany.app;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({ ATestClass.class, BTestClass.class})
public class TestSuite {

    @BeforeClass
    public static void setUp() {
        System.out.println("Running setup");
        // throw new RuntimeException("ex"); // Comment this out, and both surefire-junit-platform and surefire-junit47 pass the tests. 
    }
}
