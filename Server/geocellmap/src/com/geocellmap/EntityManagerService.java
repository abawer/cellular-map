/*
 * Class for handling datatstore persistent entities
 * Author: Amit Bawer
 * Last Update: 28 July 2012
 */
package com.geocellmap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerService {
    private static final EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("transactions-optional");

    private EntityManagerService() {
    }

    public static EntityManagerFactory get() {
        return emfInstance;
    }
}