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

package es.urjc.mctwp.service.commands.researchCmds;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.GenericDAO;
import es.urjc.mctwp.dao.ModalityDAO;
import es.urjc.mctwp.dao.UserDAO;
import es.urjc.mctwp.modelo.Modality;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindNotAssignedModalities extends ResultCommand<List<Modality>> {
	private ModalityDAO modalityDao = null;
	private UserDAO userDao = null;
	private User userObj = null;
	
	public User getUserObj() {
		return userObj;
	}

	public void setUserObj(User userObj) {
		this.userObj = userObj;
	}

	public FindNotAssignedModalities(BeanFactory bf) {
		super(bf);
		modalityDao = (ModalityDAO)bf.getBean(BeanNames.MODALITY_DAO);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);
		setAction(ActionNames.FIND_NOT_ASGN_MODALS);
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				modalityDao != null &&
				userDao != null &&
				userObj != null;
	}
	
	@Override
	public ResultCommand<List<Modality>> runCommand() throws Exception {
		List<Modality> all = modalityDao.findAll();
		userDao.reattach(userObj, GenericDAO.DIRTY_IGNORE);
		
		if(all != null){
			if(userObj.getModalities() != null){
				this.setResult(new ArrayList<Modality>());
				
				for(Modality mod : all)
					if(!userObj.getModalities().contains(mod))
						this.getResult().add(mod);
			}else
				this.setResult(all);
		}

		return this;
	}
}
