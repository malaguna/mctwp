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

public class AddUserParticipation extends Command {
	private TrialDAO trialDao = null;
	private UserDAO userDao = null;
	private RolDAO rolDao = null;
	private Integer trialId = null;
	private Integer rolId = null;
	private User userObj = null;

	public AddUserParticipation(BeanFactory bf) {
		super(bf);
		trialDao = (TrialDAO)bf.getBean(BeanNames.TRIAL_DAO);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);
		rolDao = (RolDAO)bf.getBean(BeanNames.ROL_DAO);
		setAction(ActionNames.ADD_USER_PART);
		setReadOnly(false);
	}
	
	public void setUserObj(User userObj) {
		this.userObj = userObj;
	}
	public User getUserObj() {
		return userObj;
	}
	public void setRolId(Integer rolId) {
		this.rolId = rolId;
	}
	public Integer getRolId() {
		return rolId;
	}
	public Integer getTrialId() {
		return trialId;
	}
	public void setTrialId(Integer userId) {
		this.trialId = userId;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				rolId != null &&
				userObj != null &&
				trialId != null &&
				trialDao != null &&
				userDao != null &&
				rolDao != null;
	}

	@Override
	public Command runCommand(){
		Participation result = null;
		Trial trial = null;

		if( (rolId != null) && (trialId != null) ){
			Rol rol = rolDao.findById(rolId);
			trial = trialDao.findById(trialId);
			
			result = new Participation();
			result.setRol(rol);
			result.setTrial(trial);
			userObj.addParticipation(result);
		
			userDao.persist(userObj);
		}
		
		createLogComment("audit.addUserPart", userObj.getFullName(), trial.getName());
		return this;
	}
}
