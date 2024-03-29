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

import es.urjc.mctwp.dao.ProcessDefDAO;
import es.urjc.mctwp.dao.ProtocolableDAO;
import es.urjc.mctwp.modelo.ProcessDef;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindNotAssignedProcesses extends ResultCommand<List<ProcessDef>> {
	private ProtocolableDAO protocolableDao = null;
	private ProcessDefDAO processDao = null;
	private Protocolable source = null;

	public FindNotAssignedProcesses(BeanFactory bf) {
		super(bf);
		protocolableDao = (ProtocolableDAO)bf.getBean(BeanNames.PROTOCOLABLE_DAO);
		processDao = (ProcessDefDAO)bf.getBean(BeanNames.PROCESS_DEF_DAO);
		setActionName(ActionNames.FIND_ALL_PROCESS);
	}
	
	public void setSource(Protocolable source){
		this.source = source;
	}
	public Protocolable getSource(){
		return source;
	}
	
	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				protocolableDao != null &&
				processDao != null &&
				source != null;
	}

	@Override
	public ResultCommand<List<ProcessDef>> runCommand() {
		List<ProcessDef> auxList = processDao.findAll();
		Protocolable auxSource = protocolableDao.findById(source.getCode());
		
		//Prepare SelectItem list with appropriate processes
		if( (auxList != null) && (auxSource != null) ){
			this.setResult(new ArrayList<ProcessDef>());
			for(ProcessDef processDef : auxList)
				if(!auxSource.hasProcessWithDef(processDef))
					this.getResult().add(processDef);
		}		
		
		return this;
	}
}
