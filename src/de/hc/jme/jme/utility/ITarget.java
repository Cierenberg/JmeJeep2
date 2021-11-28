/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.jme.utility;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;

/**
 *
 * @author hendrik
 */
public interface ITarget {
    public RigidBodyControl getTarget();
    public Node getNode();
    public float getTreshold();
}
