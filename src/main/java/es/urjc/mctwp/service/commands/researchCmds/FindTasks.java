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

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class FindTasks extends ResultCommand<List<Task>> {
	private SessionFactory sf = null;
	private String taskState = null;
	private Integer process = null;
	private Date startDate = null;
	private Date endDate = null;
	private Integer owner = null;

	public FindTasks(BeanFactory bf) {
		super(bf);
		sf = (SessionFactory)bf.getBean(BeanNames.SESSION_FACTORY);
		setActionName(ActionNames.FIND_TASKS);
	}

	public SessionFactory getSf() {
		return sf;
	}

	public void setSf(SessionFactory sf) {
		this.sf = sf;
	}

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public Integer getProcess() {
		return process;
	}

	public void setProcess(Integer process) {
		this.process = process;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				 sf != null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public ResultCommand<List<Task>> runCommand() {
		Session sesion = sf.getCurrentSession();
		Criteria criteria = sesion.createCriteria(Task.class);
		
		if(startDate != null)
			criteria.add(Restrictions.ge("endDate", startDate));

		if(endDate != null)
			criteria.add(Restrictions.le("endDate", endDate));
		
		if(owner != null)
			criteria.createCriteria("owner").add(Restrictions.eq("code", owner));
		
		if(process != null)
			criteria.createCriteria("process").add(Restrictions.eq("code", process));
		
		if(taskState != null)
			criteria.add(Restrictions.eq("status", taskState));
		
		this.setResult(criteria.list());
		return this;
	}
}
