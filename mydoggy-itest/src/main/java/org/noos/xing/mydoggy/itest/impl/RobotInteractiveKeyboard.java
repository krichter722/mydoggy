package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.InteractiveKeyboard;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RobotInteractiveKeyboard implements InteractiveKeyboard {
    private Robot robot;

    public RobotInteractiveKeyboard(Robot robot) {
        this.robot = robot;
    }


}