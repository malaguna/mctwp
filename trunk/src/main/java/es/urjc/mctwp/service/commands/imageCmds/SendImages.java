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

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.service.blogic.ImageUtils;
import es.urjc.mctwp.pacs.DcmDestination;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class SendImages extends Command {
	private DcmDestination destination = null;
	private ImageUtils imageUtils = null;
	private List<String> images = null;

	public SendImages(BeanFactory bf) {
		super(bf);
		setActionName(ActionNames.SEND_IMAGES);
		imageUtils = (ImageUtils)bf.getBean(BeanNames.IMAGE_UTILS);
	}
	
	public void setImages(List<String> images) {
		this.images = images;
	}
	public List<String> getImages() {
		return images;
	}
	public void setDestination(DcmDestination destination) {
		this.destination = destination;
	}
	public DcmDestination getDestination() {
		return destination;
	}

	@Override 
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				destination != null &&
				imageUtils != null &&
				images != null;
	}

	@Override
	public Command runCommand() throws Exception{
		imageUtils.sendImages(images, getTrial().getCollection(), destination);
		createLogComment("audit.sendImages", images.size(), getTrial().getCollection(), destination.getHostname());
		createUserComment("jsf.generic.SendOk");
		return this;
	}
}
