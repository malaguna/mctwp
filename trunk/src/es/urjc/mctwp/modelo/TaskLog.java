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

public class TaskLog extends DomainObject{
	
	private static final long serialVersionUID = 3681272960252995895L;
	public static final String TLF_COMMENT = "comment";
	public static final String TLF_OWNER = "owner"; 
	public static final String TLF_STATUS = "status"; 
	public static final String TLF_DATE = "date";
	
	private User author = null;
	private Date stamp = new Date();
	private String field = null;
	private String value = null;
	private String comment = null;
	private Task task = null;
	
	public User getAuthor() {return author;}
	public Date getStamp() {return stamp;}
	public String getField() {return field;}
	public String getValue() {return value;}
	public String getComment() {return comment;}
	public Task getTask(){return task;}
	
	public void setAuthor(User author) {
		this.author = author;
	}
	public void setStamp(Date date) {
		this.stamp = date;
	}
	public void setField(String field) {
		this.field = field;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setComment(String coment) {
		this.comment = coment;
	}
	public void setTask(Task task){
		this.task = task;
	}
}
