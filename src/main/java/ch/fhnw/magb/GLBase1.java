package ch.fhnw.magb;
//  -------------   JOGL Basis-Programm (fuer Erweiterungen mittels 'extends')  -------------------
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
import ch.fhnw.util.math.*;                        // Vektor- und Matrix-Algebra

public class GLBase1
       implements WindowListener, GLEventListener, KeyListener, MouseListener, MyRenderer1
{

    //  --------------  Globale Daten  -------------------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = "vShader1.glsl";               // Filename Vertex-Shader
    String fShader = "fShader1.glsl";               // Filename Fragment-Shader
    int maxVerts = 2048;                            // max. Anzahl Vertices im Vertex-Array

    float[] clearColor = {0,0,1,1};                 // Fensterhintergrund (Blau)
    GLCanvas canvas;                                // OpenGL Window

    float left = -1.0f, right = 1.0f;               // Viewing-Volume
    float bottom, top;
    float near = -10, far = 1000;

    //  --------------  Transformations-Matrizen  ----------------------
    Mat4 M = Mat4.ID;                                     // ModelView Matrix
    Mat4 P = Mat4.ID;                                     // Projection Matrix
    Stack<Mat4> matrixStack = new Stack<Mat4>();


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
    int modelViewMatrixLoc, projMatrixLoc;                     // Uniform Shader Variables
    int vPositionLocation, vColorLocation;                     // Vertex Attribute Shader Variables


    //  ------------- Konstruktoren  ---------------------------

    public GLBase1()                                           // Konstruktor ohne Parameter
    {   createFrame();
    }

    public GLBase1(String windowTitle,                        // Konstruktor
                   int windowWidth, int windowHeight,
                   String vShader, String fShader,            // Filenamen Vertex-/Fragment-Shader
                   int maxVerts)                              // max. Anzahl Vertices im Vertex-Array
    {  this.windowTitle = windowTitle;
       this.windowWidth = windowWidth;
       this.windowHeight = windowHeight;
       this.vShader = vShader;
       this.fShader = fShader;
       this.maxVerts = maxVerts;
       createFrame();
    };


    //  -------------  Methoden  ---------------------------

    void createFrame()                                    // Fenster erzeugen
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
       canvas.addKeyListener(this);
       canvas.addMouseListener(this);
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


    void setupMatrices(int pgm, GL3 gl)
    {
       // ----- get shader variable identifiers  -------------
       modelViewMatrixLoc = gl.glGetUniformLocation(pgm, "modelViewMatrix");
       projMatrixLoc = gl.glGetUniformLocation(pgm, "projMatrix");

       // -----  set uniform variables  -----------------------
       gl.glUniformMatrix4fv(modelViewMatrixLoc, 1, false, M.toArray(), 0);
       gl.glUniformMatrix4fv(projMatrixLoc, 1, false, P.toArray(), 0);
    };


    //  ----------  oeffentliche Methoden (fuer Verwendung in Erweiterungsklassen)  -------------

     public GLCanvas getGLCanvas()                              // get OpenGL-WindowHandle
     { return canvas;
     }

     public int getProgramId()                                  // get OpenGL-ProgramIdentification
     { return programId;
     }


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


   //  --------------  ModelView-Matrix  ------------------

    public void setModelViewMatrix(GL3 gl, Mat4 M)
    {  this.M = M;
       gl.glUniformMatrix4fv(modelViewMatrixLoc, 1, false, M.toArray(), 0);
    }

    public Mat4 getModelViewMatrix(GL3 gl)
    {  return M;
    }

    public void pushMatrix(GL3 gl)
    {  matrixStack.push(M);
       gl.glUniformMatrix4fv(modelViewMatrixLoc, 1, false, M.toArray(), 0);
    }

    public void popMatrix(GL3 gl)
    {  M = matrixStack.pop();
       gl.glUniformMatrix4fv(modelViewMatrixLoc, 1, false, M.toArray(), 0);
    }

    public void loadIdentity(GL3 gl)
    {  setModelViewMatrix(gl,Mat4.ID);
    }

    public void multMatrix(GL3 gl, Mat4 A)            // M = M * A
    {  M = M.postMultiply(A);
       gl.glUniformMatrix4fv(modelViewMatrixLoc, 1, false, M.toArray(), 0);
    }


    // ---------  Kamera-System festlegen  ----------

    public void setCameraSystem(GL3 gl, Vec3 A, Vec3 B, Vec3 up)       // LookAt-Psotionierung
    {  M = Mat4.lookAt(A,B,up);
       gl.glUniformMatrix4fv(modelViewMatrixLoc, 1, false, M.toArray(), 0);
    }

    //  ------------  Operationen Objekt-System  ------------

    public void rotate(GL3 gl, float phi,                                           // Objekt-System drehen, phi in Grad
                       float x, float y, float z)                                   // Drehachse
    {  multMatrix(gl,Mat4.rotate(phi,x,y,z));
    }

    public void translate(GL3 gl,                                                   // Objekt-System verschieben
                         float x, float y, float z)
    {  multMatrix(gl,Mat4.translate(x,y,z));
    }

    public void scale(GL3 gl, float scale)                                          // Skalierung Objekt-System
    //  nur ein xyz-Faktor wegen Normalentransformation
    {  multMatrix(gl,Mat4.scale(scale,scale,scale));
    }



    //  ---------  Projektions-Matrix -------------------

    public void setProjectionMatrix(GL3 gl, Mat4 P)
    {  this.P = P;
       gl.glUniformMatrix4fv(projMatrixLoc, 1, false, P.toArray(), 0);
    }

    public Mat4 getProjectionMatrix(GL3 gl)
    {  return P;
    }


    public void setOrthogonalProjection(GL3 gl, float left, float right,      // Orthogonal-Projektion auf Bildebene
                                      float bottom, float top,
                                      float near, float far)
    {   P = Mat4.ortho(left,right,bottom,top,near,far);
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, P.toArray(), 0);
    }


    public void setPerspectiveProjection(GL3 gl, float left, float right,     // Zentralprojektion auf Bildebene
                                      float bottom, float top,
                                      float near, float far)
    {   P = Mat4.perspective(left,right,bottom,top,near,far);
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, P.toArray(), 0);
    }



    //  ---------  Zeichenmethoden  ------------------------------

    public void drawAxis(GL3 gl, float a, float b, float c)                   // Koordinatenachsen zeichnen
    {  rewindBuffer(gl);
       putVertex(0,0,0);           // Eckpunkte in VertexArray speichern
       putVertex(a,0,0);
       putVertex(0,0,0);
       putVertex(0,b,0);
       putVertex(0,0,0);
       putVertex(0,0,c);
       int nVertices = 6;
       copyBuffer(gl, nVertices);
       gl.glDrawArrays(GL3.GL_LINES, 0, nVertices);
    }



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

    @Override
    public void init(GLAutoDrawable drawable)             //  Initialisierung
    {
       GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       programId = GLSetup.setupProgram(gl, getClass(), vShader, fShader);      // OpenGL-Initialisierung
       setupVertexBuffer(programId, gl, maxVerts);
       setupMatrices(programId, gl);
       gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
       gl.glEnable(GL3.GL_DEPTH_TEST);
   }


    @Override
    public void display(GLAutoDrawable drawable)                       // Bildausgabe
    {
      GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
      setColor(1.0f, 1.0f, 0.0f);
      float a = 1.0f;
      Vec3 A = new Vec3(1,0.5f,2);   // Kamera-Position
      Vec3 B = new Vec3(0,0,0);   // Ziel
      Vec3 up = new Vec3(0,1,0);
      setCameraSystem(gl,A,B,up);
      drawAxis(gl,a,a,a);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,         // Window resized
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = (float)height / width;
       float bottom = aspect * left;
       float top = aspect * right;
       setOrthogonalProjection(gl,left,right,bottom,top,near,far);
    }


    @Override
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


    //  ---------  Keuboard-Events ---------------------
    public void keyTyped(KeyEvent e) { }
    public void keyReleased(KeyEvent e) { }
    public void keyPressed(KeyEvent e) { }


    //  ---------  Mouse-Events ---------------------
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }


    //  -----------  main-Methode  ---------------------------

    public static void run(String[] args)
    { new GLBase1();
    }

}