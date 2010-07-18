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

package es.urjc.mctwp.bbeans.research;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.modelo.ProcessDef;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.CreateTask;
import es.urjc.mctwp.service.commands.researchCmds.DeleteTask;
import es.urjc.mctwp.service.commands.researchCmds.FindAllProcess;
import es.urjc.mctwp.service.commands.researchCmds.LoadTask;
import es.urjc.mctwp.service.commands.researchCmds.SaveTask;

/**
 * 
 * @author Miguel Ángel Laguna
 *
 */
public class TaskBean extends TaskAbstractBean{
	private List<SelectItem> processes = new ArrayList<SelectItem>();
	private Integer idProcessSeleted = null;
	private String taskComment = null;
	private Task task = new Task();
	
	public void setTask(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}
	
	public void setTaskComment(String var){
		this.taskComment = var;
	}
	
	public String getTaskComment(){
		return taskComment;
	}

	public void setProcesses(List<SelectItem> processes) {
		this.processes = processes;
	}
	
	public List<SelectItem> getProcesses() {
		return processes;
	}
	
	public void setIdProcessSeleted(Integer idProcessSeleted) {
		this.idProcessSeleted = idProcessSeleted;
	}

	public Integer getIdProcessSeleted() {
		return idProcessSeleted;
	}

	/**
	 * Retrieve using session state, the current source object
	 * to which task will be associated
	 * 
	 * @return
	 */
	private Protocolable currentSource(){
		Protocolable result = null;
		
		//Get appropriate container to persist images
		if(getSession().getStudy() != null)
			result = getSession().getStudy();
		else if(getSession().getPatient() != null)
			result = getSession().getPatient();
		else if(getSession().getGroup() != null)
			result = getSession().getGroup();
		else 
			result = getSession().getTrial();
		
		return result;
	}
	
	/**
	 * Prepare edition of a task
	 * 
	 * @return next view
	 */
	public String accEditTask(){
		loadPossibleUsers();
		
		Command cmd = getCommand(LoadTask.class);
		((LoadTask)cmd).setTaskId(task.getCode());
		cmd = runCommand(cmd);
		task = ((LoadTask)cmd).getResult();
		
		setNewOwner(task.getOwner().getCode());
		getSession().setTask(task);

		return ActionBeanNames.EDIT_TASK;
	}
	
	/**
	 * Prepare creation of a task
	 * 
	 * @return next view
	 */
	public String accNewTask(){
		List<ProcessDef> auxList = null;
		
		loadPossibleUsers();
		
		Command cmd = getCommand(FindAllProcess.class);
		cmd = runCommand(cmd);
		auxList = ((FindAllProcess)cmd).getResult();
		
		if(auxList != null)
			for(ProcessDef process : auxList)
				processes.add(new SelectItem(process.getCode(), process.getDescription()));

		return ActionBeanNames.CREATE_TASK;
	}
	
	/**
	 * Update an existing task
	 * 
	 * @return next view
	 */
	public String accSaveTask(){		
		Command cmd = getCommand(SaveTask.class);
		((SaveTask)cmd).setTask(task);
		((SaveTask)cmd).setNewOwner(newOwner);
		((SaveTask)cmd).setComment(taskComment);

		runCommand(cmd);
		taskComment = null;
		return accEditTask();
	}
	
	/**
	 * Create manually a new task
	 * 
	 * @return next view
	 */
	public String accCreateTask(){
		Command cmd = getCommand(CreateTask.class);
		((CreateTask)cmd).setTask(task);
		((CreateTask)cmd).setOwner(newOwner);
		((CreateTask)cmd).setSource(currentSource());
		((CreateTask)cmd).setProcessId(idProcessSeleted);
		
		runCommand(cmd);
		getSession().setTask(task);
		return accEditTask();
	}
	
	/**
	 * Delete existing task
	 * 
	 * @return next view
	 */
	public String accDeleteTask(){		
		Command cmd = getCommand(DeleteTask.class);
		((DeleteTask)cmd).setTask(task);
		runCommand(cmd);
		
		getSession().setTask(null);
		
		TasksBean bean = (TasksBean) getBackBeanReference("tasksBean");
		return bean.accListTasks();
	}
}
