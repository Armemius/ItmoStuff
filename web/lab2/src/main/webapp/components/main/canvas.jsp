<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<section class="viewport">
    <div class="section-header">
        <h2>Координатная плоскость</h2>
    </div>
    <div id="viewport-content" class="viewport-content">
        <canvas id="viewport"
                width="500"
                height="500"
        ></canvas>
        <span class="viewport-measure">0</span>
        <span id="viewport-measure-top" class="viewport-measure" top>R</span>
        <span id="viewport-measure-half-top" class="viewport-measure" half-top>R/2</span>
        <span id="viewport-measure-half-bottom" class="viewport-measure" half-bottom>-R/2</span>
        <span id="viewport-measure-bottom" class="viewport-measure" bottom>-R</span>
        <span id="viewport-measure-right" class="viewport-measure" right>R</span>
        <span id="viewport-measure-half-right" class="viewport-measure" half-right>R/2</span>
        <span id="viewport-measure-half-left" class="viewport-measure" half-left>-R/2</span>
        <span id="viewport-measure-left" class="viewport-measure" left>-R</span>
        <div id="shot-history-container" class="shot-history-container"></div>
        <div id="viewport-warning-sign" class="viewport-warning-sign">
            <img src="assets/ahtung.png" alt="warning-sign">
        </div>
    </div>
</section>