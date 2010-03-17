<%@ page session="true" %>
<%@ page import="java.util.zip.ZipEntry"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.util.zip.ZipOutputStream"%>
<%@ page import="es.urjc.mctwp.service.BeanNames"%>
<%@ page import="es.urjc.mctwp.image.objects.Image"%>
<%@ page import="es.urjc.mctwp.service.ServiceDelegate"%>
<%@ page import="es.urjc.mctwp.image.objects.SingleImage"%>
<%@ page import="es.urjc.mctwp.image.objects.SeriesImage"%>
<%@ page import="es.urjc.mctwp.bbeans.research.SessionInvBean"%>
<%@ page import="es.urjc.mctwp.service.commands.imageCmds.LoadImage"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils;"%>

<!--
Copyright 2008, 2009, 2010 Miguel Ángel Laguna Lobato

This file is part of Multiclinical Trial Web-PACS.

Multiclinical  Trial Web-PACS is free  software: you  can redistribute 
it and/or modify it under  the terms of the GNU General Public License 
as published by the Free  Software Foundation, either version 3 of the 
License, or (at your option) any later version.

Multiclinical  Trial Web-PACS is distributed  in the hope that it will 
be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
of  MERCHANTABILITY or  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
General Public License for more details.

You should have received a copy of the GNU General Public License
along with Multiclinical Trial Web-PACS.  If not, see 
<http://www.gnu.org/licenses/>. 
 -->

<%
	//Get access to the FS Bean to retrieve images
	ServletContext servletContext = this.getServletContext();
	WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	ServiceDelegate service = (ServiceDelegate)wac.getBean(BeanNames.SERVICE_DELEGATE);
	SessionInvBean sessionBean = (SessionInvBean)request.getSession().getAttribute("sessionInv");
	
	//Get parameters collection an image id
	String imageId = (String)request.getParameter("imageId");
	String collection = (String)request.getParameter("collection");
	int BUFFER_SIZE = 4096;
	
	if( (imageId != null) && (collection != null)){
		//Create command and store images
		LoadImage cmd = new LoadImage(wac);
		cmd.setCollection(collection);
		cmd.setImageId(imageId);
		cmd.setUser(sessionBean.getUser());
		cmd.setTrial(sessionBean.getTrial());
		cmd = (LoadImage)service.runCommand(cmd);
		Image img = cmd.getResult();

		//Avoid caching image files is response
		response.setHeader("pragma", "no-cache");
		response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
		response.setHeader("Expires", "01 Jan 2000 00:00:00 GMT");

		if(img instanceof SingleImage){
			SingleImage single = (SingleImage)img;
			
			//If the file exists
			if(single.getContent().exists()){
	
				//Get file name without parents
				String contentType = img.getType();
				String fileName = imageId + "." + contentType;
				
				//Prepare header
				response.setContentType(contentType);
				StringBuffer contentDisposition = new StringBuffer();
				contentDisposition.append("attachment;");
				contentDisposition.append("filename=\"");
				contentDisposition.append(fileName);
				contentDisposition.append("\"");
				response.setHeader ("Content-Disposition", contentDisposition.toString());
		
				//Put file content
				FileInputStream fileIS = new FileInputStream(single.getContent());
				byte bytes[] = new byte[BUFFER_SIZE];
				while(fileIS.read(bytes) != -1)
					response.getOutputStream().write(bytes);
				
				fileIS.close();
			}
		}else{
			SeriesImage serie = (SeriesImage)img;
			
			//Get file name without parents
			String contentType = img.getType();
			String fileName = imageId + ".zip";
			
			//Prepare header
			response.setContentType(contentType);
			StringBuffer contentDisposition = new StringBuffer();
			contentDisposition.append("attachment;");
			contentDisposition.append("filename=\"");
			contentDisposition.append(fileName);
			contentDisposition.append("\"");
			response.setHeader ("Content-Disposition", contentDisposition.toString());
			
			if(serie.getImages() != null){
				//Prepare ZipOutputStream and writes files into
				ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
				
				//Add every image to zip file
				for(Image i : serie.getImages()){

					if(i instanceof SingleImage){
						SingleImage si = (SingleImage)i;
						
						//Create zip entry
						ZipEntry ze = new ZipEntry(si.getContent().getName());
						FileInputStream fileIS = new FileInputStream(si.getContent());
						zos.putNextEntry(ze);
						
						//Write zip entry
						byte bytes[] = new byte[BUFFER_SIZE];
						while(fileIS.read(bytes) != -1)
							zos.write(bytes);
						
						fileIS.close();
					}					
				}
				
				//Close zip file
				zos.close();
			}
		}
	}
%>
        