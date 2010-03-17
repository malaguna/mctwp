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

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.StudyDAO;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadStudy extends ResultCommand<Study> {
	private StudyDAO studyDao = null;
	private Integer studyId = null;

	public LoadStudy(BeanFactory bf) {
		super(bf);
		studyDao = (StudyDAO)bf.getBean(BeanNames.STUDY_DAO);
		setAction(ActionNames.LOAD_STUDY);
	}

	public void setStudyId(Integer id) {
		this.studyId = id;
	}
	public Integer getStudyId() {
		return studyId;
	}
	
	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				studyDao != null &&
				studyId != null;
	}

	@Override
	public ResultCommand<Study> runCommand() {
		this.setResult(studyDao.findById(studyId));
		return this;
	}
}
