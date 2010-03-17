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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class SessionUtils {
	private final static Logger logger = Logger.getLogger("SessionUtils");
	
	public static File getOrCreateFile(String base, String fileName){
		String  aux = null;
		File auxDir = null;
		
		aux = FilenameUtils.concat(base, fileName);
		auxDir = new File(aux);
		try{
			if(!auxDir.exists())
				FileUtils.forceMkdir(auxDir);
		}catch(Exception e){
			logger.error("Could not make temp directory for thumbnails: " + e.getMessage());
		}
		return auxDir;
	}
	
	public static void cleanFolder(File folder){
		
		if( (folder != null) && (folder.isDirectory()) ){
			try{
				FileUtils.cleanDirectory(folder);
			}catch(IOException ioe){
				logger.error("Could not clean user temp directory");
				//throw ioe;
			}
		}
	}
}
