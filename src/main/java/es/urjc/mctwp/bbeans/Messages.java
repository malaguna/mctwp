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

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

/**
 * 
 * @author Lorenzo Flores
 * Devuelve los mensajes necesarios para la cola de mensajes de MyFaces
 */
public class Messages extends AbstractBean {
    private static final String BEAN_NAME = Messages.class.getName();
    private String messageImage = null;
    private String messageHeader = null;
    private Severity severityLevel = null;

    public Messages() {
        messageHeader = null;

        // Mirar si hay mensajes encolados para la página
        severityLevel = getFacesContext().getMaximumSeverity();

        if (null != severityLevel) {

            if (severityLevel.compareTo(FacesMessage.SEVERITY_ERROR) == 0) {
                messageHeader =	getMessage("PAGE_MESSAGE_ERROR");
                messageImage = getMessage("PAGE_IMAGE_ERROR");
            } else if (severityLevel.compareTo(FacesMessage.SEVERITY_INFO) == 0) {
                messageHeader =	getMessage("PAGE_MESSAGE_INFO");
                messageImage = getMessage("PAGE_IMAGE_INFO");
            } 
        }
    }

    public Boolean getRenderMessage() {
    	return (getMessageHeader() != null && !"".equals(getMessageHeader()));
    }

    public String getBeanName() {
        return BEAN_NAME;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public String getMessageImage() {
        return messageImage;
    }
}
