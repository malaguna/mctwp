package es.urjc.mctwp.service.commands.imageCmds;

import java.util.Set;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.ImageDataDAO;
import es.urjc.mctwp.image.objects.DicomSCHeaderAttrs;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;
import es.urjc.mctwp.service.blogic.ImageUtils;

public class FindHeaderForNonDicomImg extends ResultCommand<DicomSCHeaderAttrs> {
	private ImageUtils imageUtils = null;
	private ImageDataDAO imageDao = null;
	private Integer imageId = null;
	
	public FindHeaderForNonDicomImg(BeanFactory bf) {
		super(bf);
		setActionName(ActionNames.SEND_IMAGES);
		imageDao = (ImageDataDAO)bf.getBean(BeanNames.IMAGE_DATA_DAO);
		imageUtils = (ImageUtils) bf.getBean(BeanNames.IMAGE_UTILS);
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public Integer getImageId() {
		return imageId;
	}
	
	@Override
	public boolean isValidCommand(){
		return super.isValidCommand() &&
			imageUtils != null &&
			imageDao != null &&
			imageId != null;
	}

	@Override
	public ResultCommand<DicomSCHeaderAttrs> runCommand() throws Exception {
		DicomSCHeaderAttrs dicomHeader = null;
		
		ImageData image = imageDao.findById(imageId);
		if(image != null){
			Set<ImageData> sources = image.getResult().getTask().getImages();

			if(sources != null && sources.size() == 1){
				ImageData orig = (sources.toArray(new ImageData[0]))[0];
				dicomHeader = imageUtils.getDicomHeader(orig.getImageId(), orig.getStudy().getCollection());
			}
		}
		
		this.setResult(dicomHeader);
		return this;
	}
}
