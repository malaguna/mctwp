package es.urjc.mctwp.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;

import es.urjc.mctwp.modelo.File;
import es.urjc.mctwp.modelo.ImageContainer;

public class FileDAO extends GenericDAO<File, Integer> {

	@SuppressWarnings("unchecked")
	public List<File> getFilesOfSource(ImageContainer source){
		Example example = Example.create(new File());
		Example related = Example.create(source);
		
		Criteria criteria = this.getSession().createCriteria(File.class);
		criteria.add(example);
		criteria.createCriteria("source").add(related);
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<File> findFilesBySource(ImageContainer source){
		List<File> result = null;
		String query = "SELECT f FROM File f WHERE f.source=? ORDER BY f.name";
		
		try{
			result = this.getHibernateTemplate().find(query, source); 
		}catch(RuntimeException re){
			logErrMsg("findFilesBySource", re);
			throw re;
		}
		
		return result;
	}
}
