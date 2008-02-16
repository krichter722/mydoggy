package org.noos.xing.mydoggy.mydoggyset.view.contents.model;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManagerListener;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.event.ContentManagerEvent;

import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public final class ContentsTableModel extends DefaultTableModel implements PropertyChangeListener, ContentManagerListener {
	private static final long serialVersionUID = -7576229275613987979L;

    private final ToolWindowManager windowManager;

	public ContentsTableModel(ToolWindowManager windowManager) {
        this.windowManager = windowManager;

        setColumnIdentifiers(new Object[]{
                "Title", "Enabled", "Selected", "Detached", "Flashing"
        });
        initToolsListeners();
        updateModel();
    }

    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    public void setValueAt(Object aValue, int row, int column) {
        switch (column) {
            case 1 :
                windowManager.getContentManager().getContent(row).setEnabled((Boolean) aValue);
                break;
            case 2 :
                windowManager.getContentManager().getContent(row).setSelected((Boolean) aValue);
                break;
            case 3 :
                windowManager.getContentManager().getContent(row).setDetached((Boolean) aValue);
                break;
            case 4 :
                windowManager.getContentManager().getContent(row).setFlashing((Boolean) aValue);
                break;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateModel();
    }

    public void contentAdded(ContentManagerEvent event) {
        event.getContent().addPropertyChangeListener(this);
        updateModel();
    }

    public void contentRemoved(ContentManagerEvent event) {
        event.getContent().removePropertyChangeListener(this);
        updateModel();
    }

    public void contentSelected(ContentManagerEvent event) {
    }

    protected void initToolsListeners() {
        windowManager.getContentManager().addContentManagerListener(this);
    }

    protected void updateModel() {
        getDataVector().clear();

        Content[] contents = windowManager.getContentManager().getContents();
        for (Content content : contents) {
            addRow(new Object[]{
                    content.getTitle(),
                    content.isEnabled(),
                    content.isSelected(),
                    content.isDetached(),
                    content.isFlashing(),
                    content
            });
        }

        fireTableDataChanged();
    }

}
