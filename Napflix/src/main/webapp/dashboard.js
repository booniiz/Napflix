function check(){
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "ajax/employeeCheck",
        success: function(data){
            if(data === "admin"){
                return;
            }
            else{
                window.history.back()
            }
        },
    });
}

function handleStars(data){
    console.log("sadasdsa");
    let json = JSON.parse(data);
    console.log(json);
}

function addStars(){
    console.log("ajax");
    $.ajax({
            type: "POST",
            url: "/ajax/addStars",
            data: {name:$("#nameID").val(), birthday:$("#birthYearID").val()},
            success: handleStars,
            dataType: 'Text'
        }
    );

}

