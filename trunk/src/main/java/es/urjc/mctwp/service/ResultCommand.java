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

import org.springframework.beans.factory.BeanFactory;


/**
 * Implements business logic of a generic Command that returns something
 * 
 * @author mall02
 *
 * @param <T>
 */
public abstract class ResultCommand<T> extends Command {

	/**
	 * Result of the command
	 */
	private T result = null;

	public ResultCommand(BeanFactory bf) {
		super(bf);
	}

	public T getResult(){
		return this.result;
	}
	protected void setResult(T resultado){
		this.result = resultado;
	}

	/**
	 * Run the command. It must set the result and a log and user comment.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public abstract ResultCommand<T> runCommand() throws Exception;
}
