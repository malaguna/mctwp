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

package es.urjc.mctwp.service.commands.imageCmds;

import java.util.Set;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.service.blogic.ImageUtils;
import es.urjc.mctwp.dao.ImageDataDAO;
import es.urjc.mctwp.image.objects.DicomSCHeaderAttrs;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.pacs.DcmDestination;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class SendImageAsSC extends Command {
	private DicomSCHeaderAttrs dicomHeader = null;
	private DcmDestination destination = null;
	private ImageDataDAO imageDao = null;
	private ImageUtils imageUtils = null;
	private Integer imageId = null;

	public void setDicomHeader(DicomSCHeaderAttrs dicomHeader) {
		this.dicomHeader = dicomHeader;
	}

	public DicomSCHeaderAttrs getDicomHeader() {
		return dicomHeader;
	}

	public SendImageAsSC(BeanFactory bf) {
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

	public void setDestination(DcmDestination destination) {
		this.destination = destination;
	}

	public DcmDestination getDestination() {
		return destination;
	}

	@Override
	public boolean isValidCommand() {
		return super.isValidCommand() && destination != null
				&& imageUtils != null && imageId != null &&
				dicomHeader != null;
	}

	@Override
	public Command runCommand() throws Exception {
		ImageData image = imageDao.findById(imageId);
		if(image != null){
			Set<ImageData> sources = image.getResult().getTask().getImages();
			if(sources != null && sources.size() == 1){
				ImageData orig = (sources.toArray(new ImageData[0]))[0];
				imageUtils.sendImageAsSC(image.getImageId(), orig.getStudy().getCollection(), destination, dicomHeader);
				createLogComment("audit.sendImageAsSC", imageId, getTrial()
						.getCollection(), destination.getHostname());
			}
		}
		return this;
	}
}
