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

package es.urjc.mctwp.image.collection;

import java.util.List;

import org.w3c.dom.Node;

import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.objects.Attribute;

/**
 * 
 * @author Miguel Ángel Laguna
 *
 */
public interface ImageXMLCollection {

	//Collection operations
	public abstract void createCollection(String collection) throws ImageCollectionException;
	public abstract void deleteCollection(String collection) throws ImageCollectionException;
	
	//Image Operations
	public abstract void storeNode(String collection, String idNode, Node node) throws ImageCollectionException;
	public abstract void deleteNode(String collection, String idNode) throws ImageCollectionException; 
	public abstract List<String> findNodes(String collection, List<Attribute> criteria);
}
