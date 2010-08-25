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

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.image.management.ImageCollectionManager;
import es.urjc.mctwp.image.objects.Image;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadImage extends ResultCommand<Image> {
	private ImageCollectionManager imageManager = null;
	private String collection = null;
	private String imageId = null;

	public LoadImage(BeanFactory bf) {
		super(bf);
		imageManager = (ImageCollectionManager)bf.getBean(BeanNames.IMG_COL_MGR);
		setAction(ActionNames.LOAD_IMAGE);
	}
	
	public void setImageId(String id) {
		this.imageId = id;
	}
	public String getImageId() {
		return imageId;
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
				imageId != null;
	}	

	@Override
	public ResultCommand<Image> runCommand() throws Exception {
		this.setResult(imageManager.getImage(collection, imageId, false));
		return this;
	}
}
