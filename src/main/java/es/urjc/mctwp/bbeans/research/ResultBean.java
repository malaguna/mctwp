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

import java.util.List;

import javax.faces.event.ActionEvent;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.Result;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.DeleteResult;
import es.urjc.mctwp.service.commands.researchCmds.FindResultsByTask;
import es.urjc.mctwp.service.commands.researchCmds.LoadResult;
import es.urjc.mctwp.service.commands.researchCmds.SaveResult;

public class ResultBean extends RequestInvAbstractBean {
	private Result result = new Result();
	private List<Result> results = null;
	
	public void setResult(Result result) {
		this.result = result;
	}
	
	public Result getResult() {
		return result;
	}
	
	public void setResults(List<Result> results) {
		this.results = results;
	}
	
	public List<Result> getResults() {
		return results;
	}
	
	
	/**
	 * It retrieve result from ActionEvent and update selected 
	 * result into session state
	 *  
	 * @param event
	 */
	public void alUpdateResultSelected(ActionEvent event) {
		getSession().setResult(null);
		Result result = (Result)selectRowFromEvent(event, Result.class);

		if(result != null){
			Command cmd = getCommand(LoadResult.class);
			((LoadResult)cmd).setResultId(result.getCode());
			cmd = runCommand(cmd);
			getSession().setResult(((LoadResult)cmd).getResult());
		}
	}
	
	public String accListResultsOfTask(){
		
		if(getSession().getTask() != null){
			Command cmd = getCommand(FindResultsByTask.class);
			((FindResultsByTask)cmd).setTask(getSession().getTask());
			runCommand(cmd);
			results = ((FindResultsByTask)cmd).getResult();
		}
		
		return ActionBeanNames.LIST_RESULTS_TASK;
	}
	
	public String accEditResult(){
		Command cmd = getCommand(LoadResult.class);
		((LoadResult)cmd).setResultId(result.getCode());
		cmd = runCommand(cmd);
		result = ((LoadResult)cmd).getResult();

		getSession().setResult(result);
		return ActionBeanNames.EDIT_RESULT;
	}
	
	public String accSaveResult(){
		Command cmd = getCommand(SaveResult.class);
		((SaveResult)cmd).setResult(result);
		((SaveResult)cmd).setTask(getSession().getTask());
		runCommand(cmd);
		
		//Update result into session
		getSession().setResult(result);
		
		return accListResultsOfTask();
	}

	public String accDeleteResult(){
		Command cmd = getCommand(DeleteResult.class);
		((DeleteResult)cmd).setResult(result);
		runCommand(cmd);
		
		return accListResultsOfTask();
	}
}
