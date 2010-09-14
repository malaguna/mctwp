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


import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.service.blogic.TaskUtils;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class CreateTask extends Command {
	private TaskUtils taskUtils = null;
	private Protocolable source = null;
	private Integer processId = null;
	private Integer owner = null;
	private Task task = null;

	public CreateTask(BeanFactory bf) {
		super(bf);
		taskUtils = (TaskUtils)bf.getBean(BeanNames.TASK_UTILS);
		setActionName(ActionNames.CREATE_TASK);
		setReadOnly(false);
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}
	
	public void setOwner(Integer newOwner) {
		this.owner = newOwner;
	}
	
	public Integer getOwner() {
		return owner;
	}

	public void setSource(Protocolable source) {
		this.source = source;
	}

	public Protocolable getSource() {
		return source;
	}

	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public Integer getProcessId() {
		return processId;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				processId != null &&
				taskUtils != null &&
				source != null &&
				owner != null &&
				task != null;
	}

	@Override
	public Command runCommand() {
		taskUtils.createTask(task, owner, processId, source);
		createLogComment("audit.createTask", task.getCode());
		return this;
	}
}
