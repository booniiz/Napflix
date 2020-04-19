function handleCart(data){
    console.log("Handling data")
    if (data == "null"){
        return;
    }
    let json = JSON.parse(data)["items"];
    let carts = $("#shoping_cart")
    for(i = 0; i < json.length; i ++){
        carts.append("<tr>"+
            "<th>" +
            json[i]["movieID"] +
            "</th>" +
            "<th>" +
            json[i]["price"] +
            "</th>" +
            "<th>" +
            "<input type=\"number\" step=\"1\" value=\"" + json[i]["quantity"]+ "\">" +
            "</th>" +
            "</tr>")
        console.log(json[i]["movieID"])
        console.log(json[i]["quantity"])
        console.log(json[i]["price"])
        console.log(json[i]["title"])
    }
}

function doAjax(){

    $.ajax({
            url: "ajax/getcart",
            type: "GET",
            success: handleCart
        }
    );
}

$(document).ready(doAjax());