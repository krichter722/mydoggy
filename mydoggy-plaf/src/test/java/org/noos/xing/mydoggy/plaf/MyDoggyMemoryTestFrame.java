package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

enum MAction {
    MYDOGGY_SIMPLE("MyDoggy Simple"),
    MYDOGGY_UNACTIV("MyDoggy Unactiv"),
    MYDOGGY_ACTIV("MyDoggy Activ"),
    MYDOGGY_FLASHING("MyDoggy Flashing"),
    NORMAL("Normal Frame"),
    DISPOSE_ALL("Dispose all"),
    GC("Run GC");

    private String str;

    private MAction(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}

/**
 * Test frame for memory problem in MyDoggy
 */
public class MyDoggyMemoryTestFrame extends JFrame implements ActionListener {

    public MyDoggyMemoryTestFrame() {
        super("MyDoggy memory-test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        getContentPane().setLayout(new FlowLayout());
        for (MAction act : MAction.values()) {
            JButton button = new JButton(act.toString());
            button.setActionCommand(act.name());
            button.addActionListener(this);
            getContentPane().add(button);
        }

        pack();
        setSize(400, 200);
        setLocationByPlatform(true);
    }

    public void actionPerformed(ActionEvent e) {
        MAction act = MAction.valueOf(e.getActionCommand());
        switch (act) {
            case MYDOGGY_SIMPLE:
            case MYDOGGY_UNACTIV:
            case MYDOGGY_ACTIV:
            case MYDOGGY_FLASHING:
                MyDoggyFrame frame = new MyDoggyFrame(act);
                frame.setVisible(true);
                break;
            case NORMAL:
                NormalFrame normalFrame = new NormalFrame();
                normalFrame.setVisible(true);
                break;
            case DISPOSE_ALL:
                // Retrieve all active frames
                Frame[] oframes = Frame.getFrames();
                for (Frame oframe : oframes) {
                    if (oframe != this) {
                        oframe.dispose();
                    }
                }
                break;
            case GC:
                System.gc();
                break;
        }
    }

    public static void main(String[] args) {
        final MyDoggyMemoryTestFrame frame = new MyDoggyMemoryTestFrame();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
                frame.toFront();
            }
        });
    }

}

class MyDoggyFrame extends JFrame {

    static int nb;
    int num = ++nb;
    MAction act;

    public MyDoggyFrame(MAction act) {
        super("MyDoggy");
        this.act = act;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        final MyDoggyToolWindowManager toolWindowManager = new MyDoggyToolWindowManager();
        toolWindowManager.setMainContent(new JLabel("Content"));

        if (!MAction.MYDOGGY_SIMPLE.equals(act)) {
            configureTools(toolWindowManager);
        }

        this.getContentPane().add(toolWindowManager);

        setSize(400, 400);
    }

    private void configureTools(MyDoggyToolWindowManager toolWindowManager) {
        // tool1 - unactive
        if (MAction.MYDOGGY_UNACTIV.equals(act)) {
            ToolWindow tool1 = toolWindowManager.registerToolWindow("1", "Tool 1", null, new JButton("Tool 1"), ToolWindowAnchor.LEFT);
            tool1.setAvailable(true);
        }

        // tool2 - active
        if (MAction.MYDOGGY_ACTIV.equals(act)) {
            JPanel pane2 = new JPanel(new BorderLayout());
            pane2.add(new JComboBox(new String[]{"coucou1", "coucou2", "taratata1", "taratata2"}), BorderLayout.NORTH);
            pane2.add(new JButton("Tool 2"), BorderLayout.CENTER);
            final ToolWindow tool2 = toolWindowManager.registerToolWindow("2", "Tool 2", null, pane2, ToolWindowAnchor.LEFT);
            tool2.setAvailable(true);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tool2.setType(ToolWindowType.SLIDING);
                    tool2.setActive(true);
                }
            });
        }

        // tool3 - flashing
        if (MAction.MYDOGGY_FLASHING.equals(act)) {
            final ToolWindow tool3 = toolWindowManager.registerToolWindow("3", "Tool 3", null, new JButton("Tool 3"), ToolWindowAnchor.TOP);
            tool3.setAvailable(true);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tool3.setFlashing(true);
                }
            });
        }
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("MyDoggyFrame " + act + " finalize " + num);
        super.finalize();
    }

}

class NormalFrame extends JFrame {

    static int nb;
    int num = ++nb;

    public NormalFrame() {
        super("Normal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        this.getContentPane().add(new JPanel());
        setSize(200, 200);
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("NormalFrame finalize " + num);
        super.finalize();
    }

}
