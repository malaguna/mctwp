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
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import es.urjc.mctwp.bbeans.GenericDownloadBean;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.MultipleImage;
import es.urjc.mctwp.image.objects.SingleImage;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.service.commands.imageCmds.FindImagesByStudy;
import es.urjc.mctwp.service.commands.imageCmds.FindImagesByTask;
import es.urjc.mctwp.service.commands.imageCmds.LoadImage;

public class DownloadBean extends GenericDownloadBean {
	private final static String contentType = "zip";
	private String imageId = null;
	
	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String accDownloadImage(){
		
		if(imageId != null && !imageId.isEmpty()){
			Trial trial = getSession().getTrial();
			
			try{
				if(trial != null){
					HttpServletResponse response = prepareResponse();  
	
					//Get Image
					LoadImage cmd = (LoadImage) getCommand(LoadImage.class);
					cmd.setCollection(trial.getCollection());
					cmd.setImageId(imageId);
					cmd = (LoadImage)runCommand(cmd);
					Image img = cmd.getResult();

					//Get file name without parents and prepare header
					String fileName = imageId + "." + contentType;
					configResponseHeader(response, contentType, fileName);
					
					//Prepare ZipOutputStream and writes files into
					ZipOutputStream zos = null;
					zos = new ZipOutputStream(response.getOutputStream());
					addImageToZos(zos, img, null);
					zos.close();
					
					completeResponse();
				}
			} catch (Exception e) {
				setErrorMessage(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
				
		return null;
	}
	
	public String accDownloadStudyImages(){
		Study std = getSession().getStudy();
		
		if(std != null){
			//Get Images of Study
			FindImagesByStudy cmd = (FindImagesByStudy) getCommand(FindImagesByStudy.class);
			cmd.setStudy(std);
			cmd = (FindImagesByStudy)runCommand(cmd);

			downloadImages(cmd.getResult(), std.getDescription(), std.getCode().toString());
		}
		
		return null;
	}
	
	public String accDownloadTaskImages(){
		Task tsk = getSession().getTask();
		
		if(tsk != null){
			//Get Images of Task
			FindImagesByTask cmd = (FindImagesByTask) getCommand(FindImagesByTask.class);
			cmd.setTask(tsk);
			cmd = (FindImagesByTask)runCommand(cmd);

			downloadImages(cmd.getResult(), "Task-" + tsk.getCode().toString(), tsk.getCode().toString());
		}
		
		return null;
	}
	
	private void downloadImages(List<ImageData> images, String cntDesc, String cntCode){

		if(images != null && !images.isEmpty()){
			
			try{
				HttpServletResponse response = prepareResponse();  
	
				//Get file name without parents and prepare header
				String fileName = cntDesc + "." + contentType;
				configResponseHeader(response, contentType, fileName);

				//Prepare ZipOutputStream and writes files into
				ZipOutputStream zos = null;
				zos = new ZipOutputStream(response.getOutputStream());
				
				for(ImageData imdt : images){
					LoadImage cmd = (LoadImage) getCommand(LoadImage.class);
					cmd.setCollection(getSession().getTrial().getCollection());
					cmd.setImageId(imdt.getImageId());
					cmd = (LoadImage)runCommand(cmd);
					
					if(cmd != null && cmd.getResult() != null)
						addImageToZos(zos, cmd.getResult(), cntCode);
				}
				
				zos.close();
				completeResponse();
			} catch (Exception e) {
				setErrorMessage(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void addImageToZos(ZipOutputStream zos, Image img, String path) throws IOException{
		
		if(img != null){
			if(path == null)
				path = "";
			
			if(img instanceof SingleImage)
				addSingleImageToZos(zos, (SingleImage) img, path);
			
			else if(img instanceof MultipleImage){
				MultipleImage serie = (MultipleImage)img;
				
				if(serie.getImages() != null)
					for(Image i : serie.getImages())
						addImageToZos(zos, i, path + "/" + serie.getId());
			}
		}
	}
	
	private void addSingleImageToZos(ZipOutputStream zos, SingleImage si, String path) throws IOException{
		
		//Prepare entry path
		String entry = null;		
		if(path != null && !path.isEmpty())
			entry = path + "/" + si.getContent().getName();
		else
			entry = si.getContent().getName();
		
		//Create zip entry
		ZipEntry ze = new ZipEntry(entry);
		zos.putNextEntry(ze);

		//Write zip entry
		byte bytes[] = new byte[BUFFER_SIZE];
		FileInputStream fileIS = new FileInputStream(si.getContent());;
		while(fileIS.read(bytes) != -1)
			zos.write(bytes);
		
		fileIS.close();
	}
}