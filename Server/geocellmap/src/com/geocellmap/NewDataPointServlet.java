/*
 * Servlet for adding new data to datastore
 * Author: Amit Bawer
 * Last Update: 28 July 2012
 */
package com.geocellmap;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geocellmap.DataPoint;
import com.geocellmap.EntityManagerService;

@SuppressWarnings("serial")
public class NewDataPointServlet extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        EntityManager entityManager = EntityManagerService.get().createEntityManager();
        DataPoint dPoint = new DataPoint();
        dPoint.setCellID(Integer.parseInt(req.getParameter("cid")));
        dPoint.setSigLevel(Integer.parseInt(req.getParameter("siglevel")));
        dPoint.setLatitude(Double.parseDouble(req.getParameter("latitude")));
        dPoint.setLongitude(Double.parseDouble(req.getParameter("longitude")));
        
        try {
            entityManager.persist(dPoint);
        } finally {
            entityManager.close();
        }      
    }
}