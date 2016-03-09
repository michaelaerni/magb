package com.michaelaerni.fhnw.magb;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.FPSAnimator;

import ch.fhnw.magb.GLBase1;
import ch.fhnw.magb.MyFirst3D;
import ch.fhnw.util.math.Vec3;

public class Assignment02 extends GLBase1 {
    
    private Vec3 A = new Vec3(3,0.3f,1);                // Kamera-Position
    private Vec3 B = new Vec3(0,0,0);                   // Zielpunkt
    private Vec3 up = new Vec3(0,1,0);                  // up-Vektor
    private float left=-5, right=5;                     // Viewing-Volume
    private float bottom, top;
    private float near=-10, far=1000;
    
    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        
        FPSAnimator animator = new FPSAnimator(this.getGLCanvas(), 60, true);
        animator.start();
    }
    
    float rotation = 0;
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        
        setCameraSystem(gl,A,B,up);
        
        setColor(1, 1, 1);
        drawAxis(gl, 8, 8, 8);
        
        setColor(1, 0, 0);
        int count = 20;
        float phi = 0;
        float height = 3f;
        for(int i = 0; i < count; i++) {
            pushMatrix(gl);
            rotate(gl, phi, 0, 1, 0);
            translate(gl, 1f, height, 0);
            this.drawRectangle(gl, 0.5f, 0.5f);
            popMatrix(gl);
            phi += 720f / count;
            height -= 3f / count;
        }
        
        rotate(gl, rotation, 0, 1, 0);
        rotation += 1f;
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
       GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);

       // Set projection matrix
       float aspect = (float)height / width;
       bottom = aspect * left;
       top = aspect * right;
       setOrthogonalProjection(gl,left,right,bottom,top,near,far);
    }
    
    private void drawRectangle(GL3 gl, float halfwidth, float halfheight) {
        putVertex(-halfwidth, 0, -halfheight);
        putVertex(halfwidth, 0, -halfheight);
        putVertex(halfwidth, 0, halfheight);
        putVertex(-halfwidth, 0, halfheight);
        
        int vertexCount = 4;
        copyBuffer(gl, vertexCount);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, vertexCount);
    }
    
    public static void main(String[] args) {
        new Assignment02();
    }
}
