MyDoggy - My Java Docking Framework 1.4.1 (http://mydoggy.sourceforge.net/)

CONTENTS
========

* License
* System Requirements
* Introduction
* Directory Structure
* Usage and Installation
* Building instructions
* Contact
* Changes

 
License
=======

MyDoggy is licensed under the LGPL, please see the LGPL.txt file for more information.

System Requirements
===================

MyDoggy requires JDK5 or higher.


Introduction
============

MyDoggy is a Java docking framework for use in cross-platform Swing applications.
MyDoggy is an IntelliJ Idea like docking framework for managing secondary windows within the main
window. MyDoggy allows to move, resize or extract those secondary windows.
Also, MyDoggy provides support for content management of the main window.
Like Eclipse, MyDoggy supports the concept of "prospective" using mydoggy groups.


Directory Structure
===================

mydoggy-1.4.1
    bin                         - Contains scripts for launching mydoggy examples.
    lib                         - Contains mydoggy jars (
    								mydoggy-api-1.4.1.jar,
    								mydoggy-plaf-1.4.1.jar,
    								mydoggy-res-1.4.1.jar,
    							    TableLayout-20020517.jar,
    							    mydoggy-examples-1.4.1.jar,
    								mydoggy-itest-1.4.1.jar,
    							    junit-3.8.1.jar.
    							    <other libs>
        win32                   - Contains TransparencyManager.dll
    license                     - Contains license information.
    mydoggy-api                 - Contains sources of mydoggy api.
    mydoggy-examples            - Contains sources of mydoggy examples.
    mydoggy-plaf                - Contains sources of mydoggy plaf (the default implementation of the api).
    mydoggy-site-skin           - Contains sources of mydoggy site skin.
    mydoggy-res                 - Contains sources of mydoggy res.
    mydoggy-itest               - Contains sources of mydoggy itest for interactive tests.
    src                         - Contains sources of mydoggy site.
    makefile                    - The makefile to build TransparencyManager.dll

Usage and Installation
======================

MyDoggy is build using Maven-2. So first you have to download Maven-2 from http://maven.apache.org.
The POM declares a few dependencies which are hosted on any publicly available Maven Repository.
You then should be ready to build MyDoggy.

Building instructions
=====================

To build MyDoggy for JDK5 simply call :

    mvn clean package install

To build the binary and the source distribution simply call :

    mvn -Ddistro clean package install assembly:assembly

To build the site simply call :

    mvn -Dsite clean package site assembly:assembly

To build TransparencyManager.dll simply call (starting from the directory you have chosen to unzip mydoggy):

 	mingw32-make


Contact
=======

MyDoggy is developed by Angelo De Caro, a computer science student from Italy.
Feel free to contact me at adecaro@users.sourceforge.net
If you need help with something or just want to say hello, you can also
use the forums or the support section at sourceforge.net.

Changes
=======

- For 1.4.1

    New Features:

    -- Content aliasing support into the ContentManager interface.
    -- New <code>MultiSplitContentUI</code> interface to modify the ui behaviours of a content when a
       <code>MultiSplitContentManagerUIis</code> used as current <code>ContentManagerUI</code>.
    -- Now also the representative anchor buttons for unavailable tools can be showed. See the screenshot here.


    Bug Resolved:

    -- Right click on non-selected content tab fires action : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1865256&group_id=178005&atid=883495">Request ID 1865256</a>
    -- Restoring detached windows : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1866867&group_id=178005&atid=883495">Request ID 1866867</a>
    -- Windows accidentally losing focus : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1866906&group_id=178005&atid=883495">Request ID 1866906</a>
    -- Re-Attaching content windows : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1867097&group_id=178005&atid=883495">Request ID 1867097</a>
    -- Toolpanels get glued together (split-pane-alike) : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1872768&group_id=178005&atid=883495">Request ID 1872768</a>
    -- TableLayout: ArrayIndexOutOfBounce : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1873056&group_id=178005&atid=883495">Request ID 1873056</a>
    -- Repainting issues on overlapping tools : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1872970&group_id=178005&atid=883495">Request ID 1872970</a>
    -- Title of floating toolwindow not painted at proper position. : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1874502&group_id=178005&atid=883495">Request ID 1874502</a>
    -- ToolWindow disappears when maximizing floating, un-pinned : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1877094&group_id=178005&atid=883495">Request ID 1877094</a>
    -- Repainting problems with floating tool windows : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1877465&group_id=178005&atid=883495">Request ID 1877465</a>
    -- Maximize on contentPanels does not work properly : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1878149&group_id=178005&atid=883495">Request ID 1878149</a>
    -- PropertyChangeEvent \"selected\" is dist. twice on CP-reatta : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1879855&group_id=178005&atid=883495">Request ID 1879855</a>
    -- CycleRoot-issues with floating Toolpanels : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1881720&group_id=178005&atid=883495">Request ID 1881720</a>
    -- Transition from FLOATING > SLIDING : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1881882&group_id=178005&atid=883495">Request ID 1881882</a>
    -- Maximizing floating ToolWindow also maximizes the mainwindow : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1882515&group_id=178005&atid=883495">Request ID 1882515</a>

    Feature Requests:

    -- Please reintroduce id == Object instead of String : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1863970&group_id=178005&atid=883498">Request ID 1863970</a>

- For 1.4.0

    Bug Resolved:

    -- Problem with focushandling in DockedContainer : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1765219&group_id=178005&atid=883495">Request ID 1765219</a>
    -- TabbedContentManager problem when display icon and title : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1765198&group_id=178005&atid=883495">Request ID 1765198</a>

    Feature Requests:
    
    -- Maximize windows - Handle multiple monitors : <a href="https://sourceforge.net/tracker/index.php?func=detail&aid=1761886&group_id=178005&atid=883498">Request ID 1761886</a>

- For 1.3.0

    New Features:

    -- ToolWindow Preview: if you wait on the representative button of a tool not visible you'll see a transparent preview of the tool of size 151x75. If you move the mouse outside the representative button the preview will become invisible;
    -- PushAwayMode.MOST_RECENT : whoever is pressed last toolwindow push away the previous toolwindows. This way the order of opening the toolwindows determine who push who (and you can achieve any "priority" mode by selecting the order of clicks);
    -- ToolWindow Flashing : Sets the flashing mode. If the flashing mode is enabled then the toolwindow representative button will be flashing until the tool will be made visible. Of if the tool is already visible but not active then the tool's title bar will be flashing until the tool will be made active.
    -- Maximize windows : Request ID 1722871
    -- Rearrange Windows : Request ID 1725144
    -- ToolWindowTab : every toolwindow can be considered as a special JTabbedPane and so it can contain more than one component. Every tab is described by a title, an icon and a component. A tab can be selected or not.
    -- Corner Component : you can add a specified component at the given corner of the toolwindow manager.

    Bug Resolved:

    -- Alignment of dragged Button wrong : Request ID 1734976

- For 1.2.0

    New Features:

    -- A Persistence Mechanism to load and store the ToolWindowManager's workspace.
    -- Transparency support for Slidying Mode.
    -- New dragAndDrop system for the toolwindow representative buttons.
    -- Support for PushAway Mode of the ToolWindowManager.
    -- ToolWindow aliasing support.
    -- New toolwindow's visualization method called aggregate.
    -- Now you can disable or enable Slidying and Floating mode for the single toolwindow.