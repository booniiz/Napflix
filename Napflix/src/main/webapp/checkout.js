let json = null;
function payment(){
    window.location.replace("/Napflix/payment.html")
}
function updateTotal(){
    $.ajax({
            url: "ajax/getcart",
            type: "GET",
            success: function (data) {
                let total = JSON.parse(data)["total"]
                console.log(total)
                $("#total").text("Your total: $" + total)
                $("#update").slideDown(500);
                $("#update").slideUp(1000);
            }
        }
    );
}
function update($this){
    let movieID = $this.attr("name");
    let quantity = $this.val();
    console.log(movieID);
    console.log(quantity);
    $.ajax(
        {
            url:"ajax/addtocart",
            type:"POST",
            data: {movieID:movieID, quantity:quantity, price:1, title:"", change_mode:true},
            success: updateTotal
        }
        )
}

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
            json[i]["title"] +
            "</th>" +
            "<th>" +
            json[i]["price"] +
            "</th>" +
            "<th>" +
            "<input name = \""+ json[i]["movieID"] +"\"type=\"number\" step=\"1\" value=\"" + json[i]["quantity"]+ "\">" +
            "</th>" +
            "<th>" +
            "<button type=\"button\" class=\"btn btn-danger remove\">REMOVE</button>" +
            "</th>" +
            "</tr>")
        console.log(json[i]["movieID"])
        console.log(json[i]["quantity"])
        console.log(json[i]["price"])
        console.log(json[i]["title"])
    }
    let total = JSON.parse(data)["total"]
    $("#total").text("Your total: $" + total)
    $(":input").on('input',
        function(){
            if ($(this).val() == ""){
                return;
            }
            if (isNaN($(this).val()) == false){ //not(not a number) = a number
                if ($(this).val() <= 0){
                    $(this).parents("tr").remove();
                }
                $(this).val(Math.round($(this).val()));
                update($(this));
            }
        }
    )
    $(".remove").on('click',
            function () {
                $(this).parent().prev().children().val(0).trigger("input");
            }
        )
}

function doAjax(){

    $.ajax({
            url: "ajax/getcart",
            type: "GET",
            success: handleCart
        }
    );

}

$(document).ready(
    function (){
        $("#update").hide()
        doAjax();
    }
);

