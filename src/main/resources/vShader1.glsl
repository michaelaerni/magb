//  ------  Vertex-Shader mit Transformations-Matrizen  ----------
#version 150                                         // Shader Language Version
 
//  -------- Input/Output Variabeln  ----------- 

uniform mat4 modelViewMatrix, projMatrix;            // Transformations-Matrizen
uniform vec4 lightPosition;                          // Position Lichtquelle (im Kamerasystem)
uniform int ShadingLevel;                            // Beleuchtungsstufe, 0=none, 1=diffuse
in vec4 vertexPosition, vertexColor, vertexNormal;   // Vertex-Attributes
out vec4 Color;                                      // Vertex-Farbe fuer Fragment-Shader

// Reflection parameters
float ambient = 0.2;
float diffuse = 0.8;

void main()
{  vec4 vertex = modelViewMatrix * vertexPosition;    // ModelView=Transformation  
   gl_Position = projMatrix * vertex;                 // Projection
   
   Color = vertexColor;
   
   if(ShadingLevel == 1)
   {
      vec3 normal = normalize((modelViewMatrix * vertexNormal).xyz); // n
      vec3 toLight = normalize(lightPosition.xyz - vertex.xyz); // l
      float id = ambient * dot(normal, toLight); // intensity diffuse
      id = max(id, 0.0); // 0 if id < 0 (normal is in the other direction)
      
      // id = clamp(id, 0.0, 1.0 - ambient); // TODO: Try this, avoid later checks
      
      vec3 reflectedLight = (ambient + id) * Color.rgb; // Modify the color
      
      Color.rgb = min(reflectedLight, vec3(1, 1, 1));
   }
}
