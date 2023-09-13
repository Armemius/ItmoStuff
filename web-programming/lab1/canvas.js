const canvas = document.getElementById("viewport")
const gl = canvas.getContext("webgl")

let state = 0

const isPowerOf2 = value => {
    return (value & (value - 1)) === 0;
}

const createShader = (gl, type, source) => {
    const shader = gl.createShader(type)
    gl.shaderSource(shader, source)
    gl.compileShader(shader)
    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
        console.error('Error compiling shader:', gl.getShaderInfoLog(shader));
        gl.deleteShader(shader);
        return null;
    }
    return shader;
}

const createProgram = (gl, vertexShader, fragmentShader) => {
    const program = gl.createProgram()
    gl.attachShader(program, vertexShader)
    gl.attachShader(program, fragmentShader)
    gl.linkProgram(program)
    if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
        console.error('Error linking program:', gl.getProgramInfoLog(program))
        gl.deleteProgram(program)
        return null
    }
    return program
}

const vertexShaderSource = `
    attribute vec2 position;
    void main() {
        gl_Position = vec4(position, 0.0, 1.0);
    }
`

const fragmentShaderSource = `
    precision mediump float;
    
    uniform mediump vec2 resolution;
    uniform float time;
    uniform vec2 aim;
    uniform float radius;
    uniform float state;
    
    const float LINE_WIDTH = 2.0;
    const float INTERVAL = 200.0;
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
    
    vec4 prepareColor(vec3 color) {
        return vec4(color / 255.0, 1.0);
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
            if (state == 0.0) {
                gl_FragColor = prepareColor(vec3(52, 64, 235));
            } else if (state == 1.0) {
                gl_FragColor = prepareColor(vec3(82, 235, 59));
            } else if (state == 2.0) {
                gl_FragColor = prepareColor(vec3(237, 45, 71));
            }
        }
    }
`

const vertexShader = createShader(gl, gl.VERTEX_SHADER, vertexShaderSource)
const fragmentShader = createShader(gl, gl.FRAGMENT_SHADER, fragmentShaderSource)

const program = createProgram(gl, vertexShader, fragmentShader)
gl.useProgram(program)

const positionLocation = gl.getAttribLocation(program, 'position');
const positionBuffer = gl.createBuffer();
gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer);
const positions = [
    -1, -1,
    -1, 1,
    1, 1,
    1, -1,
];
gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(positions), gl.STATIC_DRAW);
gl.enableVertexAttribArray(positionLocation);
gl.vertexAttribPointer(positionLocation, 2, gl.FLOAT, false, 0, 0);

const resolutionLocation = gl.getUniformLocation(program, 'resolution')
const aimLocation = gl.getUniformLocation(program, 'aim')
const stateLocation = gl.getUniformLocation(program, 'state')
const radiusLocation = gl.getUniformLocation(program, 'radius')


const render = () => {
    saveForm()
    const timeLocation = gl.getUniformLocation(program, 'time')
    const data = getData()
    updateValues()
    if (validateForm(data)) {
        gl.uniform2f(aimLocation, data.x, data.y)
        gl.uniform1f(radiusLocation, data.r)
    } else {
        gl.uniform2f(aimLocation, 0x7ffffff, 0x7ffffff)
        gl.uniform1f(radiusLocation, 0x7fffff)
    }
    gl.uniform2f(resolutionLocation, canvas.width, canvas.height)
    gl.uniform1f(stateLocation, state)

    gl.clearColor(0, 0, 0, 1)
    gl.clear(gl.COLOR_BUFFER_BIT)
    gl.drawArrays(gl.TRIANGLE_FAN, 0, 4)
}

function distance(x1, y1, x2, y2) {
    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}

function checkCircle(x, y, r) {
    return distance(0.0, 0.0, x, y) <= r
        && x >= 0.0
        && y >= 0.0;
}

function checkTriangle(x, y, r) {
    return x <= 0.0
        && y <= 0.0
        && x + y + r / 2.0 >= 0.0;
}

function checkRectangle(x, y, r) {
    return x >= 0.0
        && x <= r
        && y <= 0.0
        && y >= -r / 2.0;
}

function checkHit({x, y, r}) {
    return checkCircle(x, y, r)
        || checkRectangle(x, y, r)
        || checkTriangle(x, y, r);
}

const processHit = ev => {
    ev.preventDefault();
    const data = getData()

    sendData(ev).then(result => {
        if (result) {
            state = 1
        } else {
            state = 2
        }
        requestAnimationFrame(render)
        setTimeout(() => {
            state = 0
            requestAnimationFrame(render)
        }, 1000)
    }).catch(() => {
        state = 2
        requestAnimationFrame(render)
        setTimeout(() => {
            state = 0
            requestAnimationFrame(render)
        }, 1000)
    })
}

render()

form.onclick = () => requestAnimationFrame(render)
xForm.onchange = () => requestAnimationFrame(render)
yForm.onchange = () => requestAnimationFrame(render)
rForm.onchange = () => requestAnimationFrame(render)
submitButton.onclick = processHit
