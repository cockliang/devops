package com.trs.devops;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class HelloControllerTest extends TestCase {

    @Test
    public void testGetTrue() {
        HelloController helloController = new HelloController();
        assertEquals(true,helloController.getTrue());
    }

    @Test
    public void testHelloSpringBoot() {
        HelloController helloController = new HelloController();
        helloController.helloSpringBoot();
    }
}