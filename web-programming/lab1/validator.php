<?php
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Content-Type: text/plain; charset=utf-8');
    echo 'Methods except POST are not allowed for this resource';
    return;
}

date_default_timezone_set('Europe/Moscow');
$start_time = microtime(true);

function distance($x1, $y1, $x2, $y2) {
    return sqrt(($x1 - $x2) * ($x1 - $x2) + ($y1 - $y2) * ($y1 - $y2));
}

function checkCircle($x, $y, $r) {
    return distance(0.0, 0.0, $x, $y) <= $r
        && $x >= 0.0
        && $y >= 0.0;
}

function checkTriangle($x, $y, $r) {
    return $x <= 0.0
        && $y <= 0.0
        && $x + $y + $r / 2.0 >= 0.0;
}

function checkRectangle($x, $y, $r) {
    return $x >= 0.0
        && $x <= $r
        && $y <= 0.0
        && $y >= -$r / 2.0;
}

function checkHit($x, $y, $r) {
    return checkCircle($x, $y, $r)
        || checkRectangle($x, $y, $r)
        || checkTriangle($x, $y, $r);
}

$x = isset($_POST['x']) ? $_POST['x'] : null;
$y = isset($_POST['y']) ? $_POST['y'] : null;
$r = isset($_POST['r']) ? $_POST['r'] : null;

$valid = is_numeric($x) && is_numeric($y) && is_numeric($r)
    && in_array($x, [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2])
    && -3 <= $y && $y <= 5
    && in_array($r, [1, 2, 3, 4, 5]);

$hit_result = $valid ?
    checkHit($x, $y, $r) ?
        "Попадание"
        :
        "Промах"
    :
    "Ошибка";

if (!$valid) {
    http_response_code(400);
} else {
    http_response_code(200);
}

$end_time = microtime(true);

header('Content-Type: application/json; charset=utf-8');
$data = array(
    'x' => $x,
    'y' => $y,
    'r' => $r,
    'result' => $hit_result,
    'timestamp' => date('H:i:s'),
    'execution' => ($end_time - $start_time)
);

echo json_encode($data);
