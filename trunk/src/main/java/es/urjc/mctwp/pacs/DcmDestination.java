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

package es.urjc.mctwp.pacs;

/**
 * Represents a valid DICOM receiver of an Image
 * 
 * @author Miguel Ángel Laguna
 *
 */
public class DcmDestination {
	private String hostname = null;
	private String aeTitle = null;
	private int port = 0;
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getAeTitle() {
		return aeTitle;
	}
	public void setAeTitle(String aeTitle) {
		this.aeTitle = aeTitle;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isValid(){
		boolean result = false;
		
		result  = (hostname != null) && (hostname.length() > 0);
		result &= (aeTitle != null)  && (aeTitle.length() > 0);
		result &= (port > 0);
		
		return result;
	}
}
