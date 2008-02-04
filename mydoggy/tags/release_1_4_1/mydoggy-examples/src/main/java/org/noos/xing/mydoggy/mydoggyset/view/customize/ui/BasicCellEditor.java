package org.noos.xing.mydoggy.mydoggyset.view.customize.ui;

import javax.swing.*;
import javax.swing.tree.TreeCellEditor;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import java.io.Serializable;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class BasicCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor {
    protected JComponent editorComponent;
    protected EditorDelegate delegate;
    protected int clickCountToStart = 1;

    public Component getComponent() {
        return editorComponent;
    }

    public void setClickCountToStart(int count) {
        clickCountToStart = count;
    }

    public int getClickCountToStart() {
        return clickCountToStart;
    }

    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    public boolean isCellEditable(EventObject anEvent) {
        return delegate.isCellEditable(anEvent);
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return delegate.shouldSelectCell(anEvent);
    }

    public boolean stopCellEditing() {
        return delegate.stopCellEditing();
    }

    public void cancelCellEditing() {
        delegate.cancelCellEditing();
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, false);
        delegate.setValue(stringValue);
        return editorComponent;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        delegate.setValue(value);
        return editorComponent;
    }


    protected class EditorDelegate implements ActionListener, ItemListener, Serializable {
        protected Object value;

        public Object getCellEditorValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
            }
            return true;
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        public boolean startCellEditing(EventObject anEvent) {
            return true;
        }

        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        public void actionPerformed(ActionEvent e) {
            BasicCellEditor.this.stopCellEditing();
        }

        public void itemStateChanged(ItemEvent e) {
            BasicCellEditor.this.stopCellEditing();
        }
    }
}
