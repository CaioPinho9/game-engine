#define vertex
#version 330 core
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec4 aColor;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;

void main()
{
    fColor = aColor;
    gl_Position = uProjection * uView * vec4(aPosition, 1.0);
}

#define fragment
#version 330 core

in vec4 fColor;

out vec4 color;

void main()
{
    color = fColor;
}
