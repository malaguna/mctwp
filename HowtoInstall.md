# Introduction #

This project is a Java web application that needs a Servlet Container to work. It has been tested in Tomcat 6.0.X, but it must work with any container. So this project is packaged as a WAR file, that must be deployed into container.

It is supposed to work in any operating system supported by the Servlet Container, but it has only been tested into GNU/Linux Ubuntu Server 8.04, 9.04 and 10.04.

However, it needs some other prerequisites in order to work. It uses an external XML data base to store image header's metadata. One of the included plugins need (X)MedCon program installed (until I can link it through JNI) and finally it needs a directory (or remote file system) to store images.

This page gives you both, a minimal instructions to get project working, and a complete instructions set to build a server from scratch.

Basically, you have to do three things:

  * Setup environment.
    * If you already have a server, this are [minimal configuration](InstallMinimalInstructions.md).
    * If you want to build your server from scratch, here you have [complete configuration](InstallCompleteInstructions.md).
  * [Deploy application](DeployApplication.md).
  * [Configure application](ConfigureApplication.md).