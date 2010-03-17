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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class HeaderFilter implements Filter{
	 	 
	private final static String HDR_NAME = "headerName";
	private final static String HDR_VALUE = "headerValue";
	 
	private String hdrName = null;
	private String hdrValue = null;
 
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		hdrName = filterConfig.getInitParameter(HDR_NAME);
		hdrValue = filterConfig.getInitParameter(HDR_VALUE);
	}
	 
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) 
		throws IOException, ServletException {
	 
		HttpServletResponse httpResp = (HttpServletResponse) response;
		filterChain.doFilter(request, response);
		httpResp.setHeader(hdrName, hdrValue);
	}
 
	@Override
	public void destroy() {
	}
}