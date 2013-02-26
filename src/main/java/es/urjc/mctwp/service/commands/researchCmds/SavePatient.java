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
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class SavePatient extends Command {
	private PatientDAO patientDao = null;
	private Patient patient = null;
	private Group group = null;

	public SavePatient(BeanFactory bf) {
		super(bf);
		patientDao = (PatientDAO)bf.getBean(BeanNames.PATIENT_DAO);
		setActionName(ActionNames.SAVE_PATIENT);
		setReadOnly(false);
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Patient getPatient() {
		return patient;
	}	
	public void setGroup(Group group) {
		this.group = group;
	}
	public Group getGroup() {
		return group;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				patientDao != null &&
				patient != null &&
				group != null;
	}

	@Override
	public Command runCommand() {
		String msgKey = (patient.getCode() == 0)?"audit.createPatient":"audit.updatePatient";
		
		patient.setGroup(group);
		patientDao.persist(patient);
		createLogComment(msgKey, patient.getCompleteName());
		return this;
	}
}
