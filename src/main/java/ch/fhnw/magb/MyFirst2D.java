package ch.fhnw.magb;
//  -------------   JOGL 2D-Programm  -------------------
import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;
public class MyFirst2D extends GLBase1
{

    //  ---------  globale Daten  ---------------------------

    //  -------------- Konstruktor-Parameter ----------------------------------
    static String windowTitle = "JOGL-Application";
    static int windowWidth = 1024;
    static int windowHeight = 800;
    static String vShader = "vShader1.glsl";               // Filename Vertex-Shader
    static String fShader = "fShader1.glsl";               // Filename Fragment-Shader
    static int maxVerts = 2048;                            // max. Anzahl Vertices im Vertex-Array


    //  -------- Viewing-Volume  ---------------
    float left=-5f, right=5f;
    float bottom, top;
    float near=-10, far=1000;


    //  ---------  Methoden  ----------------------------------

    public MyFirst2D()
    {  super(windowTitle,windowWidth,windowHeight,vShader,fShader,maxVerts);
    }


    public void zeichneViereck(GL3 gl,
                        float w, float h)
    {  w *= 0.5f;
       h *= 0.5f;
       rewindBuffer(gl);
       putVertex(-w,-h,0);           // Eckpunkte in VertexArray speichern
       putVertex(w,-h,0);
       putVertex(w,h,0);
       putVertex(-w,h,0);
       int nVertices = 4;
       copyBuffer(gl, nVertices);
       gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, nVertices);
    }


    public void zeichnePfeil(GL3 gl, float a, float b,          // Pfeildreieck
                            float w, float h)                   // Stamm
    {  pushMatrix(gl);
       zeichneDreieck(gl,-a,0,0, a,0,0, 0,b,0);
       multMatrix(gl,Mat4.translate(0,-0.5f*h,0));
       zeichneViereck(gl,w,h);
       popMatrix(gl);
    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)
    {  super.init(drawable);
       GL3 gl = drawable.getGL().getGL3();
       gl.glClearColor(0,0,1,1);                         // Hintergrundfarbe (RGBA)
       gl.glDisable(GL3.GL_DEPTH_TEST);                  // ohne Sichtbarkeits-Test
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
      setModelViewMatrix(gl,Mat4.ID);
      setColor(0,1,1);
      float a = 0.45f, b=1.5f;
      float w = 0.21f, h=2.4f;
      zeichnePfeil(gl,a,b,w,h);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = (float)height / width;
       bottom = aspect * left;
       top = aspect * right;
       setOrthogonalProjection(gl,left,right,bottom,top,near,far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void run(String[] args)
    { new MyFirst2D();
    }

}