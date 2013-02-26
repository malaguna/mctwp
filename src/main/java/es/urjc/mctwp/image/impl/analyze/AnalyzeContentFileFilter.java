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

package es.urjc.mctwp.image.impl.analyze;

import java.io.File;
import java.io.FileFilter;

import es.urjc.mctwp.image.ImageUtils;

public class AnalyzeContentFileFilter implements FileFilter {
	private String baseName = null;
	
	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getBaseName() {
		return baseName;
	}

	@Override
	public boolean accept(File pathname) {
		boolean result = false;
		
		String name = ImageUtils.getFileName(pathname);
		if(baseName != null){
			if(baseName.equalsIgnoreCase(name)){
				String ext = ImageUtils.getFileExtension(pathname);
				result = ComplexAnalyzeImageImpl.ANALYZE_IMG_EXT.equalsIgnoreCase(ext);
			}
		}
		
		return result;
	}
}
