<%@ page session="true" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="es.urjc.mctwp.service.BeanNames"%>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="es.urjc.mctwp.image.objects.Image"%>
<%@ page import="org.apache.commons.io.FilenameUtils"%>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="es.urjc.mctwp.service.ServiceDelegate"%>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="es.urjc.mctwp.bbeans.research.SessionInvBean"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="es.urjc.mctwp.service.commands.imageCmds.StoreTemporalImages"%>
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

	//Get access to the bussines logic back bean
	ServletContext servletContext = this.getServletContext();
	WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	ServiceDelegate service = (ServiceDelegate)wac.getBean(BeanNames.SERVICE_DELEGATE);
	SessionInvBean sessionBean = (SessionInvBean)request.getSession().getAttribute("sessionInv");
	String result = null;

	if( (sessionBean != null) && (service != null) ){
		response.setContentType("text/plain");
		
		//Get path parameters and variables
		int ourMaxMemorySize  = 10000000;
		int ourMaxRequestSize = 2000000000;
		String base = sessionBean.getAbsoluteThumbDir();
		String user = sessionBean.getUser().getLogin();
	
		//Arrays for storing received files and generated images
		List<File> files = new ArrayList<File>();
		List<Image> imgs = new ArrayList<Image>();
		
		//Initialization for chunk management.
		boolean bLastChunk = false;
		int numChunk = 0;
	
		try{
			// Get URL Parameters.
			Enumeration<?> paraNames = request.getParameterNames();
			String pname;
			String pvalue;
	
			while (paraNames.hasMoreElements()) {
				pname = (String)paraNames.nextElement();
				pvalue = request.getParameter(pname);
				
				if (pname.equals("jufinal")) {
					bLastChunk = pvalue.equals("1");
				} else if (pname.equals("jupart")) {
					numChunk = Integer.parseInt(pvalue);
				}
			}
	 
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			
			// Set factory constraints
			factory.setSizeThreshold(ourMaxMemorySize);
			factory.setRepository(new File(base));
			
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			// Set overall request size constraint
			upload.setSizeMax(ourMaxRequestSize);
			
			// Parse the request
			List<?> items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			FileItem fileItem;
			File fout;
			
			while (iter.hasNext()) {
				fileItem = (FileItem) iter.next();
		
				if (!fileItem.isFormField()) {
			        //Ok, we've got a file. Let's process it.
					String uploadedFilename = fileItem.getName();
	
			        //If we are in chunk mode, we add ".partN" 
			        //at the end of the file, where N is the chunk number.
			        boolean isPart = numChunk > 0;
					uploadedFilename += ( isPart ? ".part" + numChunk : "");
			        		        
			        //write file
					fout = new File(FilenameUtils.concat(base, uploadedFilename));
					fileItem.write(fout);
					
					if(!isPart) files.add(fout);
					
					//Chunk management: if it was the last chunk, let's
					//recover the complete file by concatenating all chunk parts.
					if (bLastChunk) {	        
	
						//First: construct the final filename.
						fout = new File(FilenameUtils.concat(base, fileItem.getName()));
						FileOutputStream fos = new FileOutputStream(fout);
						byte[] byteBuff = new byte[1024];int nbBytes;
						
						//Add to the final file each chunk
						for (int i=1; i<=numChunk; i++) {
							String filename = fileItem.getName() + ".part" + i;
							File fpart = new File(FilenameUtils.concat(base, filename));
							FileInputStream fis = new FileInputStream(fpart);
							
							//write the part to the file
							while ( (nbBytes = fis.read(byteBuff)) >= 0)
								fos.write(byteBuff, 0, nbBytes);
							
							fis.close();
							fpart.delete();
						}
						fos.close();
						files.add(fout);
					}
					
					fileItem.delete();
				}	    
			}
			
			//Create command and store images
			StoreTemporalImages cmd = new StoreTemporalImages(wac);
			cmd.setFiles(files);
			cmd.setUser(sessionBean.getUser());
			cmd.setTrial(sessionBean.getTrial());
			cmd = (StoreTemporalImages)service.runCommand(cmd);
			
			result = "SUCCESS";
			
		}catch(Exception e){
			result = "ERROR: " + e.getLocalizedMessage();
		}
		
	} else result = "ERROR: unknown user session";
		
	out.println(result);
%>
        