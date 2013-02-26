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

import es.urjc.mctwp.dao.ImageTypeDAO;
import es.urjc.mctwp.dao.ProcessDAO;
import es.urjc.mctwp.dao.ProcessDefDAO;
import es.urjc.mctwp.dao.ProtocolableDAO;
import es.urjc.mctwp.dao.UserDAO;
import es.urjc.mctwp.modelo.ProcessDef;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.modelo.Process;

public class AddProcessToProtocolable extends Command {
	private ProtocolableDAO protocolableDao = null;
	private ProcessDefDAO processDefDao = null;
	private ImageTypeDAO imageTypeDao = null;
	private ProcessDAO processDao = null;
	private UserDAO userDao = null;
	private Protocolable source = null;
	private Integer processDefId = null;
	private Integer imgType = null;
	private Integer ownerId = null;
	private Integer days = null;

	public AddProcessToProtocolable(BeanFactory bf) {
		super(bf);
		protocolableDao = (ProtocolableDAO)bf.getBean(BeanNames.PROTOCOLABLE_DAO);
		processDefDao = (ProcessDefDAO)bf.getBean(BeanNames.PROCESS_DEF_DAO);
		imageTypeDao = (ImageTypeDAO)bf.getBean(BeanNames.IMAGETYPE_DAO);
		processDao = (ProcessDAO)bf.getBean(BeanNames.PROCESS_DAO);
		userDao = (UserDAO)bf.getBean(BeanNames.USER_DAO);
		setActionName(ActionNames.ADD_PROCESS_PRT);
		setReadOnly(false);
	}
	
	public void setProtocolable(Protocolable source) {
		this.source = source;
	}
	public Protocolable getProtocolable() {
		return source;
	}
	public Integer getProcessDefId() {
		return processDefId;
	}
	public void setProcessDefId(Integer processDefId) {
		this.processDefId = processDefId;
	}
	public void setImgType(Integer imgType) {
		this.imgType = imgType;
	}

	public Integer getImgType() {
		return imgType;
	}

	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				protocolableDao != null &&
				processDefDao != null &&
				processDefId != null &&
				processDao != null &&
				userDao != null &&
				ownerId != null &&
				source != null;
	}

	@Override
	public Command runCommand() {
		ProcessDef pdef = processDefDao.findById(processDefId);
		User owner = userDao.findById(ownerId);

		Process process = new Process();
		process.setDaysToDo((days == null)?pdef.getDaysToDo():days);
		process.setProcessDef(pdef);
		process.setOwner(owner);
		process.setImgType(imageTypeDao.findById(imgType));
		
		source.addProcess(process);
		protocolableDao.persist(source);
		createLogComment("audit.addProcessProtocolable", pdef.getName(), source.getType(), source.getDescription());
		
		return this;
	}
}
