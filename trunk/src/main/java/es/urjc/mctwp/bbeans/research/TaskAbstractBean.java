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
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.LoadParticipationsOfTrial;

/**
 * Implements common functionality for Task and Tasks BackBeans
 * 
 * @author miguel
 * 
 */
public class TaskAbstractBean extends RequestInvAbstractBean {
	protected Integer newOwner = null;
	protected List<SelectItem> possibleUsers = new ArrayList<SelectItem>();
	private SelectItem[] stateLst = new SelectItem[] {
			new SelectItem(Task.OPEN, Task.OPEN),
			new SelectItem(Task.CLOSED, Task.CLOSED),
			new SelectItem(Task.REJECTED, Task.REJECTED) };

	public Integer getNewOwner() {
		return newOwner;
	}

	public void setNewOwner(Integer no) {
		this.newOwner = no;
	}

	public List<SelectItem> getPossibleUsers() {
		return possibleUsers;
	}

	public void setPossibleUsers(List<SelectItem> possibleUsers) {
		this.possibleUsers = possibleUsers;
	}

	public void setStateLst(SelectItem[] states) {
	}

	public SelectItem[] getStateLst() {
		return stateLst;
	}

	/**
	 * Retrieves all possible users that can be assigned a task
	 * 
	 * @return
	 */
	protected void loadPossibleUsers() {
		Command cmd = getCommand(LoadParticipationsOfTrial.class);
		cmd = runCommand(cmd);
		Iterator<Participation> it = ((LoadParticipationsOfTrial) cmd)
				.getResult();

		if (it != null) {
			possibleUsers = new ArrayList<SelectItem>();

			while (it.hasNext()) {
				User u = it.next().getUser();
				possibleUsers.add(new SelectItem(u.getCode(), u.getFullName()));
			}
		}
	}
}
