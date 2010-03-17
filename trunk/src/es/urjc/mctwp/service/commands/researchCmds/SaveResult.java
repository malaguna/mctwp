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

import es.urjc.mctwp.dao.ResultDAO;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.modelo.Result;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class SaveResult extends Command {
	private ResultDAO resultDao = null;
	private Result result = null;
	private Task task = null;

	public SaveResult(BeanFactory bf) {
		super(bf);
		resultDao = (ResultDAO)bf.getBean(BeanNames.RESULT_DAO);
		setAction(ActionNames.SAVE_RESULT);
		setReadOnly(false);
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
	public Result getResult() {
		return result;
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
				resultDao != null &&
				result != null &&
				task != null;
	}

	@Override
	public Command runCommand() {
		String msgKey = (result.getCode() == 0)?"audit.createResult":"audit.updateResult";
		
		result.setTask(task);
		resultDao.persist(result);
		createLogComment(msgKey, result.getDescription());
		return this;
	}
}
