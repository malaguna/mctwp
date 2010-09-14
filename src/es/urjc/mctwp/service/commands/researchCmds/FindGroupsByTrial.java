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

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.GroupDAO;
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindGroupsByTrial extends ResultCommand<List<Group>> {
	private GroupDAO groupDao = null;
	private Group filter = null;

	public FindGroupsByTrial(BeanFactory bf) {
		super(bf);
		groupDao = (GroupDAO)bf.getBean(BeanNames.GROUP_DAO);
		setActionName(ActionNames.FIND_GROUPS_TRIAL);
	}
	
	public void setFilter(Group filter) {
		this.filter = filter;
	}

	public Group getFilter() {
		return filter;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				groupDao != null;
	}
	
	@Override
	public ResultCommand<List<Group>> runCommand() {
		this.setResult(groupDao.findGroupsByTrialFiltered(getTrial(), filter));
		return this;
	}
}
