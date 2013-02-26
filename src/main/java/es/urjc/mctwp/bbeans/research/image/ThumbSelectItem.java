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

package es.urjc.mctwp.bbeans.research.image;

import java.io.Serializable;

/**
 * This is a model object for View layer. It mix ImageData, SelectItem and
 * ThumbNail information in order to allow JSF view represents correctly images
 * of a Study
 * 
 * @author Miguel Ángel Laguna
 * 
 */
public class ThumbSelectItem implements Serializable, Comparable<ThumbSelectItem> {

	private static final long serialVersionUID = 994902447827732494L;
	private Boolean selected = false;
	private String thumbId = null;
	private Integer imageId = null;
	private boolean result = false;
	private String patName = "";
	private String patCode = "";
	private String stdCode = "";
	private String path = null;

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean val) {
		selected = val;
	}

	public String getThumbId() {
		return thumbId;
	}

	public String getThumbIdShort() {
		return String.format("%.15s..", thumbId);
	}

	public void setThumbId(String thumbId) {
		this.thumbId = thumbId;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public void setPatName(String patName) {
		if(patName != null)
			this.patName = patName;
	}

	public String getPatName() {
		return patName;
	}

	public String getPatNameShort() {
		String result = "No Info";
		
		if(patName != null || !patName.isEmpty())
			if(patName.length() > 15)
				result = String.format("%.15s..", patName);
			else
				result = patName;
		
		return result;
	}

	public void setPatCode(String patCode) {
		if(patCode != null)
			this.patCode = patCode;
	}

	public String getPatCode() {
		return patCode;
	}

	public void setStdCode(String stdCode) {
		if(stdCode != null)
			this.stdCode = stdCode;
	}

	public String getStdCode() {
		return stdCode;
	}

	public String getStdCodeShort() {
		String result = "[No Info]";
		
		if(stdCode != null || !stdCode.isEmpty())
			if(stdCode.length() > 13)
				result = String.format("[%.13s..]", stdCode);
			else
				result = String.format("[%s]", stdCode);
		
		return result;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if(path == null)
			path ="";
		
		this.path = path;
	}

	@Override
	public int compareTo(ThumbSelectItem tsi) {
		int result = 0;
		
		if(tsi != null)
			if(this.getPatName() != null)
				return this.getPatName().compareTo(tsi.getPatName());
		
		return result;
	}
}
