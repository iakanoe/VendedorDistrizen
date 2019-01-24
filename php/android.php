<?php
    if(!isset($_POST["action"])) die("Error 1: No Action Specified");
    $action = $_POST["action"];
    $conn = mysqli_connect('bdipadress', 'bduser', 'bdpass', 'distrizen', 'bdport');
    if(!$conn) die("Error 2: Bad DB Connection");


    switch($action) {
        case 'login':
            if(!isset($_POST["user"])) die("Error 3: No Username Specified");
            $user = $_POST["user"];
            if(!isset($_POST["pass"])) die("Error 4: No Password Specified"); $pass = $_POST["pass"];
            $result = mysqli_query($conn, "SELECT (true AS result), codigo, nombre FROM vendedor WHERE (codigo = \"" . $user . "\")");
            if(!$result) die("[{\"result\": false}]");
        	die(json_encode(mysqli_fetch_assoc($result)));
            break;

        default:
            // code...
            break;
    }
?>
