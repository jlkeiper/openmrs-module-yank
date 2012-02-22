/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.yank.api.db.hibernate;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.yank.Yank;
import org.openmrs.module.yank.YankArchive;
import org.openmrs.module.yank.YankError;
import org.openmrs.module.yank.api.db.YankDAO;

/**
 * It is a default implementation of  {@link YankDAO}.
 */
public class HibernateYankDAO implements YankDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

	@Override
	public Yank saveYank(Yank yank) {
		sessionFactory.getCurrentSession().saveOrUpdate(yank);
		return yank;
	}

	@Override
	public List<Yank> getAllYanks() {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Yank.class);
		return crit.list();
	}

	@Override
	public Yank getYank(Integer id) {
		return (Yank) sessionFactory.getCurrentSession().get(Yank.class, id);
	}

	@Override
	public YankError saveYankError(YankError error) {
		sessionFactory.getCurrentSession().saveOrUpdate(error);
		return error;
	}

	@Override
	public void purgeYank(Yank yank) {
		sessionFactory.getCurrentSession().delete(yank);
	}

	@Override
	public YankArchive saveYankArchive(YankArchive archive) {
		sessionFactory.getCurrentSession().saveOrUpdate(archive);
		return archive;
	}

	@Override
	public List<Yank> getYankBatch(int start, int length, String query) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Yank.class)
				.setFirstResult(start)
				.setMaxResults(length);
		
		if (!StringUtils.isBlank(query))
			crit.add(Restrictions.like("summary", query, MatchMode.ANYWHERE));
		
		return crit.list();
	}

	@Override
	public Integer countYanks(String query) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Yank.class)
				.setProjection(Projections.count("yankId"));
		
		if (!StringUtils.isBlank(query))
			crit.add(Restrictions.like("summary", query, MatchMode.ANYWHERE));
		
		return (Integer) crit.uniqueResult();
	}

	@Override
	public List<Integer> getAllYankIds() {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Yank.class)
				.setProjection(Projections.property("yankId"));
		return crit.list();
	}
}