/*
 * Class definitions for Data record
 * Author: Amit Bawer
 * Last Update: 28 July 2012
 */
package com.geocellmap;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DataPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long 	pointId;
    private int    cellID;
    private int    sigLevel;
    private double longitude;
    private double latitude;
        
    
    public void setPointId(Long id){
    	pointId = id;
    }
    
    public Long getPointId(){
    	return pointId;
    }
    
    public void setCellID(int id){
    	cellID = id;
    }
    
    public int getCellId(){
    	return cellID;
    }
        
    public double getLongitude(){
    	return longitude;
    }
    
    public void setLongitude(double lng){
    	longitude = lng;
    }
    
    public double getLatitude(){
    	return latitude;
    }
    
    public void setLatitude(double lat){
    	latitude = lat;
    }
    
    public int getSigLevel(){
    	return sigLevel;
    }
    
    public void setSigLevel(int level){
    	sigLevel = level;
    }    
}