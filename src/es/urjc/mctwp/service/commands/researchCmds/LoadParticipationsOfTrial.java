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

import java.util.Iterator;
import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.TrialDAO;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadParticipationsOfTrial extends ResultCommand<Iterator<Participation>> {
	private TrialDAO trialDao = null;

	public LoadParticipationsOfTrial(BeanFactory bf) {
		super(bf);
		trialDao = (TrialDAO)bf.getBean(BeanNames.TRIAL_DAO);
		setActionName(ActionNames.LOAD_TRIAL_PARTICPS);
	}
	
	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				trialDao != null;
	}
	
	@Override
	public void initCommandLog(){
	}

	@Override
	public void endsCommandLog(){
	}

	@Override
	public ResultCommand<Iterator<Participation>> runCommand() {
		//trialDao.reattach(getTrial(), GenericDAO.DIRTY_IGNORE);
		setTrial(trialDao.findById(getTrial().getCode()));
		this.setResult(getTrial().getMembers().iterator());
		return this;
	}
}
