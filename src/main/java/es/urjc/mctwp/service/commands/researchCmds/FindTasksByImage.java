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

package es.urjc.mctwp.service.commands.researchCmds;

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

public class FindTasksByImage extends ResultCommand<List<Task>> {
	private ImageDataDAO imageDao = null;
	private ImageData image = null;

	public FindTasksByImage(BeanFactory bf) {
		super(bf);
		imageDao = (ImageDataDAO)bf.getBean(BeanNames.IMAGE_DATA_DAO);
		setActionName(ActionNames.FIND_TASKS_USER);
	}

	public void setImage(ImageData image) {
		this.image = image;
	}
	public ImageData getImage() {
		return image;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				imageDao != null &&
				image != null;
	}
	
	@Override
	public ResultCommand<List<Task>> runCommand() {
		image = imageDao.findById(image.getCode());
		Set<Task> aux = image.getTasks();
		
		if(aux != null){
			this.setResult(new ArrayList<Task>());
			for(Task task : aux)
				if(Task.OPEN.equalsIgnoreCase(task.getStatus()))
					this.getResult().add(task);
		}
		
		return this;
	}
}
