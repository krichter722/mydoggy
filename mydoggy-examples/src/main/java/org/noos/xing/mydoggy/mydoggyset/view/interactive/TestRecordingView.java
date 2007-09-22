package org.noos.xing.mydoggy.mydoggyset.view.interactive;

import org.noos.xing.mydoggy.itest.Tracer;
import org.noos.xing.mydoggy.itest.impl.tracer.ToolkitTracer;
import org.noos.xing.yasaf.plaf.action.ActionEnabler;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.component.ToolBarContentPanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestRecordingView extends ComponentView {
    protected JTextArea textArea;

    public TestRecordingView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        textArea = new JTextArea();
        viewContext.put(Document.class, textArea.getDocument());

        ToolBarContentPanel recordindPanel = new ToolBarContentPanel(new JScrollPane(textArea));
        recordindPanel.setBorder(new TitledBorder("Test Recording."));
        recordindPanel.getToolBar().add(
                new ActionEnabler(new ViewContextAction("Start", viewContext, "start"), viewContext, Tracer.class){
                    public void contextChange(ViewContextChangeEvent evt) {
                        action.setEnabled(evt.getNewValue() == null);
                    }
                }.getAction()
        );
        recordindPanel.getToolBar().add(new ViewContextAction("Stop", null, viewContext, "stop",
                                                              Tracer.class));
        recordindPanel.getToolBar().add(new ViewContextAction("Clear", viewContext, "clear"));

        return recordindPanel;
    }

    protected void initListeners() {
        new TracerListener(viewContext);
        viewContext.addViewContextChangeListener("clear", new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                textArea.setText("");
            }
        });
    }

    protected class TracerListener implements ViewContextChangeListener {
        protected Tracer tracer;

        public TracerListener(ViewContext viewContext) {
            final Document doc = viewContext.get(Document.class);
            tracer = new ToolkitTracer(new Tracer.Emitter() {
                public void emit(String text) {
                    try {
                        doc.insertString(doc.getLength(), text, null);
                    } catch (BadLocationException e) {
                    }
                }
            });
            viewContext.addViewContextChangeListener("start", this);
            viewContext.addViewContextChangeListener("stop", this);
        }

        public void contextChange(ViewContextChangeEvent evt) {
            if ("start".equals(evt.getProperty())) {
                tracer.start();
                viewContext.put(Tracer.class, tracer);
            } else if ("stop".equals(evt.getProperty())) {
                tracer.stop();
                viewContext.put(Tracer.class, null);
            }
        }
    }

}
