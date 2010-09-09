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
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.myfaces.custom.navmenu.NavigationMenuItem;

import es.urjc.mctwp.bbeans.SessionAbstractBean;
import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.ImageData;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Result;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.modelo.Task;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.ServiceDelegate;
import es.urjc.mctwp.service.commands.adminCmds.LoadTrial;
import es.urjc.mctwp.service.commands.userCmds.LoadMenuItems;

public class SessionInvBean extends SessionAbstractBean{
	private List<SelectItem> trials = null;
	private Integer trialSelected = null;

	// - Model objects
	private ImageData image = null;
	private Patient patient = null;
	private Trial trial	= null;
	private Group group = null;
	private Study study	= null;
	private Task  task  = null;
	private Result result = null;

	// - Temporary paths
	private String  thumbDirName = "temp"; //Default value
	private File	ctxThumbDir  = null;
	private File 	thumbDir	 = null;
	
	/**
	 * Get the environment and login validated by web server
	 */
	public SessionInvBean(){
		super();
	}

	
	public Integer getTrialSelected() {
		return trialSelected;
	}
	
	public void setTrialSelected(Integer trialSelected) {
		this.trialSelected = trialSelected;
	}
	
	public void setTrials(List<SelectItem> trials){
		this.trials = trials;
	}
	
	public List<SelectItem> getTrials(){
		return trials;
	}
	
	public String getThumbDirName() {
		return thumbDirName;
	}
	
	//Once thumbDir has been created, thumbDirName must not change
	public void setThumbDirName(String thumbDirName) {
		if(thumbDir == null)
			this.thumbDirName = thumbDirName;
	}
	
	public Trial getTrial(){
		return trial;
	}
	
	//If trial changes, menu must be regenerated. It is not possible
	//to select a null trial.
	public void setTrial(Trial obj){
		if(obj != null){
			
			//Sync menu
			if(!obj.equals(trial)){
				trial = obj;
				trialSelected = trial.getCode();
				setMenu(generateMenus());
			}
		}else{
			trial = null;
			trialSelected = null;
			setMenu(new ArrayList<NavigationMenuItem>());
		}
		
		//Sync status
		group = null;
		patient = null;
		study = null;
		image = null;
		task  = null;
	}

	public Group getGroup() {
		return group;
	}
	
	//Changing group does not propagate trial change
	public void setGroup(Group obj) {

		//Sync status
		group  = obj;
		patient = null;
		study  = null;
		image  = null;
		task   = null;
		result = null;
	}

	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient obj) {
		//Propagate sync
		if( (obj != null) && !(obj.equals(patient)) ){
			setGroup(obj.getGroup());
		}
		
		//Sync status
		patient = obj;
		study  = null;
		image  = null;
		task   = null;
		result = null;
	}

	public Study getStudy() {
		return study;
	}
	
	public void setStudy(Study obj) {
		//Propagate sync
		if( (obj != null) && !(obj.equals(study)) ){
			setPatient(obj.getPatient());
		}
		
		//Sync status
		study = obj;
		image  = null;
		task   = null;
		result = null;
	}

	public ImageData getImage() {
		return image;
	}
	
	public void setImage(ImageData obj) {
		//Propagate sync
		if( (obj != null) && !(obj.equals(image)) ){
			setStudy(obj.getStudy());
		}
		
		//Sync status
		image = obj;
		task   = null;
		result = null;
	}

	public Task getTask() {
		return task;
	}
	
	//Task doesn't propagate sync, it is not 1:N relationship with Image.
	public void setTask(Task obj) {
		//Sync status
		task   = obj;
		result = null;
	}
	
	public Result getResult(){
		return result;
	}
	
	public void setResult(Result obj){
		//Propagate sync
		if( (obj != null) && !(obj.equals(result)) ){
			setTask(obj.getTask());
		}
		
		//Sync status
		result = obj;
	}
	
	public boolean isUserAdmin(){
		return getUser().getAdmin();
	}

	public File getThumbDir() {return thumbDir;}

	public File getCtxThumbDir() {return ctxThumbDir;}

	public String getRelativeThumbDir() {
		return IOUtils.DIR_SEPARATOR  + ctxThumbDir.getPath();
	}
	
	public String getAbsoluteThumbDir() {
		return thumbDir.getAbsoluteFile().getPath();
	}	

	public void cleanUserTempDirectory(){
		SessionUtils.cleanFolder(thumbDir);
	}
	
	@Override
	public void setService(ServiceDelegate service){
		super.setService(service);
		if(getUser() != null) configPaths();
	}
	
	/**
	 * It retrieve trial from ActionEvent and update selected 
	 * trial into session state
	 *  
	 * @param event
	 */
	public String accUpdateTrialSelected() {
		String result = null;
		
		if(trialSelected != null){
			Command cmd = getCommand(LoadTrial.class);
			((LoadTrial)cmd).setTrialCode(trialSelected);
			((LoadTrial)cmd).setUser(getUser());
			cmd = runCommand(cmd);
			setTrial(((LoadTrial)cmd).getResult());
	
			GroupBean groupBean = (GroupBean)getBackBeanReference("groupBean");
			if(groupBean != null)
				result = groupBean.accListGroupsOfTrial();
		}else{
			setTrial(null);
			TrialBean trialBean = (TrialBean)getBackBeanReference("trialIBean");
			if(trialBean != null)
				result = trialBean.accListTrials();
		}
		
		return result;
	}	
		
	/**
	 * Configure temporary directories for user's images work
	 */
	private void configPaths(){
		File tempDir = null;
		
		tempDir = SessionUtils.getOrCreateFile(getServletContext().getRealPath("/"), thumbDirName);
		thumbDir = SessionUtils.getOrCreateFile(tempDir.getAbsolutePath(), getLogin());
		
		//Obtain web context relative path for thumbnails
		String aux = thumbDir.getAbsolutePath();
		aux = aux.substring(getServletContext().getRealPath(FilenameUtils.EXTENSION_SEPARATOR_STR).length() - 1);
		ctxThumbDir = new File(aux);
	}
	
	/**
	 * It generates a menu, using current trial, to decide what
	 * rol plays the user.
	 * 
	 * @param admin
	 * @return
	 */
	private List<NavigationMenuItem> generateMenus(){
		Command cmd = getCommand(LoadMenuItems.class);
		cmd.setUser(getUser());
		cmd.setTrial(getTrial());
		cmd = runCommand(cmd);
		return super.generateMenus(((LoadMenuItems)cmd).getResult());
	}	
}
