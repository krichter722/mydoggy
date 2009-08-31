package org.noos.xing.mydoggy.scenarioset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.mydoggyset.action.ExitAction;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.scenarioset.action.LoadWorkspaceAction;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentManagerEventScenario {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;


    protected void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }

    protected void setUp() {
        initComponents();
        initToolWindowManager();
        initMenubar();
    }

    protected void start() {
        SwingUtil.centrePositionOnScreen(frame);
        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("ScenarioTestSet: LoadWorkspaceScenario...");
        this.frame.setSize(640, 480);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set a layout manager. I love TableLayout. It's powerful.
        this.frame.getContentPane().setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
    }

    protected void initToolWindowManager() {
        // Create a new instance of MyDoggyToolWindowManager passing the frame.
        WindowManager myDoggyToolWindowManager = new WindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;


        initContentManager();

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");

        myDoggyToolWindowManager.insertContentTab("Hello1", new JButton("Hello1"));
        myDoggyToolWindowManager.insertContentTab("Hello2", new JButton("Hello2"));
        myDoggyToolWindowManager.insertContentTab("Hello3", new JButton("Hello3"));
        myDoggyToolWindowManager.insertContentTab("Hello4", new JButton("Hello4"));
    }


    protected void initMenubar() {
        // Create a simple JMenuBar
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new LoadWorkspaceAction(frame, toolWindowManager));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(frame));

        menuBar.add(fileMenu);
        this.frame.setJMenuBar(menuBar);
    }

    protected void initContentManager() {
//        ContentManager contentManager = toolWindowManager.getContentManager();
//        contentManager.setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());
        setupContentManagerUI();
    }


    protected void setupContentManagerUI()
  {
      // By default a TabbedContentManagerUI is installed.
      TabbedContentManagerUI contentManagerUI = (TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();

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

               //System.out.println("contentUIRemoving");
               if (component != null)
               {
//                   tabController.removeTab((JComponent) component);
               }

               return true;
           }



           public void contentUIDetached(ContentManagerUIEvent event)
           {
               System.out.println("### contentUIDetached: " + event);
//                String name = arg0.getContent().getComponent().getName();
//
//                if(name == null || name.isEmpty())
//                {
//                    return;
//                }
//
//                EventBus.publish(new ModuleSelectionEvent(arg0.getContent().getComponent().getName()));
//                System.out.println("content selected: " + arg0.getContent().getComponent().getName());
//                Main.getStatusBarManager().enableByName(arg0.getContent().getComponent().getName());
           }
       });
  }

    public static void main(String[] args) {
        ContentManagerEventScenario test = new ContentManagerEventScenario();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class WindowManager extends MyDoggyToolWindowManager implements ContentManagerUIListener
{
  public WindowManager()
  {
      super();
      resourceManager.putProperty("dialog.owner.enabled", "true");

      //add content listener to set a frame icon
      ContentManagerUI contentUIManager = getContentManager().getContentManagerUI();

      contentUIManager.addContentManagerUIListener(this);

      UIManager.put("drag.toolwindow.asTab", "false");

      ContentManager manager = this.getContentManager();
      manager.addContentManagerListener(new ManagerListener());
  }



  public void insertContentTab(String title, JComponent component)
  {
      ContentManager manager = this.getContentManager();

      Content content = manager.addContent(title, title, null, component);
      //content.addPropertyChangeListener(new Listener());

      TabbedContentUI contentUI = (TabbedContentUI) content.getContentUI();

      //contentUI.addPropertyChangeListener(new Listener());

      configureDetachability(contentUI, component);
  }



  public void insertContentTab(String title, Icon icon, JComponent component)
  {
      ContentManager manager = this.getContentManager();
      Content content = manager.addContent(title, title, icon, component);

      TabbedContentUI contentUI = (TabbedContentUI) content.getContentUI();
      // without the need of the cast

      configureDetachability(contentUI, component);
  }



  public void selectContentTab(JComponent component)
  {
      System.out.println("### select tab");

      ContentManager manager = this.getContentManager();

      Content contents[] = manager.getContents();

      for (Content content : contents)
      {
          Component item = content.getComponent();

          if (item.equals(component))
          {
              if (content.isMinimized() == true)
              {
                  //content.setMinimized(false);
                  content.setMaximized(true);
              }
              else
              {
                  content.setSelected(true);
              }

              //item.setVisible(true);
              content.setSelected(true);
          }
          else
          {
              //item.setVisible(false);
          }
      }
  }



  public void removeContentTab(Component component)
  {
      System.out.println("### Remove content");

      ContentManager manager = this.getContentManager();

      Content contents[] = manager.getContents();

      for (Content content : contents)
      {
          Component item = content.getComponent();

          if (item.equals(component))
          {
              manager.removeContent(content);
          }
      }
  }



  public void substituteTabContent(JComponent componentToRemove, JComponent componentToAdd)
  {
      ContentManager manager = this.getContentManager();
      ContentManagerUI contentManagerUI = manager.getContentManagerUI();

      Content contents[] = manager.getContents();

      for (Content content : contents)
      {
          Component item = content.getComponent();

          if (item.equals(componentToRemove))
          {
              ContentUI contentUI = contentManagerUI.getContentUI(content);

              if (content.isDetached() == true)
              {
                  //content.setComponent(componentToAdd);
              }
              else
              {
                  content.setComponent(componentToAdd);
              }
          }
      }
  }



  public boolean contentUIRemoving(ContentManagerUIEvent arg0)
  {
      return true;
  }



  public void contentUIDetached(ContentManagerUIEvent arg0)
  {
//      Window windows[] = Frame.getWindows();
//
//      for (Window window : windows)
//      {
//          window.setIconImage(Main.getMainWindow().getIconImage());
//      }
  }




  private void configureDetachability(TabbedContentUI contentUI, JComponent component)
  {
//      if (component instanceof NotDetachableInterface)
//      {
//          configureAsUndetachable(contentUI);
//      }
//      else
//      {
          configureAsDetachable(contentUI);
//      }
  }



  private void configureAsUndetachable(TabbedContentUI contentUI)
  {
      contentUI.setAlwaysOnTop(false);
      contentUI.setAddToTaskBarWhenDetached(true);
      contentUI.setCloseable(true);
      contentUI.setDetachable(false);
      contentUI.setMinimizable(false);
      contentUI.setMaximizable(false);
      contentUI.setTransparentMode(false);
  }



  private void configureAsDetachable(TabbedContentUI contentUI)
  {
//      contentUI.setAlwaysOnTop(false);
//      contentUI.setAddToTaskBarWhenDetached(true);
//      contentUI.setCloseable(true);
      contentUI.setDetachable(true);
//      contentUI.setMinimizable(false);
//      contentUI.setMaximizable(false);
//      contentUI.setTransparentMode(false);
  }
}

    class Listener implements PropertyChangeListener
    {
      public void propertyChange(PropertyChangeEvent evt)
      {
          System.out.println("### property changed: " + evt.getPropertyName());

          System.out.println("### value: " + evt.getNewValue());
          System.out.println("### source: " + evt.getSource());
      }
    }


    class ManagerListener implements ContentManagerListener
    {
      public void contentAdded(ContentManagerEvent arg0)
      {
          System.out.println("content added: " + arg0.getContent().getComponent().getName());
//          Main.getStatusBarManager().enableByName(arg0.getContent().getComponent().getName());
      }



      public void contentRemoved(ContentManagerEvent arg0)
      {
          System.out.println("content removed: " + arg0.getContent().getComponent().getName());
//          Main.getStatusBarManager().disableByName(arg0.getContent().getComponent().getName());
      }



      public void contentSelected(ContentManagerEvent arg0)
      {
//          String name = arg0.getContent().getComponent().getName();

//          if(name == null /*|| name.isEmpty()*/)
//          {
//              return;
//          }

//          EventBus.publish(new ModuleSelectionEvent(arg0.getContent().getComponent().getName()));
          System.out.println("content selected: " + arg0/*.getContent().getComponent().getName()*/);
//          Main.getStatusBarManager().enableByName(arg0.getContent().getComponent().getName());
      }
    }

}