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

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.service.blogic.ImageUtils;
import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class StoreTemporalImages extends Command {
	private ImageUtils imageUtils = null;
	private List<File> files = null;

	public StoreTemporalImages(BeanFactory bf) {
		super(bf);
		setAction(ActionNames.STORE_TEMP_IMAGES);
		imageUtils = (ImageUtils)bf.getBean(BeanNames.IMAGE_UTILS);
		setReadOnly(false);
	}
	
	public void setFiles(List<File> files) {
		this.files = files;
	}
	public List<File> getFiles() {
		return files;
	}

	@Override 
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				imageUtils != null;
	}

	@Override
	public Command runCommand() throws ImageCollectionException, ImageException{
		imageUtils.storeTemporalImages(getUser(), files);
		createLogComment("audit.storeTempImages", files.size(), getUser().getLogin());
		return null;
	}
}
