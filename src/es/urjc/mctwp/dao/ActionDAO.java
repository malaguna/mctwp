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

import java.util.Collections;
import java.util.List;

import es.urjc.mctwp.modelo.Action;

public class ActionDAO extends GenericDAO<Action, Integer> {

	/**
	 * 
	 * @param rol
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Action> findActionsByRol(int rol){
		List<Action> result = null;
		String query = "SELECT a FROM Rol r JOIN r.actions a WHERE r.code=? ORDER BY a.name";
		
		try{
			result = this.getHibernateTemplate().find(query, rol); 
		}catch(RuntimeException re){
			logErrMsg("findActionsByRol", re);
			throw re;
		}
		
		return result;
	}
	
	/**
	 * Find an action by name of method
	 * 
	 * @param method
	 * @return
	 */
	public Action findActionByName(String name){
		List<?> list = null;
		Action result = null;
		String query = "FROM Action a WHERE a.name=?";
		
		try{
			list = this.getHibernateTemplate().find(query, name);
			result = (list.isEmpty())?null:(Action)list.get(0);
		}catch(RuntimeException re){
			logErrMsg("findActionByName", re);
			throw re;
		}
		
		return result;
	}
	
	@Override
	public List<Action> findAll(){
		List<Action> result = null;
		
		result = super.findAll();
		Collections.sort(result);
		
		return result;
	}
}
