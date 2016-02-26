//  ------  Vertex-Shader mit Transformations-Matrizen  ----------
#version 150                                         // Shader Language Version
 
//  -------- Input/Output Variabeln  ----------- 

uniform mat4 modelViewMatrix, projMatrix;            // Transformations-Matrizen
in vec4 vertexPosition, vertexColor;                 // Vertex-Attributes
out vec4 Color;                                      // Vertex-Farbe fuer Fragment-Shader
    
void main()
{  vec4 vertex = modelViewMatrix * vertexPosition;    // ModelView=Transformation  
   gl_Position = projMatrix * vertex;                 // Projection
   Color = vertexColor;
}
