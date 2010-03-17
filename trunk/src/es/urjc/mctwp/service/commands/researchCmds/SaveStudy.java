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

import es.urjc.mctwp.dao.StudyDAO;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class SaveStudy extends Command {
	private StudyDAO studyDao = null;
	private Patient patient = null;
	private Study study = null;

	public SaveStudy(BeanFactory bf) {
		super(bf);
		studyDao = (StudyDAO)bf.getBean(BeanNames.STUDY_DAO);
		setAction(ActionNames.SAVE_STUDY);
		setReadOnly(false);
	}
	
	public void setStudy(Study study) {
		this.study = study;
	}
	public Study getStudy() {
		return study;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Patient getPatient() {
		return patient;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				studyDao != null &&
				patient != null &&
				study != null;
	}

	@Override
	public Command runCommand() {
		String msgKey = (study.getCode() == 0)?"audit.createStudy":"audit.updateStudy";
		
		study.setPatient(patient);
		studyDao.persist(study);
		createLogComment(msgKey, study.getHospitalId());
		return this;
	}
}
