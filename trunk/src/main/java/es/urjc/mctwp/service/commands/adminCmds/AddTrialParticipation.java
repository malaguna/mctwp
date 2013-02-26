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

package es.urjc.mctwp.service.commands.adminCmds;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.RolDAO;
import es.urjc.mctwp.dao.TrialDAO;
import es.urjc.mctwp.dao.UserDAO;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.Rol;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class AddTrialParticipation extends Command {
	private TrialDAO trialDao = null;
	private UserDAO userDao = null;
	private RolDAO rolDao = null;
	private Integer userId = null;
	private Integer rolId = null;
	private Trial trial = null;

	public AddTrialParticipation(BeanFactory bf) {
		super(bf);
		trialDao = (TrialDAO)bf.getBean(BeanNames.TRIAL_DAO);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);
		rolDao = (RolDAO)bf.getBean(BeanNames.ROL_DAO);
		setActionName(ActionNames.ADD_TRIAL_PART);
		setReadOnly(false);
	}
	
	public void setTrial(Trial trial){
		this.trial = trial;
	}
	public Trial getTrial(){
		return trial;
	}
	public void setRolId(Integer rolId) {
		this.rolId = rolId;
	}
	public Integer getRolId() {
		return rolId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				rolId != null &&
				trial != null &&
				userId != null &&
				trialDao != null &&
				userDao != null &&
				rolDao != null;
	}

	@Override
	public Command runCommand() {
		Participation result = null;
		User user = null;

		if( (rolId != null) && (userId != null) ){
			Rol rol = rolDao.findById(rolId);
			user = userDao.findById(userId);
			
			result = new Participation();
			result.setRol(rol);
			result.setUser(user);
			trial.addMember(result);
		
			trialDao.persist(trial);
		}
		
		createLogComment("audit.addTrialPart", trial.getName(), user.getFullName());
		return this;
	}
}
