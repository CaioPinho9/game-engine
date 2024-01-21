#define vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoord;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;

void main()
{
    fColor = aColor;
    fTexCoords = aTexCoord;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#define fragment
#version 330 core

uniform float uTime;
uniform sampler2D uTexture;

in vec4 fColor;
in vec2 fTexCoords;

out vec4 color;

void main()
{
    color = texture(uTexture, fTexCoords);
}
