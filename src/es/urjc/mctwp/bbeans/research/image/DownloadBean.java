//Copyright 2008, 2009, 2010 Miguel Ángel Laguna Lobato
//
//This file is part of Multiclinical Trial Web-PACS.
//
//Multiclinical  Trial Web-PACS is free  software: you  can redistribute 
//it and/or modify it under  the terms of the GNU General Public License 
//as published by the Free  Software Foundation, either version 3 of the 
//License, or (at your option) any later version.
//
//Multiclinical  Trial Web-PACS is distributed  in the hope that it will 
//be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
//of  MERCHANTABILITY or  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
//General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Multiclinical Trial Web-PACS.  If not, see 
//<http://www.gnu.org/licenses/>.

package es.urjc.mctwp.bbeans.research.image;

import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.SeriesImage;
import es.urjc.mctwp.image.objects.SingleImage;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.service.commands.imageCmds.LoadImage;

public class DownloadBean extends RequestInvAbstractBean {
	private int BUFFER_SIZE = 4096;
	
	public String accDownloadImage(){
		String imageId = getSession().getImage().getImageId();
		
		if(imageId != null && !imageId.isEmpty()){
			Trial trial = getSession().getTrial();
			
			try{
				if(trial != null){
					FacesContext ctx = FacesContext.getCurrentInstance();  
					HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();  
	
					LoadImage cmd = (LoadImage) getCommand(LoadImage.class);
					cmd.setCollection(trial.getCollection());
					cmd.setImageId(imageId);
					cmd.setUser(getSession().getUser());
					cmd.setTrial(trial);
					cmd = (LoadImage)runCommand(cmd);
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
							byte bytes[] = new byte[BUFFER_SIZE];
							FileInputStream fileIS = new FileInputStream(single.getContent());
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
							ZipOutputStream zos;
							zos = new ZipOutputStream(response.getOutputStream());
							
							//Add every image to zip file
							for(Image i : serie.getImages()){
	
								if(i instanceof SingleImage){
									SingleImage si = (SingleImage)i;
									
									//Create zip entry
									ZipEntry ze = new ZipEntry(si.getContent().getName());
									zos.putNextEntry(ze);

									//Write zip entry
									byte bytes[] = new byte[BUFFER_SIZE];
									FileInputStream fileIS = new FileInputStream(si.getContent());;
									while(fileIS.read(bytes) != -1)
										zos.write(bytes);
									
									fileIS.close();
								}					
							}
							
							//Close zip file
							zos.close();
						}
					}
					
					ctx.responseComplete();
				}
			} catch (Exception e) {
				setErrorMessage(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
				
		return null;
	}
}
