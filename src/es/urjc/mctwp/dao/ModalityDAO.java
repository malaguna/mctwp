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

import es.urjc.mctwp.modelo.Modality;
import es.urjc.mctwp.modelo.User;

public class ModalityDAO extends GenericDAO<Modality, Integer> {

	/**
	 * Get all modalities a user is allowed to access
	 * 
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Modality> getModalitiesUser(User user){
		List<Modality> result = null;
		String query = "FROM Modality m WHERE m.user=?";
		
		try{
			result = this.getHibernateTemplate().find(query, user); 
		}catch(RuntimeException re){
			logErrMsg("getModalityUsers", re);
			throw re;
		}
		
		return result;
	}
}
