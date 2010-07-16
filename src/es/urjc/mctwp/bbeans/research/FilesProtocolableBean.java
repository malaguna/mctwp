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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.GenericDownloadBean;
import es.urjc.mctwp.modelo.File;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.researchCmds.AddFileToProtocolable;
import es.urjc.mctwp.service.commands.researchCmds.GetAttachmentStream;
import es.urjc.mctwp.service.commands.researchCmds.LoadProtocolableAttachments;
import es.urjc.mctwp.service.commands.researchCmds.RemoveFile;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public class FilesProtocolableBean extends GenericDownloadBean {
	private Protocolable source = new Protocolable();
	private List<FileItem> files = null;
	private UploadedFile upFile = null; 
	private FileItem file = new FileItem();
	
	public void setSource(Protocolable source) {
		this.source = source;
	}

	public Protocolable getSource() {
		return source;
	}
	
	public void setFiles(List<FileItem> files) {
		this.files = files;
	}

	public List<FileItem> getFiles() {
		return files;
	}

	public void setUpFile(UploadedFile upFile) {
		this.upFile = upFile;
	}

	public UploadedFile getUpFile() {
		return upFile;
	}

	public void setFile(FileItem file) {
		this.file = file;
	}

	public FileItem getFile() {
		return file;
	}

	/**
	 * Reattach source to session in order to view its files
	 * 
	 * @return
	 */
	public String accViewFiles(){
		
		//Reload Protocolable from database and update state
		Command cmd = getCommand(LoadProtocolableAttachments.class);
		((LoadProtocolableAttachments)cmd).setSource(source);
		cmd = runCommand(cmd);
		
		if(cmd != null){
			List<File> auxFiles = ((LoadProtocolableAttachments)cmd).getResult();
			if(auxFiles != null){
				files = new ArrayList<FileItem>();
				for(File file : auxFiles)
					files.add(new FileItem(file));
			}
		}
		
		return ActionBeanNames.EDIT_FILES;
	}
	
	/**
	 * Delete an specified file of a source protocol
	 * 
	 * @return
	 */
	public String accDelFile(){
		Command cmd = getCommand(RemoveFile.class);
		((RemoveFile)cmd).setFileId(file.getCode());
		runCommand(cmd);

		return accViewFiles();
	}	
	
	/**
	 * Download an specified file of a source protocol
	 * 
	 * @return
	 */
	public String accDownloadFile(){
		Command cmd = getCommand(GetAttachmentStream.class);
		((GetAttachmentStream)cmd).setFileId(file.getCode());
		cmd = runCommand(cmd);
		
		if(cmd != null && ((GetAttachmentStream)cmd).getResult() != null){
			try{
				HttpServletResponse response = prepareResponse();

				//Get file name without parents and prepare header
				String fileName = file.getName();
				String contentType = file.getName().substring(file.getName().lastIndexOf('.'));
				configResponseHeader(response, contentType, fileName);
				
				//Prepare OutputStream and writes files into
				InputStream is = ((GetAttachmentStream)cmd).getResult();
				OutputStream os = response.getOutputStream();

				byte bytes[] = new byte[BUFFER_SIZE];
				while(is.read(bytes) != -1)
					os.write(bytes);
				
				is.close();
				
				completeResponse();
			} catch (Exception e) {
				setErrorMessage(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}

		return accViewFiles();
	}
	
	public String accUploadFile() throws IOException{
		if(upFile != null){
			File file = new File();
			
			file.setContentType(upFile.getContentType());
			file.setSize(upFile.getSize());
			file.setName(upFile.getName());
			file.setSource(source);
			file.setStamp(new Date());
			
			Command cmd = getCommand(AddFileToProtocolable.class);
			((AddFileToProtocolable)cmd).setFile(file);
			((AddFileToProtocolable)cmd).setStream(upFile.getInputStream());
			cmd = runCommand(cmd);
			
		}else
			setWarnMessage(getMessage("jsf.info.NoFileUploaded"));
		
		return accViewFiles();
	}
}
