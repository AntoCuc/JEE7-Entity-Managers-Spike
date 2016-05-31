/*
 * The MIT License
 *
 * Copyright 2016 Antonio Cucchiara.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.cucchiara.service.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * The facade pattern (or façade pattern) is a software design pattern commonly
 * used with object-oriented programming. The name is by analogy to an
 * architectural facade.
 *
 * A facade is an object that provides a simplified interface to a larger body
 * of code, such as a class library. A facade can:
 *
 * make a software library easier to use, understand and test, since the facade
 * has convenient methods for common tasks; make the library more readable, for
 * the same reason; reduce dependencies of outside code on the inner workings of
 * a library, since most code uses the facade, thus allowing more flexibility in
 * developing the system; wrap a poorly designed collection of APIs with a
 * single well-designed API. The Facade design pattern is often used when a
 * system is very complex or difficult to understand because the system has a
 * large number of interdependent classes or its source code is unavailable.
 * This pattern hides the complexities of the larger system and provides a
 * simpler interface to the client. It typically involves a single wrapper class
 * which contains a set of members required by client. These members access the
 * system on behalf of the facade client and hide the implementation details.
 *
 * Source:
 * <ul>
 * <li>Page name: Facade pattern</li>
 * <li>Author: Wikipedia contributors</li>
 * <li>Publisher: Wikipedia, The Free Encyclopedia.</li>
 * <li>Date of last revision: 15 April 2016 21:28 UTC</li>
 * <li>Date retrieved: 27 May 2016 07:35 UTC</li>
 * <li>Permanent link:
 * https://en.wikipedia.org/w/index.php?title=Facade_pattern&oldid=715448933</li>
 * <li>Primary contributors: Revision history statistics</li>
 * <li>Page Version ID: 715448933</li>
 * </ul>
 *
 * @author Antonio Cucchiara
 * @param <T>
 */
public abstract class ManagedFacade<T> {

    private final Class<T> entityClass;

    public ManagedFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    public void edit(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    public void remove(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
