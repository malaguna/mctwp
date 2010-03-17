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

package es.urjc.mctwp.service.blogic;

import java.util.List;
import java.util.Set;

import es.urjc.mctwp.dao.GenericDAO;
import es.urjc.mctwp.dao.ProcessDefDAO;
import es.urjc.mctwp.dao.ProtocolableDAO;
import es.urjc.mctwp.dao.TaskDAO;
import es.urjc.mctwp.dao.UserDAO;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.modelo.TaskLog;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.modelo.ProcessDef;

public class TaskUtils extends AbstractBLogic{
	private ProtocolableDAO protocolableDao = null;
	private ProcessDefDAO processDefDao = null;
	private UserDAO userDao = null;
	private TaskDAO taskDao = null;
	
	public ProcessDefDAO getProcessDefDao() {
		return processDefDao;
	}
	
	public void setProcessDefDao(ProcessDefDAO processDao) {
		this.processDefDao = processDao;
	}
	
	public void setProtocolableDao(ProtocolableDAO protocolableDao) {
		this.protocolableDao = protocolableDao;
	}

	public ProtocolableDAO getProtocolableDao() {
		return protocolableDao;
	}

	public UserDAO getUserDao() {
		return userDao;
	}
	
	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}
	
	public TaskDAO getTaskDao() {
		return taskDao;
	}
	
	public void setTaskDao(TaskDAO taskDao) {
		this.taskDao = taskDao;
	}
	
	/**
	 * Creates a task manually, the user selected the source, the processDef, the owner and 
	 * the time to do.
	 * 
	 * @param task
	 * @param ownerId
	 * @param processId
	 * @param source
	 */
	public void createTask(Task task, Integer ownerId, Integer processId, Protocolable source){
		ProcessDef process = null;

		//Retrieve associated objects
		source  = protocolableDao.findById(source.getCode());
		process = processDefDao.findById(processId);

		//Create task
		task.setOwner(userDao.findById(ownerId));
		task.setProcess(process);
		task.setSource(source);
		
		//Associate to source's images 
		Set<ImageData> auxSet = source.getAllImages();
		if(auxSet != null)
			for(ImageData image : auxSet){
				
				//There is no convenience method for this n:m relationship
				task.addImage(image);
				image.addTask(task);
			}
		
		taskDao.persist(task);
	}

	/**
	 * It saves a task and log (taskLog) the change made
	 * 
	 * @param t
	 */
	public void saveTask(Task task, User author, Integer newOwnerId, String comment){
		
		if(task != null){
			Task taux = taskDao.findById(task.getCode());
			User newOwner = userDao.findById(newOwnerId);
			author = userDao.findById(author.getCode());
			
			//For every possible change, we make a log
			TaskLog tl = new TaskLog();
			tl.setAuthor(author);
			if( (comment == null) || (comment.isEmpty()) )
				comment = null;
			tl.setComment(comment);
			
			//Check owner change
			if(!taux.getOwner().equals(newOwner)){
				tl.setField(TaskLog.TLF_OWNER);
				tl.setValue(newOwner.getFullName());
				taux.setOwner(newOwner);

			//Check date change
			}else if(!taux.getEndDate().equals(task.getEndDate())){
				tl.setField(TaskLog.TLF_DATE);
				tl.setValue(task.getEndDate().toString());
				taux.setEndDate(task.getEndDate());

			//Check status change
			}else if(taux.isDone() != task.isDone()){
				tl.setField(TaskLog.TLF_DONE);
				tl.setValue(Boolean.toString(task.isDone()));
				taux.setDone(task.isDone());
				
			//Only a comment is done
			}else {
				tl.setField(TaskLog.TLF_COMMENT);
			}

			taux.addLog(tl);
			taskDao.persist(taux);
		}
	}
	
	/**
	 * Reassign a list of tasks to a new user
	 * 
	 * @param tasks
	 * @param owner
	 */
	public void reassignTasks(List<Integer> tasks, User author, Integer newOwnerId, String comment){
		if( (tasks != null) && (!tasks.isEmpty()) ){
			
			User newOwner = userDao.findById(newOwnerId);
			userDao.reattach(author, GenericDAO.DIRTY_IGNORE);;

			for(Integer ti : tasks){
				Task t = taskDao.findById(ti);
				TaskLog tl = new TaskLog();
				
				tl.setAuthor(author);
				tl.setField(TaskLog.TLF_OWNER);
				tl.setValue(newOwner.getFullName());
				tl.setComment(comment);
				
				t.setOwner(newOwner);
				t.addLog(tl);
				
				taskDao.persist(t);
			}
		}
	}
}
