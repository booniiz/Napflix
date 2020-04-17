function makeAjax(){
    $.ajax({
            url: "ajax/login",
            type: "POST",
            data: {username:$("#username").val(), password:$("#password").val()}
        }
    );
}

$("button").click(makeAjax);
