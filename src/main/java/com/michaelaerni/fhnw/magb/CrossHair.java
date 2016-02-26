package com.michaelaerni.fhnw.magb;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;

import ch.fhnw.magb.GLMinimal;

public class CrossHair extends GLMinimal {
    
    void zeichneKreis(GL3 gl, float r, int nPunkte) {
        double phi = Math.PI * 2.0 / nPunkte;
        
        rewindBuffer(gl);
        for(int i = 0; i < nPunkte; i++) {
            double x = r * Math.cos(i * phi);
            double y = r * Math.sin(i * phi);
            
            putVertex((float)x, (float)y, 0);
        }
        
        copyBuffer(gl, nPunkte);
        gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, nPunkte);
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        
        GL3 gl = drawable.getGL().getGL3();
        gl.glDisable(GL.GL_DEPTH_TEST);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        setColor(1.0f, 0.0f, 0.0f);
        zeichneKreis(gl, 0.4f, 60);
    }
    
    public static void main(String[] args) {
        new CrossHair();
    }

}
