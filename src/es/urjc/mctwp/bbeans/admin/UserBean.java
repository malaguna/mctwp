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
import es.urjc.mctwp.modelo.Log;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.Rol;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.AddUserParticipation;
import es.urjc.mctwp.service.commands.adminCmds.FindAllRoles;
import es.urjc.mctwp.service.commands.adminCmds.FindLogsByUser;
import es.urjc.mctwp.service.commands.adminCmds.FindNotAssignedTrials;
import es.urjc.mctwp.service.commands.adminCmds.FindUser;
import es.urjc.mctwp.service.commands.adminCmds.LoadUser;
import es.urjc.mctwp.service.commands.adminCmds.RemoveUserParticipation;
import es.urjc.mctwp.service.commands.adminCmds.SaveUser;

/**
 * 
 * @author miguel
 *
 */
public class UserBean extends RequestAdmAbstractBean{
	private User fSrcObject = new User();
	private User fEdcObject = new User();
	private User user = new User();
	private List<User> usersList = null;
	private List<Log> logsByUser = null;
	private Integer rolSelected = null;
	private Integer trialSelected = null;
	private Participation partSelected = null;
	private List<SelectItem> allRoles = new ArrayList<SelectItem>();
	private List<SelectItem> notAssignedTrials = new ArrayList<SelectItem>();
	
	public User getUser() {return user;}
	public void setUser(User obj) {
		this.user = obj;
	}

	public User getFSrcObject() {return fSrcObject;}
	public void setFSrcObject(User object) {
		fSrcObject = object;
	}
	public User getFEdcObject() {return fEdcObject;}
	public void setFEdcObject(User object) {
		fEdcObject = object;
	}
	
	public List<User> getUsersList() {return usersList;}
	public void setUsersList(List<User> obj) {
		usersList = obj;
	}

	public Integer getRolSelected() {return rolSelected;}
	public void setRolSelected(Integer rolSelected) {
		this.rolSelected = rolSelected;
	}
	
	public Integer getTrialSelected() {return trialSelected;}
	public void setTrialSelected(Integer trialSelected) {
		this.trialSelected = trialSelected;
	}
	
	public Participation getPartSelected() {return partSelected;}
	public void setPartSelected(Participation partSelected) {
		this.partSelected = partSelected;
	}
	
	public List<Log> getLogsByUser(){return logsByUser;}
	public void setLogsByUser(List<Log> list){
		this.logsByUser = list;
	}

	public List<SelectItem> getAllRoles(){return allRoles;}
	public void setAllRoles(List<SelectItem> list){
		this.allRoles = list;
	}

	public List<SelectItem> getNotAssignedTrials(){return notAssignedTrials;}	
	public void setNotAssignedTrials(List<SelectItem> list){
		this.notAssignedTrials = list;
	}
	
	/**
	 * It retrieve user from ActionEvent and update it to do
	 * proper participations management
	 *  
	 * @param event
	 */
	public void alUserSelected(ActionEvent event) {
		User user = (User)selectRowFromEvent(event, User.class);

		if(user != null){
			Command cmd = getCommand(LoadUser.class);
			((LoadUser)cmd).setUserCode(user.getCode());
			cmd = runCommand(cmd);
			this.user = ((LoadUser)cmd).getResult();
		}
	}	

	/**
	 * It prepares the list of user logs.
	 * 
	 * @param event
	 */
	public void alScrollTable(ActionEvent event){
		Command cmd = getCommand(FindLogsByUser.class);
		((FindLogsByUser)cmd).setUserObj(user);
		cmd = runCommand(cmd);
		logsByUser = ((FindLogsByUser)cmd).getResult();
	}

	/**
	 * Search a user by criteria and returns valid list
	 * 
	 * @return
	 */
	public String accFindUser(){
		List<User> auxList = null;
		
		//Prepare for search by criteria
		fSrcObject.setOid(null);
		fSrcObject.setCode(null);
		fSrcObject.setDate(null);
		
		Command cmd = getCommand(FindUser.class);
		((FindUser)cmd).setUserObj(fSrcObject);
		cmd = runCommand(cmd);
		auxList = ((FindUser)cmd).getResult();
		
		if(auxList != null){
			usersList = new ArrayList<User>();
			for(User user : auxList)
				usersList.add(user);
		}
		
		return ActionBeanNames.SEARCH_USERS;
	}
		
	public String accEditUser(){
		return ActionBeanNames.EDIT_USER;
	}
	
	/**
	 * Reattach user to session in order to view his participations
	 * 
	 * @return
	 */
	public String accViewParticipations(){
		List<Rol> auxRolList = null;
		List<Trial> auxTrialList = null;
		List<Trial> assigned = new ArrayList<Trial>();
		
		//Get all assignable trials
		Command cmd = getCommand(FindNotAssignedTrials.class);
		((FindNotAssignedTrials)cmd).setUserObj(user);
		cmd = runCommand(cmd);
		auxTrialList = ((FindNotAssignedTrials)cmd).getResult();
		
		if(auxTrialList != null){
			notAssignedTrials = new ArrayList<SelectItem>();
			for(Trial trial : auxTrialList)
				if(!assigned.contains(trial))
					notAssignedTrials.add(new SelectItem(trial.getCode(), trial.getName()));
		}		
		
		//Get all roles
		cmd = getCommand(FindAllRoles.class);
		cmd = runCommand(cmd);
		auxRolList = ((FindAllRoles)cmd).getResult();
		
		if(auxRolList != null){
			allRoles = new ArrayList<SelectItem>();
			for(Rol rol : auxRolList)
				allRoles.add(new SelectItem(rol.getCode(), rol.getName()));
		}
		
		return ActionBeanNames.VIEW_USER_PRATICIP;
	}
	
	/**
	 * Adds a new participacion to the user in a specified trial
	 * using a specified rol
	 * 
	 * @return
	 */
	public String accAddParticipationUser(){
		Command cmd = getCommand(AddUserParticipation.class);
		((AddUserParticipation)cmd).setRolId(rolSelected);
		((AddUserParticipation)cmd).setTrialId(trialSelected);
		((AddUserParticipation)cmd).setUserObj(user);
		runCommand(cmd);
		
		return accViewParticipations();
	}
	
	/**
	 * Delete an specified participation of a user
	 * 
	 * @return
	 */
	public String accDelParticipationUser(){
		Command cmd = getCommand(RemoveUserParticipation.class);
		((RemoveUserParticipation)cmd).setParticipation(partSelected);
		((RemoveUserParticipation)cmd).setUserObj(user);
		runCommand(cmd);

		return accViewParticipations();
	}
	
	/**
	 * Prepares all user logs.
	 * 
	 * @return
	 */
	public String accViewLogs(){
		alScrollTable(null);
		return ActionBeanNames.VIEW_USER_LOGS;
	}
	
	/**
	 * Save or creates an user
	 * 
	 * @return
	 */
	public String accSaveUser(){
		Command cmd = getCommand(SaveUser.class);
		((SaveUser)cmd).setUserObj(fEdcObject);
		runCommand(cmd);

		return accFindUser();
	}
}
