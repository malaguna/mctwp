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

import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.exception.ImageException;
import es.urjc.mctwp.image.management.ImageCollectionManager;
import es.urjc.mctwp.image.objects.ThumbNail;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadThumbsOfTemporalImages extends ResultCommand<List<ThumbNail>> {
	private ImageCollectionManager imcm = null;
	private String tempColl = null;

	public LoadThumbsOfTemporalImages(BeanFactory bf) {
		super(bf);
		imcm = (ImageCollectionManager)bf.getBean(BeanNames.IMG_COL_MGR);
		setAction(ActionNames.STORE_TEMP_IMAGES);
	}
	
	public void setTempColl(String tempColl) {
		this.tempColl = tempColl;
	}
	public String getTempColl() {
		return tempColl;
	}

	@Override 
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				tempColl != null &&
				imcm != null;
	}

	@Override
	public ResultCommand<List<ThumbNail>> runCommand() throws ImageException, ImageCollectionException{
		this.setResult(imcm.getThumbNails(tempColl, true));
		return this;
	}
}
