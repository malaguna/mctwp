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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class ImageData extends DomainObject implements java.io.Serializable {

	private static final long serialVersionUID = -42348173197117786L;
	private String  imageId;
	private String  imageType;
	private Date date;
	private byte[] thumbnail;
	private Study study = null;
	private Result result = null;
	private Set<Task> tasks = null;

	public String getImageId() {
		return this.imageId;
	}

	public void setImageId(String idImagen) {
		this.imageId = idImagen;
	}

	public String getImageType() {
		return this.imageType;
	}

	public void setImageType(String tipo) {
		this.imageType = tipo;
	}
	
	public boolean isResultImage(){
		return result == null;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result resultado) {
		this.result = resultado;
	}

	public Study getStudy() {
		return this.study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date fecha) {
		this.date = fecha;
	}

	private void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	private byte[] getThumbnail() {
		return thumbnail;
	}
	
	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}	

	/**
	 * Convenience method, task.addImage is not convenience method
	 * 
	 * @param task
	 */
	public void addTask(Task task){
		if(task != null){
			if(tasks == null)
				tasks = new HashSet<Task>();
			
			tasks.add(task);
			task.addImage(this);
		}
	}

	public int getThumbnailSize() {
		return (int)thumbnail.length;
	}

	public InputStream getThumbnailStream() throws SQLException {
		if (getThumbnail() == null)
			return null;
		
		return new ByteArrayInputStream(getThumbnail());
	}

	public void setThumbanilContent( InputStream sourceStream ) throws IOException {
		setThumbnail(IOUtils.toByteArray(sourceStream));
	}
}
