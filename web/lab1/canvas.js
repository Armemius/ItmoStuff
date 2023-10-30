const canvas = document.getElementById("viewport")
const gl = canvas.getContext("webgl")

let state = 0

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

const fetchShaderSources = async (path) => {
    const rawData = await fetch(path)
    return rawData.text()
}

const instantiateProgram = async () => {
    const [vertexShaderSource, fragmentShaderSource] = await Promise.all([
        fetchShaderSources("graph.vert"),
        fetchShaderSources("graph.frag")
    ])

    const vertexShader = createShader(gl, gl.VERTEX_SHADER, vertexShaderSource)
    const fragmentShader = createShader(gl, gl.FRAGMENT_SHADER, fragmentShaderSource)

    const program = createProgram(gl, vertexShader, fragmentShader)
    gl.useProgram(program)
    return program
}

instantiateProgram().then(program => {
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

    let lastTimeAttempt = performance.now()

    const resolutionLocation = gl.getUniformLocation(program, 'resolution')
    const aimLocation = gl.getUniformLocation(program, 'aim')
    const stateLocation = gl.getUniformLocation(program, 'state')
    const radiusLocation = gl.getUniformLocation(program, 'radius')
    const timeLocation = gl.getUniformLocation(program, 'time')
    const lastTimeAttemptLocation = gl.getUniformLocation(program, "last_attempt_time")

    const render = () => {
        const data = getData()
        if (validateForm(data)) {
            gl.uniform2f(aimLocation, data.x, data.y)
            gl.uniform1f(radiusLocation, data.r)
        } else {
            gl.uniform2f(aimLocation, 0x7ffffff, 0x7ffffff)
            gl.uniform1f(radiusLocation, 0x7fffff)
        }
        gl.uniform2f(resolutionLocation, canvas.width, canvas.height)
        gl.uniform1f(stateLocation, state)
        gl.uniform1f(timeLocation, performance.now() / 1000)
        gl.uniform1f(lastTimeAttemptLocation, lastTimeAttempt / 1000)

        gl.clearColor(0, 0, 0, 1)
        gl.clear(gl.COLOR_BUFFER_BIT)
        gl.drawArrays(gl.TRIANGLE_FAN, 0, 4)
        requestAnimationFrame(render)
    }

    const processHit = ev => {
        ev.preventDefault();

        sendData(ev).then(result => {
            if (result) {
                state = 1
            } else {
                state = 2
            }
        }).catch(() => {
            state = 2
        }).finally(() => {
            lastTimeAttempt = performance.now()
        })
    }

    render()

    submitButton.onclick = processHit
})