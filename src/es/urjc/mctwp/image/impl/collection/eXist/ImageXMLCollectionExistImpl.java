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

package es.urjc.mctwp.image.impl.collection.eXist;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import org.xmldb.api.*;

import es.urjc.mctwp.image.collection.ImageXMLCollection;
import es.urjc.mctwp.image.exception.ImageCollectionException;
import es.urjc.mctwp.image.objects.Attribute;
import es.urjc.mctwp.image.objects.Image;

import javax.xml.transform.OutputKeys;

/**
 * This class manage XMLDB for creating collections, store and retrieve 
 * xml definitios of images
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public class ImageXMLCollectionExistImpl extends ImageXMLCollection {

    private String dburi;
    private String prefix;
    private String user;
    private String pass;
	private Logger logger;

    public ImageXMLCollectionExistImpl() throws ImageCollectionException {
        String driver = "org.exist.xmldb.DatabaseImpl";
        logger = Logger.getLogger(this.getClass());
        
        //Init driver
        try{
	        Class<?> cl = Class.forName(driver);
	        Database database = (Database) cl.newInstance();
	        DatabaseManager.registerDatabase(database);
        }catch(Exception e){
        	logger.error(e.getMessage());
        	throw new ImageCollectionException(e);
        }
    }

    public void setDburi(String dburi) {
		this.dburi = dburi;
	}
	public String getDburi() {
		return dburi;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getPrefix() {
		return prefix;
	}

	public void setUser(String user) {
		this.user = user;
	}
	public String getUser() {
		return user;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getPass() {
		return pass;
	}

	private Collection getCollection(String collection) throws XMLDBException, ImageCollectionException{
		Collection col = null;
		String colname = null;

		colname = (collection == null)?prefix:prefix + "/" + collection;
		col = DatabaseManager.getCollection(dburi + colname, user, pass);
		
		if(col != null)
			col.setProperty(OutputKeys.INDENT, "no");
		else
			throw new ImageCollectionException("Can't connect to XMLDB");
        
        return col;
	}
	
    /**
     * Create a new collection into the root colection
     * 
     * @param name of the collection
     * @throws Exception
     */
    public void createCollection(String name) throws ImageCollectionException{
    	CollectionManagementService srv = null;
    	
    	if( (name != null) && (name.length() > 0) ){
    		try{
		    	Collection col = getCollection(null);
		    	
		        srv = (CollectionManagementService)col.getService("CollectionManagementService", "1.0");
		        srv.createCollection(name);
		      
    		}catch(XMLDBException e){
				logger.error(e.getMessage());
				throw new ImageCollectionException(e);
    		}
    	}
    }

	
	/**
     * Remove an existing collection
     * 
     * @param name of the collection
     * @throws Exception
	 */
	public void deleteCollection(String name) throws ImageCollectionException{
    	CollectionManagementService srv = null;

    	if( (name != null) && (name.length() > 0) ){
    		try{
		    	Collection col = getCollection(null);
		    	
		        srv = (CollectionManagementService)col.getService("CollectionManagementService", "1.0");
		        srv.removeCollection(name);
		      
    		}catch(XMLDBException e){
				logger.error(e.getMessage());
				throw new ImageCollectionException(e);
    		}
    	}
	}
    
    /**
     * Store an xml image definition into the coleccion specified
     * 
     * @param xml image resource
     * @param coleccion that store the xml
     * @return id of the xml definition stored
     * @throws Exception
     */
	@Override
    public void storeImage(String collection, Image image) throws ImageCollectionException{
    	XMLResource document = null;
    	Node xml = null;
    	
		if( (collection != null) && (image != null) && (collection.length() > 0) ){
			try{
				
				xml = imng.obtainNode(image);
				
		    	Collection col = getCollection(collection);
		
		        document = (XMLResource)col.createResource(image.getId(), "XMLResource");
		        document.setContentAsDOM(xml);
		        col.storeResource(document);
		        
			}catch(Exception e){
				logger.error(e.getMessage());
				throw new ImageCollectionException(e);
			}
		}
    }
    
	/**
     * Delete an stored xml image definition of the specified collection
     * 
     * @param image id of the image
     * @param coleccion that contains the xml
     * @throws Exception
	 */
	@Override
	public void deleteImage(String collection, String idImage) throws ImageCollectionException{
    	XMLResource document = null;
		
		if( (collection != null) && (collection.length() > 0) &&
			(idImage != null) && (idImage.length() > 0) ){
			
			try{
		    	Collection col = getCollection(collection);

		        document = (XMLResource)col.getResource(idImage);
		        col.removeResource(document);
		        
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		}
	}
	
	/**
	 * It must implement a xpath search on every document of the collection
	 */
	@Override
	public List<String> findImages(String collection, List<Attribute> criteria){
		List<String> result = null;
		
		if( (collection != null) && (collection.length() > 0) &&
			(criteria != null) && (criteria.size() > 0) ){
			
			try{
		    	//TODO: Implementar búsqueda xpath mediante los atributos
		    	//Collection col = getCollection(collection);
		    	
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		}

		return result;
	}    
}
