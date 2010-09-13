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

package es.urjc.mctwp.dao;

import java.util.List;

import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Result;
import es.urjc.mctwp.modelo.Study;

public class ImageDataDAO extends GenericDAO<ImageData, Integer> {

	/**
	 * Get all images of a study, it does not retrieve 
	 * result images
	 * 
	 * @param study
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public List<ImageData> findImagesByStudy(Study study){
		List<ImageData> result = null;
		String query = "FROM ImageData i WHERE i.study=? AND i.result IS NULL ORDER BY i.code";
		
		try{
			result = this.getHibernateTemplate().find(query, study);
		}catch(RuntimeException re){
			logErrMsg("findImagesByStudy", re);
			throw re;
		}
		
		return result;
	}
	
	/**
	 * Get all images of a patient, it does not retrieve 
	 * result images
	 * 
	 * @param study
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public List<ImageData> findImagesByPatient(Patient patient){
		List<ImageData> result = null;
		String query = "FROM ImageData i WHERE i.study.patient=? AND i.result IS NULL ORDER BY i.code";
		
		try{
			result = this.getHibernateTemplate().find(query, patient);
		}catch(RuntimeException re){
			logErrMsg("findImagesByPatient", re);
			throw re;
		}
		
		return result;
	}
	
	/**
	 * Get all images of a study, it does not retrieve 
	 * result images
	 * 
	 * @param study
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public List<ImageData> findImagesByResult(Result res){
		List<ImageData> result = null;
		String query = "FROM ImageData i WHERE i.result = ? ORDER BY i.code";
		
		try{
			result = this.getHibernateTemplate().find(query, res);
		}catch(RuntimeException re){
			logErrMsg("findImagesByStudy", re);
			throw re;
		}
		
		return result;
	}

	/**
	 * Retrieves an imageData by imageId and study code
	 * 
	 * @param uid
	 * @param study
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ImageData findImageByUidAndStudy(String uid, Integer study){
		ImageData result = null;
		String query = "FROM ImageData i WHERE i.imageId = ? AND i.study.code = ?";
		
		try{
			Object args[] = new Object[] {uid, study};
			List<ImageData> aux = this.getHibernateTemplate().find(query, args);
			if(aux != null && !aux.isEmpty())
				result = aux.get(0);
		}catch(RuntimeException re){
			logErrMsg("findImagesByStudy", re);
			throw re;
		}
		
		return result;
	}
	
	/**
	 * Retrieves an imageData by imageId
	 * 
	 * @param uid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ImageData findImageByUid(String uid){
		ImageData result = null;
		String query = "FROM ImageData i WHERE i.imageId = ?";
		
		try{
			Object args[] = new Object[] {uid};
			List<ImageData> aux = this.getHibernateTemplate().find(query, args);
			if(aux != null && !aux.isEmpty())
				result = aux.get(0);
		}catch(RuntimeException re){
			logErrMsg("findImagesByUid", re);
			throw re;
		}
		
		return result;
	}
}
