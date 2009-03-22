package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class DockingWithDoggy extends JFrame {
    private static final long serialVersionUID = 1L;

    public Component mFocusStart;

    public DockingWithDoggy() {
        super("Dockable Window Demo");
        this.add(createToolWindowManager());
        this.setSize(800, 600);
    }

    protected JPanel createToolWindowManager() {
// Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager vDockingToolWindow = new MyDoggyToolWindowManager();

// Register a Tool.
        vDockingToolWindow.registerToolWindow("Docking-Demo", // Id
                                              "Docking-Demo", // Title
                                              null, // Icon
                                              createDockablePanel(), ToolWindowAnchor.LEFT); // Anchor

        ToolWindow vToolWindow = vDockingToolWindow.getToolWindow(1);
        vToolWindow.getTypeDescriptor(DockedTypeDescriptor.class).setDockLength(250);
        vToolWindow.setVisible(true);

        vDockingToolWindow.getContentManager().addContent("Hello World", "Hello World", null,
                                                          new JLabel("Hello World!"));

// Make all tools available
        for (ToolWindow window : vDockingToolWindow.getToolWindows()) {
            window.setAvailable(true);
        }

        return vDockingToolWindow;
    }

    private JPanel createDockablePanel() {
        JPanel vPanel = new JPanel(new GridBagLayout());

        JLabel vLabel = new JLabel("Eins: ");
        JTextField vTextField = new JTextField("eins");
        vTextField.requestFocusInWindow();
        vLabel.setLabelFor(vTextField);
        vLabel.setFocusable(false);
        vPanel.add(vLabel);
        vPanel.add(vTextField);

        mFocusStart = vTextField;

        vLabel = new JLabel("Zwei: ");
        vTextField = new JTextField("zwei");
        vLabel.setLabelFor(vTextField);
        vLabel.setFocusable(false);
        vPanel.add(vLabel);
        vPanel.add(vTextField);

        vLabel = new JLabel("Spinner: ");
        JSpinner vSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 36, 1));
        vLabel.setLabelFor(vSpinner);
        vLabel.setFocusable(false);
        vPanel.add(vLabel);
        vPanel.add(vSpinner);

        vPanel.setFocusCycleRoot(true);
        vPanel.setName("DockPanel");

        vPanel.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent aE) {
                mFocusStart.requestFocusInWindow();
            }

            public void focusLost(FocusEvent aE) {
            }
        });

        JPanel vTestPanel = new JPanel();
        vTestPanel.setName("TestPanel");
        vTestPanel.add(vPanel, BorderLayout.NORTH);

        return vTestPanel;
    }

    /**
     * prints the focus traversal sequence
     */
    public void printFocusSequence() {
        Container vContainer = mFocusStart.getFocusCycleRootAncestor();
        if (vContainer == null) {
            System.out.println("Keinen FocusTraversalPolicyProvider gefunden!");
            return;
        }

        FocusTraversalPolicy vPolicy = vContainer.getFocusTraversalPolicy();

        if (vPolicy == null) {
            System.out.println(vContainer.getClass() + " hat keine FocusPolicy");
            return;
        }

        System.out.println("Container: " + vContainer.getClass() + ", Name: " + vContainer.getName());
        Component vCurrentComponent = vPolicy.getComponentAfter(vContainer, mFocusStart);

        System.out.println("Erste Komponente: " + mFocusStart.getClass());

        int vCounter = 0;
        while ((vCurrentComponent != mFocusStart) && (vCurrentComponent != null) && (vCounter++ < 20)) {
            System.out.println("N�chste Komponente: " + vCurrentComponent.getClass() + ", Name: "
                               + vCurrentComponent.getName());
            vCurrentComponent = vPolicy.getComponentAfter(vContainer, vCurrentComponent);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        DockingWithDoggy vMainWindow = new DockingWithDoggy();

        vMainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vMainWindow.setLocationRelativeTo(null);
        vMainWindow.setVisible(true);

        vMainWindow.printFocusSequence();
    }


}