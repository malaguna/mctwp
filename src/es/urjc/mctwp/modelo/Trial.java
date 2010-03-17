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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.urjc.mctwp.service.blogic.PersistImagesVisitor;

public class Trial extends Protocolable implements java.io.Serializable {

	private static final long serialVersionUID = -2828405165433613012L;
	private String name;
	private String financing;
	private String agency;
	private String project;
	private String collection;
	private Date startDate;
	private Date endDate;
	private Set<Participation> members = null;
	private Set<Group> groups = null;

	public String getName() {
		return this.name;
	}

	public void setName(String nombre) {
		this.name = nombre;
	}

	public String getFinancing() {
		return this.financing;
	}

	public void setFinancing(String financiacion) {
		this.financing = financiacion;
	}

	public String getAgency() {
		return this.agency;
	}

	public void setAgency(String agencia) {
		this.agency = agencia;
	}

	public String getProject() {
		return this.project;
	}

	public void setProject(String proyecto) {
		this.project = proyecto;
	}

	public void setCollection(String coleccion) {
		this.collection = coleccion;
	}

	@Override
	public String getCollection() {
		return collection;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date inicio) {
		this.startDate = inicio;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date fin) {
		this.endDate = fin;
	}

	public void setMembers(Set<Participation> participantes) {
		this.members = participantes;
	}

	public Set<Participation> getMembers() {
		return members;
	}
	
	public void addMember(Participation part){
		if(part != null){
			if(members == null)
				members = new HashSet<Participation>();
			
			members.add(part);
			part.setTrial(this);
		}
	}
	
	public void delMember(Participation part){
		if(part != null){
			if(members != null)
				members.remove(part);
		}
	}

	public void setGroups(Set<Group> casos) {
		this.groups = casos;
	}

	public Set<Group> getGroups() {
		return groups;
	}
	
	public void addGroup(Group group){
		if(group != null){
			if(groups == null)
				groups = new HashSet<Group>();
		
			groups.add(group);
			group.setTrial(this);
		}
	}
	
	@Override
	public void accept(PersistImagesVisitor piv, List<String> imagesId,	String tempCol) throws Exception {
		piv.visit(this, imagesId, tempCol);
	}

	@Override
	public String getType(){
		return "Group";
	}	
	
	@Override
	public Set<ImageData> getAllImages(){
		Set<ImageData> auxSet = new HashSet<ImageData>();
		
		if(this.getGroups() != null)
			for(Group group : this.getGroups())
				auxSet.addAll(group.getAllImages());
		
		return auxSet;
	}
}
