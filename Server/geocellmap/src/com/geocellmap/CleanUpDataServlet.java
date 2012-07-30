/*
 * Servlet for cleaning up collected datastore data
 * Author: Amit Bawer
 * Last Update: 28 July 2012
 */
package com.geocellmap;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.io.IOException;

@SuppressWarnings("serial")
public class CleanUpDataServlet extends HttpServlet {
        public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {
        	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        	Query mydeleteq = new Query();
        	PreparedQuery pq = datastore.prepare(mydeleteq);
        	for (Entity result : pq.asIterable()) {
        		datastore.delete(result.getKey());      
        	}        	        	
        }
}