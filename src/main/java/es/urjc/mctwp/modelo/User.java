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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends DomainObject implements java.io.Serializable {

	private static final long serialVersionUID = -4481001355926228263L;
	private int center;
	private String name = null;
	private String login = null;
	private Boolean admin = null;
	private String surName = null;
	private String lastName = null;
	private Date date = new Date();
	private Set<Trial> trials = null;
	private Set<Modality> modalities = null;
	private Set<Participation> participations = null;

	public User() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String nombre) {
		this.name = nombre;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String apellido1) {
		this.lastName = apellido1;
	}

	public String getSurName() {
		return this.surName;
	}

	public void setSurName(String apellido2) {
		this.surName = apellido2;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date fecAlta) {
		this.date = fecAlta;
	}

	public int getCenter() {
		return this.center;
	}

	public void setCenter(int centro) {
		this.center = centro;
	}

	public void setParticipations(Set<Participation> participaciones) {
		this.participations = participaciones;
	}

	public Set<Participation> getParticipations() {
		return participations;
	}
	
	public List<Participation> getParticipationsList() {
		return new ArrayList<Participation>(participations);
	}
	
	public void addParticipation(Participation p){
		if(p != null)
			if(participations == null)
				participations = new HashSet<Participation>();
		
			participations.add(p);
			p.setUser(this);
	}
	
	public void delParticipation(Participation p){
		if(p != null)
			if(participations != null)
				participations.remove(p);
	}
	
	public Participation getParticipationByTrial(Trial trial){

		for(Participation p : participations)
			if(p.getTrial().equals(trial))
				return p;
		
		return null;
	}

	public void setTrials(Set<Trial> trials) {
		this.trials = trials;
	}

	public Set<Trial> getTrials() {
		return trials;
	}

	public void setModalities(Set<Modality> modalities) {
		this.modalities = modalities;
	}

	public Set<Modality> getModalities() {
		return modalities;
	}

	public void addModality(Modality m){
		if(m != null){
			if(modalities == null)
				modalities = new HashSet<Modality>();
			
			modalities.add(m);
		}
	}
	
	public void delModality(Modality m){
		if(m != null)
			if(modalities != null)
				modalities.remove(m);
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public Boolean getAdmin() {
		return admin;
	}
	
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	public String getFullName(){
		String result = null;
		
		if(name != null){
			result = name;
			if(lastName != null)
				result += " " + lastName;
			if(surName != null)
				result += " " + surName;
		}
		
		return result;
	}
}
