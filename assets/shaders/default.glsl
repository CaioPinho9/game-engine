#define vertex
#version 330 core
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTextureCoordinates;
layout (location = 3) in float aTextureId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoordinates;
out float fTextureId;

void main()
{
    fColor = aColor;
    fTextureCoordinates = aTextureCoordinates;
    fTextureId = aTextureId;
    gl_Position = uProjection * uView * vec4(aPosition, 1.0);
}

#define fragment
#version 330 core

in vec4 fColor;
in vec2 fTextureCoordinates;
in float fTextureId;

uniform sampler2D uTextures[8];

out vec4 color;

void main()
{
    color = fColor;
    if (fTextureId > 0) {
        int textureId = int(fTextureId);
        if (textureId == 0) {
            color *= texture(uTextures[0], fTextureCoordinates);
        } else if (textureId == 1) {
            color *= texture(uTextures[1], fTextureCoordinates);
        } else if (textureId == 2) {
            color *= texture(uTextures[2], fTextureCoordinates);
        } else if (textureId == 3) {
            color *= texture(uTextures[3], fTextureCoordinates);
        } else if (textureId == 4) {
            color *= texture(uTextures[4], fTextureCoordinates);
        } else if (textureId == 5) {
            color *= texture(uTextures[5], fTextureCoordinates);
        } else if (textureId == 6) {
            color *= texture(uTextures[6], fTextureCoordinates);
        } else if (textureId == 7) {
            color *= texture(uTextures[7], fTextureCoordinates);
        }
    }
}
