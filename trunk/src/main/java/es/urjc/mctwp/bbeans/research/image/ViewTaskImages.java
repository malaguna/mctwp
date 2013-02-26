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
import es.urjc.mctwp.service.commands.imageCmds.FindImagesByTask;

/**
 * 
 * @author miguel
 *
 */
public class ViewTaskImages extends AbstractViewImages {

	public String accListImagesOfTask(){
		populateImageList();
		return ActionBeanNames.LIST_IMAGES_TASK;
	}
	
	public String accSendImages(){
		sendImages();
		return accListImagesOfTask();
	}
	
	/**
	 * Gets all images of a task and store it into thumbs list
	 */
	private void populateImageList(){
		List<ImageData> aux;
		getSession().cleanUserTempDirectory();
		
		if(getSession().getTask() != null){
			setThumbs(new ArrayList<ThumbSelectItem>());

			Command cmd = getCommand(FindImagesByTask.class);
			((FindImagesByTask)cmd).setTask(getSession().getTask());
			cmd = runCommand(cmd);
			aux = ((FindImagesByTask)cmd).getResult();

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
