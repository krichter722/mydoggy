package org.noos.xing.mydoggy.mydoggyset.view.contents.model;

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

    public int getRowCount() {
        return (windowManager != null) ? windowManager.getContentManager().getContentCount() : 0;
    }

    public boolean isCellEditable(int row, int column) {
        return true;
    }

    public void setValueAt(Object aValue, int row, int column) {
        switch (column) {
            case 0 :
                windowManager.getContentManager().getContent(row).setTitle((String) aValue);
                break;
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

    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0 :
                return windowManager.getContentManager().getContent(row).getTitle();
            case 1 :
                return windowManager.getContentManager().getContent(row).isEnabled();
            case 2 :
                return windowManager.getContentManager().getContent(row).isSelected();
            case 3 :
                return windowManager.getContentManager().getContent(row).isDetached();
            case 4 :
                return windowManager.getContentManager().getContent(row).isFlashing();
            default:   
                return windowManager.getContentManager().getContent(row);
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
        fireTableDataChanged();
    }

}
