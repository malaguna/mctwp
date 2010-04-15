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

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.ImageDataDAO;
import es.urjc.mctwp.dao.TaskDAO;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class DeleteTask extends Command {
	private TaskDAO taskDao = null;
	private ImageDataDAO imgDao = null;
	private Task task = null;

	public DeleteTask(BeanFactory bf) {
		super(bf);
		taskDao = (TaskDAO)bf.getBean(BeanNames.TASK_DAO);
		imgDao = (ImageDataDAO)bf.getBean(BeanNames.IMAGE_DATA_DAO);
		setAction(ActionNames.DELETE_TASK);
		setReadOnly(false);
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
				taskDao != null &&
				imgDao != null &&
				task != null;
	}

	@Override
	public Command runCommand() {
		List<Integer> imgIds = null;
		task = taskDao.findById(task.getCode());
		
		if(task.getImages() != null){
			imgIds = new ArrayList<Integer>();
			
			for(ImageData image : task.getImages()){
				imgIds.add(image.getCode());
			}
			
			for(Integer imgId : imgIds){
				ImageData image = imgDao.findById(imgId);
				image.delTask(task);
				imgDao.persist(image);
			}
		}
		
		taskDao.delete(task);
		return this;
	}
}
