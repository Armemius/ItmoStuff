<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Web lab 1</title>
        <link rel="stylesheet" href="index.css">
        <link rel="icon" href="assets/pepe.png">
        <script defer src="index.js"></script>
        <script defer src="canvas.js"></script>
    </head>
    <body>
        <div class="background background-image">
            <img src="assets/background.jpg" alt="bg">
        </div>
        <div class="background background-filter"></div>
        <header>
            <img src="assets/pepe.png" alt="pepe"/>
            <span>P3209</span>
            <span>Степанов А. А.</span>
            <span>ЛАБ1 / 1977</span>
            <img src="assets/pepe.png" alt="pepe"/>
        </header>
        <main>
            <section class="controls">
                <h2>
                    Управление
                    <span class="text-button" onclick="clearForm()">[Сброс]</span>
                </h2>
                <form id="form" method="post">
                    <section class="selection x-selection">
                        <label for="x-form-input">Значение X:</label>
                        <input name="x"
                               id="x-form-input"
                               type="text"
                               value="0"
                               hidden
                        />
                        <?php
                            $jt = 0;
                            for ($it = -2.0; $it <= 2.0; $it += 0.5) {
                                echo "<input id='x-form-input-button-$jt' type='button' value='$it'>";
                                $jt++;
                            }
                        ?>
                    </section>
                    <section class="selection y-selection">
                        <label for="y-form-input">Значение Y:</label>
                        <input name="y"
                               id="y-form-input"
                               type="text"
                               maxlength="8"
                               placeholder="-3 . . 5"
                        />
                    </section>
                    <section class="selection r-selection">
                        <label for="r-form-input">Значение R:</label>
                        <select name="r" id="r-form-input">
                            <?php
                                for ($it = 1; $it <= 5; $it++) {
                                    echo "<option value=\"$it\">$it</option>";
                                }
                            ?>
                        </select>
                    </section>
                    <input id="form-submit-button"
                           type="submit"
                           value="Проверка"
                    />
                    <div id="error-message" class="error-message"></div>
                </form>
            </section>
            <section class="viewport">
                <h2>Координатная плоскость</h2>
                <div>
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
                </div>
            </section>
            <section class="history">
                <h2>
                    Результаты попаданий
                    <span class="text-button" onclick="clearStorage()">
                        [Очистить]
                    </span>
                </h2>
                <section id="table">
                    <div class="row legend">
                        <span>X</span>
                        <span>Y</span>
                        <span>R</span>
                        <span>Результат</span>
                        <span>Время начала</span>
                        <span>Время вычисления</span>
                    </div>
                </section>
            </section>
        </main>
        <aside class="i-love-php">
            <div class="image-wrapper php">
                <img src="assets/php.png"
                     alt="php"
                />
            </div>
            <div class="image-wrapper">
                <img src="assets/trash_base.png"
                     alt="trash"
                />
            </div>
            <div class="image-wrapper">
                <img class="trash-bin trash-can"
                     src="assets/trash_cap.png"
                     alt="trash"
                />
            </div>
        </aside>
    <aside class="illuminati">
        <img src="assets/luminati.png" alt="luminati">
    </aside>
    </body>
</html>