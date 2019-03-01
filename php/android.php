<?php
    $error = Array(
        "Error 0: Not authorized",
        "Error 1: No action specified",
        "Error 2: Action not recognized",
        "Error 3: Bad DB connection",
        "Error 4: No username specified",
        "Error 5: No password specified",
        "Error 6: No client specified.",
        "Error 7: No array specified.",
        "Error 8: Error while inserting in DB.",
        "Error 9: No ID specified.",
        "Error 10: ID not exists.",
        "Error 11: No \"importe\" specified.",
        "Error 11: No \"factura\" specified."
    );

    if($_SERVER["CONTENT_TYPE"] == "application/json")
        $_POST = json_decode(file_get_contents('php://input'), true);
    if(!isset($_POST["auth"])) die($error[0]);
    if($_POST["auth"] != "00distrizen00") die($error[0]);
    if(!isset($_POST["action"])) die($error[1]);
    $action = $_POST["action"];
    $conn = mysqli_connect("softway.com.ar", "distrizensql", "00distrizen00", "distrizen", 3306);
    if(!$conn) die($error[3]);
    switch($action) {
        case "login":
            if(!isset($_POST["user"])) die($error[4]);
            $user = $_POST["user"];
            if(!isset($_POST["pass"])) die($error[5]);
            $pass = $_POST["pass"];
            $result = mysqli_query($conn, "SELECT true AS result, codigo, nombre FROM vendedor WHERE (codigo = \"$user\")");
            if(!$result) die("{\"result\": false}");
        	die(json_encode(mysqli_fetch_assoc($result)));
            break;

        case "clientes":
            if(!isset($_POST["user"])) die($error[4]);
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
            if(!isset($_POST["pedidoclient"])) die($error[6]);
            if(!isset($_POST["pedidoarray"])) die($error[7]);
            $client = $_POST["pedidoclient"];
            $array = $_POST["pedidoarray"];
            $idpedido = mysqli_fetch_row(mysqli_query($conn, "SELECT getNuevoPedidoID()"))[0];
            $itemcount = 0;
            foreach($array as $producto){
                $articulo = $producto["articulo"];
                $cantidad = $producto["cantidad"];
                $importe = $producto["importe"];
                $query = "CALL nuevoArticulo($idpedido, \"$client\", $itemcount, \"$articulo\", $cantidad, $importe)";
                if(!mysqli_query($conn, $query)) die($error[8]);
                $itemcount++;
            }
            die("success");
            break;

        case "getpedidos":
            if(!isset($_POST["user"])) die($error[4]);
            $user = $_POST["user"];
            $result = mysqli_query($conn, "CALL getPedidos(\"$user\")");
            if(!$result) die("[]");
            $rows = array();
            while($r = mysqli_fetch_assoc($result)) $rows[] = $r;
            die(json_encode($rows));
            break;

        case "pedidoinfo":
            if(!isset($_POST["id"])) die($error[9]);
            $id = $_POST["id"];
            $result = mysqli_query($conn, "CALL getPedidoInfo($id)");
            if(!$result) die($error[10]);
            $items = array();
            while($r = mysqli_fetch_assoc($result)) $items[] = $r;
            mysqli_close($conn);
            $conn = mysqli_connect("softway.com.ar", "distrizensql", "00distrizen00", "distrizen", 3306);
            if(!$conn) die($error[3]);
            $result = mysqli_query($conn, "CALL getClienteFromPedido($id)");
            if(!$result) die($error[10]);
            $cliente = mysqli_fetch_assoc($result);
            $return["items"] = $items;
            $return["cliente"] = $cliente;
            die(json_encode($return));
            break;

        case "cobrar":
            if(!isset($_POST["cliente"])) die($error[6]);
            if(!isset($_POST["importe"])) die($error[11]);
            if(!isset($_POST["factura"])) die($error[12]);
            $cliente = $_POST["cliente"];
            $importe = $_POST["importe"];
            $factura = $_POST["factura"];
            $query = "INSERT INTO pagos (cliente, importe, factura) VALUES (\"$cliente\", $importe, $factura)";
            if(!mysqli_query($conn, $query)) die($error[8]);
            die("success");
            break;

        default:
            die($error[2]);
            break;
    }
?>
