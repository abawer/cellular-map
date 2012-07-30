/*
 * Servlet for listing datastore contents 
 * Author: Amit Bawer
 * Last Update: 28 July 2012
 */
package com.geocellmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geocellmap.DataPoint;
import com.geocellmap.EntityManagerService;

@SuppressWarnings("serial")
public class ListDataServlet extends HttpServlet {
    
    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        EntityManager entityManager = EntityManagerService.get().createEntityManager();
        List<DataPoint> points = null;
        
        resp.getWriter().println("<!DOCTYPE html>");
        resp.getWriter().println("<html>");
        resp.getWriter().println("<body>");

                
        try {
            Query pointQuery = entityManager.createQuery("select d from DataPoint d");
            points = new ArrayList<DataPoint>(pointQuery.getResultList());
        } finally {
            entityManager.close();
        }
        
        if (points != null & !points.isEmpty()) {
            resp.getWriter().println("<p>Data:<p>");
            for (DataPoint d : points) {
                resp.getWriter().println("<p>"+d.getCellId() + " : " + d.getLatitude() + " " + d.getLongitude() + " " + d.getSigLevel()+"</p>" );
            }
        } else {
            resp.getWriter().println("No data.");
        }
        
        resp.getWriter().println("</body>");
        resp.getWriter().println("</html>");        
    }
}
