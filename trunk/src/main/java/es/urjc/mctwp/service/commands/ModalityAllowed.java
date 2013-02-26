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

package es.urjc.mctwp.service.commands;

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.ModalityDAO;
import es.urjc.mctwp.modelo.Modality;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class ModalityAllowed extends ResultCommand<Boolean> {
	public ModalityDAO modalityDao = null;
	private String[] exclude = null;
	public String modality = null;
	
	public ModalityAllowed(BeanFactory bf) {
		super(bf);
		modalityDao = (ModalityDAO)bf.getBean(BeanNames.MODALITY_DAO);
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	@Override
	public boolean isValidCommand(){
		return  modalityDao != null &&
				modality != null;
	}
	
	@Override
	public boolean isCommandAuthorized(){
		return true;
	}
	
	@Override
	public void initCommandLog(){
	}
	
	@Override
	public void endsCommandLog(){
	}
	
	@Override
	public ResultCommand<Boolean> runCommand() throws Exception {
		Modality criteria = new Modality();
		criteria.setName(modality);
		List<Modality> mod = modalityDao.findByCriteria(criteria, exclude);
		
		this.setResult((mod == null) || (mod.size() == 0));
		
		return this;
	}

}
