#version 330
#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:chunksection.glsl>
#moj_import <minecraft:projection.glsl>
in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;
uniform sampler2D Sampler2;
out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
vec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {
    return texture(lightMap, clamp((uv / 256.0) + 0.5 / 16.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));
}
void main() {
    vec3 pos = Position + (ChunkPosition - CameraBlockPos) + CameraOffset;
    // The mesh builder marks upper vertices of selected grass with a tiny Y offset.
    float marker = fract(Position.y * 16.0);
    if (marker > 0.0030 && marker < 0.0065) {
        vec3 worldPos = Position + ChunkPosition;
        float time = GameTime * 24000.0;
        float wind = sin(time*0.11 + worldPos.x*0.18 + worldPos.z*0.14)*0.065;
        wind += sin(time*0.045 + worldPos.x*0.08 - worldPos.z*0.11)*0.035;
        pos.x += wind;
        pos.z += wind*0.45;
    }
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    sphericalVertexDistance = fog_spherical_distance(pos);
    cylindricalVertexDistance = fog_cylindrical_distance(pos);
    vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
}
