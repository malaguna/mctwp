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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.ServletException;

public class ErrorHandler extends AbstractBean{

	private String abrir = "<div id='textoTrazaError'>";
	private String cerrar= "</div>";
	
	public String getMsgError(){
		Map<String,Object> request = getFacesContext().getExternalContext().getRequestMap();
		Throwable ex = (Throwable) request.get("javax.servlet.error.exception");
		return ex.getLocalizedMessage();
	}
	
	public String getTraza(){
		Map<String,Object> request = getFacesContext().getExternalContext().getRequestMap();
		Throwable ex = (Throwable) request.get("javax.servlet.error.exception");
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		rellenarTraza(ex, pw);
		return sw.toString();
	}
	
	private static void rellenarTraza(Throwable t, PrintWriter w){
		if (t == null) return;
		t.printStackTrace(w);
		
		if ( t instanceof ServletException){
			Throwable cause = ((ServletException) t).getRootCause();
			if (cause != null){
				w.println("<p>");
				rellenarTraza(cause, w);
				w.println("</p>");
			}
		}
		else{
			Throwable cause = t.getCause();
			if (cause != null){
				w.println("<p>");
				rellenarTraza(cause,w);
				w.println("</p>");
			}
		}
	}

	public String getAbrir() {
		return abrir;
	}

	public void setAbrir(String abrir) {
		this.abrir = abrir;
	}

	public String getCerrar() {
		return cerrar;
	}

	public void setCerrar(String cerrar) {
		this.cerrar = cerrar;
	}
}

