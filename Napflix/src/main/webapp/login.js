function handleResult(data){
    let json = JSON.parse(data);
    if (json["Login"] == "True"){
        //After logon, we redirect
        window.location.replace('/Napflix')
    }else{
        $("#login_failed").slideDown(1000);
    }
}

function makeAjax(){
    $.ajax({
            url: "ajax/login",
            type: "POST",
            data: {username:$("#username").val(), password:$("#password").val()},
            success: handleResult,
            dataType: 'Text'
        }
    );

}

$("button").click(makeAjax);
$("#login_failed").hide();
