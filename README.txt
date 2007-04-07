MyDoggy - My Java Docking Framework 1.2.0 (http://mydoggy.sourceforge.net/)

CONTENTS
========

* License
* System Requirements
* Introduction
* Directory Strucutre
* Usage and Installation
* Building instructions
* Contant
* New in this release

 
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

mydoggy-1.2.0
    bin                         - Contains scripts for launching mydoggy examples.
    lib                         - Contains mydoggy jars (
    								mydoggy-api-1.2.0.jar,
    								mydoggy-plaf-1.2.0.jar,
    							    TableLayout-20020517.jar,     
    							    mydoggy-examples-1.2.0.jar,
    							    junit-3.8.1.jar).
        win32                   - Contains TransparencyManager.dll
    license                     - Contains license information.
    mydoggy-api                 - Contains sources of mydoggy api.
    mydoggy-examples            - Contains sources of mydoggy examples.
    mydoggy-plaf                - Contains sources of mydoggy plaf (the default implementation of the api).
    mydoggy-site-skin           - Contains sources of mydoggy site skin.
    src                         - Contains sources of mydoggy site.
    makefile                    - The makefile to build TransparencyManager.dll

Usage and Installation
======================

MyDoggy is build using Maven-2. So first you have to download Maven-2 from
http://maven.apache.org. The POM declares a few dependencies which are hosted
on any publicly available Maven Repository.


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

New in this release
===================

In this new release you can found :
- A Persistence Mechanism to load and store the ToolWindowManager's workspace.
- Transparency support for Slidying Mode.
- New dragAndDrop system for the toolwindow representative buttons.
- Support for PushAway Mode of the ToolWindowManager.
- ToolWindow aliasing support.
- New toolwindow's visualization method called aggregate.
- Now you can disable or enable Slidying and Floating mode for the single toolwindow.