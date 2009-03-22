/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.noos.xing.mydoggy.dj;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

/**
 *
 * @author Jens
 */
public class DJScenario
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        UIUtils.setPreferredLookAndFeel();
        NativeInterface.open();

        //System.setProperty( "dj.nativeswing.ui.webbrowser", "mozilla");

        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new MainWindow();
            }
        });

        NativeInterface.runEventPump();
    }
}
