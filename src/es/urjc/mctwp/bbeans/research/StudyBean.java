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

package es.urjc.mctwp.bbeans.research;

import java.util.List;

import javax.faces.event.ActionEvent;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.DeleteStudy;
import es.urjc.mctwp.service.commands.researchCmds.FindStudiesByPatient;
import es.urjc.mctwp.service.commands.researchCmds.LoadStudy;
import es.urjc.mctwp.service.commands.researchCmds.SaveStudy;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 * 
 */
public class StudyBean extends RequestInvAbstractBean {
	private Study filter = new Study();
	private Study study = new Study();
	private List<Study> studies = null;

	public void setFilter(Study filter) {
		this.filter = filter;
	}

	public Study getFilter() {
		return filter;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudies(List<Study> studies) {
		this.studies = studies;
	}

	public List<Study> getStudies() {
		return studies;
	}

	/**
	 * It retrieve study from ActionEvent and update selected study into session
	 * state
	 * 
	 * @param event
	 */
	public void alUpdateStudySelected(ActionEvent event) {
		getSession().setStudy(null);
		Study study = (Study) selectRowFromEvent(event, Study.class);

		if (study != null) {
			Command cmd = getCommand(LoadStudy.class);
			((LoadStudy) cmd).setStudyId(study.getCode());
			cmd = runCommand(cmd);
			getSession().setStudy(((LoadStudy) cmd).getResult());
		}
	}

	/**
	 * Retrieve studies of a patient from data base. If no patient is selected
	 * into session, it does nothing.
	 * 
	 * @return
	 */
	public String accListStudiesOfPatient() {

		if (getSession().getPatient() != null) {
			Command cmd = getCommand(FindStudiesByPatient.class);
			((FindStudiesByPatient) cmd).setPatient(getSession().getPatient());
			((FindStudiesByPatient) cmd).setFilter(filter);
			cmd = runCommand(cmd);
			studies = ((FindStudiesByPatient) cmd).getResult();
		}

		return ActionBeanNames.LIST_STUDIES_PATIENT;
	}

	/**
	 * Prepare a study to be edited
	 * 
	 * @return nextview
	 */
	public String accEditStudy() {
		Command cmd = getCommand(LoadStudy.class);
		((LoadStudy) cmd).setStudyId(study.getCode());
		cmd = runCommand(cmd);
		study = ((LoadStudy) cmd).getResult();

		getSession().setStudy(study);
		return ActionBeanNames.EDIT_STUDY;
	}

	/**
	 * Save new or existing Study
	 * 
	 * @return next view
	 */
	public String accSaveStudy() {
		Command cmd = getCommand(SaveStudy.class);
		((SaveStudy) cmd).setStudy(study);
		((SaveStudy) cmd).setPatient(getSession().getPatient());
		runCommand(cmd);

		// Update study state from session
		getSession().setStudy(study);

		return accListStudiesOfPatient();
	}

	/**
	 * Delete an existing study
	 * 
	 * @return next view
	 */
	public String accDeleteStudy() {
		Command cmd = getCommand(DeleteStudy.class);
		((DeleteStudy) cmd).setStudy(study);
		runCommand(cmd);

		// Clean study state from session
		getSession().setStudy(null);

		return accListStudiesOfPatient();
	}
}
