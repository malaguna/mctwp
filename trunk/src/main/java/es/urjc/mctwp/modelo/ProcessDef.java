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

public class ProcessDef extends DomainObject implements java.io.Serializable {

	private static final long serialVersionUID = -4699031185965277874L;
	private String name = null;
	private String help = null;
	private Integer daysToDo = null;
	private String description = null;
	
	public String getName() {return name;}
	public String getHelp() {return help;}
	public Integer getDaysToDo() {return daysToDo;}
	public String getDescription() {return description;}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setHelp(String help) {
		this.help = help;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setDaysToDo(Integer daysToDo) {
		this.daysToDo = daysToDo;
	}
}
