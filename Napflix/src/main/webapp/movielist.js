let title;
let movieID;
function show_msg() {

}

function add_to_cart(button){
    console.log(button);
    console.log(button.attr("movieid"));
    console.log(button.attr("title"));
    $.ajax(
        {
            url : "/Napflix/ajax/addtocart",
            data : {movieID: button.attr("movieid"), quantity:1, price:1, title:button.attr("title"), change_mode: false},
            method : "POST",
            success : function(){
                $("#add").slideDown(1000);
                $("#add").slideUp(1000);
            },
            error: function () {
                $("#add_error").slideDown(1000);
            }

        }
    )
}

$("#add").hide()
$("#add_error").hide()
$(document).on("click", '.add_to_cart',
    function(){
    console.log($(this).attr("movieid"));
    console.log($(this).attr("title"));
    add_to_cart($(this));
    }
    )