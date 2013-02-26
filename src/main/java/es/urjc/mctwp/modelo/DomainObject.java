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

import java.io.Serializable;

/**
 * This is the base class for every domain model object, that implements
 * valid OID for the entire hibernate life cicle of model object and also
 * implements valid hashCode and equals. 
 * 
 * @author miguel
 *
 */
public class DomainObject implements Serializable{

	private static final long serialVersionUID = -8865181522821958994L;
	private Integer code = 0;
	private String oid = null;

	public DomainObject(){
		oid = java.util.UUID.randomUUID().toString();
	}
	
	public Integer getCode() {return code;}
	public void setCode(Integer code) {
		this.code = code;
	}

	public String getOid() {return oid;}
	public void setOid(String code) {
		this.oid = code;
	}

	@Override
	public int hashCode(){
		return getOid().hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		boolean result = false;
		
		result = (this == obj);
		
		if( (!result) && (obj != null))
			if(obj instanceof DomainObject)
				result = this.getOid().equals(((DomainObject)obj).getOid());
		
		return result;
	}	
}
