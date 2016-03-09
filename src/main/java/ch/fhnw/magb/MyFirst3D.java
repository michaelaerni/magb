package ch.fhnw.magb;
//  -------------   JOGL 3D-Programm  -------------------
import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;

public class MyFirst3D extends GLBase1
{

    //  ---------  globale Daten  ---------------------------

    //  -------------- Konstruktor-Parameter ----------------------------------
    static String windowTitle = "JOGL-Application";
    static int windowWidth = 800;
    static int windowHeight = 600;
    static String vShader = "vShader1.glsl";               // Filename Vertex-Shader
    static String fShader = "fShader1.glsl";               // Filename Fragment-Shader
    static int maxVerts = 2048;                            // max. Anzahl Vertices im Vertex-Array


    //  ------- Kamera-System  ------------
    Vec3 A = new Vec3(2,0.3f,1);                // Kamera-Position
    Vec3 B = new Vec3(0,0,0);                   // Zielpunkt
    Vec3 up = new Vec3(0,1,0);                  // up-Vektor
    float left=-5, right=5;                     // Viewing-Volume
    float bottom, top;
    float near=-10, far=1000;


    //  ---------  Methoden  ----------------------------------

    public MyFirst3D()
    {  super(windowTitle,windowWidth,windowHeight,vShader,fShader,maxVerts);
    }

    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)
    {  super.init(drawable);
       GL3 gl = drawable.getGL().getGL3();
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

      setCameraSystem(gl,A,B,up);      // Kamera-System festlegen

      setColor(0.8f, 0.8f, 0.8f);
      drawAxis(gl, 8,8,8);             //  Koordinatenachsen
      setColor(1,0,0);
      zeichneDreieck(gl,3,2,4,5,1.8f,8,5,2,3);
      setColor(0.2f,0.2f,0.2f);
      rotate(gl, 60, 0, 1, 0);
      zeichneDreieck(gl,3,0,4,5,0,8,5,0,3);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);

       // -----  Projektionsmatrix festlegen  -----
       float aspect = (float)height / width;
       bottom = aspect * left;
       top = aspect * right;
       setOrthogonalProjection(gl,left,right,bottom,top,near,far);
    }


    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new MyFirst3D();
    }

}