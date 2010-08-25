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

import java.util.Date;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.GenericDAO;
import es.urjc.mctwp.dao.TrialDAO;
import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.management.ImageCollectionManager;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.Command;

public class SaveTrial extends Command {
	private ImageCollectionManager imcm = null;
	private TrialDAO trialDao = null;
	private Trial trial = null;

	public SaveTrial(BeanFactory bf) {
		super(bf);
		trialDao = (TrialDAO)bf.getBean(BeanNames.TRIAL_DAO);
		imcm = (ImageCollectionManager)bf.getBean(BeanNames.IMG_COL_MGR);
		setAction(ActionNames.SAVE_TRIAL);
		setReadOnly(false);
	}
	
	public void setTrial(Trial trial){
		this.trial = trial;
	}
	public Trial getTrial(){
		return trial;
	}

	@Override
	public boolean isValidCommand(){
		return  super.isValidCommand() && 
				trialDao != null &&
				trial != null &&
				imcm != null;
	}	

	@Override
	public Command runCommand() throws ImageCollectionException{
		String msgKey = (trial.getCode() == 0)?"audit.createTrial":"audit.updateTrial";
		
		//If trial is new, a new collection is created
		if(trial.getCode() == 0){

			Date time = new Date();
			trial.setCollection(Long.toString(time.getTime()));
			imcm.createCollection(trial.getCollection());
			trialDao.persist(trial);
		}else{
			trialDao.reattach(trial, GenericDAO.DIRTY_EVALUATION);
		}		

		createLogComment(msgKey, trial.getName());
		
		return this;
	}
}
