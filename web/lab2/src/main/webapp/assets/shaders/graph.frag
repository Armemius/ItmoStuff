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
    return distance(coords.x, point.x) <= 3.0
            && distance(coords.y, point.y) <= 3.0;
}

bool checkCircle(vec2 point) {
    return distance(vec2(0.0), point.xy) <= INTERVAL / 2.0
    && point.x <= 0.0
    && point.y <= 0.0;
}

bool checkTriangle(vec2 point) {
    return point.x >= 0.0
        && point.y >= 0.0
        && point.x / 2.0 + point.y - INTERVAL / 2.0 <= 0.0;
}

bool checkRectangle(vec2 point) {
    return point.x <= 0.0
        && point.x >= -INTERVAL / 2.0
        && point.y >= 0.0
        && point.y <= INTERVAL;
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

void main() {
    vec2 uv = (gl_FragCoord.xy - resolution / 2.0);
    // Render hit location
    if (checkAim(uv)) {
        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
        return;
    }
    // Render lines
    if (abs(uv.x) < LINE_WIDTH / 2.0 || abs(uv.y) < LINE_WIDTH / 2.0) {
        gl_FragColor = vec4(vec3(1.0), 1.0);
        return;
    }
    if (abs(mod(uv.x, INTERVAL / 2.0)) < LINE_WIDTH / 2.0
        || abs(mod(uv.y, INTERVAL / 2.0)) < LINE_WIDTH / 2.0) {
        gl_FragColor = vec4(vec3(1.0), 1.0);
        return;
    }
    // Render figures
    if (checkCircle(uv) || checkTriangle(uv) || checkRectangle(uv)) {
        if (state == 1.0) {
            gl_FragColor = vec4(0.0, 0.8, 0.0, 1.0);
        } else if (state == 2.0) {
            if (mod(uv.x + uv.y, 3.0) < 0.05) {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            } else {
                gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
            }
        } else {
            gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
        }
    }
}