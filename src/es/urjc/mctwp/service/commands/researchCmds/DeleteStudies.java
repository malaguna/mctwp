package es.urjc.mctwp.service.commands.researchCmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.StudyDAO;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class DeleteStudies extends ResultCommand<List<Study>> {
	private List<Study> studies = null;
	private StudyDAO studyDao = null;
	private boolean delete = false;

	public DeleteStudies(BeanFactory bf) {
		super(bf);
		setAction(ActionNames.DELETE_PATIENT);
		studyDao = (StudyDAO) bf.getBean(BeanNames.STUDY_DAO);
	}

	public void setStudies(List<Study> studies) {
		this.studies = studies;
	}

	public List<Study> getStudies() {
		return studies;
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
			studyDao != null &&
			studies != null;
	}

	@Override
	public ResultCommand<List<Study>> runCommand() throws Exception {
		this.setResult(new ArrayList<Study>());
		
		for (Study aux: studies){
			boolean canDeleteStudy = true;
			Study study = studyDao.findById(aux.getCode());
			
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

			if(canDeleteStudy && delete)
				studyDao.delete(study);
			if(!canDeleteStudy)
				this.getResult().add(study);
		}
		
		return this;
	}
}
