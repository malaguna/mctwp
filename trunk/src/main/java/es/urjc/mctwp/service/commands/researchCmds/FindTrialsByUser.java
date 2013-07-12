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

import es.urjc.mctwp.dao.TrialDAO;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindTrialsByUser extends ResultCommand<List<Trial>> {
	private TrialDAO trialDao = null;
	private Trial filter = null;

	public FindTrialsByUser(BeanFactory bf) {
		super(bf);
		trialDao = (TrialDAO)bf.getBean(BeanNames.TRIAL_DAO);
		setActionName(ActionNames.FIND_TRIALS_USER);
	}

	public void setFilter(Trial filter) {
		this.filter = filter;
	}

	public Trial getFilter() {
		return filter;
	}

	@Override
	public boolean isValidCommand(){
		return 	getActionName() != null && 
				getUserUtils() != null && 
				getUser() != null &&
				trialDao != null;
	}
	
	/**
	 * This operation is always authorized, other way it can't be used
	 * cause user is not rol assigned until it takes a trial. 
	 */
	@Override
	public boolean isCommandAuthorized(){
		return true;
	}
	
	@Override
	public ResultCommand<List<Trial>> runCommand() {
		this.setResult(trialDao.findTrialsByUserFiltered(getUser(), filter));
		return this;
	}
}
