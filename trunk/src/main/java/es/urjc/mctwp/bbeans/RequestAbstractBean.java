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

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;

public abstract class RequestAbstractBean extends AbstractBean {
	
	/**
	 * This method will be call to get all data initialized for a
	 * request bean
	 */
	protected abstract void init();
	
	/**
	 * Method that obtains the selected row of an action event
	 * 
	 * @param anEvent
	 */
	protected Object selectRowFromEvent(ActionEvent anEvent, Class<?> clazz) {
		Object tmpRowData = null;

		UIComponent tmpComponent = anEvent.getComponent();

		while (null != tmpComponent && !(tmpComponent instanceof UIData))
			tmpComponent = tmpComponent.getParent();

		if (tmpComponent != null && (tmpComponent instanceof UIData)) {
			tmpRowData = ((UIData)tmpComponent).getRowData();
			
			if(!tmpRowData.getClass().equals(clazz)){
				setErrorMessage("Error de casting para el objeto fila: se esperaba [" + clazz.toString() + "] y es [" + tmpRowData.getClass().toString() + "]");
				tmpRowData = null;
			}
		}
		
		return tmpRowData;
	}
}
