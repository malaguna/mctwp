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

import es.urjc.mctwp.dao.ImageTypeDAO;
import es.urjc.mctwp.modelo.ImageType;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadImageType extends ResultCommand<ImageType> {
	private String[] exclude = null;
	private Integer code = null;
	private ImageTypeDAO dao = null;

	public LoadImageType(BeanFactory bf) {
		super(bf);
		dao = (ImageTypeDAO)bf.getBean(BeanNames.IMAGETYPE_DAO);
		setActionName(ActionNames.LOAD_IMAGETYPE);
	}
	
	public void setCode(Integer code) {
		this.code = code;
	}
	public Integer getCode() {
		return code;
	}
	public void setExcludeProps(String[] exclude) {
		this.exclude = exclude;
	}
	public String[] getExcludeProps() {
		return exclude;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				code != null &&
				dao != null;
	}	

	@Override
	public ResultCommand<ImageType> runCommand() {
		this.setResult(dao.findById(code));
		return this;
	}
}
