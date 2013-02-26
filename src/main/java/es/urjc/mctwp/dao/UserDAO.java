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

package es.urjc.mctwp.dao;

import java.util.List;

import es.urjc.mctwp.modelo.User;

public class UserDAO extends GenericDAO<User, Integer> {

	/**
	 * Find a user by login
	 * 
	 * @param login
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public User findUserByLogin(String login){
		List<User> list = null;
		User result = null;
		String query = "FROM User u WHERE u.login=?";
		
		try{
			list = this.getHibernateTemplate().find(query, login);
			result = (list.isEmpty())?null:(User)list.get(0);
		}catch (RuntimeException re) {
			logErrMsg("findUserByLogin", re);
			throw re;
		}
		
		return result;
	}
}
