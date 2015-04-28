# Deploy Application #

Depends on the Servlet Container, the way to deploy the application may be different. Remember that if you don't setup libraries into shared libraries of Servlet Container, you have to put them into _WEB-INF/lib_ of the application before to deploy it.

Before you deploy the application, I recommend you to configure **applicationContext.xml**. This file is inside directory **WEB-INF** of deployment package _mctwp.war_

You must configure this file as [follows](ConfigureApplication.md).