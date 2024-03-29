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


import java.util.ArrayList;
import java.util.List;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.imageCmds.FindImagesByStudy;

/**
 * 
 * @author miguel
 *
 */
public class ViewStudyImages extends AbstractViewImages {
	
	/**
	 * Retrieves images of a study
	 * 
	 * @return
	 */
	public String accListImagesOfStudy(){
		populateImageList();
		return ActionBeanNames.LIST_IMAGES_STUDY;
	}
	
	/**
	 * It sends all selected images to destination
	 * @return
	 */
	public String accSendImages(){
		sendImages();
		
		return accListImagesOfStudy();
	}
	
	/**
	 * Gets all images of a study and store it into thumbs list
	 */
	private void populateImageList(){
		List<ImageData> aux;
		getSession().cleanUserTempDirectory();
		
		if(getSession().getStudy() != null){
			Command cmd = getCommand(FindImagesByStudy.class);
			((FindImagesByStudy)cmd).setStudy(getSession().getStudy());
			cmd = runCommand(cmd);
			aux = ((FindImagesByStudy)cmd).getResult();

			if(aux != null && !aux.isEmpty()){
				setThumbs(new ArrayList<ThumbSelectItem>());
				
				for(ImageData image : aux){
					try{
						getThumbs().add(getThumbnailContent(image));
					}catch (Exception e){
						setErrorMessage("Error al preparar thumbnails: " + e.getMessage());
					}
				}
			}
		}
	}	
}
