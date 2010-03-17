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

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestAdmAbstractBean;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.Rol;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.AddTrialParticipation;
import es.urjc.mctwp.service.commands.adminCmds.FindAllRoles;
import es.urjc.mctwp.service.commands.adminCmds.FindNotAssignedUsers;
import es.urjc.mctwp.service.commands.adminCmds.FindTrial;
import es.urjc.mctwp.service.commands.adminCmds.LoadTrial;
import es.urjc.mctwp.service.commands.adminCmds.RemoveTrialParticipation;
import es.urjc.mctwp.service.commands.adminCmds.SaveTrial;

/**
 * 
 * @author miguel
 *
 */
public class TrialBean extends RequestAdmAbstractBean{
	private Trial fSrcObject = new Trial();
	private Trial fEdcObject = new Trial();
	private Trial trial = new Trial();
	private List<Trial> trialsList = null;
	private Integer rolSelected = null;
	private Integer userSelected = null;
	private Participation partSelected = null;
	private List<SelectItem> notAssignedUsers = new ArrayList<SelectItem>();
	private List<SelectItem> allRoles = new ArrayList<SelectItem>();
	
	public Trial getTrial() {return trial;}
	public void setTrial(Trial obj) {
		this.trial = obj;
	}

	public Trial getFSrcObject() {return fSrcObject;}
	public void setFSrcObject(Trial object) {
		fSrcObject = object;
	}
	public Trial getFEdcObject() {return fEdcObject;}
	public void setFEdcObject(Trial object) {
		fEdcObject = object;
	}
	
	public List<Trial> getTrialsList() {return trialsList;}
	public void setTrialsList(List<Trial> obj) {
		trialsList = obj;
	}

	public Integer getRolSelected() {return rolSelected;}
	public void setRolSelected(Integer rolSelected) {
		this.rolSelected = rolSelected;
	}
	
	public Integer getUserSelected() {return userSelected;}
	public void setUserSelected(Integer trialSelected) {
		this.userSelected = trialSelected;
	}
	
	public Participation getPartSelected() {return partSelected;}
	public void setPartSelected(Participation partSelected) {
		this.partSelected = partSelected;
	}

	public List<SelectItem> getAllRoles(){return allRoles;}
	public void setAllRoles(List<SelectItem> roles){
		this.allRoles = roles;
	}

	public List<SelectItem> getNotAssignedUsers(){return notAssignedUsers;}
	public void setNotAssignedUsers(List<SelectItem> list){
		this.notAssignedUsers = list;
	}

	/**
	 * It retrieve trial from ActionEvent and update it to do
	 * proper participations management
	 *  
	 * @param event
	 */
	public void alTrialSelected(ActionEvent event) {
		Trial trial = (Trial)selectRowFromEvent(event, Trial.class);

		if(trial != null){
			Command cmd = getCommand(LoadTrial.class);
			((LoadTrial)cmd).setTrialCode(trial.getCode());
			cmd = runCommand(cmd);
			this.trial = ((LoadTrial)cmd).getResult();
		}
	}	

	/**
	 * Search a trial by criteria and returns valid list
	 * 
	 * @return
	 */
	public String accFindTrial(){
		List<Trial> auxList = null;
		
		//Prepare for search by criteria
		fSrcObject.setOid(null);
		fSrcObject.setCode(null);
		
		Command cmd = getCommand(FindTrial.class);
		((FindTrial)cmd).setTrial(fSrcObject);
		cmd = runCommand(cmd);
		auxList = ((FindTrial)cmd).getResult();
		
		if(auxList != null){
			trialsList = new ArrayList<Trial>();
			for(Trial trial : auxList)
				trialsList.add(trial);
		}
		
		return ActionBeanNames.SEARCH_TRIALS;
	}
	
	/**
	 * Retrieve all roles and not assigned users, and prepares to edit
	 * all memebrs participations.
	 * 
	 * @return
	 */
	public String accViewMembers(){
		List<Rol> auxRolList = null;
		List<User> auxUserList = null;
		
		//Get all roles
		Command cmd = getCommand(FindAllRoles.class);
		cmd = runCommand(cmd);
		auxRolList = ((FindAllRoles)cmd).getResult();
		
		if(auxRolList != null){
			allRoles = new ArrayList<SelectItem>();
			for(Rol rol : auxRolList)
				allRoles.add(new SelectItem(rol.getCode(), rol.getName()));
		}
		
		//Get all available users
		cmd = getCommand(FindNotAssignedUsers.class);
		((FindNotAssignedUsers)cmd).setTrialObj(trial);
		cmd = runCommand(cmd);
		auxUserList = ((FindNotAssignedUsers)cmd).getResult();
		
		if(auxUserList != null){
			notAssignedUsers = new ArrayList<SelectItem>();
			for(User user : auxUserList)
					notAssignedUsers.add(new SelectItem(user.getCode(), user.getFullName()));
		}
						
		return ActionBeanNames.VIEW_TRIAL_MEMBERS;
	}

	/**
	 * Adds a new participacion to the trial 
	 * 
	 * @return
	 */
	public String accAddParticipationTrial(){
		Command cmd = getCommand(AddTrialParticipation.class);
		((AddTrialParticipation)cmd).setRolId(rolSelected);
		((AddTrialParticipation)cmd).setUserId(userSelected);
		((AddTrialParticipation)cmd).setTrial(trial);
		runCommand(cmd);
				
		return accViewMembers();
	}
	
	/**
	 * Delete an specified participation of a trial
	 * 
	 * @return
	 */
	public String accDelParticipationTrial(){
		Command cmd = getCommand(RemoveTrialParticipation.class);
		((RemoveTrialParticipation)cmd).setParticipation(partSelected);
		((RemoveTrialParticipation)cmd).setTrial(trial);
		runCommand(cmd);

		return accViewMembers();
	}
	
	public String accEditTrial(){
		return ActionBeanNames.EDIT_TRIAL;
	}	
	
	/**
	 * Save or creates an trial
	 * 
	 * @return
	 */
	public String accSaveTrial(){
		Command cmd = getCommand(SaveTrial.class);
		((SaveTrial)cmd).setTrial(fEdcObject);
		runCommand(cmd);
		
		return accFindTrial();
	}
}
