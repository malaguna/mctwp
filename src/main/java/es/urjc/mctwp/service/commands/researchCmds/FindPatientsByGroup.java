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

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.PatientDAO;
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindPatientsByGroup extends ResultCommand<List<Patient>> {
	private PatientDAO patientDao = null;
	private Patient filter = null;
	private Group group = null;

	public FindPatientsByGroup(BeanFactory bf) {
		super(bf);
		patientDao = (PatientDAO) bf.getBean(BeanNames.PATIENT_DAO);
		setActionName(ActionNames.FIND_PATIENTS_GROUP);
	}

	public void setFilter(Patient filter) {
		this.filter = filter;
	}

	public Patient getFilter() {
		return filter;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	@Override
	public boolean isValidCommand() {
		return super.isValidCommand() && patientDao != null && group != null;
	}

	@Override
	public ResultCommand<List<Patient>> runCommand() {
		this.setResult(patientDao.findPatientsByGroupFiltered(group, filter));
		return this;
	}
}
