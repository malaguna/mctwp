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

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.LoadTrial;
import es.urjc.mctwp.service.commands.researchCmds.FindTrialsByUser;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 * 
 */
public class TrialBean extends RequestInvAbstractBean {
	private Trial filter = new Trial();
	private Trial trial = new Trial();
	private List<Trial> trials = null;

	@Override
	protected void init() {
	}

	public void setFilter(Trial filter) {
		this.filter = filter;
	}

	public Trial getFilter() {
		return filter;
	}

	public void setTrial(Trial trial) {
		this.trial = trial;
	}

	public Trial getTrial() {
		return trial;
	}

	public List<Trial> getTrials() {
		return trials;
	}

	public void setTrials(List<Trial> list) {
		trials = list;
	}

	/**
	 * It retrieve trial from ActionEvent and update selected trial into session
	 * state
	 * 
	 * @param event
	 */
	public void alUpdateTrialSelected(ActionEvent event) {
		Trial trial = (Trial) selectRowFromEvent(event, Trial.class);

		if (trial != null) {
			Command cmd = getCommand(LoadTrial.class);
			((LoadTrial) cmd).setTrialCode(trial.getCode());
			cmd = runCommand(cmd);
			getSession().setTrial(((LoadTrial) cmd).getResult());
		}
	}

	/**
	 * Retrieves trials of user from data base
	 * 
	 * @return
	 */
	public String accListTrials() {
		Command cmd = getCommand(FindTrialsByUser.class);
		((FindTrialsByUser)cmd).setFilter(filter);
		cmd = runCommand(cmd);
		trials = ((FindTrialsByUser) cmd).getResult();

		// Sincronize trials session info
		if (trials != null) {
			List<SelectItem> aux = getSession().getTrials();

			if ((aux == null) || (aux.size() != trials.size())) {
				aux = new ArrayList<SelectItem>();

				for (Trial t : trials)
					aux.add(new SelectItem(t.getCode(), t.getName()));

				getSession().setTrials(aux);
			}
		}

		return ActionBeanNames.LIST_TRIALS_USER;
	}

	/**
	 * Prepare a trial for edition
	 * 
	 * @return next view
	 */
	public String accEditTrial() {
		getSession().setTrial(this.trial);
		return ActionBeanNames.EDIT_TRIAL_RES;
	}
}