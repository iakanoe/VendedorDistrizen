<?php
    if($_SERVER["CONTENT_TYPE"] == "application/json"){
        $_POST = json_decode(file_get_contents('php://input'), true);
    }
    if(!isset($_POST["auth"])) die("Error 0: Not Authorized");
    if($_POST["auth"] != "00distrizen00") die("Error 0: Not Authorized");
    if(!isset($_POST["action"])) die("Error 1: No Action Specified");
    $action = $_POST["action"];
    $conn = mysqli_connect("softway.com.ar", "distrizensql", "00distrizen00", "distrizen", 3306);
    if(!$conn) die("Error 3: Bad DB Connection");
    switch($action) {
        case "login":
            if(!isset($_POST["user"])) die("Error 4: No Username Specified");
            $user = $_POST["user"];
            if(!isset($_POST["pass"])) die("Error 5: No Password Specified");
            $pass = $_POST["pass"];
            $result = mysqli_query($conn, "SELECT true AS result, codigo, nombre FROM vendedor WHERE (codigo = \"$user\")");
            if(!$result) die("{\"result\": false}");
        	die(json_encode(mysqli_fetch_assoc($result)));
            break;

        case "clientes":
            if(!isset($_POST["user"])) die("Error 4: No Username Specified");
            $user = $_POST["user"];
            $result = mysqli_query($conn, "SELECT codigo, nombre, listap, civa FROM clientes WHERE (vend = \"$user\")");
            if(!$result) die("[]");
            $rows = array();
            while($r = mysqli_fetch_assoc($result)) $rows[] = $r;
        	die(json_encode($rows));
            break;

        case "articulos":
            $result = mysqli_query($conn, "SELECT codiarti, descrip, codirubr, venta, venta2, venta3 FROM articulos");
            if(!$result) die("[]");
            $rows = array();
            while($r = mysqli_fetch_assoc($result)) $rows[] = $r;
            die(json_encode($rows));
            break;

        case "lineas":
            $result = mysqli_query($conn, "SELECT codigo, descrip FROM lineas");
            if(!$result) die("[]");
            $rows = array();
            while($r = mysqli_fetch_assoc($result)) $rows[] = $r;
            die(json_encode($rows));
            break;

        case "crearpedido":
            if(!isset($_POST["pedidoclient"])) die("Error 6: No client specified.");
            if(!isset($_POST["pedidoarray"])) die("Error 7: No array specified.");
            $client = $_POST["pedidoclient"];
            $array = $_POST["pedidoarray"];
            $idpedido = mysqli_fetch_row(mysqli_query($conn, "SELECT getNuevoPedidoID()"))[0];
            $itemcount = 0;
            foreach($array as $producto){
                $articulo = $producto["articulo"];
                $cantidad = $producto["cantidad"];
                $importe = $producto["importe"];
                $query = "CALL nuevoArticulo($idpedido, \"$client\", $itemcount, \"$articulo\", $cantidad, $importe)";
                if(!mysqli_query($conn, $query)) die("Error 8: Error while inserting in DB.");
                $itemcount++;
            }
            die("success");
            break;

        default:
            die("Error 2: Action not recognized");
            break;
    }
?>
