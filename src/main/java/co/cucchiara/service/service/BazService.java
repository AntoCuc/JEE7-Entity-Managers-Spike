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

import co.cucchiara.model.Baz;
import co.cucchiara.service.service.deltaspike.BazRepository;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

/**
 * Service class leveraging the Java Persistence Api and delegating the
 * management of database transactions to Deltaspike annotation.
 *
 * <p>
 * Main characteristic of such a usage is the manual handling of database
 * transactions in the Deltaspike <code>Transactional</code> annotation.
 *
 * <p>
 * Manual handling of database transactions offers flexibility but also requires
 * extra-effort in the design of error handling. Ensuring all state within the
 * boundary of a transactional annotation boundary gets attached and persisted,
 * in the event of new state, merged in the case of amended state, deleted, or
 * rolled back in the event in which an exception occurs is now delegated to the
 * application itself for manual handling.
 *
 * @author Antonio Cucchiara
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Path("baz")
public class BazService {

    @Inject
    private BazRepository bazRepository;

    @POST
    @Consumes({"application/xml", "application/json"})
    @Transactional // This is the Deltaspike NOT the JavaEE @Transactional
    public void create(Baz entity) {
        bazRepository.saveAndFlush(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    @Transactional // This is the Deltaspike NOT the JavaEE @Transactional
    public void edit(@PathParam("id") Long id, Baz entity) {
        bazRepository.saveAndFlush(entity);
    }

    @DELETE
    @Path("{id}")
    @Transactional // This is the Deltaspike NOT the JavaEE @Transactional
    public void remove(@PathParam("id") Long id) {
        bazRepository.removeAndFlush(bazRepository.findBy(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Baz find(@PathParam("id") Long id) {
        return bazRepository.findBy(id);
    }

    @GET
    @Produces({"application/xml", "application/json"})
    public List<Baz> findAll() {
        return bazRepository.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Baz> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        throw new RuntimeException("Can't really do this in few lines. Maybe it isn't necessary, though.");
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional // This is the Deltaspike NOT the JavaEE @Transactional
    public String countREST() {
        return String.valueOf(bazRepository.count());
    }

}
