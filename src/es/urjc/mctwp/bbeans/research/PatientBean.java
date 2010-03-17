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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.DeletePatients;
import es.urjc.mctwp.service.commands.researchCmds.FindGroupsByTrial;
import es.urjc.mctwp.service.commands.researchCmds.FindPatientsByGroup;
import es.urjc.mctwp.service.commands.researchCmds.LoadGroup;
import es.urjc.mctwp.service.commands.researchCmds.LoadPatient;
import es.urjc.mctwp.service.commands.researchCmds.MovePatients;
import es.urjc.mctwp.service.commands.researchCmds.SavePatient;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 */
public class PatientBean extends RequestInvAbstractBean {
	private Patient[] selection = null; //Patients selected to move or delete
	private List<Patient> toMove = null; //Patients that can be moved
	private List<Patient> patients = null; //List of patients
	private List<Patient> toDelete = null; //Patients that can be deleted
	private List<Patient> cantDelete = null; //Patients that can not be deleted

	private Integer group = null;
	private Patient filter = new Patient();
	private Patient patient = new Patient();
	private List<SelectItem> groups = null;

	public void setSelection(Patient[] selection) {
		this.selection = selection;
	}

	public Patient[] getSelection() {
		return selection;
	}

	public void setToMove(List<Patient> toMove) {
		this.toMove = toMove;
	}

	public List<Patient> getToMove() {
		return toMove;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public Integer getGroup() {
		return group;
	}

	public void setFilter(Patient filter) {
		this.filter = filter;
	}

	public Patient getFilter() {
		return filter;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setToDelete(List<Patient> toDelete) {
		this.toDelete = toDelete;
	}

	public List<Patient> getToDelete() {
		return toDelete;
	}

	public void setCantDelete(List<Patient> cantDelete) {
		this.cantDelete = cantDelete;
	}

	public List<Patient> getCantDelete() {
		return cantDelete;
	}

	public void setGroups(List<SelectItem> groups) {
		this.groups = groups;
	}

	public List<SelectItem> getGroups() {
		return groups;
	}

	/**
	 * It retrieve patient from ActionEvent and update selected patient into
	 * session state
	 * 
	 * @param event
	 */
	public void alUpdatePatientSelected(ActionEvent event) {
		getSession().setPatient(null);
		Patient patient = (Patient) selectRowFromEvent(event, Patient.class);

		if (patient != null) {
			Command cmd = getCommand(LoadPatient.class);
			((LoadPatient) cmd).setPatientId(patient.getCode());
			cmd = runCommand(cmd);
			getSession().setPatient(((LoadPatient) cmd).getResult());
		}
	}

	/**
	 * Retrieves patients of group from data base. If no group is selected into
	 * session, it does nothing.
	 * 
	 * @return
	 */
	public String accListPatientsOfGroup() {
		if (getSession().getGroup() != null) {
			Command cmd = getCommand(FindPatientsByGroup.class);
			((FindPatientsByGroup) cmd).setGroup(getSession().getGroup());
			((FindPatientsByGroup) cmd).setFilter(filter);
			cmd = runCommand(cmd);
			patients = ((FindPatientsByGroup) cmd).getResult();
		}

		return ActionBeanNames.LIST_PATIENTS_GROUP;
	}

	/**
	 * Prepare edition of a patient
	 * 
	 * @return next view
	 */
	public String accEditPatient() {
		Command cmd = getCommand(LoadPatient.class);
		((LoadPatient) cmd).setPatientId(patient.getCode());
		cmd = runCommand(cmd);
		patient = ((LoadPatient) cmd).getResult();

		getSession().setPatient(patient);
		return ActionBeanNames.EDIT_PATIENT;
	}

	/**
	 * Save or update a new or existing patient
	 * 
	 * @return next view
	 */
	public String accSavePatient() {
		Command cmd = getCommand(SavePatient.class);
		((SavePatient) cmd).setPatient(patient);
		((SavePatient) cmd).setGroup(getSession().getGroup());
		runCommand(cmd);

		// Update patient into session state info
		getSession().setPatient(patient);

		return accListPatientsOfGroup();
	}

	/**
	 * Delete existing patient
	 * 
	 * @return next view
	 */
	public String accPreparePatientToDelete() {
		if(patient != null){
			toDelete = new ArrayList<Patient>();
			toDelete.add(patient);
		}
		
		return preparePatientDeletion();
	}
	
	/**
	 * Delete existing patient
	 * 
	 * @return next view
	 */
	public String accPrepareGroupPatientsToDelete() {
		if(getSession().getGroup() != null){
			Command cmd = getCommand(LoadGroup.class);
			((LoadGroup) cmd).setGroupId(getSession().getGroup().getCode());
			cmd = runCommand(cmd);
			toDelete = new ArrayList<Patient>(((LoadGroup) cmd).getResult().getPatients());
		}
		
		return preparePatientDeletion();
	}

	public String accPreparePatientsToDelete(){
		if( (selection != null) && (selection.length > 0) ){
			toDelete = new ArrayList<Patient>();
			for(Patient patient : selection)
				toDelete.add(patient);
		}
		
		return preparePatientDeletion();
	}
	
	public String accPreparePatientsToMove(){
		if( (selection != null) && (selection.length > 0) )
			toMove = Arrays.asList(selection);			
		else if( (cantDelete != null) && !(cantDelete.isEmpty()) )
			toMove = cantDelete;

		return prepareMovement();
	}
	
	public String accPrepareGroupPatientsToMove() {
		if(getSession().getGroup() != null){
			Command cmd = getCommand(LoadGroup.class);
			((LoadGroup) cmd).setGroupId(getSession().getGroup().getCode());
			cmd = runCommand(cmd);
			toMove = new ArrayList<Patient>(((LoadGroup) cmd).getResult().getPatients());
		}
		
		return prepareMovement();
	}

	public String accDeletePatients(){
		String result = null;
		
		Command cmd = getCommand(DeletePatients.class);
		((DeletePatients) cmd).setPatients(toDelete);
		((DeletePatients) cmd).setDelete(true);
		cmd = runCommand(cmd);

		if(cmd != null){
			cantDelete = ((DeletePatients) cmd).getResult();
			
			if(cantDelete == null || cantDelete.isEmpty()){
				setInfoMessage(getMessage("jsf.info.AllPatientsDeleted"));
				result = accListPatientsOfGroup();
			}else{
				setWarnMessage(getMessage("jsf.info.SomePatientsDeleted"));
				result = ActionBeanNames.DELETE_PATIENTS;
			}
		}
		
		return result;
	}
	
	public String accMovePatients(){
		
		if( (toMove != null) && (!toMove.isEmpty())
				&& (group != null) ){
			
			Command cmd = getCommand(MovePatients.class);
			((MovePatients) cmd).setPatients(toMove);
			((MovePatients) cmd).setGroup(group);
			cmd = runCommand(cmd);
			toMove = new ArrayList<Patient>();
			setInfoMessage(getMessage("jsf.info.AllPatientsMoved"));
		}else if (group == null){
			setErrorMessage(getMessage("jsf.info.NoGroupSelected"));
		}
		
		return ActionBeanNames.MOVE_PATIENTS;
	}
	
	private String preparePatientDeletion(){
		String result = null;
		
		if( (toDelete != null) && (!toDelete.isEmpty()) ){
			
			Command cmd = getCommand(DeletePatients.class);
			((DeletePatients) cmd).setPatients(toDelete);
			((DeletePatients) cmd).setDelete(false);
			cmd = runCommand(cmd);
			
			if(cmd != null){
				cantDelete = ((DeletePatients) cmd).getResult();
			
				if(cantDelete != null && !cantDelete.isEmpty()){ 
					toDelete.removeAll(cantDelete);
					setWarnMessage(getMessage("jsf.info.SomePatientsCantBeDeleted"));
				}else
					cantDelete = new ArrayList<Patient>();
			}
			
			result = ActionBeanNames.DELETE_PATIENTS;
		}else{
			setWarnMessage(getMessage("jsf.info.NoPatientsSelected"));
		}
		
		return result;		
	}
	
	private String prepareMovement(){
		String result = null;
		
		if( toMove != null){
			Command cmd = getCommand(FindGroupsByTrial.class);
			cmd = runCommand(cmd);
			List<Group> groups = ((FindGroupsByTrial) cmd).getResult();
			this.groups = new ArrayList<SelectItem>();
			
			for(Group group : groups)
				if(!group.equals(getSession().getGroup()))
					this.groups.add(new SelectItem(group.getCode(), group.getDescription()));
			
			result = ActionBeanNames.MOVE_PATIENTS;
		}else
			setInfoMessage(getMessage("jsf.info.NoPatientsSelected"));

		return result;
	}
}
