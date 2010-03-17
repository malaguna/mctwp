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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.ImageDataDAO;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindImagesByTask extends ResultCommand<List<ImageData>> {
	private ImageDataDAO imageDao = null;
	private Task task = null;

	public FindImagesByTask(BeanFactory bf) {
		super(bf);
		imageDao = (ImageDataDAO)bf.getBean(BeanNames.IMAGE_DATA_DAO);
		setAction(ActionNames.FIND_IMAGES_STUDY);
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
	public Task getTask() {
		return task;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
		imageDao != null &&
				task != null;
	}	

	@Override
	public ResultCommand<List<ImageData>> runCommand() {
		Set<ImageData> auxSet = null; 
		this.setResult(new ArrayList<ImageData>());
		
		auxSet = task.getImages();
		if(auxSet != null)
			for(ImageData image : auxSet)
				this.getResult().add(image);
		
		return this;
	}
}
