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

package es.urjc.mctwp.image.management;

import java.util.UUID;

import es.urjc.mctwp.dao.MapSeriesIdDAO;
import es.urjc.mctwp.modelo.MapSeriesId;

public class SeriesIdMapper {
	MapSeriesIdDAO dao = null;

	public MapSeriesIdDAO getDao() {return dao;}
	public void setDao(MapSeriesIdDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * Search a map by foreignId, if it is not found it creates new map
	 * and return generated UUID.
	 * 
	 * @param foreingId
	 * @return
	 */
	public String getSeriesPropId(String foreignId){

		MapSeriesId msi = dao.findById(foreignId);
		if(msi == null){
			msi = new MapSeriesId();
			msi.setForeignId(foreignId);
			msi.setPropId(UUID.randomUUID().toString());
			dao.persist(msi);
		}
		
		return msi.getPropId();
	}

	public String getSeriesForeignId(String propId){
		MapSeriesId msi = dao.findMapByPropId(propId);
		return (msi==null)?null:msi.getForeignId();
	}

}
