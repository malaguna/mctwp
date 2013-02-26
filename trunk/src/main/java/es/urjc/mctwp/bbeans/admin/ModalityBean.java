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

import java.util.List;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestAdmAbstractBean;
import es.urjc.mctwp.modelo.Modality;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.FindModality;
import es.urjc.mctwp.service.commands.adminCmds.SaveModality;

public class ModalityBean extends RequestAdmAbstractBean {
	private Modality fSrcObject = new Modality();
	private Modality fEdcObject = new Modality();
	private Modality modality = new Modality();
	private List<Modality> modalitiesList = null;

	public Modality getFSrcObject() {
		return fSrcObject;
	}

	public void setFSrcObject(Modality srcObject) {
		fSrcObject = srcObject;
	}

	public Modality getFEdcObject() {
		return fEdcObject;
	}

	public void setFEdcObject(Modality edcObject) {
		fEdcObject = edcObject;
	}

	public Modality getModality() {
		return modality;
	}

	public void setModality(Modality modality) {
		this.modality = modality;
	}

	public List<Modality> getModalitiesList() {
		return modalitiesList;
	}

	public void setModalitiesList(List<Modality> modalitiesList) {
		this.modalitiesList = modalitiesList;
	}

	/**
	 * Search modalities by criteria and returns valid list
	 * 
	 * @return
	 */
	public String accFindModality(){

		//Prepare for search by criteria
		fSrcObject.setOid(null);
		fSrcObject.setCode(null);

		Command cmd = getCommand(FindModality.class);
		((FindModality)cmd).setModality(fSrcObject);
		cmd = runCommand(cmd);
		modalitiesList = ((FindModality)cmd).getResult();
		
		return ActionBeanNames.SEARCH_MODALITIES;
	}
	
	public String accEditModality(){
		return ActionBeanNames.EDIT_MODALITY;
	}
	
	/**
	 * Save or creates an modality
	 * 
	 * @return
	 */
	public String accSaveModality(){
		Command cmd = getCommand(SaveModality.class);
		((SaveModality)cmd).setModality(fEdcObject);
		runCommand(cmd);
		
		return accFindModality();
	}
}
