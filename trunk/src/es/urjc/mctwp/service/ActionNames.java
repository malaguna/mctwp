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

public class ActionNames {

	public final static String ADD_PROCESS_PRT		= "addProcessProtocolable";
	public final static String ADD_ROL_AUTH			= "addRolAuthorization";
	public final static String ADD_TRIAL_PART		= "addTrialParticipation";
	public final static String ADD_USER_MODALITY	= "addUserModality";
	public final static String ADD_USER_PART		= "addUserParticipation";
	
	public final static String CREATE_TASK			= "createTask";

	public final static String DELETE_GROUP			= "deleteGroup";
	public final static String DELETE_PATIENT		= "deletePatient";
	public final static String DELETE_STUDY			= "deleteStudy";
	public final static String DELETE_TASK			= "deleteTask";
	
	public final static String FIND_ALL_ACTIONS		= "findAllActions";
	public final static String FIND_ALL_PROCESS		= "findAllProcesses";
	public final static String FIND_ALL_ROLES		= "findAllRoles";
	public final static String FIND_ALL_TRIALS		= "findAllTrials";
	public final static String FIND_ALL_USERS		= "findAllUsers";
	public final static String FIND_GROUPS_TRIAL	= "findGroupsByTrial";
	public final static String FIND_IMAGES_RESULT	= "findImagesByResult";
	public final static String FIND_IMAGES_STUDY	= "findImagesByStudy";
	public final static String FIND_IMAGES_TASK		= "findImagesByTask";
	public final static String FIND_MODALITIES		= "findModalities";
	public final static String FIND_NOT_ASGN_MODALS	= "findNotAssignedModals";
	public final static String FIND_NOT_ASGN_TRIALS	= "findNotAssignedTrials";
	public final static String FIND_NOT_ASGN_USERS	= "findNotAssignedUsers";
	public final static String FIND_PATIENTS_GROUP	= "findPatientsByGroup";
	public final static String FIND_PROCESS_DEF		= "findProcessDef";
	public final static String FIND_PROCESS			= "findProcess";
	public final static String FIND_RESULTS_TASK	= "findResultsByTask";
	public final static String FIND_ROL				= "findRol";
	public final static String FIND_STUDIES_PATIENT = "findStudiesByPatient";
	public final static String FIND_TASKS_IMAGE		= "findTasksOfImage";
	public final static String FIND_TASKS_USER		= "findTasksOfUser";
	public final static String FIND_TRIAL			= "findTrial";
	public final static String FIND_TRIALS_USER		= "findTrialsByUser";
	public final static String FIND_USER			= "findUser";
	public final static String FIND_USER_LOGS		= "findUserLogs";

	public final static String LOAD_GROUP			= "loadGroup";
	public final static String LOAD_IMAGE			= "loadImage";
	public final static String LOAD_IMAGE_DATA		= "loadImageData";
	public final static String LOAD_MENU_ITEMS		= "loadMenuItem";
	public final static String LOAD_PATIENT			= "loadPatient";
	public final static String LOAD_PROTOCOLABLE	= "loadProtocolable";
	public final static String LOAD_RESULT			= "loadResult";
	public final static String LOAD_ROL				= "loadRol";
	public final static String LOAD_STUDY			= "loadStudy";
	public final static String LOAD_TASK			= "loadTask";
	public final static String LOAD_TEMP_THUMBS		= "loadThumbsOfTemporalImages";
	public final static String LOAD_TRIAL			= "loadTrial";
	public final static String LOAD_TRIAL_PARTICPS	= "loadParticipationsOfTrial";
	public final static String LOAD_USER			= "loadUser";
	public final static String LOAD_USER_BY_LOGIN	= "loadUserByLogin";
	public final static String LOAD_USER_FOLDERS	= "loadUserFolders";
	
	public final static String MOVE_PATIENT			= "movePatient";
	
	public final static String PERSIST_IMAGES		= "persistImages";
	
	public final static String REMOVE_PROCESS_PRT 	= "removeProcessProtocolable";
	public final static String REMOVE_ROL_AUTH		= "removeRolAuthorization";
	public final static String REMOVE_TRIAL_PART	= "removeTrialParticipation";
	public final static String REMOVE_USER_PART		= "removeUserParticipation";
	public final static String REMOVE_USER_MODALITY = "removeUserModality";
	
	public final static String SAVE_GROUP			= "saveGroup";
	public final static String SAVE_MODALITY		= "saveModality";
	public final static String SAVE_PATIENT			= "savePatient";
	public final static String SAVE_PROCESS_DEF		= "saveProcessDef";
	public final static String SAVE_PROCESS			= "saveProcess";
	public final static String SAVE_RESULT			= "saveResult";
	public final static String SAVE_ROL				= "saveRol";
	public final static String SAVE_STUDY			= "saveStudy";
	public final static String SAVE_TASK			= "saveTask";
	public final static String SAVE_TRIAL			= "saveTrial";
	public final static String SAVE_USER			= "saveUser";
	
	public final static String SEND_IMAGES			= "sendImages";
	
	public final static String STORE_TEMP_IMAGES	= "storeTemporalImages";
	public static final String FIND_TASKS 			= "searchTasks";
}
