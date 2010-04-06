package es.urjc.mctwp.service.commands.researchCmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.ImageDataDAO;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class DeleteImages extends ResultCommand<List<ImageData>> {
	private List<Integer> images = null;
	private ImageDataDAO imageDao = null;
	private boolean delete = false;

	public DeleteImages(BeanFactory bf) {
		super(bf);
		setAction(ActionNames.DELETE_PATIENT);
		imageDao = (ImageDataDAO) bf.getBean(BeanNames.IMAGE_DATA_DAO);
	}

	public void setImages(List<Integer> images) {
		this.images = images;
	}

	public List<Integer> getImages() {
		return images;
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
			imageDao != null &&
			images != null;
	}

	@Override
	public ResultCommand<List<ImageData>> runCommand() throws Exception {
		this.setResult(new ArrayList<ImageData>());
		
		for (Integer imageId: images){
			boolean canDeleteImage = true;
			
			ImageData image = imageDao.findById(imageId);
					
			if( (image.getTasks() != null) && !image.getTasks().isEmpty()){
				Iterator<Task> tasks = image.getTasks().iterator();
				boolean canDeleteTask = true;
				
				while (tasks.hasNext() && canDeleteTask){
					Task task = tasks.next();
					
					if((task.getResults() != null) && !task.getResults().isEmpty())
						canDeleteTask = false;
					
				}canDeleteImage = canDeleteTask;
			}

			if(canDeleteImage && delete)
				imageDao.delete(image);
			if(!canDeleteImage)
				this.getResult().add(image);
		}
		
		return this;
	}
}
