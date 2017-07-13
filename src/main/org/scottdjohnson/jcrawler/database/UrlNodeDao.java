package org.scottdjohnson.jcrawler.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scottdjohnson.jcrawler.node.URLNode;

public class UrlNodeDao extends Dao
{
	private static final Logger logger = Logger.getLogger(UrlNodeDao.class.getPackage().getName());

	public UrlNodeDao (Session s, Transaction t) {
		super(s,t);
	}

	/**
	* Delete the results of the query
	* @param queryString The HQL-style string to query, all results will be deleted
	**/
	public void deleteAllUrls()
	{
                try
                {
                        Query query = session.createQuery("from URLNode url_list");
                        List list = query.list();

                        for (int i = 0; i < list.size(); i++)
                        {
                                session.delete( list.get(i) );
                        }

                        tx.commit();
                }
                catch (Exception e)
                {
                         System.out.println(e.getMessage());
                }
	}

        /**
        * Get the List of Urls with the given parent
        * @param parentKey The key of the parent URL
        **/
        public List<URLNode> getUrlsByParent(Integer parentKey)
	{
		List list = null;
		try
		{
			Query query = session.createQuery("from URLNode url_list where parent_key = :parentKey");
			query.setParameter("parentKey", parentKey);
			list = query.list();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		return list;
	}

        /**
        * Get the number of crawled URLs with the given parent
        * @param parentKey The key of the parent URL
        **/
        public int getNumUrlsWithParent(Integer parentKey)
        {
                List list = null;
                try
                {
                        Query query = session.createQuery("select count(*) from URLNode url_list where parent_key = :parentKey");
                        query.setParameter("parentKey", parentKey);
                        list = query.list();
                }
                catch (Exception e)
                {
                        System.out.println(e.getMessage());
                }

                return list == null ? -1 : (int)list.get(0);
        }

        /**
        * Get the List of Urls which have this key
        * @param urlKey The key of the URL
        **/
        public URLNode getUrlByKey(Integer urlKey)
        {
		logger.log(Level.INFO, "Get Url with key: " + urlKey);

                List<URLNode> list = null;
                try
                {
                        Query query = session.createQuery("from URLNode url_list where url_key = :urlKey");
                        query.setParameter("urlKey", urlKey);
                        list = query.list();
                }
                catch (Exception e)
                {
                        System.out.println(e.getMessage());
                }

                return (list == null || list.size() == 0) ? null : list.get(0);
        }
}
