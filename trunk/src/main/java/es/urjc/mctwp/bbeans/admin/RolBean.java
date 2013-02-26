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
import es.urjc.mctwp.modelo.Action;
import es.urjc.mctwp.modelo.Rol;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.AddRolAuthorization;
import es.urjc.mctwp.service.commands.adminCmds.FindAllActions;
import es.urjc.mctwp.service.commands.adminCmds.FindRol;
import es.urjc.mctwp.service.commands.adminCmds.LoadRol;
import es.urjc.mctwp.service.commands.adminCmds.RemoveRolAuthorization;
import es.urjc.mctwp.service.commands.adminCmds.SaveRol;

public class RolBean extends RequestAdmAbstractBean {
	private Rol fSrcObject = new Rol();
	private Rol fEdcObject = new Rol();
	private Rol rol = new Rol();
	private List<Rol> rolesList = null;
	private Integer idActionSelected = null;
	private List<SelectItem> notAssignedActions = new ArrayList<SelectItem>();
	
	public Rol getFSrcObject() {return fSrcObject;}
	public void setFSrcObject(Rol srcObject) {
		fSrcObject = srcObject;
	}
	
	public Rol getFEdcObject() {return fEdcObject;}
	public void setFEdcObject(Rol edcObject) {
		fEdcObject = edcObject;
	}
	
	public Rol getRol() {return rol;}
	public void setRol(Rol rol) {
		this.rol = rol;
	}
	
	public Integer getIdActionSelected() {return idActionSelected;}
	public void setIdActionSelected(Integer ac) {
		this.idActionSelected = ac;
	}
	
	public List<Rol> getRolesList() {return rolesList;}
	public void setRolesList(List<Rol> usersList) {
		this.rolesList = usersList;
	}
	
	public List<SelectItem> getNotAssignedActions(){return notAssignedActions;}
	public void setNotAssignedActions(List<SelectItem> list){
		this.notAssignedActions = list;
	}
	
	/**
	 * It retrieve rol from ActionEvent and update it to do
	 * proper authorization management
	 *  
	 * @param event
	 */
	public void alRolSelected(ActionEvent event) {
		Rol rol = (Rol)selectRowFromEvent(event, Rol.class);

		if(rol != null){
			Command cmd = getCommand(LoadRol.class);
			((LoadRol)cmd).setRolCode(rol.getCode());
			cmd = runCommand(cmd);
			this.rol = ((LoadRol)cmd).getResult();
		}
	}	

	/**
	 * Search roles by criteria and returns valid list
	 * 
	 * @return
	 */
	public String accFindRol(){

		//Prepare for search by criteria
		fSrcObject.setOid(null);
		fSrcObject.setCode(null);

		Command cmd = getCommand(FindRol.class);
		((FindRol)cmd).setRol(fSrcObject);
		cmd = runCommand(cmd);
		rolesList = ((FindRol)cmd).getResult();
		
		return ActionBeanNames.SEARCH_ROLES;
	}
	
	public String accEditRol(){
		return ActionBeanNames.EDIT_ROL;
	}
	
	/**
	 * Reattach rol to session in order to view his participations,
	 * and retrieves actions not assigned to rol
	 * 
	 * @return
	 */
	public String accViewRolAuthorizations(){
		List<Action> auxList = null;
		
		//Reload rol to show authorizations
		Command cmd = getCommand(LoadRol.class);
		((LoadRol)cmd).setRolCode(rol.getCode());
		runCommand(cmd);
		rol = ((LoadRol)cmd).getResult();
		
		//Get all available actions
		cmd = getCommand(FindAllActions.class);
		cmd = runCommand(cmd);
		auxList = ((FindAllActions)cmd).getResult();
		
		if(auxList != null){
			notAssignedActions = new ArrayList<SelectItem>();
			for(Action action : auxList)
				if(!rol.getActions().contains(action))
					notAssignedActions.add(new SelectItem(action.getCode(), action.getName()));
		}		

		return ActionBeanNames.VIEW_ROL_AUTHS;
	}
	
	/**
	 * Save or creates an rol
	 * 
	 * @return
	 */
	public String accSaveRol(){
		Command cmd = getCommand(SaveRol.class);
		((SaveRol)cmd).setRol(fEdcObject);
		runCommand(cmd);
		
		return accFindRol();
	}
	
	/**
	 * Adds a new authorization to the rol
	 * 
	 * @return
	 */
	public String accAddAuthorizationRol(){
		Command cmd = getCommand(AddRolAuthorization.class);
		((AddRolAuthorization)cmd).setActionId(idActionSelected);
		((AddRolAuthorization)cmd).setRol(rol);
		runCommand(cmd);
		
		return accViewRolAuthorizations();
	}
	
	/**
	 * Delete an specified authorization of a rol
	 * 
	 * @return
	 */
	public String accDelAuthorizationRol(){
		Command cmd = getCommand(RemoveRolAuthorization.class);
		((RemoveRolAuthorization)cmd).setActionId(idActionSelected);
		((RemoveRolAuthorization)cmd).setRol(rol);
		runCommand(cmd);

		return accViewRolAuthorizations();
	}	
}
