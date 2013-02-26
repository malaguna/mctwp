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

import java.util.List;

import org.apache.myfaces.custom.navmenu.NavigationMenuItem;

import es.urjc.mctwp.bbeans.SessionAbstractBean;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.ServiceDelegate;
import es.urjc.mctwp.service.commands.userCmds.LoadMenuItems;

public class SessionAdmBean extends SessionAbstractBean{	

	public SessionAdmBean(){
		super();
	}

	@Override
	public void setService(ServiceDelegate service) {
		super.setService(service);
		setMenu(generateMenus());
	}
	
	/**
	 * It generates a menu, if current user is admin.
	 * 
	 * @param admin
	 * @return
	 */
	protected List<NavigationMenuItem> generateMenus(){
		Command cmd = getCommand(LoadMenuItems.class);
		cmd.setUser(getUser());
		cmd = runCommand(cmd);
		return super.generateMenus(((LoadMenuItems)cmd).getResult());
	}	
}
