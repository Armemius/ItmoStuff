precision mediump float;

uniform mediump vec2 resolution;
uniform float time;
uniform float last_attempt_time;
uniform vec2 aim;
uniform float radius;
uniform float state;

float TIME_DELTA = time - last_attempt_time;
float LINE_WIDTH = 2.0;
float INTERVAL = 200.0;
float RATIO = INTERVAL / radius;

bool checkAim(vec2 point) {
    vec2 coords = aim * RATIO;
    return distance(coords.xy, point.xy) <= 3.0;
}

bool checkCircle(vec2 point) {
    return distance(vec2(0.0), point.xy) <= INTERVAL
    && point.x >= 0.0
    && point.y >= 0.0;
}

bool checkTriangle(vec2 point) {
    return point.x <= 0.0
    && point.y <= 0.0
    && point.x + point.y + INTERVAL / 2.0 >= 0.0;
}

bool checkRectangle(vec2 point) {
    return point.x >= 0.0
    && point.x <= INTERVAL
    && point.y <= 0.0
    && point.y >= -INTERVAL / 2.0;
}

vec3 palette(float t) {
    vec3 a = vec3(0.5, 0.5, 0.5);
    vec3 b = vec3(0.5, 0.5, 2);
    vec3 c = vec3(1.0, 1.0, 1.0);
    vec3 d = vec3(0.263, 0.416, 0.557);

    return a + b * cos(6.28318 * (c * t + d));
}

vec4 render(float animation_time) {
    vec2 p = ( gl_FragCoord.xy * 2.0 - resolution.xy ) / resolution.y;
    mat2 transform_matrix = mat2(
    cos(animation_time),  sin(time),
    -sin(time), cos(animation_time)
    );
    p  = transform_matrix * p;
    vec2 p2 = p * 0.5;
    vec3 cc = vec3(0.0);
    for (float i = 0.0; i < 4.0; ++i) {
        p *= 1.0;
        p = fract(p * 1.5);
        p -= 0.5;
        float d = length(p) * tan(-length(p2));
        vec3 c = palette(length(p2) + i * 0.4 + animation_time * 0.4);
        d = sin(d * 8.0 + animation_time) / 8.0;
        d = abs(d);
        d = pow(0.01 / d, 2.0);
        c *= d;
        cc += c;
    }

    return vec4(cc, 1.0);
}

float minmax(float min, float value, float max) {
    if (value < min) {
        return min;
    }
    if (value > max) {
        return max;
    }
    return value;
}

vec4 applyFilter(vec2 coords) {

    vec2 clamped_aim = vec2(
        minmax(-resolution.x / 2.0, aim.x * RATIO, resolution.x / 2.0),
        minmax(-resolution.x / 2.0, aim.y * RATIO, resolution.x / 2.0)
    );
    vec4 filterValue;
    if (state == 0.0
    || TIME_DELTA > 4.0
    || distance(clamped_aim, coords) > resolution.x * TIME_DELTA * TIME_DELTA / 4.0) {
        return vec4(1.0);
    }
    if (state == 1.0) {
        filterValue = vec4(0.05, 2.0, 0.05, 1.0);
    } else {
        filterValue = vec4(2.0, 0.05, 0.05, 1.0);
    }
    if (TIME_DELTA < 2.0) {
        return filterValue;
    }
    float FADE_DELTA = (TIME_DELTA - 2.0) / 2.0;
    return vec4(1.0) * FADE_DELTA + filterValue * (1.0 - FADE_DELTA);
}

void main() {
    vec2 uv = (gl_FragCoord.xy - resolution / 2.0);
    // Render hit location
    if (checkAim(uv)) {
        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
        return;
    }
    // Render lines
    if (abs(uv.x) < LINE_WIDTH / 2.0 || abs(uv.y) < LINE_WIDTH / 2.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
    if (abs(mod(uv.x, INTERVAL / 2.0)) < LINE_WIDTH / 2.0
    || abs(mod(uv.y, INTERVAL / 2.0)) < LINE_WIDTH / 2.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
    // Render figures
    if (checkCircle(uv) || checkTriangle(uv) || checkRectangle(uv)) {
        vec4 raw_color = render(time) + render(time - 3.14 / 2.0);
        gl_FragColor = raw_color * applyFilter(uv);
    }
}
