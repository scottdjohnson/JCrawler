package scottdjohnson.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	public void deleteFromQuery(String queryString)
	{
                try
                {
                        Query query = session.createQuery(queryString);
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
        * Get the results of the query, as a List
        * @param queryString The query string
        **/
        public List getFromQuery(String queryString)
	{
		List list = null;
		try
		{
			Query query = session.createQuery(queryString);
			list = query.list();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		return list;
	}
}
