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

package es.urjc.mctwp.service.commands.adminCmds;

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.ProcessDefDAO;
import es.urjc.mctwp.modelo.ProcessDef;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindProcessDef extends ResultCommand<List<ProcessDef>> {
	private ProcessDefDAO processDao = null;
	private String[] exclude = null;
	private ProcessDef processDef = null;

	public FindProcessDef(BeanFactory bf) {
		super(bf);
		processDao = (ProcessDefDAO)bf.getBean(BeanNames.PROCESS_DEF_DAO);
		setAction(ActionNames.FIND_PROCESS_DEF);
	}
	
	public void setProcessDef(ProcessDef processDef) {
		this.processDef = processDef;
	}
	public ProcessDef getProcessDef() {
		return processDef;
	}
	public void setExcludeProps(String[] exclude) {
		this.exclude = exclude;
	}
	public String[] getExcludeProps() {
		return exclude;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				processDef != null &&
				processDao != null;
	}	

	@Override
	public ResultCommand<List<ProcessDef>> runCommand() {
		this.setResult(processDao.findByCriteria(processDef, exclude));
		return this;
	}
}
