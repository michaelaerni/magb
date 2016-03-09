package com.michaelaerni.fhnw.magb;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;

import ch.fhnw.magb.GLBase1;
import ch.fhnw.magb.GLMinimal;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public class Assignment01 extends GLBase1 {
    
    private float[] xs;
    private float[] ys;
    private final Vec3[] colors = new Vec3[] {
        new Vec3(1, 1, 0),
        new Vec3(0, 1, 0),
        new Vec3(1, 0, 1)
    };
    private final int sideCount = 3;
    
    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        
        float size = .5f;
        
        // Create rectangle points
        this.xs = new float[sideCount];
        this.ys = new float[sideCount];
        float phi = 2f * (float)Math.PI / sideCount;
        for(int i = 0; i < sideCount; i++) {
            this.xs[i] = (float) (Math.sin(phi * i + Math.PI / 4) * size);
            this.ys[i] = (float) (Math.cos(phi * i + Math.PI / 4) * size);
        }
        
        GL3 gl = drawable.getGL().getGL3();
        gl.glDisable(GL.GL_DEPTH_TEST);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        
        int recursionDepth = 11;
        int colorIndex = 0;
        for(int i = 0; i < recursionDepth; i++) {
            // Draw filled area
            setColor(this.colors[colorIndex].x, this.colors[colorIndex].y, this.colors[colorIndex].z);
            colorIndex = (colorIndex + 1) % this.colors.length;
            this.drawPolygon(gl, xs, ys, true);
            
            // Draw border
            setColor(0, 0, 0);
            this.drawPolygon(gl, this.xs, this.ys, false);
            
            // Rotate and scale
            rotate(gl, 360f / (this.xs.length * 2f), 0, 0, 1);
            scale(gl, (float)Math.sqrt(2) / 2);
        }
    }
    
    private void drawPolygon(GL3 gl, float[] x, float[] y, boolean solid) {
        rewindBuffer(gl);
        
        int nVertices = x.length;
        
        for(int i = 0; i < nVertices; i++) {
            putVertex(x[i], y[i], 0);
        }
        
        copyBuffer(gl, nVertices);
        gl.glDrawArrays(solid ? GL3.GL_TRIANGLE_FAN : GL3.GL_LINE_LOOP, 0, nVertices);
    }
    
    public static void main(String[] args) {
        new Assignment01();
    }

}
