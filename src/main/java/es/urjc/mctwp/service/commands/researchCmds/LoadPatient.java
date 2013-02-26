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

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.PatientDAO;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadPatient extends ResultCommand<Patient> {
	private PatientDAO patientDao = null;
	private Integer patientId = null;

	public LoadPatient(BeanFactory bf) {
		super(bf);
		patientDao = (PatientDAO)bf.getBean(BeanNames.PATIENT_DAO);
		setActionName(ActionNames.LOAD_PATIENT);
	}

	public void setPatientId(Integer id) {
		this.patientId = id;
	}
	public Integer getPatientId() {
		return patientId;
	}
	
	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				patientDao != null &&
				patientId != null;
	}

	@Override
	public ResultCommand<Patient> runCommand() {
		this.setResult(patientDao.findById(patientId));
		return this;
	}
}
