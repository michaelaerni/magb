package ch.fhnw.magb;
//  --------  Interface to OpenGL 3.0  --------------
//                                        E. Gutknecht Feb 2016
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import ch.fhnw.util.math.*;                        // Vektor- und Matrix-Algebra

public interface MyRenderer1
{

    public GLCanvas getGLCanvas();                         // OpenGL-Window

    public int getProgramId();                             // OpenGL Program-Identification


    //  --------  Vertex-Methoden  -----------

    public void setColor(float r, float g, float b);       // aktuelle Vertexfarbe setzen

    void putVertex(float x, float y, float z);             // Vertex-Daten in Buffer speichern

    public void copyBuffer(GL3 gl,int nVertices);          // Vertex-Array in OpenGL-Buffer kopieren

    public void rewindBuffer(GL3 gl);                      // Bufferposition zuruecksetzen


    //  ---------  Operationen fuer ModelView-Matrix  --------------------

    public void setModelViewMatrix(GL3 gl, Mat4 M);

    public Mat4 getModelViewMatrix(GL3 gl);

    public void pushMatrix(GL3 gl);                                                   // aktuelle Matrix auf Stack ablegen

    public void popMatrix(GL3 gl);                                                    // Matrix vom Stack reaktivieren

    public void multMatrix(GL3 gl, Mat4 A);                                           // M = M * A

    public void loadIdentity(GL3 gl);                                                 // Rueckstellung auf Einheitsmatrix

    //  -------  Kamera-System festlegen  --------------------------------

    public void setCameraSystem(GL3 gl, Vec3 A, Vec3 B, Vec3 up);                     // LookAt-Positionierung


    //  -------  Operationen fuer Objekt-System  ---------------------

    public void rotate(GL3 gl, float phi, float x, float y, float z);                 // Objekt-System drehen, phi in Grad

    public void translate(GL3 gl, float x, float y, float z);                         // Objekt-System verschieben

    public void scale(GL3 gl, float scale);                                           // Skalierung Objekt-System


    //  ---------  Projektion auf Bildebene -------------------

    public void setProjectionMatrix(GL3 gl, Mat4 P);

    public Mat4 getProjectionMatrix(GL3 gl);

    public void setOrthogonalProjection(GL3 gl, float left, float right,           // Grenzen des ViewingVolumes
                                      float bottom, float top,
                                      float near, float far);

    public void setPerspectiveProjection(GL3 gl, float left, float right,          // Grenzen des ViewingVolumes
                                      float bottom, float top,
                                      float near, float far);

}
