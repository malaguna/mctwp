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
import es.urjc.mctwp.service.Command;

public class DeleteResult extends Command {
	private ResultDAO resultDao = null;
	private Result result = null;

	public DeleteResult(BeanFactory bf) {
		super(bf);
		resultDao = (ResultDAO)bf.getBean(BeanNames.RESULT_DAO);
		setAction(ActionNames.DELETE_TASK);
		setReadOnly(false);
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
	public Result getResult() {
		return result;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				resultDao != null &&
				result != null;
	}

	@Override
	public Command runCommand() {
		resultDao.delete(result);
		return this;
	}
}
