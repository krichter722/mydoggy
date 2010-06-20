package org.noos.xing.mydoggy.itest;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InteractiveTest {

    String getName();

    String getDescription();

    void setup();

    void dispose();

    void execute();

}
