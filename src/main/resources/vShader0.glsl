//  ------  Vertex-Shader   ----------
#version 150                                         // Shader Language Version
 
//  -------- Input/Output Variabeln  ----------- 

in vec4 vertexPosition, vertexColor;                 // Vertex-Attributes
out vec4 Color;                                      // Vertex-Farbe fuer Fragment-Shader
    
void main()
{  
   gl_Position = vertexPosition;                            // pass through
   Color = vertexColor;
}
