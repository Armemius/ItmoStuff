restoreTable = () => {
    fetch("../table_data")
        .then(response => response.text())
        .then(data => {
            hitHistory = JSON.parse(decodeURI(atob(data)))
            hitHistory.forEach(rowData => {
                appendRow(rowData)
            })
            updateShotHitHistory()
        }).catch(() => {})
}

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

restoreTable()