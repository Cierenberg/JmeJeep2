/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.jme.models.vehicle;

import com.jme3.math.Vector3f;

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
    
    public MovingMetrics(long time, Vector3f position) {
        this.lastTime = time;
        this.lastPosition = position;
    }
    
    public void addMessure(long time, Vector3f position) {
        long duration = time - this.lastTime;
        float distance = this.lastPosition.distance(position);
        if (duration > 500) {
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

    public int getAcceleration() {
        return (int) this.accelertation;
    }

    
    @Override
    public String toString() {
        return "> " + this.getAcceleration();
    }
    
    


    
}
