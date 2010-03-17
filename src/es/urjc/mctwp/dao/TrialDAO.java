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

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;

import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.modelo.User;

public class TrialDAO extends GenericDAO<Trial, Integer> {

	/**
	 * find all trials where user participate in
	 * 
	 * @param usuario
	 * @return
	 */
	public List<Trial> findTrialsByUser(User usuario){
		return findTrialsByUserFiltered(usuario, null);
	}
	
	/**
	 * find all trials where user participate in, filtered
	 * 
	 * @param usuario
	 * @param filter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Trial> findTrialsByUserFiltered(User usuario, Trial filter){
		List<Trial> result = null;
		Criteria criteria = null;
		String[] exclude = {"oid"};
		
		//Create the criteria search
		criteria = this.getSession().createCriteria(Trial.class);
		
		//Create example query based on filter
		if(filter != null){		
			Example example = createExample(filter, exclude);		
			criteria.add(example);
		}
			
		//Create criteria relation based on User
		if(usuario != null){			
			Example exPart = createExample(new Participation(), exclude);
			Criteria crPart = criteria.createCriteria("members").add(exPart);
			
			Example exUser = createExample(usuario, exclude);		
			crPart.createCriteria("user").add(exUser);
		}
		
		//Search
		try{
			result = criteria.list();
		}catch(RuntimeException re){
			logErrMsg("findTrialsByUser", re);
			throw re;
		}
		
		return result;
	}
}
