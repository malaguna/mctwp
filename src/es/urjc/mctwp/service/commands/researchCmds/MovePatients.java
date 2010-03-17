package es.urjc.mctwp.service.commands.researchCmds;

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.GroupDAO;
import es.urjc.mctwp.dao.PatientDAO;
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class MovePatients extends Command {
	private List<Patient> patients = null;
	private PatientDAO patientDao = null;
	private GroupDAO groupDao = null;
	private Integer group = null;

	public MovePatients(BeanFactory bf) {
		super(bf);
		setReadOnly(false);
		setAction(ActionNames.MOVE_PATIENT);
		groupDao = (GroupDAO) bf.getBean(BeanNames.GROUP_DAO);
		patientDao = (PatientDAO) bf.getBean(BeanNames.PATIENT_DAO);
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatientDao(PatientDAO patientDao) {
		this.patientDao = patientDao;
	}

	public PatientDAO getPatientDao() {
		return patientDao;
	}

	public void setGroupDao(GroupDAO groupDao) {
		this.groupDao = groupDao;
	}

	public GroupDAO getGroupDao() {
		return groupDao;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public Integer getGroup() {
		return group;
	}
	
	@Override
	public boolean isValidCommand(){
		return super.isValidCommand() &&
			patientDao != null &&
			groupDao != null &&
			patients != null &&
			group != null;
	}

	@Override
	public Command runCommand() throws Exception {
		Group groupDest = groupDao.findById(group);
		
		for(Patient patient : patients){
			//Group groupOrig = groupDao.findById(patient.getGroup().getCode());
			//groupOrig.delPatient(patient);
			//groupDest.addPatient(patient);
			patient.setGroup(groupDest);
			patientDao.persist(patient);
		}
		
		return this;
	}
}
