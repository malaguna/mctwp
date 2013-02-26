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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.myfaces.custom.navmenu.NavigationMenuItem;

import es.urjc.mctwp.modelo.Menu;
import es.urjc.mctwp.modelo.User;
import es.urjc.mctwp.service.Command;
import es.urjc.mctwp.service.ServiceDelegate;
import es.urjc.mctwp.service.commands.userCmds.LoadUserByLogin;

public abstract class SessionAbstractBean extends AbstractBean{
	//Local attributes
	private String login = null; //read-only
	private User user = null; //read-only

	// - Graphical elements
	private List<NavigationMenuItem> menu = new ArrayList<NavigationMenuItem>();	

	public SessionAbstractBean(){
		super();
		menu = new ArrayList<NavigationMenuItem>();
		login = getExternalContext().getRemoteUser();
	}

	public String getLogin(){return login;}
	public User getUser() {return user;}
	
	public List<NavigationMenuItem> getMenu() {return menu;}	
	protected void setMenu(List<NavigationMenuItem> menu) {
		this.menu = menu;
	}
	
	@Override
	public void setService(ServiceDelegate service) {
		super.setService(service);
		
		//This command load user by login
		Command cmd = getCommand(LoadUserByLogin.class);
		((LoadUserByLogin)cmd).setLogin(login);
		cmd = runCommand(cmd);
		
		user = ((LoadUserByLogin)cmd).getResult();
	}
	
	/**
	 * It returns an appropriate JSF menu based on a list of model menus.
	 * 
	 * @param menu
	 * @return
	 */
	protected List<NavigationMenuItem> generateMenus(List<Menu> userMenu){
		List<NavigationMenuItem> result = null;

		//If user has actions authorized, build the appropriate menu
		if(userMenu != null) {
			result = new ArrayList<NavigationMenuItem>();
			
			//Retrieve first level menus
			Iterator<Menu> items = userMenu.iterator();
			while(items.hasNext()){
				Menu auxItem = items.next();
				NavigationMenuItem navItem = new NavigationMenuItem(auxItem.getText(), null);
				
				//Retrieve second level menus
				Iterator<Menu> items2 = auxItem.getChilds().iterator();
				while(items2.hasNext()){
					Menu auxItem2 = items2.next();
					navItem.add(new NavigationMenuItem(auxItem2.getText(), auxItem2.getAction().getAction()));
				}
				
				result.add(navItem);
			}
		}
		
		return result;
	}
}
