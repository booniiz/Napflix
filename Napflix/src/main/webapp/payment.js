function handleResult(data){
    try{
        if (JSON.parse(data)["CCValid"] == "True"){
            console.log("Good!");
            sessionStorage.setItem("payment_response",data);
            window.location.replace('/Napflix/confirmation.html')
        }else{
            throw "CC Not found"
        }
    }catch(e){
        $("#payment_failed").slideDown(1000);
        $("#payment_failed").slideUp(1000);
    }

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

$("#payment_failed").hide();
$("button").click(makeAjax);
