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

package es.urjc.mctwp.service.blogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.urjc.mctwp.dao.ActionDAO;
import es.urjc.mctwp.dao.LogDAO;
import es.urjc.mctwp.dao.MenuDAO;
import es.urjc.mctwp.dao.ParticipationDAO;
import es.urjc.mctwp.dao.RolDAO;
import es.urjc.mctwp.modelo.Action;
import es.urjc.mctwp.modelo.Log;
import es.urjc.mctwp.modelo.Menu;
import es.urjc.mctwp.modelo.Participation;
import es.urjc.mctwp.modelo.Rol;
import es.urjc.mctwp.modelo.Trial;
import es.urjc.mctwp.modelo.User;

public class UserUtils extends AbstractBLogic{
	//DAO's
	private ActionDAO 	actionDao	= null;
	private MenuDAO 	menuDao 	= null;
	private RolDAO 		rolDao 		= null;
	private LogDAO		logDao		= null;
	private ParticipationDAO participationDao = null;
	
	//Local attribute
	private int adminRolCode = 1; //Default value
	

	public ActionDAO getActionDao() {
		return actionDao;
	}
	public void setActionDao(ActionDAO actionDao) {
		this.actionDao = actionDao;
	}
	public MenuDAO getMenuDao() {
		return menuDao;
	}
	public void setMenuDao(MenuDAO menuDao) {
		this.menuDao = menuDao;
	}
	public RolDAO getRolDao() {
		return rolDao;
	}
	public void setRolDao(RolDAO rolDao) {
		this.rolDao = rolDao;
	}
	public LogDAO getLogDao() {
		return logDao;
	}
	public void setLogDao(LogDAO logDao) {
		this.logDao = logDao;
	}
	public ParticipationDAO getParticipationDao() {
		return participationDao;
	}
	public void setParticipationDao(ParticipationDAO participationDao) {
		this.participationDao = participationDao;
	}
	public int getAdminRolCode() {
		return adminRolCode;
	}
	public void setAdminRolCode(int adminRolCode) {
		this.adminRolCode = adminRolCode;
	}

	/**
	 * It loads all user authorized actions, then it retrieves items menu and
	 * ask whether item menu action is authorized, then it creates a list menu
	 * items.
	 *  
	 * @param user
	 * @param trial
	 * @return
	 */
	public List<Menu> loadMenuItems(User user, Trial trial){
		List<Menu> result = null;
		Rol rol = getUserActiveRol(user, trial);
		
		if(rol != null){
			List<Action> actions = actionDao.findActionsByRol(rol.getCode());
			
			if(actions != null){
				result = new ArrayList<Menu>();
				
				//Take all first level items menu
				Iterator<Menu> auxMenu = menuDao.loadItemsMenu(null).iterator();
				while(auxMenu.hasNext()){
					Menu item = auxMenu.next();
					
					//Take all second level items menu for each first level item
					Iterator<Menu> aux2Menu = menuDao.loadItemsMenu(item).iterator();
					while(aux2Menu.hasNext()){
						Menu item2 = aux2Menu.next();
						
						//If user has authorization for the menu action
						if(actions.contains(item2.getAction())){
							item.addChild(item2);
						}
					}
					
					//Avoid empty first level menus
					if(!item.getChilds().isEmpty())
						result.add(item);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * It determines the active role of a user. If trial is null, the active role could
	 * be Admin, if only if, user isAdmin. If trial is not null and there is a participation
	 * for that trial and user, it retrieves participation's role. Otherwise it returns null.
	 * 
	 * @param user
	 * @param trial
	 * @return
	 */
	public Rol getUserActiveRol(User user, Trial trial){
		Participation part = null;
		Rol result = null;

		if(user != null){
			if(trial != null){
				part = participationDao.findParticipation(user.getCode(), trial.getCode()); 
				result = (part != null)?part.getRol():null;
			}else if(user.getAdmin()){
				result = rolDao.findById(adminRolCode);
			}
		}
		
		return result;
	}
	
	/**
	 * whether or not, a user is authorized to do some action. It depends on the 
	 * role the user plays. If actions doesn't exists it returns true.
	 * 
	 * @param user
	 * @param trial
	 * @param name
	 * @return
	 */
	public Action getActionIfAuthorized(User user, Trial trial, String name){
		Action action = null;
		Rol rol = getUserActiveRol(user, trial);
		
		//Controls bad conditions
		if( (rol != null) && (name != null) ){
			action = actionDao.findActionByName(name);			
			action = (action != null)?((rol.getActions().contains(action))?action:null):null;
		}
		
		return action;
	}
	
	/**
	 * 
	 * @param log
	 * @param action
	 * @return
	 */
	public Log logAction(Log log, Action action){
		Log result = null;
		
		if( (log != null) && (action != null) ){
			log.setAction(action);
			logDao.persist(log);
			result = log;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param log
	 */
	public void logAction(Log log){
		logDao.persist(log);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Rol> findAllRoles(){
		List<Rol> auxList = new ArrayList<Rol>();
		
		//Rol admin is no intended to be assigned in a participation
		for(Rol rol : rolDao.findAll())
			if(rol.getCode() != adminRolCode)
				auxList.add(rol);
					
		return auxList;
	}
}
