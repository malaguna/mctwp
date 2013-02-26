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
import org.hibernate.criterion.Restrictions;

import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.Trial;

public class GroupDAO extends GenericDAO<Group, Integer> {

	/**
	 * Get the groups of a trial
	 *  
	 * @param trial
	 * @return
	 */
	public List<Group> findGroupsByTrial(Trial trial){
		return findGroupsByTrialFiltered(trial, null);
	}

	/**
	 * Get the groups of a trial by filter conditions
	 *  
	 * @param trial
	 * @param filter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Group> findGroupsByTrialFiltered(Trial trial, Group filter){
		List<Group> result = null;
		Criteria criteria = null;
		String[] exclude = {"oid"};
		
		//Create the criteria search
		criteria = this.getSession().createCriteria(Group.class);
		
		//Create example query based on filter
		if(filter != null){		
			Example example = createExample(filter, exclude);
			criteria.add(example);
		}
			
		//Create criteria relation based on trial
		if(trial != null){
			criteria.createCriteria("trial")
				.add(Restrictions.eq("code", trial.getCode()));
		}
		
		//Search
		try{
			result = criteria.list();
		}catch(RuntimeException re){
			logErrMsg("findGroupsByTrial", re);
			throw re;
		}
		
		return result;
	}
}
