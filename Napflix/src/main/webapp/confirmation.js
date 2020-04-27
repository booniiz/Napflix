let objs = JSON.parse(JSON.parse(sessionStorage.getItem("payment_response"))["confirmation"])

for(i = 0; i < objs["items"].length; i++){
    var sale = objs["items"][i];
    console.log(sale);
    $("#confirmation").append(
        "<tr>" +
            "<th>"+
                sale["saleID"] +
            "</th>" +
            "<th>"+
                sale["title"] +
            "</th>" +
            "<th>"+
                sale["price"] +
            "</th>" +
            "<th>"+
                sale["quantity"] +
            "</th>" +
        "</tr>"
    )


}
$("#total").text("Your total: $" + objs["total"])
sessionStorage.removeItem("payment_response")