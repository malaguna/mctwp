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

package es.urjc.mctwp.modelo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.urjc.mctwp.service.blogic.PersistImagesVisitor;

public class Patient extends Protocolable {

	private static final long serialVersionUID = 1365395335385371723L;
	private String hospitalId = null;
	private String completeName = null;
	private Group group = null;
	private Set<Study> studies = null;
	
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}
	public String getCompleteName() {
		return completeName;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public Group getGroup() {
		return group;
	}
	public void setStudies(Set<Study> studies) {
		this.studies = studies;
	}
	public Set<Study> getStudies() {
		return studies;
	}
	/**
	 * Convenience method for establish correctly relationship between 
	 * Patient and Study
	 *  
	 * @param action
	 */
	public void addStudy(Study study){
		if(study != null){
			if(studies == null)
				studies = new HashSet<Study>();
			
			study.setPatient(this);
			studies.add(study);
		}
	}

	/**
	 * Convenience method for remove correctly relationship between 
	 * Patient and Study
	 *  
	 * @param action
	 */
	public void delStudy(Study study){
		if(study != null){
			if(studies != null)
				studies.remove(study);
			
			study.setPatient(null);
		}
	}

	@Override
	public void accept(PersistImagesVisitor piv, List<String> imagesId,	String tempCol) throws Exception {
		piv.visit(this, imagesId, tempCol);
	}
	
	@Override
	public String getDescription(){
		return completeName;
	}

	@Override
	public String getType(){
		return "Patient";
	}	
	
	@Override
	public String getCollection(){
		return this.getGroup().getTrial().getCollection();
	}	
	
	@Override
	public Set<ImageData> getAllImages(){
		Set<ImageData> auxSet = new HashSet<ImageData>();
		
		if(this.getStudies() != null)
			for(Study study : this.getStudies())
				auxSet.addAll(study.getImages());
		
		return auxSet;
	}	
}
