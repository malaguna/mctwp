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

public class Process extends DomainObject {

	private static final long serialVersionUID = -3893014283892311091L;
	private ProcessDef processDef = null;
	private Protocolable source = null;
	private Integer daysToDo = null;
	private User owner = null;

	public ProcessDef getProcessDef() {
		return processDef;
	}

	public void setProcessDef(ProcessDef processDef) {
		this.processDef = processDef;
	}

	public void setSource(Protocolable source) {
		this.source = source;
	}

	public Protocolable getSource() {
		return source;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Integer getDaysToDo() {
		return daysToDo;
	}

	public void setDaysToDo(Integer daysToDo) {
		this.daysToDo = daysToDo;
	}
}
