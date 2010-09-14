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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.GenericDAO;
import es.urjc.mctwp.dao.TrialDAO;
import es.urjc.mctwp.dao.UserDAO;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindNotAssignedUsers extends ResultCommand<List<User>> {
	private TrialDAO trialDao = null;
	private UserDAO userDao = null;
	private Trial trialObj = null;
	
	public FindNotAssignedUsers(BeanFactory bf) {
		super(bf);
		trialDao = (TrialDAO)bf.getBean(BeanNames.TRIAL_DAO);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);
		setActionName(ActionNames.FIND_NOT_ASGN_USERS);
	}

	public void setTrialObj(Trial trialObj) {
		this.trialObj = trialObj;
	}
	public Trial getTrialObj() {
		return trialObj;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				trialDao != null &&
				trialObj != null &&
				userDao != null;
	}
	
	@Override
	public ResultCommand<List<User>> runCommand() throws Exception {
		List<User> auxList = null;
		List<User> assigned = new ArrayList<User>();

		trialDao.reattach(trialObj, GenericDAO.DIRTY_IGNORE);
		
		if(trialObj.getMembers() != null)
			for(Participation p : trialObj.getMembers())
				assigned.add(p.getUser());
		
		auxList = userDao.findAll();
		
		if(auxList != null){
			this.setResult(new ArrayList<User>());
			for(User user : auxList)
				if(!assigned.contains(user))
					this.getResult().add(user);
		}		
		
		return this;
	}

}
