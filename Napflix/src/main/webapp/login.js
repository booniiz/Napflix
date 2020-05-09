function handleResult(data){
    let json = JSON.parse(data);
    if (json["Recaptcha"] === "True"){
        if (json["Login"] === "True"){
            //After logon, we redirect
            window.location.replace('/Napflix/mainmenu.html')
        }else{
            $("#login_failed").slideDown(1000);
        }
    }
    else{
        $("#recaptcha_failed").slideDown(1000);
    }
}

function makeAjax(){
    $.ajax({
            url: "ajax/login",
            type: "POST",
            data: {username:$("#username").val(), password:$("#password").val(), recaptcha: $("#g-recaptcha-response").val()},
            success: handleResult,
            dataType: 'Text'
        }
    );

}

$("button").click(makeAjax);
$("#login_failed").hide();
$("#recaptcha_failed").hide();