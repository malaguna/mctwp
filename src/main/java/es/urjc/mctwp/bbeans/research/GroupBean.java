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

package es.urjc.mctwp.bbeans.research;

import java.util.List;

import javax.faces.event.ActionEvent;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.DeleteGroup;
import es.urjc.mctwp.service.commands.researchCmds.FindGroupsByTrial;
import es.urjc.mctwp.service.commands.researchCmds.LoadGroup;
import es.urjc.mctwp.service.commands.researchCmds.SaveGroup;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 * 
 */
public class GroupBean extends RequestInvAbstractBean {
	private Group filter = new Group();
	private Group group = new Group();
	private List<Group> groups = null;

	public void setFilter(Group filter) {
		this.filter = filter;
	}

	public Group getFilter() {
		return filter;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Group> getGroups() {
		return groups;
	}
	
	/**
	 * It retrieves group from ActionEvent and update selected group into session
	 * state
	 * 
	 * @param event
	 */
	public void alUpdateGroupSelected(ActionEvent event) {
		getSession().setGroup(null);
		Group group = (Group) selectRowFromEvent(event, Group.class);

		if (group != null) {
			Command cmd = getCommand(LoadGroup.class);
			((LoadGroup) cmd).setGroupId(group.getCode());
			cmd = runCommand(cmd);
			getSession().setGroup(((LoadGroup) cmd).getResult());
		}
	}

	/**
	 * Retrieves groups of trial from data base. If no trial is selected into
	 * session, it does nothing.
	 * 
	 * @return
	 */
	public String accListGroupsOfTrial() {
		if (getSession().getTrial() != null) {
			Command cmd = getCommand(FindGroupsByTrial.class);
			if( (filter.getCode() > 0 || (filter.getDescription() != null && !filter.getDescription().isEmpty())))
				((FindGroupsByTrial)cmd).setFilter(filter);
			cmd = runCommand(cmd);
			groups = ((FindGroupsByTrial) cmd).getResult();
		}

		return ActionBeanNames.LIST_GROUPS_TRIAL;
	}

	/**
	 * Prepare edition of a case
	 * 
	 * @return next view
	 */
	public String accEditGroup() {
		Command cmd = getCommand(LoadGroup.class);
		((LoadGroup) cmd).setGroupId(group.getCode());
		cmd = runCommand(cmd);
		group = ((LoadGroup) cmd).getResult();

		getSession().setGroup(group);
		return ActionBeanNames.EDIT_GROUP;
	}

	/**
	 * Save or update a new or existing case
	 * 
	 * @return next view
	 */
	public String accSaveGroup() {
		Command cmd = getCommand(SaveGroup.class);
		((SaveGroup) cmd).setGroup(group);
		runCommand(cmd);

		// Update group into session state info
		getSession().setGroup(group);

		return accListGroupsOfTrial();
	}

	/**
	 * Delete existing case
	 * 
	 * @return next view
	 */
	public String accDeleteGroup() {
		Command cmd = getCommand(DeleteGroup.class);
		((DeleteGroup) cmd).setGroup(group);
		runCommand(cmd);

		// Clean group into session state info
		getSession().setGroup(null);

		return accListGroupsOfTrial();
	}
}
