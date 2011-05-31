uniform sampler2D lineTex;
varying vec4 color;
void main() {
   gl_FragColor = texture2D(lineTex, gl_TexCoord[0].st) * color;
}
   

