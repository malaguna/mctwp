package es.urjc.mctwp.service.commands.researchCmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.PatientDAO;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class DeletePatients extends ResultCommand<List<Patient>> {
	private List<Patient> patients = null;
	private PatientDAO patientDao = null;
	private boolean delete = false;

	public DeletePatients(BeanFactory bf) {
		super(bf);
		setActionName(ActionNames.DELETE_PATIENT);
		patientDao = (PatientDAO) bf.getBean(BeanNames.PATIENT_DAO);
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
		setReadOnly(!delete);
	}

	public boolean isDelete() {
		return delete;
	}
	
	@Override
	public boolean isValidCommand(){
		return super.isValidCommand() &&
			patientDao != null &&
			patients != null;
	}

	@Override
	public ResultCommand<List<Patient>> runCommand() throws Exception {
		this.setResult(new ArrayList<Patient>());
		
		for (Patient aux: patients){
			boolean canDeletePatient = true;
			Patient patient = patientDao.findById(aux.getCode());
			
			if( (patient.getStudies() != null) && !patient.getStudies().isEmpty()){
				Iterator<Study> studies =  patient.getStudies().iterator();
				boolean canDeleteStudy = true;
				
				while (studies.hasNext() && canDeleteStudy) {
					Study study = studies.next();
					
					if( (study.getImages() != null) && !study.getImages().isEmpty()){
						Iterator<ImageData> images = study.getImages().iterator();
						boolean canDeleteImage = true;
						
						while (images.hasNext() && canDeleteImage){
							ImageData image = images.next();
							
							if( (image.getTasks() != null) && !image.getTasks().isEmpty()){
								Iterator<Task> tasks = image.getTasks().iterator();
								boolean canDeleteTask = true;
								
								while (tasks.hasNext() && canDeleteTask){
									Task task = tasks.next();
									
									if((task.getResults() != null) && !task.getResults().isEmpty())
										canDeleteTask = false;
									
								}canDeleteImage = canDeleteTask;
							}
						}canDeleteStudy = canDeleteImage;
					}						
				}canDeletePatient = canDeleteStudy;
			}

			if(canDeletePatient && delete)
				patientDao.delete(patient);
			if(!canDeletePatient)
				this.getResult().add(patient);
		}
		
		return this;
	}
}
