const form = document.getElementById("form")
const submitButton = document.getElementById("form-submit-button")
const table = document.getElementById("table")
const messageProvider = document.getElementById("error-message")

const xForm = document.getElementById("x-form-input")
const yForm = document.getElementById("y-form-input")
const rForm = document.getElementById("r-form-input")

const viewportMeasureTop = document.getElementById("viewport-measure-top")
const viewportMeasureHalfTop = document.getElementById("viewport-measure-half-top")
const viewportMeasureHalfBottom = document.getElementById("viewport-measure-half-bottom")
const viewportMeasureBottom = document.getElementById("viewport-measure-bottom")
const viewportMeasureRight = document.getElementById("viewport-measure-right")
const viewportMeasureHalfRight = document.getElementById("viewport-measure-half-right")
const viewportMeasureHalfLeft = document.getElementById("viewport-measure-half-left")
const viewportMeasureLeft = document.getElementById("viewport-measure-left")

const isNumber = value => /[+-]?([0-9]*[.])?[0-9]+/.test(value)

let hitHistory = []

const setCookie = (name, value, days) => {
    let expires = ""
    if (days) {
        const date = new Date()
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000))
        expires = "; expires=" + date.toUTCString()
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/"
}
const getCookie = name => {
    const nameEQ = name + "="
    const ca = document.cookie.split(';')
    for(let it= 0;it < ca.length; it++) {
        let c = ca[it]
        while (c.charAt(0) === ' ') {
            c = c.substring(1, c.length)
        }
        if (c.indexOf(nameEQ) === 0) {
            return c.substring(nameEQ.length,c.length)
        }
    }
    return null
}
const eraseCookie = name => {
    document.cookie = name +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

const getData = () => {
    return {
        x: xForm.value,
        y: yForm.value,
        r: rForm.value
    }
}

const validateForm = ({x, y, r}) => {
    if (!isNumber(x)) {
        messageProvider.innerHTML = "Значение x не является числом"
        return false
    }
    if (!isNumber(y)) {
        messageProvider.innerHTML = "Значение y не является числом"
        return false
    }
    if (!isNumber(r)) {
        messageProvider.innerHTML = "Значение r не является числом"
        return false
    }
    if (y < -3 || 5 < y) {
        messageProvider.innerHTML = "Значение y вне допустимых занчений [-3, 5]"
        return false
    }
    if (r <= 0) {
        messageProvider.innerHTML = "Значение r не положительно"
        return false
    }
    messageProvider.innerHTML = ""
    return true
}

const setX = value => {
    return () => {
        const input = document.getElementById("x-form-input")
        input.value = value
    }
}

const convertExecutionTime = float => {
    let power = 0;
    while (float < 1) {
        float *= 10
        power--
    }
    const node = document.createElement("span")
    node.innerHTML = `${float.toFixed(2)} · 10<sup>${power}</sup> μs`
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
        data.result,
        data.timestamp
    ]

    children = children.map(it => createTableItem(it))
    children = [...children, convertExecutionTime(data.execution)]

    children.forEach(it => {
        node.appendChild(it)
    })

    table.appendChild(node)
}

const updateValues = () => {
    const data = getData()
    const r = data.r
    if (validateForm(data)) {
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

[-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2].forEach((value, it) => {
    const button = document.getElementById(`x-form-input-button-${it}`)
    button.onclick = setX(value)
})

const HITS_SAVE_LOCATION = "saved_hits"
const FORM_SAVE_LOCATION = "saved_form"

const saveData = () => {
    setCookie(HITS_SAVE_LOCATION, btoa(encodeURI(JSON.stringify(hitHistory))), 7)
}

const restoreForm = () => {
    const data = getCookie(FORM_SAVE_LOCATION)
    if (!data) {
        return
    }
    const {x, y, r} = JSON.parse(decodeURI(atob(data)))
    xForm.value = x
    yForm.value = y
    rForm.value = r
}

const saveForm = () => {
    const data = getData()
    setCookie(FORM_SAVE_LOCATION, btoa(encodeURI(JSON.stringify(data))), 7)
}

const addDataToStorage = data => {
    hitHistory = [...hitHistory, data]
    saveData()
}

const clearStorage = () => {
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

const X_DEFAULT = 0
const Y_DEFAULT = ""
const R_DEFAULT = 1

const clearForm = () => {
    xForm.value = X_DEFAULT
    yForm.value = Y_DEFAULT
    rForm.value = R_DEFAULT
    render()
}

const restoreStorage = () => {
    const data = getCookie(HITS_SAVE_LOCATION)
    if (!data) {
        return
    }
    hitHistory = JSON.parse(decodeURI(atob(data)))
    hitHistory.forEach(rowData => {
        appendRow(rowData)
    })
}

const sendData = async ev => {
    ev.preventDefault()
    const requestData = getData()
    if (!validateForm(requestData)) {
        return
    }

    const response = await fetch("./validator.php", {
        method: "post",
        body: new FormData(form)
    })

    const responseData = await response.json()

    appendRow(responseData)
    addDataToStorage(responseData)
    return responseData.result === "Попадание"
}

restoreStorage()
restoreForm()
