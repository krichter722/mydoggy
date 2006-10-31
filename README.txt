MyDoggy - My Java Docking Framework 1.0.0

CONTENTS
========

* License
* System Requirements
* Introduction
* Directory Strucutre
* Usage and Installation
* Building instructions

License
=======

MyDoggy is licensed under the LGPL, please see the LGPL.txt file for more information.

System Requirements
===================

MyDoggy requires JDK5 for compilation.


Introduction
============

MyDoggy is a Java docking framework for use in cross-platform Swing applications.
MyDoggy is an IntelliJ Idea like docking framework for managing secondary windows within the main
window. MyDoggy allows to move, resize or extract those secondary windows.
Also, MyDoggy provides support for content management of the main window.


Directory Structure
===================

MyDoggy
    bin                         - Contains scripts for launching mydoggy examples.
    lib                         - Contains mydoggy jars.
        win32                   - Contains TransparencyManager.dll
    license                     - Contains license information.
    mydoggy-api                 - Contains sources of mydoggy api.
    mydoggy-examples            - Contains sources of mydoggy examples.
    mydoggy-plaf                - Contains sources of mydoggy plaf (the default implementation of api).
    mydoggy-site-skin           - Contains sources of mydoggy site skin.
    src                         - Contains sources of mydoggy site.


Usage and Installation
======================

The plugin is build using Maven-2. So first you have to download Maven-2 from
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
