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

import es.urjc.mctwp.dao.UserDAO;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadUser extends ResultCommand<User> {
	private String[] exclude = null;
	private Integer userCode = null;
	private UserDAO userDao = null;

	public LoadUser(BeanFactory bf) {
		super(bf);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);
		setAction(ActionNames.LOAD_USER);
	}
	
	public void setUserCode(Integer code) {
		this.userCode = code;
	}
	public Integer getUserCode() {
		return userCode;
	}
	public void setExcludeProps(String[] exclude) {
		this.exclude = exclude;
	}
	public String[] getExcludeProps() {
		return exclude;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				userCode != null &&
				userDao != null;
	}	

	@Override
	public ResultCommand<User> runCommand() {
		this.setResult(userDao.findById(userCode));
		return this;
	}
}
