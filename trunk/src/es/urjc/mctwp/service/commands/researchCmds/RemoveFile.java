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

import es.urjc.mctwp.dao.FileDAO;
import es.urjc.mctwp.modelo.File;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class RemoveFile extends Command {
	private FileDAO fileDao = null;
	private Integer fileId = null;

	public RemoveFile(BeanFactory bf) {
		super(bf);
		fileDao = (FileDAO) bf.getBean(BeanNames.FILE_DAO);
		setAction(ActionNames.REMOVE_FILE_PRT);
		setReadOnly(false);
	}

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	@Override
	public boolean isValidCommand() {
		return super.isValidCommand() && 
				fileId != null;
	}

	@Override
	public Command runCommand() {
		File file = fileDao.findById(fileId);
		fileDao.delete(file);
		
		createLogComment("audit.removeFileProtocolable", file
				.getName(), null, null);

		return this;
	}
}
