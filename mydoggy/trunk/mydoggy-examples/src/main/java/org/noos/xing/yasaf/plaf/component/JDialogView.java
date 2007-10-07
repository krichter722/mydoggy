package org.noos.xing.yasaf.plaf.component;

import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContainer;
import org.noos.xing.yasaf.plaf.view.PanelViewContainer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JDialogView extends JDialog {
    private ViewContainer viewContainer;
    private View view;

    public JDialogView(View view) throws HeadlessException {
        this.view = view;
        initComponents();
    }

    public JDialogView(Frame owner, View view) throws HeadlessException {
        super(owner);
        this.view = view;
        initComponents();
    }

    public JDialogView(Frame owner, boolean modal, View view) throws HeadlessException {
        super(owner, modal);
        this.view = view;
        initComponents();
    }

    public JDialogView(Frame owner, String title, View view) throws HeadlessException {
        super(owner, title);
        this.view = view;
        initComponents();
    }

    public JDialogView(Frame owner, String title, boolean modal, View view) throws HeadlessException {
        super(owner, title, modal);
        this.view = view;
        initComponents();
    }

    public JDialogView(Frame owner, String title, boolean modal, GraphicsConfiguration gc, View view) {
        super(owner, title, modal, gc);
        this.view = view;
        initComponents();
    }

    public JDialogView(Dialog owner, View view) throws HeadlessException {
        super(owner);
        this.view = view;
        initComponents();
    }

    public JDialogView(Dialog owner, boolean modal, View view) throws HeadlessException {
        super(owner, modal);
        this.view = view;
        initComponents();
    }

    public JDialogView(Dialog owner, String title, View view) throws HeadlessException {
        super(owner, title);
        this.view = view;
        initComponents();
    }

    public JDialogView(Dialog owner, String title, boolean modal, View view) throws HeadlessException {
        super(owner, title, modal);
        this.view = view;
        initComponents();
    }

    public JDialogView(Dialog owner, String title, boolean modal, GraphicsConfiguration gc, View view) throws HeadlessException {
        super(owner, title, modal, gc);
        this.view = view;
        initComponents();
    }

    public void setVisible(boolean b) {
        if (b)
            viewContainer.plugView(view);
        super.setVisible(b);
    }

    protected void initComponents() {
        viewContainer = new PanelViewContainer(getContentPane());
    }

}
