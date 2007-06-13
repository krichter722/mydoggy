package org.noos.xing.mydoggy.itest;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface InteractiveAssertor {

    void askForConfirm(String message);

    void assertTrue(String message, boolean expression);
}
