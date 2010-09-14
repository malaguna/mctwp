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

import es.urjc.mctwp.dao.RolDAO;
import es.urjc.mctwp.modelo.Rol;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class SaveRol extends Command {
	private RolDAO rolDao = null;
	private Rol rol = null;

	public SaveRol(BeanFactory bf) {
		super(bf);
		rolDao = (RolDAO)bf.getBean(BeanNames.ROL_DAO);
		setActionName(ActionNames.SAVE_ROL);
		setReadOnly(false);
	}
	
	public void setRol(Rol rol) {
		this.rol = rol;
	}
	public Rol getRol() {
		return rol;
	}
	
	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				rolDao != null &&
				rol != null;
	}

	@Override
	public Command runCommand() {
		String msgKey = (rol.getCode() == 0)?"audit.createRol":"audit.updateRol";
		rolDao.persist(rol);
		createLogComment(msgKey, rol.getName());
		return this;
	}
}
