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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.FileDAO;
import es.urjc.mctwp.modelo.File;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class GetAttachmentStream extends ResultCommand<InputStream> {
	private byte[] buffer = new byte[1024];
	public final static String TMP_PREFIX = "flt-";
	public final static String TMP_SUBFIX = ".tmp";	

	private FileDAO fileDao = null;
	private Integer fileId = null;

	public GetAttachmentStream(BeanFactory bf) {
		super(bf);
		fileDao = (FileDAO) bf.getBean(BeanNames.FILE_DAO);
		setAction(ActionNames.ADD_FILE_PRT);
	}

	public FileDAO getFileDao() {
		return fileDao;
	}

	public void setFileDao(FileDAO fileDao) {
		this.fileDao = fileDao;
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
				fileDao != null &&
				fileId != null;
	}

	@Override
	public ResultCommand<InputStream> runCommand() throws IOException, SQLException {
		File aux = fileDao.findById(fileId);
		
		//Temp file to return valid stream
		java.io.File temp = java.io.File.createTempFile(TMP_PREFIX, TMP_SUBFIX);
		temp.deleteOnExit();

		//Copy BLOB into Temp file
		InputStream fis = aux.getFile().getBinaryStream();
		OutputStream fos = new FileOutputStream(temp);
		while(fis.read(buffer) > 0)
			fos.write(buffer);
		fis.close();
		fos.close();
		
		//Return stream
		this.setResult(new FileInputStream(temp));
		return this;
	}
}
