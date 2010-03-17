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

public class FindNotAssignedTrials extends ResultCommand<List<Trial>> {
	private TrialDAO trialDao = null;
	private UserDAO userDao = null;
	private User userObj = null;
	
	public FindNotAssignedTrials(BeanFactory bf) {
		super(bf);
		trialDao = (TrialDAO)bf.getBean(BeanNames.TRIAL_DAO);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);
		setAction(ActionNames.FIND_NOT_ASGN_TRIALS);
	}

	public void setUserObj(User userObj) {
		this.userObj = userObj;
	}
	public User getUserObj() {
		return userObj;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				trialDao != null &&
				userObj != null &&
				userDao != null;
	}
	
	@Override
	public ResultCommand<List<Trial>> runCommand() throws Exception {
		List<Trial> auxList = null;
		List<Trial> assigned = new ArrayList<Trial>();

		userDao.reattach(userObj, GenericDAO.DIRTY_IGNORE);
		
		if(userObj.getParticipations() != null)
			for(Participation p : userObj.getParticipations())
				assigned.add(p.getTrial());
		
		auxList = trialDao.findAll();
		
		if(auxList != null){
			this.setResult(new ArrayList<Trial>());
			for(Trial trial : auxList)
				if(!assigned.contains(trial))
					this.getResult().add(trial);
		}		
		
		return this;
	}

}
