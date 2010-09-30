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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import es.urjc.mctwp.bbeans.GenericDownloadBean;
import es.urjc.mctwp.image.objects.ComplexImage;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.image.objects.SeriesImage;
import es.urjc.mctwp.image.objects.SingleImage;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.service.commands.imageCmds.FindImagesByStudy;
import es.urjc.mctwp.service.commands.imageCmds.FindImagesByTask;
import es.urjc.mctwp.service.commands.imageCmds.LoadImage;
import es.urjc.mctwp.service.commands.researchCmds.LoadPatient;

public class DownloadBean extends GenericDownloadBean {
	private final static String patientHeader = "Pat-";
	private final static String studyHeader = "Std-";
	private final static String taskHeader = "Tsk-";
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
					ZipOutputStream zos = prepareZOS(imageId);
	
					//Get Image
					LoadImage cmd = (LoadImage) getCommand(LoadImage.class);
					cmd.setCollection(trial.getCollection());
					cmd.setImageId(imageId);
					cmd = (LoadImage)runCommand(cmd);
					Image img = cmd.getResult();

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
		Study study = getSession().getStudy();
		
		try {
			if(study != null){
				ZipOutputStream zos = prepareZOS(studyHeader + study.getDescription());
				downloadStudyImages(study, zos, null);
				if(zos != null) zos.close();
				completeResponse();
			}
		} catch (IOException e) {
			setErrorMessage(e.getLocalizedMessage());
			e.printStackTrace();
		}

		return null;
	}
	
	public String accDownloadPatientImages(){
		LoadPatient cmd = (LoadPatient)getCommand(LoadPatient.class);
		cmd.setPatientId(getSession().getPatient().getCode());
		cmd = (LoadPatient) runCommand(cmd);
		
		if(cmd != null && cmd.getResult() != null){
			Patient patient = cmd.getResult();
			
			try {
				if(patient.getStudies() != null){
					ZipOutputStream zos = prepareZOS(patientHeader + patient.getCode());
					downloadPatienImages(patient, zos, null);
					if(zos != null) zos.close();
					completeResponse();
				}
			} catch (IOException e) {
				setErrorMessage(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public String accDownloadTaskImages(){
		Task tsk = getSession().getTask();
		
		try {
			if(tsk != null){
				
				//Get images of task
				FindImagesByTask cmd = (FindImagesByTask) getCommand(FindImagesByTask.class);
				cmd.setTask(tsk);
				cmd = (FindImagesByTask)runCommand(cmd);
				
				if(cmd != null && cmd.getResult() != null){
					ZipOutputStream zos = prepareZOS(taskHeader + tsk.getCode());
					downloadImages(cmd.getResult(), zos, taskHeader + tsk.getCode());
					if(zos != null) zos.close();
					completeResponse();
				}
			}
		} catch (IOException e) {
			setErrorMessage(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void downloadPatienImages(Patient patient, ZipOutputStream zos, String path){
		if(patient.getStudies() != null){
			
			if(path != null && !path.isEmpty())
				path += File.separator + patientHeader + patient.getCode().toString();
			else
				path = patientHeader + patient.getCode().toString();

			for(Study study : patient.getStudies())
				downloadStudyImages(study, zos, patientHeader + patient.getCode());
		}
	}
	
	private void downloadStudyImages(Study study, ZipOutputStream zos, String path){
		FindImagesByStudy cmd = (FindImagesByStudy) getCommand(FindImagesByStudy.class);
		cmd.setStudy(study);
		cmd = (FindImagesByStudy)runCommand(cmd);
		
		if(path != null && !path.isEmpty())
			path += File.separator + studyHeader + study.getCode().toString();
		else
			path = studyHeader + study.getCode().toString();
		
		downloadImages(cmd.getResult(), zos, path);
	}
	
	private void downloadImages(List<ImageData> images, ZipOutputStream zos, String path){

		if(images != null && zos != null && !images.isEmpty()){
			
			try{				
				for(ImageData imdt : images){
					LoadImage cmd = (LoadImage) getCommand(LoadImage.class);
					cmd.setCollection(getSession().getTrial().getCollection());
					cmd.setImageId(imdt.getImageId());
					cmd = (LoadImage)runCommand(cmd);
					
					if(cmd != null && cmd.getResult() != null)
						addImageToZos(zos, cmd.getResult(), path);
				}
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
				addFileToZos(zos, ((SingleImage)img).getContent(), path);
			
			else if(img instanceof SeriesImage){
				SeriesImage serie = (SeriesImage)img;
				
				if(serie.getImages() != null)
					for(Image i : serie.getImages())
						addImageToZos(zos, i, path + File.separator + serie.getId());
			}
			
			else if(img instanceof ComplexImage){
				ComplexImage complex = (ComplexImage)img;
				
				if(complex.getContent() != null)
					for(File file : complex.getContent())
						addFileToZos(zos, file, path + File.separator + complex.getId());
			}
		}
	}
	
	private void addFileToZos(ZipOutputStream zos, File file, String path) throws IOException{
		
		//Prepare entry path
		String entry = null;		
		if(path != null && !path.isEmpty())
			entry = path + "/" + file.getName();
		else
			entry = file.getName();
		
		//Create zip entry
		ZipEntry ze = new ZipEntry(entry);
		zos.putNextEntry(ze);

		//Write zip entry
		byte bytes[] = new byte[BUFFER_SIZE];
		FileInputStream fileIS = new FileInputStream(file);
		while(fileIS.read(bytes) != -1)
			zos.write(bytes);
		
		zos.flush();
		fileIS.close();		
	}
	
	private ZipOutputStream prepareZOS(String cntDesc) throws IOException{
		ZipOutputStream result = null;
		HttpServletResponse response = prepareResponse();  
		
		//Get file name without parents and prepare header
		String fileName = cntDesc + FilenameUtils.EXTENSION_SEPARATOR_STR + contentType;
		configResponseHeader(response, contentType, fileName);
		result = new ZipOutputStream(response.getOutputStream());
		
		return result;
	}	
}
