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

package es.urjc.mctwp.service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.modelo.Action;
import es.urjc.mctwp.modelo.Log;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.blogic.UserUtils;

/**
 * Implements business logic of a generic Command:
 * 
 * @author mall02
 * 
 */
public abstract class Command {
	// Transaction configuration values
	public static final String ISOLATION_DEFAULT = "default";
	public static final String PROPAGATION_REQUIRED = "required";

	// Transaction configuration
	private boolean readOnly = true;
	private String isolation = ISOLATION_DEFAULT;
	private String propagation = PROPAGATION_REQUIRED;

	/**
	 * Message bundle
	 */
	private ResourceBundle messages = null;

	/**
	 * BeanFactory to retrieve Service and DAO objects
	 */
	private BeanFactory factory = null;

	/**
	 * User business logic
	 */
	private UserUtils userUtils = null;

	/**
	 * Info that commands will offer to final user
	 */
	private String userComment = null;

	/**
	 * Info that commands will store into application log
	 */
	private String logComment = null;

	/**
	 * Who runs the command. If this is null, the command is not valid.
	 */
	private User user = null;

	/**
	 * Trial where user is working, it helps to find role played by user. If
	 * this attribute is null, the role of the user could be admin, otherwise
	 * the command is invalid.
	 */
	private Trial trial = null;

	/**
	 * The action name of this command
	 */
	private String actionName = null;

	/**
	 * The action of the command, it is never manipulated by user
	 */
	private Action action = null;

	/**
	 * Log of the command.
	 */
	private Log log = null;

	/**
	 * It creates a new command and retrieves user business logic
	 * 
	 * @param bf
	 */
	public Command(BeanFactory bf) {
		if (bf != null) {

			// Get ResourceBundle
			messages = ResourceBundle
					.getBundle("es.urjc.mctwp.resources.messages_en");

			try {
				factory = bf;
				userUtils = (UserUtils) bf.getBean(BeanNames.USER_UTILS);
			} catch (Exception e) {
			}
		}
	}

	protected BeanFactory getBeanFactory() {
		return factory;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	protected void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getIsolation() {
		return isolation;
	}

	protected void setIsolation(String isolation) {
		this.isolation = isolation;
	}

	public String getPropagation() {
		return propagation;
	}

	protected void setPropagation(String propagation) {
		this.propagation = propagation;
	}

	protected UserUtils getUserUtils() {
		return userUtils;
	}

	public String getUserComment() {
		return userComment;
	}

	protected void setUserComment(String coment) {
		this.userComment = coment;
	}

	protected String getLogComment() {
		return logComment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setTrial(Trial trial) {
		this.trial = trial;
	}

	public Trial getTrial() {
		return trial;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String accion) {
		this.actionName = accion;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	protected Log getLogger() {
		return log;
	}

	/**
	 * This method get the appropriate message template and applies arguments to
	 * generate a valid localized message.
	 * 
	 * @param arguments
	 * @param template
	 */
	protected void createLogComment(String template, Object... arguments) {
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(messages.getString(template));
		logComment = formatter.format(arguments);
	}

	/**
	 * This method get the appropriate message template and applies arguments to
	 * generate a valid localized message.
	 * 
	 * @param arguments
	 * @param template
	 */
	protected void createUserComment(String template, Object... arguments) {
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(messages.getString(template));
		userComment = formatter.format(arguments);
	}

	/**
	 * Whether the user has permission to run the action of the command
	 * 
	 * @return
	 */
	public boolean isCommandAuthorized() {
		action = userUtils.getActionIfAuthorized(user, trial, actionName);
		return action != null;
	}

	/**
	 * Whether the command is well formed and ready to run. It must be overwrite
	 * to add parameter's validation of specialized commands
	 * 
	 * @return
	 */
	public boolean isValidCommand() {
		return actionName != null && userUtils != null && user != null
				&& (user.getAdmin() || trial != null);
	}

	/**
	 * Log that the user is trying to do something.
	 */
	public void initCommandLog() {
		if ((action != null) && (action.isLogeable())) {
			log = new Log();
			log.setUser(user);
			log.setStamp(new Date());
			log.setComment("trying...");

			log = userUtils.logAction(log, action);
		}
	}

	/**
	 * Log the user do the action successfully. If there is no comment it puts
	 * complete action
	 */
	public void endsCommandLog() {
		if ((action != null) && (log != null)) {
			if (logComment == null)
				logComment = "complete";

			log.setComment(logComment);
			userUtils.logAction(log);
		}
	}

	/**
	 * It could be overwrite to do something before runCommand
	 */
	public void preCommand() {
	}

	/**
	 * It could be overwrite to do something after runCommand
	 */
	public void postCommand() {
	}

	/**
	 * Run the command. It must set the result and a log and user comment.
	 * 
	 * @return
	 */
	public abstract Command runCommand() throws Exception;
}
