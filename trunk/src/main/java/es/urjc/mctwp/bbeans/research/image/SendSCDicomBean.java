package es.urjc.mctwp.bbeans.research.image;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.dcm4che2.data.UID;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.image.objects.DicomSCHeaderAttrs;
import es.urjc.mctwp.pacs.DcmDestination;
import es.urjc.mctwp.service.commands.imageCmds.FindHeaderForNonDicomImg;
import es.urjc.mctwp.service.commands.imageCmds.SendImageAsSC;

public class SendSCDicomBean extends RequestInvAbstractBean {
	private DicomSCHeaderAttrs dicomHeader = new DicomSCHeaderAttrs();
	private DcmDestination destination = new DcmDestination();
	private List<SelectItem> SOPUids = null;
	private Integer imageId = null;

	public DicomSCHeaderAttrs getDicomHeader() {
		return dicomHeader;
	}

	public void setDicomHeader(DicomSCHeaderAttrs dicomHeader) {
		this.dicomHeader = dicomHeader;
	}

	public DcmDestination getDestination() {
		return destination;
	}

	public void setDestination(DcmDestination destination) {
		this.destination = destination;
	}

	public List<SelectItem> getSOPUids() {
		return SOPUids;
	}

	public void setSOPUids(List<SelectItem> sOPUids) {
		SOPUids = sOPUids;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}
	
	public String accPrepareSend(){
		String result = null;
		
		SOPUids = new ArrayList<SelectItem>();
		SOPUids.add(new SelectItem(UID.SecondaryCaptureImageStorage, "SC Image IOD"));
		SOPUids.add(new SelectItem(UID.MultiframeSingleBitSecondaryCaptureImageStorage, "Multi-frame Single Bit"));
		SOPUids.add(new SelectItem(UID.MultiframeGrayscaleByteSecondaryCaptureImageStorage, "Multi-frame Grayscale Byte"));
		SOPUids.add(new SelectItem(UID.MultiframeGrayscaleWordSecondaryCaptureImageStorage, "Multi-frame Grayscale Word"));
		SOPUids.add(new SelectItem(UID.MultiframeTrueColorSecondaryCaptureImageStorage, "Multi-frame True Color"));
		
		FindHeaderForNonDicomImg cmd = (FindHeaderForNonDicomImg) getCommand(FindHeaderForNonDicomImg.class);
		cmd.setImageId(imageId);
		cmd = (FindHeaderForNonDicomImg) runCommand(cmd);
		
		if(cmd != null && cmd.getResult() != null){
			dicomHeader = cmd.getResult();
			result = ActionBeanNames.SEND_IMAGE_SC;
		}else
			setWarnMessage(getMessage("jsf.generic.NoImageHeader"));
		
		return result;
	}

	public String accSendImages(){
		String result = null;
		
		SendImageAsSC cmd = (SendImageAsSC) getCommand(SendImageAsSC.class);
		cmd.setDestination(destination);
		cmd.setDicomHeader(dicomHeader);
		cmd.setImageId(imageId);
		cmd = (SendImageAsSC) runCommand(cmd);
		
		if(cmd != null)
			setInfoMessage(getMessage("jsf.info.OkImageSending"));
		
		return result;
	}
}
