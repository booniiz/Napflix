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
function printMetadata(data){
    var x =  "<p></p>";
    var y =  "<p></p>";
    var flag = 0;
    var first = 1;
    for (i = 0; i < data.length; i++) {
        if(data[i] === "break"){
            if(flag === 0){
                flag = 1;
                first =1;
                y = y + "<h3>-------------------------</h3>";
            }
            else{
                flag = 0;
                first =1;
                x = x + "<h3>-------------------------</h3>";
            }

        }
        else if(flag ===1) {
            if(first === 1){
                x = x + "<h3>" + data[i] + "</h3>";
            }
            else {
                x = x + "<p>" + data[i] + "</p>";
            }
            first =0;
        }
        else{
            if(first === 1){
                y = y + "<h3>" + data[i] + "</h3>";
            }
            else {
                y = y + "<p>" + data[i] + "</p>";
            }
            first =0;
        }
    }
    document.getElementById("right").innerHTML = x;
    document.getElementById("left").innerHTML = y;
}
function getMetadata(){
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "ajax/metadata",
        success: printMetadata,
    });
}
function handleStars(data){
    let json = JSON.parse(data);
    document.getElementById("message").innerHTML = json;
    $("#message").slideDown(1000);
}

function addStars(){
    event.preventDefault()
    $.ajax({
            type: "POST",
            url: "ajax/addStars",
            data: {nameID:$("#nameID").val(), birthYearID:$("#birthYearID").val()},
            success: handleStars,
            dataType: 'Text'
        }
    );
}
function handleMovie(data){
    let json = JSON.parse(data);
    document.getElementById("message").innerHTML = json;
    $("#message").slideDown(1000);
}

function addMovie(){
    event.preventDefault()
    $.ajax({
            type: "POST",
            url: "ajax/addMovies",
            data: {titleID:$("#titleID").val(), yearID:$("#yearID").val()
                , directorID:$("#directorID").val(), starID:$("#starID").val()
                , birthdayID:$("#birthdayID").val(), genreID:$("#genreID").val()},
            success: handleMovie,
            dataType: 'Text'
        }
    );
}
function start(){
    check();
    getMetadata();
}
$("#message").hide();
