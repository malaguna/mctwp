package es.urjc.mctwp.bbeans.research.image;

import java.util.ArrayList;
import java.util.List;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.DeleteImages;

public class ImageDeletionBean extends RequestInvAbstractBean {
	private List<Integer> toDelete = null; //Images that can be deleted
	private List<ImageData> cantDelete = null; //Images that can not be deleted

	public void setCantDelete(List<ImageData> cantDelete) {
		this.cantDelete = cantDelete;
	}

	public List<ImageData> getCantDelete() {
		return cantDelete;
	}

	public void setToDelete(List<Integer> toDelete) {
		this.toDelete = toDelete;
	}

	public List<Integer> getToDelete() {
		return toDelete;
	}

	public String accDeleteImages(){
		
		Command cmd = getCommand(DeleteImages.class);
		((DeleteImages) cmd).setImages(toDelete);
		((DeleteImages) cmd).setDelete(true);
		cmd = runCommand(cmd);

		if(cmd != null){
			cantDelete = ((DeleteImages) cmd).getResult();
			
			if(cantDelete == null || cantDelete.isEmpty()){
				setInfoMessage(getMessage("jsf.info.AllImagesDeleted"));
			}else{
				setWarnMessage(getMessage("jsf.info.SomeImagesDeleted"));
			}
		}
		
		return null;
	}

	public String prepareImageDeletion(){
		String result = null;
		
		if( (toDelete != null) && (!toDelete.isEmpty()) ){
			
			Command cmd = getCommand(DeleteImages.class);
			((DeleteImages) cmd).setImages(toDelete);
			((DeleteImages) cmd).setDelete(false);
			cmd = runCommand(cmd);
			
			if(cmd != null){
				cantDelete = ((DeleteImages) cmd).getResult();
			
				if(cantDelete != null && !cantDelete.isEmpty()){
					setWarnMessage(getMessage("jsf.info.SomeImagesCantBeDeleted"));
				}else
					cantDelete = new ArrayList<ImageData>();
			}
			
			result = ActionBeanNames.DELETE_IMAGES;
		}else{
			setWarnMessage(getMessage("jsf.info.NoImagesSelected"));
		}
		
		return result;		
	}
}
