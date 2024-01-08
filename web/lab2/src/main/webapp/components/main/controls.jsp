<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="controls">
    <div class="section-header">
        <h2>Управление</h2>
        <span id="form-reset-button">[ Сброс ]</span>
    </div>
    <div class="controls-input">
        <form id="form" method="post" autocomplete="off">
            <section class="selection x-selection">
                <label for="x-form-input">Значение X:</label>
                <input name="x" id="x-form-input" value="undefined" type="text" hidden>
                <input id="x-form-input-button-0" type="button" value="-5.0">
                <input id="x-form-input-button-1" type="button" value="-4.0">
                <input id="x-form-input-button-2" type="button" value="-3.0">
                <input id="x-form-input-button-3" type="button" value="-2.0">
                <input id="x-form-input-button-4" type="button" value="-1.0">
                <input id="x-form-input-button-5" type="button" value="0.0">
                <input id="x-form-input-button-6" type="button" value="1.0">
                <input id="x-form-input-button-7" type="button" value="2.0">
                <input id="x-form-input-button-8" type="button" value="3.0">
            </section>
            <section class="selection y-selection">
                <label for="y-form-input">Значение Y:</label>
                <input name="y" id="y-form-input" type="text" placeholder="-3 . . 3">
            </section>
            <section class="selection r-selection">
                <label for="r-form-input">Значение R:</label>
                <input name="r" id="r-form-input" type="text" placeholder="1 . . 4">
            </section>
            <input id="form-submit-button" type="submit" value="Проверка" disabled>
        </form>
    </div>
    <div class="controls-indicator-container">
        <div id="ready-indicator" class="controls-indicator ready-indicator">Готов</div>
        <div id="work-indicator" class="controls-indicator work-indicator">Работа</div>
        <div id="error-indicator" class="controls-indicator error-indicator">Ошибка</div>
    </div>
</section>