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

import es.urjc.mctwp.dao.ParticipationDAO;
import es.urjc.mctwp.dao.TrialDAO;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class RemoveTrialParticipation extends Command {
	private ParticipationDAO partDao = null;
	private Participation part = null;
	private TrialDAO trialDao = null;
	private Trial trial = null;

	public RemoveTrialParticipation(BeanFactory bf) {
		super(bf);
		partDao = (ParticipationDAO)bf.getBean(BeanNames.PARTICIPATION_DAO);
		trialDao = (TrialDAO)bf.getBean(BeanNames.TRIAL_DAO);
		setAction(ActionNames.REMOVE_TRIAL_PART);
		setReadOnly(false);
	}
	
	public void setTrial(Trial trial){
		this.trial = trial;
	}
	public Trial getTrial(){
		return trial;
	}
	public void setParticipation(Participation participation) {
		this.part = participation;
	}
	public Participation getParticipation() {
		return part;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				part != null &&
				trial != null &&
				trialDao != null &&
				partDao != null;
	}

	@Override
	public Command runCommand() {
		trial.delMember(part);
		partDao.delete(part);
		trialDao.persist(trial);
		createLogComment("audit.removeTrialPart", trial.getName(), part.getUser().getFullName());
		return this;
	}
}
