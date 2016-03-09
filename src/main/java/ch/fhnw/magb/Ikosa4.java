package ch.fhnw.magb;

//  -------------   Ikosaeder   -------------------------
//
//    Adaptiert von Beispiel 2-19, p.115, OpenGL Programming Guide 7e, E. Gutknecht
//
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.event.*;
import ch.fhnw.util.math.*;

public class Ikosa4 extends GLBase1 {

    // --------------- Globale Daten ---------------

    float left = -3, right = 3; // ViewingVolume im KameraSystem
    float bottom, top;
    float near = -10, far = 100;

    // ------ Kamera-System ------------
    Vec3 A = new Vec3(0.3f, 0.1f, 1); // Kamera-Position
    Vec3 B = new Vec3(0, 0, 0); // Zielpunkt
    Vec3 up = new Vec3(0, 1, 0); // up-Vektor

    float rBackg = 0, gBackg = 0.4f, bBackg = 1; // Hintergrund-Farbe

    Vec3 lightPosition = new Vec3(-0.3f, 0.4f, 1f);// Richtung zur Lichtquelle
    float ambient = 0.2f, diffuse = 0.8f; // Parameter fuer
                                          // Helligkeitsberechnung

    // ------ Ikosaeder-Ecken ----------
    float X = 0.525731112119133606f;
    float Z = 0.850650808352039932f;
    Vec3[] vdata = { new Vec3(-X, 0, Z), new Vec3(X, 0, Z), new Vec3(-X, 0, -Z), new Vec3(X, 0, -Z), new Vec3(0, Z, X),
            new Vec3(0, Z, -X), new Vec3(0, -Z, X), new Vec3(0, -Z, -X), new Vec3(Z, X, 0), new Vec3(-Z, X, 0),
            new Vec3(Z, -X, 0), new Vec3(-Z, -X, 0) };

    // ------ Ikosaeder-Flaechen -------
    int[][] tindices = { // Indizes der Dreiecke
            { 0, 4, 1 }, { 0, 9, 4 }, { 9, 5, 4 }, { 4, 5, 8 }, { 4, 8, 1 }, { 8, 10, 1 }, { 8, 3, 10 }, { 5, 3, 8 },
            { 5, 2, 3 }, { 2, 7, 3 }, { 7, 10, 3 }, { 7, 6, 10 }, { 7, 11, 6 }, { 11, 0, 6 }, { 0, 1, 6 }, { 6, 1, 10 },
            { 9, 0, 11 }, { 9, 11, 2 }, { 9, 2, 5 }, { 7, 2, 11 } };

    // --------------- Methoden --------------------

    void zeichneIkosa(GL3 gl) {
        int[] indices;
        for (int i = 0; i < tindices.length; i++) {
            indices = tindices[i];
            int ia = indices[0];
            int ib = indices[1];
            int ic = indices[2];
            zeichneDreieck(gl, vdata[ia], vdata[ib], vdata[ic]);
        }
    }

    // @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        super.init(drawable);
        gl.glClearColor(rBackg, gBackg, bBackg, 1);
        gl.glEnable(GL3.GL_DEPTH_TEST); // Sichtbarkeits-Test

        FPSAnimator animator = new FPSAnimator(canvas, 60, true);
        animator.start();
    }

    float phi = 0;
    float lightRotationHorizontal = 0;
    float lightRotationVertical = 0;

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        setCameraSystem(gl, A, B, up); // Kamera-System festlegen
        
        // Calculate light position
        Vec3 targetLightPosition = Mat4.multiply(
                Mat4.rotate(lightRotationHorizontal, Vec3.Y),
                Mat4.rotate(lightRotationVertical, Vec3.X))
                .transform(lightPosition);
                
        setLightPosition(gl, targetLightPosition);
        
        // Draw axis
        setColor(0, 1, 1);
        drawAxis(gl, 6, 6, 6); // Koordinaten-Achsen
        
        // Rotate the figure and draw it
        rotate(gl, phi, 0, 1, 0);
        setColor(1, 0, 0);
        setShadingLevel(gl, ShadingLevel.Diffuse);
        zeichneIkosa(gl);
        
        // Update rotation
        phi += 1f;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float) height / width;
        bottom = aspect * left;
        top = aspect * right;
        // Set ViewingVolume for Orthogonalprojection
        setOrthogonalProjection(gl, left, right, bottom, top, near, far);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                lightRotationHorizontal -= 3f; break;
            case KeyEvent.VK_RIGHT:
                lightRotationHorizontal += 3f; break;
            case KeyEvent.VK_UP:
                lightRotationVertical -= 3f; break;
            case KeyEvent.VK_DOWN:
                lightRotationVertical += 3f; break;
        }
    }

    // ----------- main-Methode ---------------------------

    public static void main(String[] args) {
        Ikosa4 sample = new Ikosa4();
    }

}