package es.urjc.mctwp.bbeans;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class GenericDownloadBean extends RequestInvAbstractBean {

	protected HttpServletResponse prepareResponse(){
		HttpServletResponse result = null;
		
		FacesContext ctx = FacesContext.getCurrentInstance();  
		result = (HttpServletResponse) ctx.getExternalContext().getResponse();  
		result.setHeader("pragma", "no-cache");
		result.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
		result.setHeader("Expires", "01 Jan 2000 00:00:00 GMT");
		
		return result;
	}
	
	protected void configResponseHeader(HttpServletResponse response, String contentType, String fileName){
		response.setContentType(contentType);
		StringBuffer contentDisposition = new StringBuffer();
		contentDisposition.append("attachment;");
		contentDisposition.append("filename=\"");
		contentDisposition.append(fileName);
		contentDisposition.append("\"");
		response.setHeader ("Content-Disposition", contentDisposition.toString());
	}
	
	protected void completeResponse(){
		FacesContext.getCurrentInstance().responseComplete();
	}
}
