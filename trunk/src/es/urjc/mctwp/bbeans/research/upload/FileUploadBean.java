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

package es.urjc.mctwp.bbeans.research.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.imageCmds.StoreTemporalImages;

public class FileUploadBean extends RequestInvAbstractBean{
	private UploadedFile img1;
	private UploadedFile img2;	
		
	@Override
	public void init(){}
	
	public UploadedFile getImg1() {return img1;}
	public void setImg1(UploadedFile img1) {
		this.img1 = img1;
	}
	
	public UploadedFile getImg2() {return img2;}
	public void setImg2(UploadedFile img2) {
		this.img2 = img2;
	}
	
	public String accUploadFiles() throws IOException {
		List<File> files = new ArrayList<File>();
		String base = getSession().getAbsoluteThumbDir();
		String action = "uploadSuccess";
		
		//Get uploaded files wrote to disk
		if(img1 != null){
			File f = new File(FilenameUtils.concat(base, img1.getName()));
			FileUtils.writeByteArrayToFile(f, img1.getBytes());
			files.add(f);
		}
		if(img2 != null){
			File f = new File(FilenameUtils.concat(base, img2.getName()));
			FileUtils.writeByteArrayToFile(f, img2.getBytes());
			files.add(f);
		}
		
		Command cmd = getCommand(StoreTemporalImages.class);
		((StoreTemporalImages)cmd).setFiles(files);
		runCommand(cmd);
					
		return action;
	}	
}
