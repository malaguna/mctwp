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
import java.util.Set;

public class Action extends DomainObject implements java.io.Serializable, Comparable<Action> {

	private static final long serialVersionUID = 4614056412279880866L;
	private boolean logeable = false;
	private String name;
	private String action;
	private Set<Rol> roles = null;
	
	public void setLogeable(boolean logeable) {
		this.logeable = logeable;
	}

	public boolean isLogeable() {
		return logeable;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String nombre) {
		this.name = nombre;
	}

	public void setAction(String accion) {
		this.action = accion;
	}

	public String getAction() {
		return action;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}

	public Set<Rol> getRoles() {
		return roles;
	}
	
	/**
	 * This is not convenience method, it is in Rol class, because the
	 * inverse is in Action mapping
	 * 
	 * @param rol
	 */
	public void addRol(Rol rol){
		if(rol != null){
			if(roles == null)
				roles = new HashSet<Rol>();
			
			roles.add(rol);
		}
	}
	
	/**
	 * This is not convenience method, it is in Rol class, because the
	 * inverse is in Action mapping
	 * 
	 * @param rol
	 */
	public void delRol(Rol rol){
		if(rol != null){
			if(roles != null)
				roles.remove(rol);
		}
	}

	@Override
	public int compareTo(Action arg0) {
		return (arg0 != null)?this.name.compareTo(arg0.name):-1;
	}
}
