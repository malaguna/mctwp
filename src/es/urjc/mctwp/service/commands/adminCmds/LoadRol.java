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
import es.urjc.mctwp.service.ResultCommand;

public class LoadRol extends ResultCommand<Rol> {
	private String[] exclude = null;
	private Integer rolCode = null;
	private RolDAO rolDao = null;

	public LoadRol(BeanFactory bf) {
		super(bf);
		rolDao = (RolDAO)bf.getBean(BeanNames.ROL_DAO);
		setAction(ActionNames.LOAD_ROL);
	}
	
	public void setRolCode(Integer code) {
		this.rolCode = code;
	}
	public Integer getRolCode() {
		return rolCode;
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
				rolCode != null &&
				rolDao != null;
	}	

	@Override
	public ResultCommand<Rol> runCommand() {
		this.setResult(rolDao.findById(rolCode));
		return this;
	}
}
