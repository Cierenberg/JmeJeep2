/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.jme.models.vehicle;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.awt.geom.Point2D;

/**
 *
 * @author hendrik
 */
public class MovingMetrics {
    private long lastTime = Long.MAX_VALUE;
    private Vector3f lastPosition = null;
    private double speed = Double.NEGATIVE_INFINITY;
    private double lastSpeed = Double.NEGATIVE_INFINITY;
    private double accelertation = 0;
    private double direction;
    private final Vector3f north = new Vector3f(0, 0, 0);
    
    public MovingMetrics(long time, Vector3f position) {
        this.lastTime = time;
        this.lastPosition = position;
    }
    
    public void addMessure(long time, Vector3f position) {
        long duration = time - this.lastTime;
        float distance = this.lastPosition.distance(position);
        
        if (duration > 500) {
            if (distance > .1f) {
                this.direction = this.getYbasedDirection(position, this.lastPosition);
            }
            double durationFaktor = 1000 / duration;
            double secDistance = distance * durationFaktor;
            this.lastSpeed = this.speed;
            this.speed = (secDistance * 3600) / 1000;
            this.lastPosition = position;
            this.lastTime = time;      
            if (this.lastSpeed != Double.NEGATIVE_INFINITY) {
                this.accelertation = Math.round((this.speed - this.lastSpeed) * 5);
            }
            
        }
    }

    public int getSpeed() {
        return (int) this.speed;
    }

    public int getDirection() {
        return (int) Math.round(this.direction);
    }
    
    public int getAcceleration() {
        return (int) this.accelertation;
    }

    public int getYbasedDirection(Vector3f v) {
        return this.getYbasedDirection(v, this.north);
    }
   
    
    private int getYbasedDirection(Vector3f v1, Vector3f v2) {
        
        double a = (v1.x - v2.x);
        double b = (v1.z - v2.z);
        double w = 0;
        if (b != 0) {
            w = Math.toDegrees(Math.atan(a/b)); 
        }
        if (w < 0) {
            w = 180 + w;
        }
        if (a < 0) {
            w = 180 + w;
        }
         
        return (int) Math.round(w);
    }
    
    @Override
    public String toString() {
        return "> " + this.getAcceleration();
    }
    
    


    
}
