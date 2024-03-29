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
import java.util.Set;

public class Task extends DomainObject {
	public static final String OPEN = "open";
	public static final String CLOSED = "closed";
	public static final String REJECTED = "rejected";

	private static final long serialVersionUID = -8921868396324541777L;
	private Date endDate = null;
	private User owner = null;
	private String status = OPEN;
	private Set<TaskLog> logs = null;
	private ProcessDef process = null;
	private Protocolable source = null;
	private Set<Result> results = null;
	private Set<ImageData> images = null;
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public void setEndDate(Date date) {
		this.endDate = date;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setStatus(String status) {
		if(OPEN.equalsIgnoreCase(status))
			this.status = status;
		else if(CLOSED.equalsIgnoreCase(status))
			this.status = status;
		else if(REJECTED.equalsIgnoreCase(status))
			this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setImages(Set<ImageData> images) {
		this.images = images;
	}

	public Set<ImageData> getImages() {
		return images;
	}
	
	public void setProcess(ProcessDef process) {
		this.process = process;
	}
	
	public ProcessDef getProcess() {
		return process;
	}
	
	public void addImage(ImageData image){
		if(image != null){
			if(images == null)
				images = new HashSet<ImageData>();
			
			images.add(image);
		}
	}
	
	public void delImage(ImageData image){
		if(image != null)
			if(images != null)
				images.remove(image);
	}
	
	public void setLogs(Set<TaskLog> logs) {
		this.logs = logs;
	}
	
	public Set<TaskLog> getLogs() {
		return logs;
	}
	
	public void setResults(Set<Result> results) {
		this.results = results;
	}

	public Set<Result> getResults() {
		return results;
	}

	public void addLog(TaskLog log){
		if(log != null){
			if(logs == null)
				logs = new HashSet<TaskLog>();
		
			logs.add(log);
			log.setTask(this);
		}
	}
	
	public void setSource(Protocolable source) {
		this.source = source;
	}
	
	public Protocolable getSource() {
		return source;
	}
}
