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

package es.urjc.mctwp.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * This class performs generic actions on persistent POJO 
 * instances. 
 * 
 * @author miguel
 *
 */
public abstract class GenericDAO<T, ID extends Serializable> extends HibernateDaoSupport{
	public static final int DIRTY_EVALUATION = 1;
	public static final int DIRTY_IGNORE = 2;

	private Logger logger = Logger.getLogger(this.getClass());
	private Class<T> persistentClass = null;
	
	@SuppressWarnings("unchecked")
	public GenericDAO(){
		super();
		this.persistentClass = (Class<T>)
			((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}
	
	/**
	 * Log an error with important information for correction
	 * 
	 * @param obj
	 * @param method
	 * @param e
	 */
	protected void logErrMsg(String method, Exception e){
		String typeObjt = persistentClass.getClass().getName();
		String cadError = "Method [" + method + "], " +
			"Object Type [" + typeObjt + "]. " +
			"Error: " + e.getMessage();
		
		logger.error(cadError);
	}
	
	/**
	 * Get an object from the database of class type by id
	 * 
	 * @param id of the object loaded
	 * @param type class of the entity type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T findById(ID id){
		T result = null;
		
		//Avoid bad parameters
		if(id != null){
			
			try{
				result = (T)this.getHibernateTemplate().get(persistentClass, id);
			}catch (RuntimeException re){
				logErrMsg("findById", re);
				throw re;
			}
		}
		
		return result;
	}
	
	/**
	 * Returns all instances of an entity
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll(){
		
		List<T> result = null;
		
		try{
			result = this.getHibernateTemplate().loadAll(persistentClass);
		}catch (RuntimeException re){
			logErrMsg("findAll", re);
			throw re;
		}
		
		return result;		
	}	
	
	/**
	 * Finds an object by criteria, excluding exlcudeProps
	 * 
	 * @param sample
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(T sample, String... excludeProps){
		List<T> result = null;
		
		Example example = createExample(sample, excludeProps);
		
		Criteria criteria = this.getSession().createCriteria(persistentClass);
		criteria.add(example);
		
		try{
			result = criteria.list();
		}catch (RuntimeException re){
			logErrMsg("findByCriteria", re);
			throw re;
		}
		
		return result;		
	}
	
	/** 
	 * Save a transient or persistent entity into session
	 * 
	 * @param obj
	 */
	public void persist(T obj){
		
		//Avoid bad parameters
		if( (obj != null) ){
			try{
				this.getHibernateTemplate().saveOrUpdate(obj);
			}catch (RuntimeException re){
				logErrMsg("persist", re);
				throw re;
			}
		}
	}
	
	/**
	 * Reattach a detached objetc to persistent context.
	 * 
	 * @param dirtyMode, allows to choose between consider an object dirty or clean
	 * @param obj
	 */
	public void reattach(T obj, int dirtyMode){
		
		//Avoid bad parameters
		if( (obj != null) ){
			try{
				switch(dirtyMode){
		
					case DIRTY_EVALUATION :
						this.getHibernateTemplate().update(obj);
						break;
						
					case DIRTY_IGNORE :
						this.getHibernateTemplate().lock(obj, LockMode.NONE);
						break;
				}
			}catch(RuntimeException re){
				logErrMsg("reattach", re);
				throw re;
			}
		}
	}
	
	/**
	 * Merge a detached object to the session
	 * 
	 * @param obj
	 * @return obj merged. It is attached to the session, nor the original object.
	 */
	@SuppressWarnings("unchecked")
	public T merge(T obj){
		T result = null;
		
		//Avoid bad parameters
		if( (obj != null) ){
			try{
				result = (T)this.getHibernateTemplate().merge(obj);
			}catch (RuntimeException re){
				logErrMsg("merge", re);
				throw re;
			}
		}
		
		return result;
	}
	
	/**
	 * Delete a persistent object
	 * 
	 * @param obj
	 */
	public void delete(T obj){

		//Avoid bad parameters
		if( (obj != null) ){
			try{
				this.getHibernateTemplate().delete(obj);
			}catch (RuntimeException re){
				logErrMsg("delete", re);
				throw re;
			}
		}
	}
	
	/**
	 * Create a wuery by example, from object
	 * 
	 * @param obj
	 * @param exclude
	 * @return
	 */
	protected Example createExample(Object obj, String[] exclude){
		Example result = null;
		
		if(obj != null){
			result = Example.create(obj);
			result.enableLike(MatchMode.ANYWHERE);
			result.excludeZeroes();
			result.ignoreCase();
			
			if(exclude != null)
				for(String prop : exclude)
					result.excludeProperty(prop);
		}
		
		return result;
	}
}
