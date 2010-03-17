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

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.GenericDAO;
import es.urjc.mctwp.dao.ModalityDAO;
import es.urjc.mctwp.dao.UserDAO;
import es.urjc.mctwp.modelo.Modality;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class RemoveUserModality extends Command {
	private ModalityDAO modalityDao = null;
	private Integer modality = null;
	private UserDAO userDao = null;
	private User userObj = null;
	
	public RemoveUserModality(BeanFactory bf) {
		super(bf);
		modalityDao = (ModalityDAO)bf.getBean(BeanNames.MODALITY_DAO);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);
		setAction(ActionNames.ADD_USER_MODALITY);
		setReadOnly(false);
	}
	
	public Integer getModality() {
		return modality;
	}

	public void setModality(Integer modality) {
		this.modality = modality;
	}

	public User getUserObj() {
		return userObj;
	}

	public void setUserObj(User user) {
		this.userObj = user;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				modalityDao != null &&
				modality != null &&
				userDao != null &&
				userObj != null;
	}

	@Override
	public Command runCommand() throws Exception {
		Modality mod = modalityDao.findById(modality);
		
		if(mod != null){
			userDao.reattach(userObj, GenericDAO.DIRTY_IGNORE);
			userObj.delModality(mod);
			userDao.persist(userObj);
			createLogComment("audit.removeUserModality", userObj.getFullName(), mod.getName());
		}
		
		return this;
	}

}
