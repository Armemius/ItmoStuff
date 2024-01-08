const queryString = window.location.search
const urlParams = new URLSearchParams(queryString)

if (urlParams.get('modal') === 'false') {
    modalResponseContainer?.remove()
    modalResponse?.remove()
    modal?.remove()
} else {
    modalButtonYes.onclick = processModalResponse("Психопат")
    modalButtonNo.onclick = processModalResponse("Молодец")
}

buttons.forEach((it, index) => {
    it.onclick = () => {
        buttons.forEach(button => {
            button.classList.remove("active")
        })
        xForm.value = buttonValues[index]
        it.classList.add("active")
        validate()
        saveForm()
    }
})

yForm.oninput = () => {
    validate()
    saveForm()
}

rForm.oninput = () => {
    validate()
    updateGridValues()
    saveForm()
}

resetButton.onclick = () => {
    displayMessage("Данные формы удалены")
    resetValues()
    saveForm()
}

submitButton.onclick = ev => processHit(ev, null, true)

resetTableButton.onclick = () => {
    const messages = [
        "Всё обернётся прахом",
        "Они всё равно были не нужны",
        "Преданы забвению",
        "Им тут не место"
    ]
    displayMessage(messages.getRandomValue())
    clearTable()
}

viewport.onclick = processViewportClick

restoreForm()
restoreTable()
validate()
updateGridValues()
