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

import java.io.InputStream;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.modelo.File;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class AddFileToProtocolable extends Command {
	private SessionFactory factory = null;
	private InputStream stream = null;
	private File file = null;

	public AddFileToProtocolable(BeanFactory bf) {
		super(bf);
		factory = (SessionFactory) bf.getBean(BeanNames.SESSION_FACTORY);
		setActionName(ActionNames.ADD_FILE_PRT);
		setReadOnly(false);
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public InputStream getStream() {
		return stream;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public boolean isValidCommand() {
		return super.isValidCommand() 
				&& factory != null
				&& file != null
				&& stream != null;
	}

	@Override
	public Command runCommand() {
		Session sesion = factory.getCurrentSession();
		file.setFile(Hibernate.createBlob(stream, file.getSize(), sesion));
		sesion.persist(file);
		
		createLogComment("audit.addFileProtocolable", file.getName(), null, null);
		
		return this;
	}
}
