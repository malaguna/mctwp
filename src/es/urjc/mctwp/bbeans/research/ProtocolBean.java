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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import es.urjc.mctwp.bbeans.ActionBeanNames;
import es.urjc.mctwp.bbeans.RequestInvAbstractBean;
import es.urjc.mctwp.modelo.ImageType;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.ProcessDef;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.commands.adminCmds.FindAllImageTypes;
import es.urjc.mctwp.service.commands.researchCmds.AddProcessToProtocolable;
import es.urjc.mctwp.service.commands.researchCmds.FindNotAssignedProcesses;
import es.urjc.mctwp.service.commands.researchCmds.LoadParticipationsOfTrial;
import es.urjc.mctwp.service.commands.researchCmds.LoadProtocolable;
import es.urjc.mctwp.service.commands.researchCmds.RemoveProcessToProtocolable;

/**
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public class ProtocolBean extends RequestInvAbstractBean{
	private List<SelectItem> notAssignedProcesses = new ArrayList<SelectItem>();
	private List<SelectItem> possibleUsers = new ArrayList<SelectItem>();
	private List<SelectItem> imgTypes = null;
	private Integer typeSelected = null;
	private Protocolable source = null;
	private Integer idProcessDefSelected = null;
	private Integer idProcessSelected = null;
	private Integer days = null;
	private Integer owner = null;
	
	public void setSource(Protocolable source) {
		this.source = source;
	}

	public Protocolable getSource() {
		return source;
	}
	
	public Integer getIdProcessSelected() {
		return idProcessSelected;
	}
	
	public void setIdProcessSelected(Integer processSelected) {
		this.idProcessSelected = processSelected;
	}
	
	public Integer getIdProcessDefSelected() {
		return idProcessDefSelected;
	}
	
	public void setIdProcessDefSelected(Integer processSelected) {
		this.idProcessDefSelected = processSelected;
	}
	
	public Integer getDays() {
		return days;
	}
	
	public void setDays(Integer days) {
		this.days = days;
	}
	
	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public void setNotAssignedProcesses(List<SelectItem> list){
		this.notAssignedProcesses = list;
	}
	
	public List<SelectItem> getNotAssignedProcesses(){
		return notAssignedProcesses;
	}

	public void setPossibleUsers(List<SelectItem> list){
		this.possibleUsers = list;
	}
	
	public List<SelectItem> getPossibleUsers(){
		return possibleUsers;
	}

	public void setImgTypes(List<SelectItem> imgTypes) {
		this.imgTypes = imgTypes;
	}

	public List<SelectItem> getImgTypes() {
		return imgTypes;
	}

	public void setTypeSelected(Integer typeSelected) {
		this.typeSelected = typeSelected;
	}

	public Integer getTypeSelected() {
		return typeSelected;
	}

	/**
	 * Reattach source to session in order to view its protocol
	 * 
	 * @return
	 */
	public String accViewProtocol(){
		
		//Reload Protocolable from database and update state
		Command cmd = getCommand(LoadProtocolable.class);
		((LoadProtocolable)cmd).setSourceId(source.getCode());
		cmd = runCommand(cmd);
		source = ((LoadProtocolable)cmd).getResult();
		
		return ActionBeanNames.EDIT_PROTOCOL;
	}
	
	public String accPrepareAddition(){
		List<ProcessDef> aux = null;

		if(source != null){
			
			//Prepare image types
			FindAllImageTypes cmda = (FindAllImageTypes) getCommand(FindAllImageTypes.class);
			cmda = (FindAllImageTypes) runCommand(cmda);
			
			if(cmda != null && cmda.getResult() != null){
				imgTypes = new ArrayList<SelectItem>();
				
				for(ImageType imgt : cmda.getResult()){
					imgTypes.add(new SelectItem(imgt.getCode(), imgt.getName()));
				}
			}else
				setErrorMessage(getMessage("jsf.info.NoImageTypes"));
			
			//Get not assigned processes
			Command cmd = getCommand(FindNotAssignedProcesses.class);
			((FindNotAssignedProcesses)cmd).setSource(source);
			cmd = runCommand(cmd);
			aux = ((FindNotAssignedProcesses)cmd).getResult();
			
			if(aux != null && !aux.isEmpty()){
				notAssignedProcesses = new ArrayList<SelectItem>();
				for(ProcessDef processDef : aux)
					notAssignedProcesses.add(new SelectItem(processDef.getCode(), processDef.getName()));

				//Get possible users
				cmd = getCommand(LoadParticipationsOfTrial.class);
				cmd = runCommand(cmd);
				Iterator<Participation> it = ((LoadParticipationsOfTrial)cmd).getResult();
		
				if(it != null && it.hasNext()){
					possibleUsers = new ArrayList<SelectItem>();
					
					while(it.hasNext()){
						User u = it.next().getUser();
							possibleUsers.add(new SelectItem(u.getCode(), u.getFullName()));
					}
				}else
					setWarnMessage(getMessage("jsf.info.NoPossibleUsers"));

			}else
				setWarnMessage(getMessage("jsf.info.NoAvailableProcess"));
			
		}else
			setWarnMessage(getMessage("jsf.info.NoProtocolSelected"));
		
		return ActionBeanNames.ADD_PROCESS;
	}
	
	/**
	 * Adds a new process to the source protocol
	 * 
	 * @return
	 */
	public String accAddProcess(){
		Command cmd = getCommand(AddProcessToProtocolable.class);
		((AddProcessToProtocolable)cmd).setProtocolable(source);
		((AddProcessToProtocolable)cmd).setProcessDefId(idProcessDefSelected);
		((AddProcessToProtocolable)cmd).setOwnerId(owner);
		((AddProcessToProtocolable)cmd).setDays(days);
		((AddProcessToProtocolable)cmd).setImgType(typeSelected);
		
		runCommand(cmd);

		return accViewProtocol();
	}
	
	/**
	 * Delete an specified process of a source protocol
	 * 
	 * @return
	 */
	public String accDelProcess(){
		Command cmd = getCommand(RemoveProcessToProtocolable.class);
		((RemoveProcessToProtocolable)cmd).setProtocolable(source);
		((RemoveProcessToProtocolable)cmd).setProcessId(idProcessSelected);
		runCommand(cmd);

		return accViewProtocol();
	}	
}
