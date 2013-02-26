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

package es.urjc.mctwp.bbeans.admin;

import java.util.ArrayList;
import java.util.List;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestAdmAbstractBean;
import es.urjc.mctwp.modelo.ProcessDef;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.FindProcessDef;
import es.urjc.mctwp.service.commands.adminCmds.SaveProcessDef;

public class ProcessDefBean extends RequestAdmAbstractBean {
	private ProcessDef fSrcObject = new ProcessDef();
	private ProcessDef fEdcObject = new ProcessDef();
	private ProcessDef processDef = new ProcessDef();
	private List<ProcessDef> processDefList = null;

	
	public ProcessDef getFSrcObject() {return fSrcObject;}
	public void setFSrcObject(ProcessDef srcObject) {
		fSrcObject = srcObject;
	}
	
	public ProcessDef getFEdcObject() {return fEdcObject;}
	public void setFEdcObject(ProcessDef edcObject) {
		fEdcObject = edcObject;
	}
	
	public ProcessDef getProcessDef() {return processDef;}
	public void setProcessDef(ProcessDef prc) {
		this.processDef = prc;
	}
	
	public List<ProcessDef> getProcessDefList() {return processDefList;}
	public void setProcessDefList(List<ProcessDef> lst) {
		this.processDefList = lst;
	}

	/**
	 * Search Processes by criteria and returns valid list
	 * 
	 * @return
	 */
	public String accFindProcess(){
		List<ProcessDef> auxList = null;
		
		//Prepare for search by criteria
		fSrcObject.setOid(null);
		fSrcObject.setCode(null);
		
		Command cmd = getCommand(FindProcessDef.class);
		((FindProcessDef)cmd).setProcessDef(fSrcObject);
		cmd = runCommand(cmd);
		auxList = ((FindProcessDef)cmd).getResult();
		
		if(auxList != null){
			processDefList = new ArrayList<ProcessDef>();
			
			for(ProcessDef proc : auxList)
				processDefList.add(proc);
		}
		
		return ActionBeanNames.SEARCH_PROCESSES;
	}
	
	/**
	 * @return nextAction
	 */
	public String accEditProcess(){
		return ActionBeanNames.EDIT_PROCESS;
	}
	
	/**
	 * Save or creates an ProcessDef
	 * 
	 * @return
	 */
	public String accSaveProcess(){
		
		Command cmd = getCommand(SaveProcessDef.class);
		((SaveProcessDef)cmd).setProcessDef(fEdcObject);
		cmd = runCommand(cmd);
		
		return accFindProcess();
	}
}
