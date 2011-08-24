package es.urjc.mctwp.bbeans.research;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.modelo.ProcessDef;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.FindAllProcess;
import es.urjc.mctwp.service.commands.researchCmds.FindTasks;
import es.urjc.mctwp.service.commands.researchCmds.LoadTask;

public class TasksSearchBean extends TaskAbstractBean {
	private List<SelectItem> processList = new ArrayList<SelectItem>();
	private List<Task> taskList = null;
	private String taskState = null;
	private Integer process = null;
	private Date startDate = null;
	private Date endDate = null;

	public void setProcessList(List<SelectItem> processList) {
		this.processList = processList;
	}

	public List<SelectItem> getProcessList() {
		return processList;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

	public List<Task> getTaskList() {
		return taskList;
	}

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public Integer getProcess() {
		return process;
	}

	public void setProcess(Integer process) {
		this.process = process;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * It retrieve task from ActionEvent and update selected 
	 * task into session state.
	 *  
	 * @param event
	 */
	public void alUpdateTaskSelected(ActionEvent event) {
		getSession().setTask(null);
		Task task = (Task)selectRowFromEvent(event, Task.class);

		if(task != null){
			Command cmd = getCommand(LoadTask.class);
			((LoadTask)cmd).setTaskId(task.getCode());
			cmd = runCommand(cmd);
			getSession().setTask(((LoadTask)cmd).getResult());
		}
	}
	
	public String accPrepareSearch(){
		loadPossibleUsers();
		loadPossibleProcess();
		
		return ActionBeanNames.SEARCH_TASKS;
	}
	
	public String accSearchTasks(){
		Command cmd = getCommand(FindTasks.class);
		((FindTasks)cmd).setEndDate(endDate);
		((FindTasks)cmd).setOwner(getNewOwner());
		((FindTasks)cmd).setProcess(process);
		((FindTasks)cmd).setStartDate(startDate);
		((FindTasks)cmd).setTaskState(taskState);
		cmd = runCommand(cmd);
		taskList = ((FindTasks)cmd).getResult();
		
		return ActionBeanNames.SEARCH_TASKS;
	}
	
	private void loadPossibleProcess(){
		List<ProcessDef> auxLst = null;
		
		Command cmd = getCommand(FindAllProcess.class);
		cmd = runCommand(cmd);
		auxLst = ((FindAllProcess)cmd).getResult();
		
		if(auxLst != null){
			processList = new ArrayList<SelectItem>();
			
			for(ProcessDef process : auxLst)
				processList.add(new SelectItem(process.getCode(), process.getDescription()));
		}
	}
}
