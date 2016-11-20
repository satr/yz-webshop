package io.github.satr.yzwebshop.repositories;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.List;

public abstract class HibernateRepositoryBase<T> implements Repository<T> {
    private final EntityManagerFactory entityManagerFactory;

    public HibernateRepositoryBase() {
        entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnit");
    }

    @Override
    public List<T> getList() throws SQLException {
        return getQueryable(getSqlForList(), null);
    }

    protected abstract String getSqlForList();

    @Override
    public T get(int id) throws SQLException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            return (T)entityManager.find(getEntityClass(), id);
        }
        finally {
            if(entityManager != null && entityManager.isOpen())
                entityManager.close();
        }
    }

    /*
    * Example:
    * getQueryable("from EntityName where email = :email and ID > :id",
    *                                                (query) -> {
    *                                                   query.setParameter("email", email);
    *                                                   query.setParameter("id", 10);
    *                                                });
    * */
    protected List<T> getQueryable(String sql, QueryParamsDelegate setParamsDelegate) throws SQLException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            Query query = entityManager.createQuery(sql);
            if(setParamsDelegate != null)
                setParamsDelegate.setParams(query);
            return query.getResultList();
        }
        finally {
            if(entityManager != null && entityManager.isOpen())
                entityManager.close();
        }
    }

    protected abstract Class getEntityClass();

    @Override
    public void save(T entity) throws SQLException {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            if (entityManager.contains(entity))
                entityManager.persist(entity);
            else
                entityManager.merge(entity);
            entityManager.flush();
            entityManager.getTransaction().commit();
        }
        catch(Exception ex) {
            if (entityManager != null && entityManager.isOpen() && entityManager.getTransaction().isActive())
                entityManager.getTransaction().rollback();
            throw ex;
        }
        finally {
            if(entityManager != null && entityManager.isOpen())
                entityManager.close();
        }
    }

    protected interface QueryParamsDelegate {
        void setParams(Query query);
    }
}
