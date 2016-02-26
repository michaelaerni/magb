package ch.fhnw.magb;
/**
* This is a port of some sample code from:
* http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
*/
import java.io.*;
import com.jogamp.opengl.*;
public class GLSetup
{


    public static int setupProgram(GL3 gl, Class cls,
                                  String vShaderName, String fShaderName)
    {  int vShaderId = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
       int fShaderId = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
       loadShader(gl,cls,vShaderId, vShaderName);
       loadShader(gl, cls,fShaderId, fShaderName);
       int programId = gl.glCreateProgram();
       gl.glAttachShader(programId, vShaderId);
       gl.glAttachShader(programId, fShaderId);
       gl.glLinkProgram(programId);
       gl.glUseProgram(programId);
       System.out.println("ProgramInfoLog:");
       System.out.println(getProgramInfoLog(gl, programId));
       System.out.println();
       return programId;
    }


    public static String getProgramInfoLog(GL3 gl, int obj)
    {
       int params[] = new int[1];
       gl.glGetProgramiv(obj, GL3.GL_INFO_LOG_LENGTH, params, 0);       // get log-length
       int logLen = params[0];
       if (logLen <= 0)
         return "";
       byte[] bytes = new byte[logLen + 1];
       int[] retLength = new int[1];
       gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);     // get log-data
       String logMessage = new String(bytes);
       int iend = logMessage.indexOf(0);
       if (iend < 0 ) iend = 0;
       return logMessage.substring(0,iend);
    }



    static void loadShader(GL3 gl, Class cls,                          // load and compile shader
                           int shaderId,
                           String ShaderFileName)
    {  String s = textFileRead(ShaderFileName,cls);
       gl.glShaderSource(shaderId, 1, new String[] { s }, null);
       gl.glCompileShader(shaderId);
       System.out.println("ShaderLog:");
       System.out.println(getShaderInfoLog(gl, shaderId));
       System.out.println();
    }


    static public String getShaderInfoLog(GL3 gl, int obj)
    {  int params[] = new int[1];
       gl.glGetShaderiv(obj, GL3.GL_INFO_LOG_LENGTH, params, 0);         // get log-length
       int logLen = params[0];
       if (logLen <= 0)
         return "";
       // Get the log
       byte[] bytes = new byte[logLen + 1];
       int[] retLength = new int[1];
       gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);       // get log-data
       String logMessage = new String(bytes);
       int iend = logMessage.indexOf(0);
       if (iend < 0 ) iend = 0;
       return logMessage.substring(0,iend);
    }


    static public String textFileRead(String filePath, Class cls)
    {  // Read the data in
       BufferedReader reader = null;
       try
       {  // Read in the source
//          reader = new BufferedReader(new FileReader(filePath));

          InputStream is = GLSetup.class.getClassLoader().getResourceAsStream(filePath);
          reader = new BufferedReader(new InputStreamReader(is));
          StringBuilder sb = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null)
            sb.append(line).append("\n");
          return sb.toString();
       }
       catch (final Exception ex)
       {  ex.printStackTrace();
       }
      return "";
    }


}