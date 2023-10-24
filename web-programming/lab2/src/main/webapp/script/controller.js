modalButtonYes.onclick = processModalResponse("Психопат")
modalButtonNo.onclick = processModalResponse("Молодец")

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

submitButton.onclick = processHit

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