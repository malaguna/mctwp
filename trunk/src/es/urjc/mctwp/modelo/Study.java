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


import java.util.Date;
import java.util.List;
import java.util.Set;

import es.urjc.mctwp.service.blogic.ImageContainerTypeVisitor;
import es.urjc.mctwp.service.blogic.PersistImagesVisitor;

public class Study extends Protocolable implements java.io.Serializable {

	private static final long serialVersionUID = -4834785989309195804L;
	private Date date = null;
	private String studyType;
	private String hospitalId;
	private Patient patient = null;	
	private Set<ImageData> images = null;

	public Patient getPatient() {
		return this.patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date fecha) {
		this.date = fecha;
	}

	public String getStudyType() {
		return this.studyType;
	}

	public void setStudyType(String tipo) {
		this.studyType = tipo;
	}

	public String getHospitalId() {
		return this.hospitalId;
	}

	public void setHospitalId(String hid) {
		this.hospitalId = hid;
	}

	public void setImages(Set<ImageData> imagenes) {
		this.images = imagenes;
	}

	public Set<ImageData> getImages() {
		return images;
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
	public String getDescription(){
		String resultado = null;
		
		if(studyType != null){
			resultado = studyType;
			if(hospitalId != null)
				resultado += " - " + hospitalId;
		}else{
			if(hospitalId != null)
				resultado = hospitalId;
			else
				resultado = getCode().toString();
		}
			
		return resultado;
	}	

	@Override
	public String getType(){
		return "Study";
	}	
	
	@Override
	public String getCollection(){
		return this.getPatient().getGroup().getTrial().getCollection();
	}
	
	@Override
	public Set<ImageData> getAllImages(){
		return this.getImages();
	}	
}
