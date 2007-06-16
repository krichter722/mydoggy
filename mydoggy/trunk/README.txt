MyDoggy - My Java Docking Framework 1.3.0 (http://mydoggy.sourceforge.net/)

CONTENTS
========

* License
* System Requirements
* Introduction
* Directory Strucutre
* Usage and Installation
* Building instructions
* Contant
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

mydoggy-1.3.0
    bin                         - Contains scripts for launching mydoggy examples.
    lib                         - Contains mydoggy jars (
    								mydoggy-api-1.3.0.jar,
    								mydoggy-plaf-1.3.0.jar,
    								mydoggy-res-1.3.0.jar,
    							    TableLayout-20020517.jar,
    							    mydoggy-examples-1.3.0.jar,
    								mydoggy-itest-1.3.0.jar,
    							    junit-3.8.1.jar).
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

To build TransparencyManager.dll simply call (starting from the directory you have choosed to unzip mydoggy):

 	mingw32-make


Contact
=======

MyDoggy is developed by Angelo De Caro, a computer science student from Italy.
Feel free to contact me at adecaro@users.sourceforge.net
If you need help with something or just want to say hello, you can also
use the forums or the support section at sourceforge.net.

Changes
=======

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