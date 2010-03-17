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

import javax.faces.event.ActionEvent;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.FindTasksByImage;
import es.urjc.mctwp.service.commands.researchCmds.FindTasksByUser;
import es.urjc.mctwp.service.commands.researchCmds.LoadTask;
import es.urjc.mctwp.service.commands.researchCmds.ReassignTasks;

public class TasksBean extends TaskAbstractBean{
	public final int TASK_MODE_USR = 1;
	public final int TASK_MODE_IMG = 2;
	public final int TASK_MODE_SRC = 3;
	
	private Integer taskListMode = TASK_MODE_USR;
	private List<TaskTableItem> tasks = null;

	public List<TaskTableItem> getTasks(){
		return tasks;
	}

	public void setTasks(List<TaskTableItem> tasks){
		this.tasks = tasks;
	}	
	
	public Integer getTaskListMode() {
		return taskListMode;
	}
	
	public void setTaskListMode(Integer taskListMode) {
		this.taskListMode = taskListMode;
	}
	
	public boolean isUserMode(){
		return taskListMode == TASK_MODE_USR;
	}
	
	public boolean isImageMode(){
		return taskListMode == TASK_MODE_IMG;
	}
	
	public boolean isSourceMode(){
		return taskListMode == TASK_MODE_SRC;
	}
		
	/**
	 * It retrieve task from ActionEvent and update selected 
	 * task into session state.
	 *  
	 * @param event
	 */
	public void alUpdateTaskSelected(ActionEvent event) {
		getSession().setTask(null);
		TaskTableItem task = (TaskTableItem)selectRowFromEvent(event, TaskTableItem.class);

		if(task != null){
			Command cmd = getCommand(LoadTask.class);
			((LoadTask)cmd).setTaskId(task.getCode());
			cmd = runCommand(cmd);
			getSession().setTask(((LoadTask)cmd).getResult());
		}
	}
	
	/**
	 * Wrapper action to list task of image loaded in session
	 * @return
	 */
	public String accListImgTasks(){
		taskListMode = TASK_MODE_IMG;
		return accListTasks();
	}
	
	/**
	 * Wrapper action to list task of a protocolable loaded in session
	 * @return
	 */
	public String accListSrcTasks(){
		taskListMode = TASK_MODE_SRC;
		return accListTasks();
	}
	
	/**
	 * Prepares a list of tasks depending on task list mode.
	 * 
	 * @return
	 */
	public String accListTasks(){
		loadPossibleUsers();
		
		if( (taskListMode == TASK_MODE_USR) ||
			( (taskListMode == TASK_MODE_IMG) && (getSession().getImage() != null) ) )
			populateTasks();
		
		return ActionBeanNames.LIST_TASKS;
	}
	
	/**
	 * It change the owner to a new user
	 * 
	 * @return next action
	 */
	public String accReassignTasks(){
		String result = null;
		
		if(tasks !=null){
			List<Integer> taskIds = new ArrayList<Integer>();
			
			for(TaskTableItem tti : tasks){
				if(tti.getSelected()){
					taskIds.add(tti.getCode());
					//msgTasks.append(tti.getCode() + ", ");
				}
			}
			
			Command cmd = getCommand(ReassignTasks.class);
			((ReassignTasks)cmd).setTask(taskIds);
			((ReassignTasks)cmd).setNewOwner(newOwner);
			((ReassignTasks)cmd).setComment(null);
			runCommand(cmd);
		}
		
		return result;
	}
	
	
	/**
	 * It populates the task list by taskListMode selected
	 * @return
	 */	
	private void populateTasks(){
		List<Task> aux = null;
		
		if(tasks == null){
			if(taskListMode.equals(TASK_MODE_USR)){
				Command cmd = getCommand(FindTasksByUser.class);
				cmd = runCommand(cmd);
				aux = ((FindTasksByUser)cmd).getResult();
				
			}else if(taskListMode.equals(TASK_MODE_IMG)){
				Command cmd = getCommand(FindTasksByImage.class);
				((FindTasksByImage)cmd).setImage(getSession().getImage());
				cmd = runCommand(cmd);
				aux = ((FindTasksByImage)cmd).getResult();
			}
			
			if(aux != null)
				tasks = createTaskItems(aux);
		}
	}	
	
	/**
	 * it prepares a list of task view items from a tasks list
	 * 
	 * @param tasks
	 * @return
	 */
	private List<TaskTableItem> createTaskItems(List<Task> tasks){
		List<TaskTableItem> result = null;
		
		if(tasks != null){
			result = new ArrayList<TaskTableItem>();
			
			for(Task t : tasks){
				TaskTableItem tti = new TaskTableItem();
				tti.setCode(t.getCode());
				tti.setDate(t.getEndDate());
				tti.setProcessName(t.getProcess().getName());
				result.add(tti);
			}
		}
		
		return result;
	}		
}
