/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.jme.models.vehicle;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import jme3test.bullet.TestPhysicsCar;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import de.hc.jme.jme.scene.controll.SceneControll;
import fe.hc.jme.models.Arrow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hendrik
 */
public class Jeep2 {
    private TestPhysicsCar parent;
    private AssetManager assetManager;
    private VehicleControl vehicle;
    private float maxAccelerationForce = 0.3f;
    private float maxSpeed = 150;
    private float brakeForce = 60.0f;
    private float maxSteeringValue = 0.45f;
    private float currentSteeringValue = 0.0f;
    private float steeringDeltaValue = 0.1f;    
    private float accelerationValue = 0;
    final private Vector3f jumpForce = new Vector3f(0, 2000, 0);
    private Node vehicleNode;
    private boolean sport;
    private boolean forward = true;
    protected Camera cam;
    protected Vector3f camTarget;
    private List<Vector3f> lastPositions = new ArrayList<>();
    boolean initCampos = false;
    private float rotateY;
    private RigidBodyControl target;
    private Arrow arrow;
    private long lastPositionChange = Long.MAX_VALUE;
    public boolean gameOver = false;
    public long gameOverSince = Long.MAX_VALUE;
    private MovingMetrics metrics;
    private Spatial body = null;
    private float targetYaw = 0f;
    private float curentYaw = 0f;
    private float yawRotation = 0;
    private float bodyRotation = 0.05f;
    private float targetRoll = 0f;
    private float curentRoll = 0f;
    private float bodyRotationFaktor = .6f;
    private float rollRotation = 0;
    private int steering = 0;
    boolean key[][] = 
    {
        {false,false,false},
        {false,false,false},
        {false,false,false}
    };
    private long lastPressed = System.currentTimeMillis();
    private Map<Node, Spatial> wheelMap = new HashMap<Node, Spatial>();
    
    public Jeep2(TestPhysicsCar parent, boolean sport, Vector3f initPosition, float rotateY) {
        this.parent = parent;
        this.sport = sport;
        this.rotateY = rotateY;
        Float offY = this.parent.getEnvironmentHeight(initPosition.x, initPosition.z);
           if (offY != null) {
                initPosition.y = offY + 2f;
           }       
        this.assetManager = this.parent.getAssetManager();
        this.initJeep(initPosition);
    }
    
    public void setTarget(RigidBodyControl target) {
        this.target = target;        
    }
    
    public void setCam(Camera cam) {
        this.cam = cam;
        this.lastPositions.clear();
        this.lastPositions.add(this.vehicle.getPhysicsLocation());
        this.camTarget = this.cam.getLocation();
        this.updateCam();
        this.cam.setLocation(camTarget);     
    }
    
    public long getLastMoveDuration() {
        return System.currentTimeMillis() - this.lastPositionChange;
    }
    
    public void updateKeys() {
        float speed = Math.min(this.maxSpeed, this.getSpeed());
        float accelerationPower = 1 - speed / this.maxSpeed;
        
        
        
        float accelerationForce = accelerationPower * this.maxAccelerationForce;
        if (this.key[0][1]) {
            if (accelerationPower == 1 && !this.sport) {
                this.turbo();
            }
            if (this.forward) {
                this.accelerationValue += accelerationForce;
            } else {
                this.accelerationValue -= accelerationForce;
            }
            this.vehicle.accelerate(this.accelerationValue);    
        } else {
            this.accelerationValue = 0;
            this.vehicle.accelerate(this.accelerationValue);
        }
        
        if (this.key[2][1]) {
            this.vehicle.brake(this.brakeForce);
        } else {
            this.vehicle.brake(0f);
        }

        if (this.key[1][0]) {
            if (this.currentSteeringValue < this.maxSteeringValue) {
                this.currentSteeringValue += this.steeringDeltaValue;
            }
            this.steering = -1;
        } else if (this.key[1][2]) {
            if (this.currentSteeringValue > -1 * this.maxSteeringValue) {
                this.currentSteeringValue -= this.steeringDeltaValue;
            }
            this.steering = 1;
        } else {
            if (this.currentSteeringValue < 0) {
                this.currentSteeringValue += this.steeringDeltaValue;
            }
            if (this.currentSteeringValue > 0) {
                this.currentSteeringValue -= this.steeringDeltaValue;
            }
            if (this.currentSteeringValue > -1 * this.steeringDeltaValue && this.currentSteeringValue < this.steeringDeltaValue) {
                this.currentSteeringValue = 0;
            }
            this.steering = 0;
        }
        this.vehicle.steer(this.currentSteeringValue);
        
        
        
        
        
        this.key = new boolean[][]
        {
            {false,false,false},
            {false,false,false},
            {false,false,false}
        };        
    }
    
    public void updateBodyEffects() {
        if (this.metrics != null) {
            int speed = this.metrics.getSpeed();
            int acceleration = this.metrics.getAcceleration();
            if (this.steering != 0 && speed > 5) {
                this.targetRoll = (float) (speed / 1.5 * this.bodyRotationFaktor * this.steering);
            } else {
                this.targetRoll = 0;
            }
            
//            System.out.println(this.steering + "*: " + this.targetRoll);
            
            if (Math.abs(acceleration) > 5 && speed < 30) {
                this.targetYaw = acceleration * this.bodyRotationFaktor;
            } else {
                this.targetYaw = 2;
            }
            
            if (!this.forward && speed > 3) {
                this.targetYaw *= -1;
                this.targetRoll *= -1;
            }
            
            
            if (this.curentYaw - 2 * this.bodyRotation < this.targetYaw) {
                this.curentYaw = this.curentYaw + this.bodyRotation;
            } else if (this.curentYaw + 2 * this.bodyRotation > this.targetYaw) {
                this.curentYaw = this.curentYaw - this.bodyRotation;
            }
            if (this.curentRoll - 2 * this.bodyRotation < this.targetRoll) {
                this.curentRoll = this.curentRoll + this.bodyRotation;
            } else if (this.curentRoll + 2 * this.bodyRotation > this.targetRoll) {
                this.curentRoll = this.curentRoll - this.bodyRotation;
            }


            float defYaw = this.curentYaw - this.yawRotation; 
            float defRoll = this.curentRoll - this.rollRotation; 
            
            
            
            this.body.rotate(
                    (float) (
                        defYaw * Math.PI / 180.0
                    ), 
                    0f, 
                    (float) (
                        defRoll * Math.PI / 180.0
                    ));

            this.yawRotation += defYaw;
            this.rollRotation += defRoll;
            
  
        }
    } 
    
    public void updateCam(){
        if (!this.gameOver) {
            this.updateBodyEffects();
            this.updateKeys();
            if (this.vehicle.getPhysicsLocation().y < 1) {
                this.setGameOver();
            }
//            System.out.println(this.vehicle.getPhysicsLocation());
            if (this.metrics == null) {
                this.metrics = new MovingMetrics(System.currentTimeMillis(), this.vehicle.getPhysicsLocation());
            } else {
                this.metrics.addMessure(System.currentTimeMillis(), this.vehicle.getPhysicsLocation());
//                System.out.println(this.metrics.toString());
            }

            if (this.target != null) {
                this.arrow.lookAt(this.target);
            }
            if (!this.lastPositions.isEmpty()) {
                if (this.lastPositions.get(this.lastPositions.size() - 1).distance(this.vehicle.getPhysicsLocation()) > 0.05) {
                    this.lastPositionChange = System.currentTimeMillis();
                    this.lastPositions.add(this.vehicle.getPhysicsLocation());
                }
                while(this.lastPositions.size() > 1 && this.lastPositions.get(0).distance(this.vehicle.getPhysicsLocation()) > 25) {
                    this.lastPositions.remove(0);
                }
                if (this.lastPositions.get(0).distance(this.vehicle.getPhysicsLocation()) > 20) {
                    this.camTarget = this.lastPositions.get(0);
                    Float offY = this.parent.getEnvironmentHeight(this.camTarget.x, this.camTarget.z);
                    if (offY != null) {
                         this.camTarget.y = offY + 5f;
                    }
                    this.cam.setLocation(camTarget);
                    this.initCampos = true;
                }

            }
            this.cam.lookAt(this.vehicle.getPhysicsLocation(), Vector3f.UNIT_Y);
            if (!this.initCampos) {

                Float distance = this.camTarget.distance(this.vehicle.getPhysicsLocation());
                if (distance > 35) {
                    this.camTarget = this.vehicle.getPhysicsLocation();
                    this.camTarget.x += Math.random() * 10 - 5;
                    this.camTarget.z += Math.random() * 10 - 5;
                    Float offY = this.parent.getEnvironmentHeight(this.camTarget.x, this.camTarget.z);
                    if (offY != null) {
                         this.camTarget.y = offY + 1f;
                    }
                    if(Math.random() * 100 > 20) {
                        this.camTarget.y += Math.random() * 5 + 2;
                    }        
                }


                Float camspeed = 0.01f * distance;
                if (this.cam.getLocation().x < this.camTarget.x - (camspeed + .1f)) {
                    this.cam.getLocation().x += camspeed;
                } else if (this.cam.getLocation().x > this.camTarget.x + (camspeed + .1f)) {
                    this.cam.getLocation().x -= camspeed;
                } 

                if (this.cam.getLocation().y < this.camTarget.y -(camspeed + .1f)) {
                    this.cam.getLocation().y += camspeed;
                } else if (this.cam.getLocation().y > this.camTarget.y + (camspeed + .1f)) {
                    this.cam.getLocation().y -= camspeed;
                } 

                if (this.cam.getLocation().z < this.camTarget.z -(camspeed + .1f)) {
                    this.cam.getLocation().z += camspeed;
                } else if (this.cam.getLocation().z > this.camTarget.z + (camspeed + .1f)) {
                    this.cam.getLocation().z -= camspeed;
                }
            }
            SceneControll.getDefault().checkTarget(this.parent);
        } else {
            if (System.currentTimeMillis() - this.gameOverSince > 5000 && this.vehicle != null) {
                this.parent.getRootNode().detachChild(this.vehicleNode);
                this.parent.getRootNode().detachAllChildren();
                this.parent.getPhysicsSpace().destroy();
                this.vehicle = null;
            }
        }
    }
    
    private void initJeep(Vector3f initPosition) {
        Material mat_body;
        
        
        if (this.sport) {
            this.bodyRotation /= 3;
            mat_body = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat_body.setTexture("ColorMap",
                assetManager.loadTexture("Textures/all.png"));
            this.body = this.assetManager.loadModel("Models/sport_export.j3o");   
            this.body.setMaterial(mat_body);
            this.body.setShadowMode(RenderQueue.ShadowMode.Cast);            
        } else {
            mat_body = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat_body.setTexture("ColorMap",
                assetManager.loadTexture("Textures/chassie.png"));
            this.body = this.assetManager.loadModel("Models/body.j3o");   
            this.body.setMaterial(mat_body);
            this.body.setShadowMode(RenderQueue.ShadowMode.Cast);          
        }
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        if (this.sport) {
            BoxCollisionShape box = new BoxCollisionShape(new Vector3f(1.5f, 0.5f, 2.4f));

            compoundShape.addChildShape(box, new Vector3f(0, 1f, 0));
            Geometry myGeo = this.parent.getGeometry(this.body);
            CollisionShape carHull;
            carHull = CollisionShapeFactory.createDynamicMeshShape(myGeo);
            compoundShape.addChildShape(carHull, new Vector3f(0f, 0.7f, -0.1f));
        } else {
            BoxCollisionShape box = new BoxCollisionShape(new Vector3f(1.5f, 0.5f, 2.4f));

            compoundShape.addChildShape(box, new Vector3f(0, 1.5f, 0));
            Geometry myGeo = this.parent.getGeometry(this.body);
            CollisionShape carHull;
            carHull = CollisionShapeFactory.createDynamicMeshShape(myGeo);
            compoundShape.addChildShape(carHull, new Vector3f(0f, 1.5f, -0.3f));
        }
        
        //create vehicle node
        this.vehicleNode = new Node("vehicleNode");
        this.vehicleNode.setLocalTranslation(initPosition);
        this.vehicleNode.rotate(0, (float) Math.toRadians(this.rotateY), 0);
                
        this.vehicleNode.attachChild(this.body);
        if (this.sport) {
            this.body.setLocalTranslation(0f, 0.7f, -0.1f);
        } else {
            this.body.setLocalTranslation(0f, 1.2f, -0.3f);
        }
        this.body.rotate(0, FastMath.PI, 0);
        if (this.sport) {
            this.body.scale(.8f, .8f, .9f);
        } else {
            this.body.scale(1f, 1f, 1f);
        }
        if (this.sport) {
            this.vehicle = new VehicleControl(compoundShape, 200);
        } else {
            this.vehicle = new VehicleControl(compoundShape, 400);
        }
        this.vehicleNode.addControl(vehicle);
        float stiffness =  80f;//200=f1 car
        float compValue = .1f; //(should be lower than damp)
        float dampValue = .3f;
        if (this.sport) {
            stiffness =  140f;//200=f1 car
            compValue = .3f; //(should be lower than damp)
            dampValue = .6f;
            this.maxAccelerationForce *= 1.2f;
            this.maxSpeed *= 1.5f;
            this.maxSteeringValue = .5f;
        }
        this.vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        this.vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        this.vehicle.setSuspensionStiffness(stiffness);
        this.vehicle.setMaxSuspensionForce(10000.0f);
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
        float radius = 0.5f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 1f;
        float zOff = 1.8f;
        this.vehicle.setFriction(0.6f);
        this.vehicle.setFrictionSlip(1f);
        Material mat_wheel = new Material(
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        Material mat_wheel = new Material(
//            assetManager, "Common/MatDefs/Light/Lighting.j3md");
//        
//        mat_wheel.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//
//        mat_wheel.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Rad_alpha.png"));
        
        
        mat_wheel.setTexture("ColorMap",
            assetManager.loadTexture("Textures/Rad_alpha.png"));
        Node node1 = new Node("wheel 1 node");
        Spatial wheelfr = this.assetManager.loadModel("Models/wheel.j3o");   
        wheelfr.setMaterial(mat_wheel);
        node1.attachChild(wheelfr);
        wheelfr.rotate(0, FastMath.PI, 0);
        wheelfr.scale(0.7f, 0.7f, 0.7f);
        wheelfr.setShadowMode(RenderQueue.ShadowMode.Cast);            
        this.wheelMap.put(node1, wheelfr);
        this.vehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);
        Node node2 = new Node("wheel 2 node");
        Spatial wheelfl = this.assetManager.loadModel("Models/wheel.j3o");   
        wheelfl.setMaterial(mat_wheel);
        node2.attachChild(wheelfl);
        wheelfl.scale(0.7f, 0.7f, 0.7f);
        wheelfl.setShadowMode(RenderQueue.ShadowMode.Cast);
        this.wheelMap.put(node2, wheelfl);
        this.vehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);
        Node node3 = new Node("wheel 3 node");
        Spatial wheelrr = this.assetManager.loadModel("Models/wheel.j3o");   
        wheelrr.setMaterial(mat_wheel);
        node3.attachChild(wheelrr);
        wheelrr.rotate(0, FastMath.PI, 0);
        wheelrr.scale(0.7f, 0.7f, 0.7f);
        wheelrr.setShadowMode(RenderQueue.ShadowMode.Cast);
        this.wheelMap.put(node3, wheelrr);
        vehicle.addWheel(node3, new Vector3f(-xOff, yOff, -0.2f-zOff),
                wheelDirection, wheelAxle, restLength, radius, false);
        Node node4 = new Node("wheel 4 node");
        Spatial wheelrl = this.assetManager.loadModel("Models/wheel.j3o");   
        wheelrl.setMaterial(mat_wheel);
        node4.attachChild(wheelrl);
        wheelrl.scale(0.7f, 0.7f, 0.7f);
        wheelrl.setShadowMode(RenderQueue.ShadowMode.Cast);
        this.wheelMap.put(node4, wheelrl);
        vehicle.addWheel(node4, new Vector3f(xOff, yOff, -0.2f-zOff),
                wheelDirection, wheelAxle, restLength, radius, false);
        this.vehicleNode.attachChild(node1);
        this.vehicleNode.attachChild(node2);
        this.vehicleNode.attachChild(node3);
        this.vehicleNode.attachChild(node4);
        this.arrow = new Arrow(this.parent,new Vector3f(0f, 3f, 0f));
        this.vehicleNode.attachChild(this.arrow.getSpatial());
    }
    
    public boolean isForward() {
        return this.forward;
    }
    
    public Node getVehicleNode() {
        return this.vehicleNode;
    }
       
    public VehicleControl getVehicleControl() {
        return this.vehicle;
    }
    
    public void steerLeft() {
       this.key[1][0] = true;
    }
    
    public void steerRight() {
       this.key[1][2] = true;
    }

    
    public void jump() {
        this.vehicle.applyImpulse(this.jumpForce, Vector3f.ZERO);
    }

    public void turbo() {
        if (this.forward) {
            this.vehicle.applyImpulse(this.vehicleNode.getLocalRotation().getRotationColumn(2).mult(20f), this.vehicleNode.getLocalRotation().getRotationColumn(2));
        } else {
            this.vehicle.applyImpulse(this.vehicleNode.getLocalRotation().getRotationColumn(2).mult(20f).negateLocal(), this.vehicleNode.getLocalRotation().getRotationColumn(2).negateLocal());
        }
    }
    
    public int getSpeed() {
        if (this.metrics != null) {
            return this.metrics.getSpeed();
        }
        return 0;
    }
    
    private void explode() {     
        Material shockWaveMaterial = new Material(
        this.parent.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        shockWaveMaterial.setTexture("Texture",
        this.parent.getAssetManager().loadTexture("Effects/Explosion/shockwave.png"));
    
        
        ParticleEmitter explosion = new ParticleEmitter("explosion effect", Type.Triangle, 1);
        this.parent.getRootNode().attachChild(explosion);
        Vector3f location =  this.vehicle.getPhysicsLocation();
        location.y = location.y + 1;
        explosion.setLocalTranslation(location);
        explosion.setFaceNormal(Vector3f.UNIT_Y);
        explosion.emitAllParticles();
        explosion.setParticlesPerSec(0);
        explosion.setMaterial(shockWaveMaterial);
        explosion.setStartColor(ColorRGBA.Blue);
        explosion.setEndColor(ColorRGBA.LightGray);

        explosion.setStartSize(5f);
        explosion.setEndSize(20f);

        explosion.setImagesX(1); // columns
        explosion.setImagesY(1); // rows
        explosion.setSelectRandomImage(false);
        
        
        this.vehicle.removeWheel(0);
        this.vehicle.removeWheel(0);
        this.vehicle.removeWheel(0);
        this.vehicle.removeWheel(0);
        
        Material mat_wheel = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_wheel.setTexture("ColorMap",
            assetManager.loadTexture("Textures/Rad_alpha.png"));
        
        for (Node node : this.wheelMap.keySet()) {
            Node tmp = new Node();
            tmp.setLocalTranslation(node.getWorldTranslation());
            tmp.setLocalRotation(node.getWorldRotation());
            this.vehicleNode.detachChild(node);
            this.wheelMap.get(node).setMaterial(mat_wheel);
            tmp.attachChild(this.wheelMap.get(node));
            this.parent.getRootNode().attachChild(tmp);
            
            RigidBodyControl rbc = new RigidBodyControl(1f);
            this.wheelMap.get(node).addControl(rbc);
            this.parent.getPhysicsSpace().add(rbc);
            if (this.forward) {
               rbc.applyImpulse(this.vehicleNode.getLocalRotation().getRotationColumn(2).mult(this.getSpeed()), this.vehicleNode.getLocalRotation().getRotationColumn(2));
            } else {
                this.vehicle.applyImpulse(this.vehicleNode.getLocalRotation().getRotationColumn(2).mult(this.getSpeed()).negateLocal(), this.vehicleNode.getLocalRotation().getRotationColumn(2).negateLocal());
            }
        }
        
        
        this.vehicle.applyImpulse(new Vector3f(1000, 20000, 100), Vector3f.ZERO);
    }
      
    public void setGameOver() {
        if (!this.gameOver) {
            this.gameOver = true;
            this.gameOverSince = System.currentTimeMillis();
            this.explode();
        }
    }

    public boolean isGameOver() {
        return this.gameOver;
    }

    
    public void accelerate() {
        if (!this.gameOver){
           this.key[0][1] = true;
        }
    }

    public void shift() {
        if (System.currentTimeMillis() - this.lastPressed > 1000) {
            this.forward = !this.forward;
            this.lastPressed = System.currentTimeMillis();
        }
    }

    public void brake() {
        if (!this.gameOver){
           this.key[2][1] = true;
        }
     }
    
    public void resetRotation() {
//                vehicle.setPhysicsLocation(Vector3f.ZERO);
        vehicle.setPhysicsRotation(new Matrix3f());
        vehicle.setLinearVelocity(Vector3f.ZERO);
        vehicle.setAngularVelocity(Vector3f.ZERO);
        vehicle.resetSuspension();
    }

    private void put(Node node1, Spatial wheelfr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
