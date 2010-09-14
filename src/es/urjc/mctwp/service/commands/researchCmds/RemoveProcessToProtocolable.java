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

import es.urjc.mctwp.dao.ProcessDAO;
import es.urjc.mctwp.dao.ProtocolableDAO;
import es.urjc.mctwp.modelo.Process;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class RemoveProcessToProtocolable extends Command {
	private ProtocolableDAO protocolableDao = null;
	private ProcessDAO processDao = null;
	private Protocolable source = null;
	private Integer processId = null;

	public RemoveProcessToProtocolable(BeanFactory bf) {
		super(bf);
		protocolableDao = (ProtocolableDAO)bf.getBean(BeanNames.PROTOCOLABLE_DAO);
		processDao = (ProcessDAO)bf.getBean(BeanNames.PROCESS_DAO);
		setActionName(ActionNames.REMOVE_PROCESS_PRT);
		setReadOnly(false);
	}
	
	public void setProtocolable(Protocolable source) {
		this.source = source;
	}
	public Protocolable getProtocolable() {
		return source;
	}
	public void setProcessId(Integer process) {
		this.processId = process;
	}
	public Integer getProcessId() {
		return processId;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				protocolableDao != null &&
				processDao != null &&
				processId != null &&
				source != null;
	}

	@Override
	public Command runCommand() {
		Process process = processDao.findById(processId);
		source.delProcess(process);
		protocolableDao.merge(source);
		createLogComment("audit.removeProcessProtocolable", process.getProcessDef().getName(), source.getType(), source.getDescription());
		
		return this;
	}
}
