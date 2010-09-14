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

package es.urjc.mctwp.service.commands.userCmds;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.UserDAO;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

/**
 * This is a special command, it does not log anything and
 * it is alwways well formed and authorized.
 * 
 * @author miguel
 *
 */
public class LoadUserByLogin extends ResultCommand<User> {
	private UserDAO userDao = null;
	private String login = null;

	public LoadUserByLogin(BeanFactory bf) {
		super(bf);
		setActionName(ActionNames.LOAD_USER_BY_LOGIN);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);		
	}

	public String getLogin() {return login;}
	public void setLogin(String login) {
		this.login = login;
	}
	
	@Override
	public boolean isValidCommand(){
		return userDao != null;
	}
	
	@Override
	public boolean isCommandAuthorized(){
		return true;
	}
	
	@Override
	public void initCommandLog(){
	}

	@Override
	public void endsCommandLog(){
	}

	@Override
	public ResultCommand<User> runCommand() {
		this.setResult(userDao.findUserByLogin(login));
		return this;
	}
}
