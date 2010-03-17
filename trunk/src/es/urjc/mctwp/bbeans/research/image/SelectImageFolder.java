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

package es.urjc.mctwp.bbeans.research.image;

import java.util.HashSet;
import java.util.Set;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.Modality;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.imageCmds.LoadUserFolders;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public class SelectImageFolder extends RequestInvAbstractBean {
	private Set<Modality> userFolders = null;
	private Modality mUser = null;
	
	public void setUserFolders(Set<Modality> userFolders) {
		this.userFolders = userFolders;
	}
	public Set<Modality> getUserFolders() {
		return userFolders;
	}	
		
	/**
	 * 
	 * @return
	 */
	public String accSelectFolders(){
		createUserModality();
		populateUserFolders();	
		return ActionBeanNames.SELECT_FOLDER;
	}

	/**
	 * Create a fake modality that represents user folder where images
	 * uploaded by web application are stored.
	 */
	private void createUserModality(){
		mUser = new Modality();
		mUser.setCode(0);
		mUser.setName(getSession().getUser().getLogin());
		//mUser.setUser(getSession().getUser().getCode());
	}
	
	/**
	 * Get all modalities for what user is allowed to access. It includes
	 * fake user modality.
	 */
	private void populateUserFolders(){
		Command cmd = getCommand(LoadUserFolders.class);
		cmd = runCommand(cmd);
		userFolders = ((LoadUserFolders)cmd).getResult();
		
		if(userFolders == null)
			userFolders = new HashSet<Modality>();
		
		userFolders.add(mUser);
	}
}
