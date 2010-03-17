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

import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Study;

public class StudyDAO extends GenericDAO<Study, Integer> {

	public Study findStudyInPatient(Integer patientId, String studyId){
		Study result = null;
		List<?> list = null;
		String query = "FROM Study s WHERE s.hospitalId=? AND s.patient.code=?";
		
		try{
			Object array[] = {studyId, patientId};
			list = this.getHibernateTemplate().find(query, array);
			result = (list.isEmpty())?null:(Study)list.get(0);
		}catch(RuntimeException re){
			logErrMsg("findStudyInPatient", re);
			throw re;
		}
		
		return result;
	}
	
	/**
	 * Get all studies of a case
	 * 
	 * @param caseId
	 * @return
	 */
	public List<Study> findStudiesByPatient(Patient patient){
		return findStudiesByPatientFiltered(patient, null);
	}
	
	/**
	 * Get the studies of a patient by filter conditions
	 *  
	 * @param patient
	 * @param filter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Study> findStudiesByPatientFiltered(Patient patient, Study filter){
		List<Study> result = null;
		Criteria criteria = null;
		String[] exclude = {"oid"};
		
		//Create the criteria search
		criteria = this.getSession().createCriteria(Study.class);
		
		//Create example query based on filter
		if(filter != null){		
			Example example = createExample(filter, exclude);
			criteria.add(example);
		}
			
		//Create criteria relation based on trial
		if(patient != null){
			Example example = createExample(patient, exclude);
			criteria.createCriteria("patient").add(example);
		}
		
		//Search
		try{
			result = criteria.list();
		}catch(RuntimeException re){
			logErrMsg("findStudiesByPatient", re);
			throw re;
		}
		
		return result;
	}	
}
