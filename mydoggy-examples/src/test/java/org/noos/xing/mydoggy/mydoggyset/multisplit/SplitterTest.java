package org.noos.xing.mydoggy.mydoggyset.multisplit;

import org.jdesktop.swingx.JXTitledPanel;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

/**
 * @author Kohsuke Kawaguchi
 */
public class SplitterTest extends JFrame{

  private ToolWindowManager toolWinManager = null;
  private ContentManager contentManager = null;
  private TabbedContentManagerUI contentManagerUI = null;
  
  
  public SplitterTest(){
    super("Bug");
    init();
  }
  
  public void init(){
    
    MyDoggyToolWindowManager mydoggyToolWindowManager = new MyDoggyToolWindowManager();
    this.toolWinManager = mydoggyToolWindowManager;
    this.getContentPane().add(mydoggyToolWindowManager, BorderLayout.CENTER); 
    initToolWindow();
    
    this.contentManager = toolWinManager.getContentManager();
    this.contentManagerUI = (TabbedContentManagerUI)contentManager.getContentManagerUI();
    this.contentManagerUI.setShowAlwaysTab(true);
    this.contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.TOP);      
        
    
    
    for(ToolWindow win : toolWinManager.getToolWindows()){
      win.setAvailable(true);
      win.setActive(true);
      win.setVisible(true);
    }
    Content content = contentManager.addContent("test", "test", null, this.getNewPanel());
    
  }
  
  private JPanel getNewPanel() {
    MainPanel p = new MainPanel("Test");
        
    return p;
  }

  public void initToolWindow() {
    this.toolWinManager.getToolWindowManagerDescriptor().setPushAwayMode(PushAwayMode.HORIZONTAL);
    this.toolWinManager.getToolWindowManagerDescriptor().setNumberingEnabled(false);
    //ToolWindow win = toolWindowManager.getToolWindow(MSG.getMSG(MSG.Help_Tab));
    
    for(ToolWindow win : toolWinManager.getToolWindows()){
      if(win == null) continue;
      win.setAvailable(true);     
      
      DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor)win.getTypeDescriptor(ToolWindowType.DOCKED);
      dockedTypeDescriptor.setDockLength(300);
      dockedTypeDescriptor.setPopupMenuEnabled(true);    
      dockedTypeDescriptor.setAnimating(true);
      dockedTypeDescriptor.setPreviewEnabled(true);
      dockedTypeDescriptor.setPreviewDelay(500);
      dockedTypeDescriptor.setPreviewTransparentRatio(0.7f);
      
      SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) win.getTypeDescriptor(ToolWindowType.SLIDING);
      slidingTypeDescriptor.setEnabled(true);
      slidingTypeDescriptor.setTransparentMode(true);
      slidingTypeDescriptor.setTransparentRatio(0.7f);
      slidingTypeDescriptor.setTransparentDelay(0);
      slidingTypeDescriptor.setAnimating(true);

      FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) win.getTypeDescriptor(ToolWindowType.FLOATING);
      floatingTypeDescriptor.setEnabled(true);
      floatingTypeDescriptor.setLocation(150,200);
      floatingTypeDescriptor.setSize(320,200);
      floatingTypeDescriptor.setModal(false);
      floatingTypeDescriptor.setTransparentMode(true);
      floatingTypeDescriptor.setTransparentRatio(0.7f);
      floatingTypeDescriptor.setTransparentDelay(500);
      floatingTypeDescriptor.setAnimating(true);
    }
      
   }  
    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      SplitterTest m = new SplitterTest();
      m.setSize(300,300);      
      m.setVisible(true);
    }

    /**
     * <code>MainPanel</code> is the information container, which plays the role as the view in MVC.
     *
     * <p>Copyright: (c) 2007</p>
     * <p>Organization: Siemens PLM Software</p>
     *
     * @author Hongyi Jiang
     * @version 1.0 2007-12-15
     */
    @SuppressWarnings("serial")
    public class MainPanel extends JPanel {

        protected JXTitledPanel navigatorPanel = null;
        protected JXTitledPanel infoPanel = null;


        public MainPanel(String title) {
            super();
            init(title);
        }

        /**
         * Initialize the main panel
         */
        private void init(String title) {
            navigatorPanel = new JXTitledPanel();
            navigatorPanel.setTitle(title);
            navigatorPanel.add(new JButton("Test"));
            //navigatorPanel.setBorder(SHADOW_BORDER);

            infoPanel = new JXTitledPanel();
            infoPanel.setTitle(title + " Information");
            //infoPanel.setBorder(SHADOW_BORDER);

            JSplitPane mainSP = createSplitPane(200, JSplitPane.HORIZONTAL_SPLIT);
            mainSP.setLeftComponent(navigatorPanel);
            mainSP.setRightComponent(infoPanel);

            this.setLayout(new BorderLayout());
            this.add(mainSP);
        }

        private JSplitPane createSplitPane(int dividerLocation, int orientation) {
            JSplitPane splitPane = new JSplitPane(orientation);
            splitPane.setDividerLocation(dividerLocation);
            splitPane.setBorder(null);
            ((BasicSplitPaneUI) splitPane.getUI()).getDivider().setBorder(BorderFactory.createEmptyBorder());
            return splitPane;
        }


    }

}
