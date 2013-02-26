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
import es.urjc.mctwp.modelo.Patient;

public class PatientDAO extends GenericDAO<Patient, Integer> {

	/**
	 * Get the patients of a group
	 *  
	 * @param groupId
	 * @return
	 */
	public List<Patient> findPatientsByGroup(Group group){
		return findPatientsByGroupFiltered(group, null);
	}
	
	/**
	 * Search a patient into a group
	 * 
	 * @param groupId
	 * @param patientId
	 * @return null if there is no such patient into group
	 */
	public Patient findPatientInGroup(Integer groupId, String patientId){
		Patient result = null;
		List<?> list = null;
		String query = "FROM Patient p WHERE p.hospitalId=? AND p.group.code=?";
		
		try{
			Object array[] = {patientId, groupId};
			list = this.getHibernateTemplate().find(query, array);
			result = (list.isEmpty())?null:(Patient)list.get(0);
		}catch(RuntimeException re){
			logErrMsg("findPatientInGroup", re);
			throw re;
		}
		
		return result;
	}
	
	/**
	 * Get the patients of a group by filter conditions
	 *  
	 * @param group
	 * @param filter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Patient> findPatientsByGroupFiltered(Group group, Patient filter){
		List<Patient> result = null;
		Criteria criteria = null;
		String[] exclude = {"oid"};
		
		//Create the criteria search
		criteria = this.getSession().createCriteria(Patient.class);
		
		//Create example query based on filter
		if(filter != null){		
			Example example = createExample(filter, exclude);
			criteria.add(example);
		}
			
		//Create criteria relation based on group
		if(group != null){
			criteria.createCriteria("group")
				.add(Restrictions.eq("code", group.getCode()));
		}
		
		//Search
		try{
			result = criteria.list();
		}catch(RuntimeException re){
			logErrMsg("findPatientsByGroup", re);
			throw re;
		}
		
		return result;
	}	
}
