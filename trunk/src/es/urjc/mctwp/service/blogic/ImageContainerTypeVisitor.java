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

package es.urjc.mctwp.service.blogic;

import es.urjc.mctwp.modelo.Group;
import es.urjc.mctwp.modelo.ImageContainer;
import es.urjc.mctwp.modelo.Patient;
import es.urjc.mctwp.modelo.Result;
import es.urjc.mctwp.modelo.Study;
import es.urjc.mctwp.modelo.Trial;

public class ImageContainerTypeVisitor {
	private static ImageContainerTypeVisitor instance = null;
	
	public static ImageContainerTypeVisitor getInstance(){
		if(instance == null)
			instance = new ImageContainerTypeVisitor();
		
		return instance;
	}

	public Class<? extends ImageContainer> visit(Trial container){
		return Trial.class;
	}
	
	public Class<? extends ImageContainer> visit(Group container){
		return Group.class;
	}

	public Class<? extends ImageContainer> visit(Patient container){
		return Patient.class;
	}
	
	public Class<? extends ImageContainer> visit(Study container){
		return Study.class;
	}
	
	public Class<? extends ImageContainer> visit(Result container){
		return Result.class;
	}
}
