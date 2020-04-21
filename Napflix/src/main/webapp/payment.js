function handleResult(data){
    alert(JSON.parse(data)["CCValid"])
}

function makeAjax(){
    console.log($("#paymentform").serialize());
    $.ajax({
            url: "ajax/pay",
            type: "POST",
            data: $("#paymentform").serialize(),
            success: handleResult,
        }
    );

}

$("button").click(makeAjax);
