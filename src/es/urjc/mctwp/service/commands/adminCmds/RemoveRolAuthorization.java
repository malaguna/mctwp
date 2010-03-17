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

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.ActionDAO;
import es.urjc.mctwp.dao.RolDAO;
import es.urjc.mctwp.modelo.Action;
import es.urjc.mctwp.modelo.Rol;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class RemoveRolAuthorization extends Command {
	private ActionDAO actionDao = null;
	private Integer actionId = null;
	private RolDAO rolDao = null;
	private Rol rol = null;

	public RemoveRolAuthorization(BeanFactory bf) {
		super(bf);
		actionDao = (ActionDAO)bf.getBean(BeanNames.ACTION_DAO);
		rolDao = (RolDAO)bf.getBean(BeanNames.ROL_DAO);
		setAction(ActionNames.REMOVE_ROL_AUTH);
		setReadOnly(false);
	}
	
	public void setRol(Rol rol) {
		this.rol = rol;
	}
	public Rol getRol() {
		return rol;
	}
	public void setActionId(Integer action) {
		this.actionId = action;
	}
	public Integer getActionId() {
		return actionId;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				rol != null &&
				actionDao != null &&
				actionId != null &&
				rolDao != null;
	}

	@Override
	public Command runCommand() {
		Rol auxRol = rolDao.merge(rol);
		Action action = actionDao.findById(actionId);
		auxRol.delAction(action);
		rolDao.persist(auxRol);
		
		createLogComment("audit.removeRolAuth", action.getName(), auxRol.getName());
		return this;
	}
}
