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

import es.urjc.mctwp.dao.ResultDAO;
import es.urjc.mctwp.modelo.Result;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadResult extends ResultCommand<Result> {
	private ResultDAO resultDao = null;
	private Integer resultId = null;

	public LoadResult(BeanFactory bf) {
		super(bf);
		resultDao = (ResultDAO)bf.getBean(BeanNames.RESULT_DAO);
		setActionName(ActionNames.LOAD_RESULT);
	}

	public void setResultId(Integer id) {
		this.resultId = id;
	}
	public Integer getResultId() {
		return resultId;
	}
	
	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() &&
				resultDao != null &&
				resultId != null;
	}

	@Override
	public ResultCommand<Result> runCommand() {
		this.setResult(resultDao.findById(resultId));
		return this;
	}
}
