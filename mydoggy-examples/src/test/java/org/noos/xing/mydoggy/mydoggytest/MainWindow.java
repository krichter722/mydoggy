/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainWindow.java
 *
 * Created on 03.05.2010, 09:12:59
 */
package org.noos.xing.mydoggy.mydoggytest;

import org.noos.xing.mydoggy.ContentManagerUIListener;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.pushingpixels.flamingo.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.common.JCommandButton;
import org.pushingpixels.flamingo.common.JCommandMenuButton;
import org.pushingpixels.flamingo.common.icon.EmptyResizableIcon;
import org.pushingpixels.flamingo.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.common.popup.PopupPanelCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Jensi
 */
public class MainWindow extends javax.swing.JFrame
{
    private WindowManager windowManager = new WindowManager();

    private JPanel panelOne = new JPanel();

    private JPanel panelTwo = new JPanel();



    /** Creates new form MainWindow */
    public MainWindow()
    {
        initComponents();

        initFrame();

        pack();
        setVisible(true);
        setSize(600, 600);
    }



    private void initFrame()
    {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add((JComponent) windowManager, BorderLayout.CENTER);

        windowManager.insertContentTab("button panel", panelOne);
        windowManager.insertContentTab("to select", panelTwo);

        JButton button = new JButton("Select other content tab");

        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                windowManager.selectContentTab(panelTwo);
            }
        });

        panelOne.add(button);
        panelOne.add(createPopupButton(CommandButtonDisplayState.MEDIUM));
    }



    protected JCommandButton createPopupButton(CommandButtonDisplayState state)
    {
        JCommandButton popupButton = new JCommandButton("Flickering");
        //new edit_paste());
        //popupButton.setExtraText("Extra for select all");
        popupButton.setPopupCallback(new ReferencePopupCallback());
        popupButton.setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
        popupButton.setDisplayState(state);
        popupButton.setFlat(false);
        popupButton.setHorizontalAlignment(JButton.LEADING);

        return popupButton;
    }



    protected class ReferencePopupCallback implements PopupPanelCallback
    {
        @Override
        public JPopupPanel getPopupPanel(JCommandButton commandButton)
        {
            JCommandPopupMenu popupMenu = new JCommandPopupMenu();

            //TODO add action listener
            JCommandMenuButton button = new JCommandMenuButton("push", new EmptyResizableIcon(16));
            //button.setActionRichTooltip(new RichTooltip(TaskBundle.get("labRichTooltipDescription"), reference.getDescription()));
            //button.setActionRichTooltip(new RichTooltip(" ", reference.getDescription()));
            button.setFlat(false);

            button.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    windowManager.selectContentTab(panelTwo);
                }
            });

            popupMenu.addMenuButton(button);

            return popupMenu;
        }
    }



    protected void setupContentManagerUI()
    {
        // By default a TabbedContentManagerUI is installed.
        TabbedContentManagerUI contentManagerUI = (TabbedContentManagerUI) windowManager.getContentManager().getContentManagerUI();

        contentManagerUI.setShowAlwaysTab(true);
        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.TOP);
        contentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);
        contentManagerUI.setDetachable(false);
        //contentManagerUI.

        contentManagerUI.addContentManagerUIListener(new ContentManagerUIListener()
        {
            public boolean contentUIRemoving(ContentManagerUIEvent event)
            {
                Component component = event.getContentUI().getContent().getComponent();

                System.out.println("contentUIRemoving: " + component);

                if(component != null)
                {
                    windowManager.removeContentTab((JComponent) component);
                }

                return true;
            }



            public void contentUIDetached(ContentManagerUIEvent event)
            {
            }
        });
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
