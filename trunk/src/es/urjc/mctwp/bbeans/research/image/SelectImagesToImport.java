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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.image.objects.ThumbNail;
import es.urjc.mctwp.modelo.ImageContainer;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.blogic.ImageContainerTypeVisitor;
import es.urjc.mctwp.service.commands.imageCmds.LoadThumbsOfTemporalImages;
import es.urjc.mctwp.service.commands.imageCmds.PersistImages;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public class SelectImagesToImport extends RequestInvAbstractBean {
	private List<ThumbSelectItem> thumbs = null;
	private String folder = null;
	
	@SuppressWarnings("unused")
	private boolean containerAPatient = false;
	@SuppressWarnings("unused")
	private boolean containerAStudy = false;
	
	@Override
	public void init(){}
	
	public List<ThumbSelectItem> getThumbs() {
		return thumbs;
	}
	
	public void setThumbs(List<ThumbSelectItem> thumbs) {
		this.thumbs = thumbs;
	}
	
	public String getFolder() {
		return folder;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}

	public boolean isContainerAPatient(){
		ImageContainer container = getProperContainer();

		return container != null && container.accept(ImageContainerTypeVisitor.getInstance())
			.equals(Patient.class);
	}

	public boolean isContainerAStudy(){
		ImageContainer container = getProperContainer();

		return container != null && container.accept(ImageContainerTypeVisitor.getInstance())
			.equals(Study.class);
	}
	
	public Patient getPatientCont(){
		Patient result = null;
		
		if(isContainerAPatient())
			result = (Patient)getProperContainer();
			
		return result;
	}

	public Study getStudyCont(){
		Study result = null;
		
		if(isContainerAStudy())
			result = (Study)getProperContainer();
			
		return result;
	}

	/**
	 * Prepares folder to list images
	 * 
	 * @return
	 */
	public String accListFolder(){
		if(folder != null)
			populateThumbNails();

		return ActionBeanNames.LIST_IMAGES_FOLDER;
	}
	
	/**
	 * Persist temporal selected images into a persistent
	 * collection. The collection is relative to study.
	 * 
	 * If there is task selected, images are stored as results, 
	 * in other case, they are stored as images of a study.
	 * 
	 * @return
	 */
	public String importImages(){
		List<String> images = new ArrayList<String>();
		
		//It must be at least a group selected, in order
		//to persist any temporal image.
		if(thumbs != null){
			
			//Get id's of selected images
			for(ThumbSelectItem tsi: thumbs){
				if(tsi.getSelected()){
					images.add(tsi.getThumbId());
				}
			}
			
			ImageContainer container = getProperContainer();
			
			//Always reattach trial
			//getFacadeService().cleanReattach(getSession().getTrial());
			Command cmd = getCommand(PersistImages.class);
			((PersistImages)cmd).setTempColl(folder);
			((PersistImages)cmd).setSource(container);
			((PersistImages)cmd).setImages(images);
			
			runCommand(cmd);		
		}

		setInfoMessage(getMessage("jsf.messages.ImagesPersisted"));
		
		return accListFolder();
	}
	
	/**
	 * Retrieve thumbnails from temp collection and stores its content 
	 * into web context user folder. It cleans previous images.
	 */
	private void populateThumbNails(){
		List<ThumbNail> aux = null;
		getSession().cleanUserTempDirectory();
				
		Command cmd = getCommand(LoadThumbsOfTemporalImages.class);
		((LoadThumbsOfTemporalImages)cmd).setTempColl(folder);
		cmd = runCommand(cmd);
		aux = ((LoadThumbsOfTemporalImages)cmd).getResult();
		
		if(aux != null){
			
			thumbs = new ArrayList<ThumbSelectItem>();
			for(ThumbNail tn : aux){

				//Copy content from collection to web context user folder
				try {
					FileUtils.copyFileToDirectory(tn.getContent(), getSession().getThumbDir());
					File th = new File(FilenameUtils.concat(getSession().getRelativeThumbDir(), tn.getContent().getName()));
					tn.setContent(th);
					
					ThumbSelectItem tsi = new ThumbSelectItem();
					tsi.setThumbId(tn.getId());
					tsi.setPath(tn.getContent().getPath());
					
					if(tn.getPatInfo() != null){
						tsi.setPatName(tn.getPatInfo().getName());
						tsi.setPatCode(tn.getPatInfo().getCode());
						tsi.setStdCode(tn.getPatInfo().getStudy());
					}
						
					thumbs.add(tsi);
				} catch (IOException e) {
				}
			}
		}
	}
	
	private ImageContainer getProperContainer(){
		ImageContainer container = null;
		
		//Get appropriate container to persist images
		if(getSession().getResult() != null)
			container = getSession().getResult();
		else if(getSession().getStudy() != null)
			container = getSession().getStudy();
		else if(getSession().getPatient() != null)
			container = getSession().getPatient();
		else 
			container = getSession().getGroup();

		return container;
	}
}
