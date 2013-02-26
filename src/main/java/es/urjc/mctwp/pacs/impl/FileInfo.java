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

//This file has been created using source code of dcm4che2 project created 
//by Gunter Zeilinger. Project dcm4che2

package es.urjc.mctwp.pacs.impl;

import java.io.File;

public class FileInfo {
	File f;
	String cuid;
	String iuid;
	String tsuid;
	long fmiEndPos;
	long length;
	
	public FileInfo(File f) {
		this.f = f;
		this.length = f.length();
	}
}
