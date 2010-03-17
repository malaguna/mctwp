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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.urjc.mctwp.service.blogic.PersistImagesVisitor;

public class Result extends ImageContainer implements java.io.Serializable {

	private static final long serialVersionUID = 6238060932231668433L;
	private Task task;
	private Date date = new Date();
	private String description = null;
	private Set<ImageData> images = null;

	public Task getTask() {
		return this.task;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}

	public Date getDate() {
		return this.date;
	}
	
	public void setDate(Date fecha) {
		this.date = fecha;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setImages(Set<ImageData> images) {
		this.images = images;
	}

	public Set<ImageData> getImages() {
		return images;
	}
	
	public void addImage(ImageData image){
		if(image != null){
			if(images == null)
				images = new HashSet<ImageData>();
			
			images.add(image);
			image.setResult(this);
		}
	}
	
	public void delImage(ImageData image){
		if(image != null){
			if(images != null){
				images.remove(image);
				image.setResult(null);
			}
		}
	}

	@Override
	public void accept(PersistImagesVisitor piv, List<String> imagesId,	String tempCol) throws Exception {
		piv.visit(this, imagesId, tempCol);
	}
	
	@Override
	public Set<ImageData> getAllImages(){
		return getImages();
	}

	@Override
	public String getCollection(){
		return getTask().getSource().getCollection();
	}
	
	@Override
	public String getType(){
		return "Result";
	}
}
