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

package es.urjc.mctwp.bbeans.research;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.Modality;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.LoadUser;
import es.urjc.mctwp.service.commands.researchCmds.AddUserModality;
import es.urjc.mctwp.service.commands.researchCmds.FindNotAssignedModalities;
import es.urjc.mctwp.service.commands.researchCmds.LoadParticipationsOfTrial;
import es.urjc.mctwp.service.commands.researchCmds.RemoveUserModality;

public class ModalityBean extends RequestInvAbstractBean {
	private List<SelectItem> modalities = null;
	private Integer modalitySelected = null;
	private List<SelectItem> users = null;
	private Integer userSelected = null;
	private User user = null;
	
	public List<SelectItem> getModalities() {
		return modalities;
	}
	public void setModalities(List<SelectItem> modalities) {
		this.modalities = modalities;
	}
	public Integer getModalitySelected() {
		return modalitySelected;
	}
	public void setModalitySelected(Integer modalitySelected) {
		this.modalitySelected = modalitySelected;
	}
	public Integer getUserSelected() {
		return userSelected;
	}
	public void setUserSelected(Integer userSelected) {
		this.userSelected = userSelected;
	}
	public List<SelectItem> getUsers() {
		return users;
	}
	public void setUsers(List<SelectItem> users) {
		this.users = users;
	}	
	public void setUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	
	public String accPrepareUsers(){
		//Get possible users
		Command cmd = getCommand(LoadParticipationsOfTrial.class);
		cmd = runCommand(cmd);
		Iterator<Participation> it = ((LoadParticipationsOfTrial)cmd).getResult();

		if(it != null){
			users = new ArrayList<SelectItem>();
			
			while(it.hasNext()){
				User u = it.next().getUser();
				users.add(new SelectItem(u.getCode(), u.getFullName()));
			}
		}		
		
		return ActionBeanNames.VIEW_USER_MODALITIES;
	}
	
	public String accPrepareModalities(){
		if(user == null){
			Command cmd = getCommand(LoadUser.class);
			((LoadUser)cmd).setUserCode(userSelected);
			cmd = runCommand(cmd);
			user = ((LoadUser)cmd).getResult();
		}
		
		Command cmd = getCommand(FindNotAssignedModalities.class);
		((FindNotAssignedModalities)cmd).setUserObj(user);
		cmd = runCommand(cmd);
		List<Modality> aux = ((FindNotAssignedModalities)cmd).getResult();
		
		if(aux != null){
			modalities = new ArrayList<SelectItem>();
			
			for(Modality modality : aux){
				modalities.add(new SelectItem(modality.getCode(), modality.getName()));
			}
		}
		
		return ActionBeanNames.VIEW_USER_MODALITIES;
	}
	
	public String accAddUserModality(){
		Command cmd = getCommand(AddUserModality.class);
		((AddUserModality)cmd).setModality(modalitySelected);
		((AddUserModality)cmd).setUserObj(user);
		cmd = runCommand(cmd);
		
		return accPrepareModalities();
	}
	
	public String accDelUserModality(){
		Command cmd = getCommand(RemoveUserModality.class);
		((RemoveUserModality)cmd).setModality(modalitySelected);
		((RemoveUserModality)cmd).setUserObj(user);
		cmd = runCommand(cmd);
		
		return accPrepareModalities();
	}
	
	public String accDeleteUserSelected(){
		user = null;
		return ActionBeanNames.VIEW_USER_MODALITIES;
	}
}
