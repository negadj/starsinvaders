attribute vec4 other;
uniform float lineWidth;
varying vec4 color;
void main() {
    gl_TexCoord[0] = gl_MultiTexCoord0;
    color = gl_Color;
    vec4 vMVP = gl_ModelViewProjectionMatrix * gl_Vertex;
    vec4 otherMVP = gl_ModelViewProjectionMatrix * other;
    vec2 lineDirProj = lineWidth * normalize( (vMVP.xy/vMVP.w) - (otherMVP.xy/otherMVP.w) );
    if( sign(otherMVP.w) != sign(vMVP.w) ) {
        lineDirProj = -lineDirProj;
    }
    vMVP.x = vMVP.x + lineDirProj.x * gl_Normal.x;
    vMVP.y = vMVP.y + lineDirProj.y * gl_Normal.x;
    vMVP.x = vMVP.x + lineDirProj.y * gl_Normal.y;
    vMVP.y = vMVP.y - lineDirProj.x * gl_Normal.y;
    gl_Position = vMVP;
}

