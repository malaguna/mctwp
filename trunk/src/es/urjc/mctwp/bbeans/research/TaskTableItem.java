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

package es.urjc.mctwp.bbeans.research;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a model object for View layer. It mix Task with a boolean attribute,
 * in order to allow JSF view represents correctly selected tasks from a table
 *  
 * @author Miguel Ángel Laguna
 *
 */
public class TaskTableItem implements Serializable{

	private static final long serialVersionUID = -1451486355240783384L;
	private Boolean selected = false;
	private Integer code = null;
	private String processName = null;
	private Date date = null;
	
	public Boolean getSelected() {return selected;}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Integer getCode() {return code;}
	public void setCode(Integer code) {
		this.code = code;
	}

	public String getProcessName() {return processName;}
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Date getDate() {return date;}
	public void setDate(Date date) {
		this.date = date;
	}
}
