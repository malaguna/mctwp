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

package es.urjc.mctwp.service.blogic;

import java.util.List;

import es.urjc.mctwp.dao.ProtocolableDAO;
import es.urjc.mctwp.dao.ResultDAO;
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.modelo.Result;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.modelo.Trial;

/**
 * Implements visitor to persist images depending on ImageContainer kind
 * 
 * @author miguel
 *
 */
public class PersistImagesVisitor {
	private ProtocolableDAO protocolableDao = null;
	private ResultDAO resultDao = null;
	private ImageUtils utils = null;
	
	public void setUtils(ImageUtils utils) {
		this.utils = utils;
	}

	public ImageUtils getUtils() {
		return utils;
	}

	public void setResultDao(ResultDAO resultDao) {
		this.resultDao = resultDao;
	}

	public ResultDAO getResultDao() {
		return resultDao;
	}

	public void setProtocolableDao(ProtocolableDAO protocolableDao) {
		this.protocolableDao = protocolableDao;
	}

	public ProtocolableDAO getProtocolableDao() {
		return protocolableDao;
	}

	public void visit(Trial imgcon, List<String> imagesId, String tempColl, Integer imgType) throws Exception{
		Protocolable aux = protocolableDao.findById(imgcon.getCode());
		utils.persistImagesTrial(tempColl, (Trial)aux, imagesId, imgType);
	}

	public void visit(Group imgcon, List<String> imagesId, String tempColl, Integer imgType) throws Exception{
		Protocolable aux = protocolableDao.findById(imgcon.getCode());
		utils.persistImagesGroup(tempColl, (Group)aux, imagesId, imgType);
	}

	public void visit(Patient imgcon, List<String> imagesId, String tempColl, Integer imgType) throws Exception{
		Protocolable aux = protocolableDao.findById(imgcon.getCode());
		utils.persistImagesPatient(tempColl, (Patient)aux, imagesId, imgType);
	}
	
	public void visit(Study imgcon, List<String> imagesId, String tempColl, Integer imgType) throws Exception{
		Protocolable aux = protocolableDao.findById(imgcon.getCode());
		utils.persistImagesStudy(tempColl, (Study)aux, imagesId, imgType);
	}

	public void visit(Result imgcon, List<String> imagesId, String tempColl, Integer imgType) throws Exception{
		Result aux = resultDao.findById(imgcon.getCode());
		utils.persistImageResult(tempColl, aux, imagesId, imgType);
	}
}
