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

package es.urjc.mctwp.bbeans;

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.ServiceDelegate;

/**
 * This class contains all dependences of JSF Back Beans and utils
 * 
 * @author Miguel Ángel Laguna Lobato
 *
 */
public abstract class AbstractBean {
	private Logger logger = Logger.getLogger(this.getClass());
	
	//Environment
	private FacesContext fc = null;
	private ExternalContext ec = null;
	private ServletContext  sc = null;
	private WebApplicationContext wac = null;
	
	//Objects
	private ServiceDelegate service;

	public AbstractBean(){
		//Get Environment
		fc  = FacesContext.getCurrentInstance();
		ec  = fc.getExternalContext(); 
		sc  = (ServletContext)ec.getContext();	
		wac = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
	}
	
	protected void logMessage(String msg, String method, Exception e){
		String cadError = "Method [" + method + "], " +	msg +
			"Error: " + e.getMessage();
	
		logger.error(cadError);
	}	
	
	protected ExternalContext getExternalContext() {return ec;}
	protected ServletContext getServletContext() {return sc;}
	protected FacesContext getFacesContext() {return FacesContext.getCurrentInstance();}

	protected ServiceDelegate getService(){return service;}
	public void setService(ServiceDelegate service){
		this.service = service;
	}
	
	/**
	 * Get a message from the bundle using the key
	 */
    protected String getMessage(String key){
		String text = null;
		
		Locale locale = fc.getViewRoot().getLocale();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		String bundleName = fc.getApplication().getMessageBundle();

		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, cl);
		
		try{
			text = bundle.getString(key);
		} catch(MissingResourceException e){
			text = "?? key " + key + " not found ??";
		}
		
		return text;
    }

	protected void setErrorMessage(String msg) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

	protected void setInfoMessage(String msg) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }	
	
	protected void setWarnMessage(String msg) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null));
    }	
	
	/**
	 * Returns BackBean reference by given name
	 * 
	 * @param backBean
	 */
	public Object getBackBeanReference(String backBean){
		ELContext context = getFacesContext().getELContext();
		return context.getELResolver().getValue(context, null, backBean);
	}		

	/**
	 * Return new instance of command identified by clazz
	 * 
	 * @param clase
	 * @return
	 */
	protected Command getCommand(Class<?> clazz){
		Command command = null;
		
		//Build a new command using Web Application Context (wac)
		try{
			Constructor<?> c = clazz.getConstructor(BeanFactory.class);
			command = (Command)c.newInstance(wac);
		}catch(Exception e){
			logger.error("Error creating new command [" + clazz.toString() + "] : " + e.getMessage());
		}

		return command;
	}
	
	/**
	 * It runs a command safely and catching all error info. If commands fail, 
	 * it will redirect action to error page.
	 * 
	 * @param cmd
	 * @return
	 */
	protected Command runCommand(Command cmd){
		Command result = cmd;

		try{
			result = service.runCommand(cmd);
			if(result.getUserComment() != null){
				setInfoMessage(result.getUserComment());
			}
		}catch(Exception ce){
			setErrorMessage(ce.getLocalizedMessage());
		}
		
		return result;
	}
}
