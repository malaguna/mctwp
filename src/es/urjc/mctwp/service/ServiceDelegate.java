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

import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class ServiceDelegate {
	private Logger logger = Logger.getLogger(this.getClass());
	private PlatformTransactionManager txManager = null;
	
	public PlatformTransactionManager getTxManager(){
		return txManager;
	}
	public void setTxManager(PlatformTransactionManager txManager){
		this.txManager = txManager;
	}
	
	private TransactionStatus getTxStatus(String action, boolean readOnly, String isolation, String propagation){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		
		def.setName(action);
		
		//Set readOnly configuration
		def.setReadOnly(readOnly);
		
		//Set isolation configuration
		if(isolation.equals(Command.ISOLATION_DEFAULT)){
			def.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_DEFAULT);
		}

		//Set propagation configuration
		if(propagation.equals(Command.PROPAGATION_REQUIRED)){
			def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		}
		
		return txManager.getTransaction(def);
	}
	
	private void logMessage(Command cmd, String msg, Exception e){
		String desc = "Command [" + cmd.getActionName() + "], ";
		if(cmd.getUser() != null)
			desc +=	"User [" + cmd.getUser().getLogin() + "], ";
		desc += msg;
		if(e != null)
			desc += ". Error: " + e.getLocalizedMessage();
		
		logger.error(desc);
	}
	
	/**
	 * This method is responsible of check if user has 
	 * authorization to run the command. If it hasn't, it will
	 * log into system and set user comment.
	 * 
	 * @param cmd
	 * @return
	 */
	private boolean isCommandAuthorized(Command cmd){
		TransactionStatus status = null;
		boolean result = false;
		
		try{
			//Check user authorization
			status = getTxStatus(cmd.getActionName(), true, Command.ISOLATION_DEFAULT, Command.PROPAGATION_REQUIRED);
			result = cmd.isCommandAuthorized(); 
		}catch(Exception e){
			logMessage(cmd, "It is not possible to check user authorization", e);
			txManager.rollback(status);
		}
		
		txManager.commit(status);
		return result;
	}
	
	/**
	 * This method is responsible of init log operation. 
	 * If it fails, it will log into system and set user comment.
	 * 
	 * @param cmd
	 * @return
	 */
	private boolean initLogCommand(Command cmd){
		TransactionStatus status = null;
		boolean result = true;

		try{
			status = getTxStatus(cmd.getActionName(), false, Command.ISOLATION_DEFAULT, Command.PROPAGATION_REQUIRED);
			cmd.initCommandLog();
		}catch(Exception e){
			logMessage(cmd, "It is not possible to log init command execution", e);
			txManager.rollback(status);
			result = false;
		}
		
		if(result)
			txManager.commit(status);
		
		return result;		
	}
	
	/**
	 * This method completes the audit log into database, but if it
	 * fails it will not abort command, it log into system that the
	 * operation fails.
	 * 
	 * @param cmd
	 */
	private void endsLogCommand(Command cmd){
		TransactionStatus status = null;
		boolean commit = true;

		try{
			//Log completion of the command
			status = getTxStatus(cmd.getActionName(), false, Command.ISOLATION_DEFAULT, Command.PROPAGATION_REQUIRED);
			cmd.endsCommandLog();
		}catch(Exception e){
			logMessage(cmd, "It is not possible to log end command execution", e);
			txManager.rollback(status);
			commit = false;
		}
		
		if(commit)
			txManager.commit(status);
	}

	/**
	 * This is responsible of command execution. A command may fail, this method
	 * is responsible of cath exception, process and throw a CommandException that
	 * view layer can understand.
	 * 
	 * @param cmd
	 * @return
	 * @throws CommandException
	 */
	public Command runCommand(Command cmd) throws CommandException{
		TransactionStatus status = null;
		Command result = cmd;

		//Chechk command is well formed
		if( (cmd != null) && (cmd.isValidCommand()) ){	
			if(isCommandAuthorized(cmd)){
				if(initLogCommand(cmd)){
					
					try{
						status = getTxStatus(cmd.getActionName(), cmd.isReadOnly(), cmd.getIsolation(), cmd.getPropagation());
						result = cmd.runCommand();
					}catch(Exception e){
						String msg = "Command failed: " + e.getLocalizedMessage();
						txManager.rollback(status);
						logger.error(msg);
						throw new CommandException(msg, e);
					}
					
					try{
						txManager.commit(status);
					}catch(Exception e){
						String msg = "Command commit failed: " + e.getLocalizedMessage();
						logger.error(msg);
						throw new CommandException(msg, e);
					}
					
					endsLogCommand(cmd);
				}else{
					String msg = "User action [" + cmd.getActionName() + "] can not be logged, execution aborted";
					logMessage(cmd, msg, null);
					throw new CommandException(msg);
				}
			}else{
				String msg = "User action [" + cmd.getActionName() + "] can not be authorized, execution aborted";
				logMessage(cmd, msg, null);
				throw new CommandException(msg);
			}
		}else{
			String msg = "The command [" + cmd.getClass().getName() + "] is not well formed";
			logMessage(cmd, msg, null);
			throw new CommandException(msg);
		}
		
		return result;
	}
}