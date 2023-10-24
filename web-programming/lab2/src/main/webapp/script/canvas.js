const canvas = document.getElementById("viewport")
const gl = canvas.getContext("webgl")

let state = 0
let lastTimeAttempt = performance.now()

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
        fetchShaderSources("assets/shaders/graph.vert"),
        fetchShaderSources("assets/shaders/graph.frag")
    ])

    const vertexShader = createShader(gl, gl.VERTEX_SHADER, vertexShaderSource)
    const fragmentShader = createShader(gl, gl.FRAGMENT_SHADER, fragmentShaderSource)

    const program = createProgram(gl, vertexShader, fragmentShader)
    gl.useProgram(program)
    return program
}

const processHit = ev => {
    ev.preventDefault();

    sendData(ev).then(result => {
        const node = document.createElement("div")
        node.classList.add("hit-result-decoration")
        node.innerHTML = `
            <div class="hit-result-decoration-square"></div>
            <div class="hit-result-decoration-diagonal-line"></div>
            <div class="hit-result-decoration-horizontal-line"></div>
            <div class="hit-result-decoration-container">
                <div>Выстрел #${hitHistory.length}</div>
                <div>Результат: ${result ? "Попадание" : "Промах"}</div>
            </div>
        `
        //
        const data = getData()
        //
        node.style.left = `${data.x * 200 / data.r + 250 - 4}px`
        node.style.top = `${-data.y * 200 / data.r + 250 - 70 + 4}px`
        //
        viewport.appendChild(node)
        // setTimeout(() => {
        //     node.remove()
        // }, 2000)

        if (result) {
            state = 1
        } else {
            state = 2
        }
    }).catch(() => {
        state = 2
    }).finally(() => {
        lastTimeAttempt = performance.now()
        setTimeout(() => {
            state = 0
        }, 1000)
    })
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
        if (isValid(data)) {
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

    render()
})