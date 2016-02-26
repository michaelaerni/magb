package ch.fhnw.magb;
//  -------------   Minimales JOGL Programm  -------------------
//
//                                                            E.Gutknecht, Februar 2016
//  adaptiert von:
//  http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
//
import java.awt.*;
import java.awt.event.*;
import java.nio.*;
import java.util.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.common.nio.*;

public class GLMinimal
       implements WindowListener, GLEventListener
{

    //  --------------  Globale Daten  -------------------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = "vShader0.glsl";               // Filename Vertex-Shader
    String fShader = "fShader0.glsl";               // Filename Fragment-Shader
    int maxVerts = 2048;                            // max. Anzahl Vertices im Vertex-Array

    float[] clearColor = {0,0,1,1};                 // Fensterhintergrund (Blau)
    GLCanvas canvas;                                // OpenGL Window


    //  --------  Vertex-Array (fuer die Attribute Position, Color)  ------------

    FloatBuffer vertexBuf;                                     // Vertex-Array
    final int vPositionSize = 4*Float.SIZE/8;                  // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    final int vColorSize = 4*Float.SIZE/8;                     // Anz. Bytes der rgba Werte
    final int vertexSize = vPositionSize + vColorSize;         // Anz. Bytes eines Vertex
    int bufSize;                                               // Anzahl Bytes des VertexArrays = maxVerts * vertexSize

    float[] currentColor = { 1,1,1,1};                         // aktuelle Farbe fuer Vertices


    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------

    int programId;                                             // Program-Object
    int vaoId;                                                 // Identifier fuer OpenGL VertexArray Object
    int vertexBufId;                                           // Identifier fuer OpenGL Vertex Buffer
    int vPositionLocation, vColorLocation;                     // Vertex Attribute Shader Variables


    //  -------------  Methoden  ---------------------------

    public GLMinimal()                                       // Konstruktor
    {   createFrame();
    }


    void createFrame()                                        // Fenster erzeugen
    {
       Frame f = new Frame(windowTitle);
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       GLProfile glp = GLProfile.get(GLProfile.GL3);
       GLCapabilities glCaps = new GLCapabilities(glp);
       canvas = new GLCanvas(glCaps);
       canvas.addGLEventListener(this);
       f.add(canvas);
       f.setVisible(true);
    };


     void setupVertexBuffer(int pgm, GL3 gl, int maxVerts)
     {
       bufSize = maxVerts * vertexSize;
       vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
       // ------  OpenGl-Objekte -----------
       int[] tmp = new int[1];
       gl.glGenVertexArrays(1, tmp, 0);                        // VertexArrayObject
       vaoId = tmp[0];
       gl.glBindVertexArray(vaoId);
       gl.glGenBuffers(1, tmp, 0);                             // VertexBuffer
       vertexBufId = tmp[0];
       gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
       gl.glBufferData(GL3.GL_ARRAY_BUFFER, bufSize,           // Speicher allozieren
                            null, GL3.GL_STATIC_DRAW);

       // ----- get shader variable identifiers  -------------
       vPositionLocation = gl.glGetAttribLocation(pgm, "vertexPosition");
       vColorLocation = gl.glGetAttribLocation(pgm, "vertexColor");

       //  ------  enable vertex attributes ---------------
       gl.glEnableVertexAttribArray(vPositionLocation);
       gl.glEnableVertexAttribArray(vColorLocation);
       gl.glVertexAttribPointer(vPositionLocation, 4, GL3.GL_FLOAT, false, vertexSize, 0);
       gl.glVertexAttribPointer(vColorLocation, 4, GL3.GL_FLOAT, false, vertexSize, vPositionSize);
    };


    // --------  Vertex-Methoden  --------------

    public void setColor(float r, float g, float b)             // aktuelle Vertexfarbe setzen
    {  currentColor[0] = r;
       currentColor[1] = g;
       currentColor[2] = b;
       currentColor[3] = 1;
    }

    public void putVertex(float x, float y, float z)            // Vertex-Daten in Buffer speichern
    {  vertexBuf.put(x);
       vertexBuf.put(y);
       vertexBuf.put(z);
       vertexBuf.put(1);
       vertexBuf.put(currentColor);                             // Farbe
    }

    public void copyBuffer(GL3 gl,int nVertices)                // Vertex-Array in OpenGL-Buffer kopieren
    {
       vertexBuf.rewind();
       if ( nVertices > maxVerts )
         throw new IndexOutOfBoundsException();
       gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
       gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, nVertices*vertexSize, vertexBuf);
    }

    public void rewindBuffer(GL3 gl)                            // Bufferposition zuruecksetzen
    {  vertexBuf.rewind();
    }


   //  ---------  Zeichenmethoden  ------------------------------

    public void zeichneDreieck(GL3 gl, float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3)
    {  rewindBuffer(gl);
       putVertex(x1,y1,z1);           // Eckpunkte in VertexArray speichern
       putVertex(x2,y2,z2);
       putVertex(x3,y3,z3);
       int nVertices = 3;
       copyBuffer(gl, nVertices);
       gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nVertices);
    }


    //  --------  OpenGL Event-Methoden fuer Ueberschreibung in Erweiterungsklassen   ----------------

    public void init(GLAutoDrawable drawable)             //  Initialisierung
    {
       GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(GL.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(GL2GL3.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       programId = GLSetup.setupProgram(gl, getClass(), vShader, fShader);      // OpenGL-Initialisierung
       setupVertexBuffer(programId, gl, maxVerts);
       gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
       gl.glEnable(GL.GL_DEPTH_TEST);
   }


    public void display(GLAutoDrawable drawable)                       // Bildausgabe
    {
      GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
      setColor(1.0f, 0.0f, 0.0f);
      float a = 0.5f;
      zeichneDreieck(gl,-a,-a,0, a,-a,0, 0,a,0);
    }


    public void reshape(GLAutoDrawable drawable, int x, int y,         // Window resized
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
    }

    public void dispose(GLAutoDrawable drawable)  { }                  // not needed


    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e)
    {   System.out.println("closing window");
    System.exit(0);
    }
    public void windowActivated(WindowEvent e) {  }
    public void windowClosed(WindowEvent e) {  }
    public void windowDeactivated(WindowEvent e) {  }
    public void windowDeiconified(WindowEvent e) {  }
    public void windowIconified(WindowEvent e) {  }
    public void windowOpened(WindowEvent e) {  }


    //  -----------  main-Methode  ---------------------------

    public static void run(String[] args)
    { new GLMinimal();
    }

}