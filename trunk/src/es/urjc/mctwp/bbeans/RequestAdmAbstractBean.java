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

package es.urjc.mctwp.bbeans;

import es.urjc.mctwp.bbeans.admin.SessionAdmBean;
import es.urjc.mctwp.service.Command;

public abstract class RequestAdmAbstractBean extends RequestAbstractBean {
	private SessionAdmBean session = null;
	
	/**
	 * Void implementation
	 */
	@Override
	protected void init(){}
	
	public SessionAdmBean getSession() {return session;}
	public void setSession(SessionAdmBean session) {
		this.session = session;
		super.setService(session.getService());
		this.init();
	}
	
	@Override
	protected Command runCommand(Command cmd){
		Command result = cmd;
		
		if(cmd != null){
			cmd.setUser(session.getUser());
			result = super.runCommand(cmd);
		}
		
		return result;
	}
}
