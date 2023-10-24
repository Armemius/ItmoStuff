// Constants
const HITS_SAVE_LOCATION = "saved_hits"
const FORM_SAVE_LOCATION = "saved_form"

// Modal elements
const modal = document.getElementById("modal")
const modalResponse = document.getElementById("modal-response")
const modalResponseContainer = document.getElementById("modal-response-container")
const modalButtonYes = document.getElementById("modal-button-yes")
const modalButtonNo = document.getElementById("modal-button-no")

// Form inputs
const form = document.getElementById("form")
const xForm = document.getElementById("x-form-input")
const yForm = document.getElementById("y-form-input")
const rForm = document.getElementById("r-form-input")
const submitButton = document.getElementById("form-submit-button")
const resetButton = document.getElementById("form-reset-button")

// Control indicators
const warningSign = document.getElementById("viewport-warning-sign")
const readyIndicator = document.getElementById("ready-indicator")
const errorIndicator = document.getElementById("error-indicator")

// Form buttons
const buttonValues = [-5, -4, -3, -2, -1, 0, 1, 2, 3]
const buttons = buttonValues.map(
    (value, index) =>
        document.getElementById(`x-form-input-button-${index}`)
)

// Viewport
const viewportMeasureTop = document.getElementById("viewport-measure-top")
const viewportMeasureHalfTop = document.getElementById("viewport-measure-half-top")
const viewportMeasureHalfBottom = document.getElementById("viewport-measure-half-bottom")
const viewportMeasureBottom = document.getElementById("viewport-measure-bottom")
const viewportMeasureRight = document.getElementById("viewport-measure-right")
const viewportMeasureHalfRight = document.getElementById("viewport-measure-half-right")
const viewportMeasureHalfLeft = document.getElementById("viewport-measure-half-left")
const viewportMeasureLeft = document.getElementById("viewport-measure-left")
const viewport = document.getElementById("viewport-content")

// Table
const table = document.getElementById("table")
const resetTableButton = document.getElementById("table-reset-button")

// Message provider
const messageProvider = document.getElementById("message-provider")

// Functions and variables
let hitHistory = []

Array.prototype.getRandomValue = function () {
    return this[(this.length * Math.random()) | 0]
}

const displayMessage = message => {
    const node = document.createElement("div")
    node.innerText = message
    node.classList.add("static-fullscreen")
    node.classList.add("message-provider")
    document.body.appendChild(node)
    setTimeout(() => {
        node.remove()
    }, 2000)
}

const convertExecutionTime = nanos => {
    let result = nanos / 1e+9
    let power = 0;
    while (result < 1) {
        result *= 10
        power--
    }
    const node = document.createElement("span")
    node.innerHTML = `${result.toFixed(2)} · 10<sup>${power}</sup> μs`
    return node
}

const createTableItem = value => {
    const node = document.createElement("span")
    node.innerHTML = value
    return node
}

const appendRow = data => {
    const node = document.createElement("div")
    node.classList.add("row");

    let children = [Number(data.x).toFixed(2),
        Number(data.y).toFixed(2),
        Number(data.r).toFixed(2),
        data.result ? "Попадание" : "Промах",
        data.timestamp
    ]

    children = children.map(it => createTableItem(it))
    children = [...children, convertExecutionTime(data.execution)]

    children.forEach(it => {
        node.appendChild(it)
    })

    table.insertBefore(node, table.children[1])
}

const isNumber = value => /^[+-]?([0-9]*[.])?[0-9]+$/.test(value)

const save = (name, value) => {
    localStorage.setItem(name, value);
}

const restore = name => {
    return localStorage.getItem(name)
}

const restoreForm = () => {
    const data = restore(FORM_SAVE_LOCATION)
    if (!data) {
        return
    }
    const {x, y, r} = JSON.parse(decodeURI(atob(data)))
    xForm.value = x
    yForm.value = y
    rForm.value = r

    if (!isNumber(x)) {
        return
    }

    buttonValues.forEach((it, index) => {
        if (Number(x) === it) {
            buttons[index].classList.add("active")
        } else {
            buttons[index].classList.remove("active")
        }
    })
}

const saveForm = () => {
    const data = getData()
    save(FORM_SAVE_LOCATION, btoa(encodeURI(JSON.stringify(data))))
}

const getData = () => {
    return {
        x: xForm.value,
        y: yForm.value,
        r: rForm.value
    }
}

const resetValues = () => {
    xForm.value = ""
    yForm.value = ""
    rForm.value = ""
    buttons.forEach(button => {
        button.classList.remove("active")
    })
    validate()
}

const processValidationSuccess = () => {
    readyIndicator.classList.add("active")
    errorIndicator.classList.remove("active")
    warningSign.classList.remove("active")
    submitButton.disabled = false
}

const processValidationError = () => {
    errorIndicator.classList.add("active")
    warningSign.classList.add("active")
    readyIndicator.classList.remove("active")
    submitButton.disabled = true
}

const validate = () => {
    const values = getData()

    if (isValid(values)) {
        processValidationSuccess()
    } else {
        processValidationError()
    }
    updateGridValues()
}

const isValid = values => {
    return -5 <= values.x && values.x <= 3
        && -3 <= values.y && values.y <= 3
        && 1 <= values.r && values.r <= 4
        && isNumber(values.x)
        && isNumber(values.y)
        && isNumber(values.r)
}

const updateGridValues = () => {
    const data = getData()
    const r = data.r
    if (isNumber(r)) {
        viewportMeasureTop.innerHTML = `${r}`
        viewportMeasureHalfTop.innerHTML = `${r / 2}`
        viewportMeasureHalfBottom.innerHTML = `${-r / 2}`
        viewportMeasureBottom.innerHTML = `${-r}`
        viewportMeasureRight.innerHTML = `${r}`
        viewportMeasureHalfRight.innerHTML = `${r / 2}`
        viewportMeasureHalfLeft.innerHTML = `${-r / 2}`
        viewportMeasureLeft.innerHTML = `${-r}`
    } else {
        viewportMeasureTop.innerHTML = `R`
        viewportMeasureHalfTop.innerHTML = `R/2`
        viewportMeasureHalfBottom.innerHTML = `-R/2`
        viewportMeasureBottom.innerHTML = `-R`
        viewportMeasureRight.innerHTML = `R`
        viewportMeasureHalfRight.innerHTML = `R/2`
        viewportMeasureHalfLeft.innerHTML = `-R/2`
        viewportMeasureLeft.innerHTML = `-R`
    }
}

const processModalResponse = message => {
    return () => {
        modalResponseContainer.classList.remove("fading")
        modalResponse.innerText = message
        modal.remove()
        modalResponseContainer.classList.add("fading")
    }
}

const sendData = async ev => {
    ev.preventDefault()
    const requestData = getData()

    if (!isValid(requestData)) {
        return
    }

    const {x, y, r} = requestData

    const response = await fetch(`./validator?x=${x}&y=${y}&r=${r}`, {
        method: "get"
    })

    const responseData = await response.json()

    appendRow(responseData)
    addDataToStorage(responseData)
    return responseData.result
}

const restoreTable = () => {
    const data = restore(HITS_SAVE_LOCATION)
    if (!data) {
        return
    }
    hitHistory = JSON.parse(decodeURI(atob(data)))
    hitHistory.forEach(rowData => {
        appendRow(rowData)
    })
}

const saveData = () => {
    save(HITS_SAVE_LOCATION, btoa(encodeURI(JSON.stringify(hitHistory))))
}

const addDataToStorage = data => {
    hitHistory = [...hitHistory, data]
    saveData()
}

const clearTable = () => {
    hitHistory = []
    saveData()
    table.innerHTML = `
        <div class="row legend">
            <span>X</span>
            <span>Y</span>
            <span>R</span>
            <span>Результат</span>
            <span>Время начала</span>
            <span>Время вычисления</span>
        </div>
    `
}

const processViewportClick = ev => {
    const rect = ev.target.getBoundingClientRect()
    const r = rForm.value
    if (!isNumber(r) || r < 1 || 4 < r) {
        return
    }
    const x = Math.round((ev.clientX - rect.left - 250) / 200 * Number(r))
    const y = -(ev.clientY - rect.top - 250) / 200 * Number(r)
    if(!isValid({x: x, y: y, r: r})) {
        const messages = [
            "Это не сработает",
            "Бесполезно",
            "Хорошая попытка",
            "Не всем мечтам суждено сбыться",
            "Жаль, но нет",
            "Продолжай сколько хочешь",
            "Законы этого мира не позволяют это"
        ]
        displayMessage(messages.getRandomValue())
        return
    }
    buttonValues.forEach((it, index) => {
        if (x === it) {
            buttons[index].classList.add("active")
        } else {
            buttons[index].classList.remove("active")
        }
    })
    xForm.value = x
    yForm.value = y
    validate()
}