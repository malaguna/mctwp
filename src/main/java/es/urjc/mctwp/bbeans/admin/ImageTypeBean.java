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

package es.urjc.mctwp.bbeans.admin;

import java.util.ArrayList;
import java.util.List;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestAdmAbstractBean;
import es.urjc.mctwp.modelo.ImageType;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.FindImageType;
import es.urjc.mctwp.service.commands.adminCmds.SaveImageType;

public class ImageTypeBean extends RequestAdmAbstractBean {
	private ImageType fSrcObject = new ImageType();
	private ImageType fEdcObject = new ImageType();
	private ImageType imageType = new ImageType();
	private List<ImageType> imageTypeList = null;

	
	public ImageType getFSrcObject() {return fSrcObject;}
	public void setFSrcObject(ImageType srcObject) {
		fSrcObject = srcObject;
	}
	
	public ImageType getFEdcObject() {return fEdcObject;}
	public void setFEdcObject(ImageType edcObject) {
		fEdcObject = edcObject;
	}
	
	public ImageType getImageType() {return imageType;}
	public void setImageType(ImageType prc) {
		this.imageType = prc;
	}
	
	public List<ImageType> getImageTypeList() {return imageTypeList;}
	public void setImageTypeList(List<ImageType> lst) {
		this.imageTypeList = lst;
	}

	/**
	 * Search ImageTypes by criteria and returns valid list
	 * 
	 * @return
	 */
	public String accFindImageTypes(){
		List<ImageType> auxList = null;
		
		//Prepare for search by criteria
		fSrcObject.setOid(null);
		fSrcObject.setCode(null);
		
		Command cmd = getCommand(FindImageType.class);
		cmd.setActionName(ActionNames.FIND_IMAGETYPES);
		((FindImageType)cmd).setImageType(fSrcObject);
		cmd = runCommand(cmd);
		auxList = ((FindImageType)cmd).getResult();
		
		if(auxList != null){
			imageTypeList = new ArrayList<ImageType>();
			
			for(ImageType proc : auxList)
				imageTypeList.add(proc);
		}
		
		return ActionBeanNames.SEARCH_IMAGETYPES;
	}
	
	/**
	 * @return nextAction
	 */
	public String accEditImageType(){
		return ActionBeanNames.EDIT_IMAGETYPE;
	}
	
	/**
	 * Save or creates an ImageType
	 * 
	 * @return
	 */
	public String accSaveImageType(){
		
		Command cmd = getCommand(SaveImageType.class);
		((SaveImageType)cmd).setImageType(fEdcObject);
		cmd = runCommand(cmd);
		
		return accFindImageTypes();
	}
}
