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

import es.urjc.mctwp.image.management.ImageCollectionManager;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class DeleteTemporalImages extends Command {
	private ImageCollectionManager imageManager = null;
	private List<String> imagesId = null;
	private String collection = null;

	public DeleteTemporalImages(BeanFactory bf) {
		super(bf);
		imageManager = (ImageCollectionManager)bf.getBean(BeanNames.IMG_COL_MGR);
		setActionName(ActionNames.DELETE_TEMP_IMAGES);
	}
	
	public void setImagesId(List<String> ids) {
		this.imagesId = ids;
	}
	public List<String> getImagesId() {
		return imagesId;
	}
	public void setCollection(String col) {
		this.collection = col;
	}
	public String getExcludeProps() {
		return collection;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				imageManager != null &&
				collection != null &&
				imagesId != null;
	}	

	@Override
	public Command runCommand() throws Exception {
		imageManager.deleteImages(collection, imagesId, true);
		return this;
	}
}
