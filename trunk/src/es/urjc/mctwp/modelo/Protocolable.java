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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.urjc.mctwp.service.blogic.ImageContainerTypeVisitor;
import es.urjc.mctwp.service.blogic.PersistImagesVisitor;

public abstract class Protocolable extends ImageContainer{
	private static final long serialVersionUID = 2489697617033960034L;
	private Set<Process> processes = null;
	private Set<Task> tasks = null;

	public void setProcesses(Set<Process> processes) {
		this.processes = processes;
	}

	public Set<Process> getProcesses() {
		return processes;
	}
	
	public ArrayList<Process> getProcessesList() {
		return new ArrayList<Process>(processes);
	}
	
	public void addProcess(Process process){
		if(process != null){
			if(processes == null)
				processes = new HashSet<Process>();
		
			processes.add(process);
			process.setSource(this);
		}
	}

	public void delProcess(Process process){
		if(process != null){
			if(processes != null){
				processes.remove(process);
				process.setSource(null);
			}
		}
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public void addTask(Task task){
		if(task != null){
			if(tasks == null)
				tasks = new HashSet<Task>();
		
			tasks.add(task);
			task.setSource(this);
		}
	}

	public void delTask(Task task){
		if(task != null)
			if(tasks != null)
				tasks.remove(task);
	}
	
	public boolean hasProcessWithDef(ProcessDef proc){
		boolean result = false;
		
		if(processes != null){
			for(Process process : processes){
				if(proc.equals(process.getProcessDef())){
					result = true;
					break;
				}
			}
		}
		
		return result;
	}

	@Override
	public abstract void accept(PersistImagesVisitor piv, List<String> imagesId, String tempCol, Integer imgType) throws Exception;

	@Override
	public abstract Class<? extends ImageContainer> accept(ImageContainerTypeVisitor imcv);
	
	@Override
	public Set<ImageData> getAllImages(){
		return null;
	}

	@Override
	public String getDescription(){
		return null;
	}
	
	@Override
	public String getCollection(){
		return null;
	}
	
	@Override
	public String getType(){
		return null;
	}
}
