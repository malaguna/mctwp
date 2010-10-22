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

import es.urjc.mctwp.service.blogic.ImageContainerTypeVisitor;
import es.urjc.mctwp.service.blogic.PersistImagesVisitor;

public class Group extends Protocolable implements java.io.Serializable {

	private static final long serialVersionUID = 7953514766208122992L;
	private String description;
	private Trial trial = null;
	private Set<Patient> patients = null;

	public Group() {
	}

	public Trial getTrial() {
		return this.trial;
	}

	public void setTrial(Trial trial) {
		this.trial = trial;
	}

	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	public String getDescription() {
		return description;
	}

	public void setPatients(Set<Patient> patients) {
		this.patients = patients;
	}

	public Set<Patient> getPatients() {
		return patients;
	}
	
	/**
	 * Convenience method to setup correctly relation between 
	 * case and study
	 * 
	 * @param study
	 */
	public void addPatient(Patient patient){
		if(patient != null){
			if(patients == null)
				patients = new HashSet<Patient>();
			
			patients.add(patient);
			patient.setGroup(this);
		}
	}

	/**
	 * Convenience method to setup correctly relation between 
	 * case and study
	 * 
	 * @param study
	 */
	public void delPatient(Patient patient){
		if(patient != null){
			if(patients != null)
			
			patients.remove(patient);
			patient.setGroup(null);
		}
	}
	
	@Override
	public void accept(PersistImagesVisitor piv, List<String> imagesId,	String tempCol, Integer imgType) throws Exception {
		piv.visit(this, imagesId, tempCol, imgType);
	}

	@Override
	public Class<? extends ImageContainer> accept(ImageContainerTypeVisitor imcv) {
		return imcv.visit(this);
	}	

	@Override
	public String getType(){
		return "Group";
	}
	
	@Override
	public String getCollection(){
		return this.getTrial().getCollection();
	}	
	
	@Override
	public Set<ImageData> getAllImages(){
		Set<ImageData> auxSet = new HashSet<ImageData>();
		
		if(this.getPatients() != null)
			for(Patient patient : this.getPatients())
				auxSet.addAll(patient.getAllImages());
		
		return auxSet;
	}
}
