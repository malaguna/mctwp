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

import es.urjc.mctwp.dao.ModalityDAO;
import es.urjc.mctwp.modelo.Modality;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class SaveModality extends Command {
	private ModalityDAO modalityDao = null;
	private Modality modality = null;

	public SaveModality(BeanFactory bf) {
		super(bf);
		modalityDao = (ModalityDAO)bf.getBean(BeanNames.MODALITY_DAO);
		setAction(ActionNames.SAVE_MODALITY);
		setReadOnly(false);
	}
	
	public void setModality(Modality modality) {
		this.modality = modality;
	}
	public Modality getModality() {
		return modality;
	}
	
	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				modalityDao != null &&
				modality != null;
	}

	@Override
	public Command runCommand() {
		String msgKey = (modality.getCode() == 0)?"audit.createModality":"audit.updateModality";
		modalityDao.persist(modality);
		createLogComment(msgKey, modality.getName());
		return this;
	}
}
