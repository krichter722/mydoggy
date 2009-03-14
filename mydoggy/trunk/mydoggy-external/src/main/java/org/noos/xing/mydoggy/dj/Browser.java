package org.noos.xing.mydoggy.dj;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * @author Jens
 */
public class Browser extends JPanel
{
    private JWebBrowser webBrowser = new JWebBrowser(JWebBrowser.destroyOnFinalization(), JWebBrowser.constrainVisibility(), JWebBrowser.proxyComponentHierarchy());


    public Browser()
    {
        initBrowser();
    }


    
    @Override
    public void removeNotify()
    {
        System.out.println("### removed");
    }


    public void setLocationBarVisible(boolean isVisible)
    {
        webBrowser.setLocationBarVisible(isVisible);
    }


    public void setMenuBarVisible(boolean isVisible)
    {
        webBrowser.setMenuBarVisible(isVisible);
    }



    public void setStatusBarVisible(boolean isVisible)
    {
        webBrowser.setStatusBarVisible(isVisible);
    }



    public void setButtonBarVisible(boolean isVisible)
    {
        webBrowser.setButtonBarVisible(isVisible);
    }


    public void setBarsVisible(boolean isVisible)
    {
        webBrowser.setBarsVisible(isVisible);
    }



    public void openUrl(String url)
    {
        webBrowser.navigate(url);
    }
    


    public JWebBrowser getBrowser()
    {
        return webBrowser;
    }



    /***************************************************************************
     * private methods
     **************************************************************************/
    
    
    private void initBrowser()
    {
        this.setLayout(new BorderLayout());
        JInternalFrame internalFrame = new JInternalFrame();
        internalFrame.setResizable(true);
        internalFrame.setVisible(true);

        internalFrame.add(webBrowser, BorderLayout.CENTER);

        //this.add(new NativeComponentWrapper(webBrowser).createEmbeddableComponent(NSComponentOptions.proxyComponentHierarchy()), BorderLayout.CENTER);
        this.add(internalFrame, BorderLayout.CENTER);
        //this.add(webBrowser, BorderLayout.CENTER);
    }




}
