/* Title: autocomplete.js
Author: Chen Li
Date: 5/23/2020
Description: Use for auto completing movies suggestion
The code is lifted and modified from professor Chen's example.
For educational purposes in Cs 122b only.
Link: https://github.com/UCI-Chenli-teaching/cs122b-spring20-project4-autocomplete-example
*/
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")

    var cache = sessionStorage.getItem(query);
    if(cache === null){
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        console.log("sending AJAX request to backend Java Servlet")
        console.log("lookup AJAX Server")
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "autocomplete?query=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }
    else{
        console.log("Request from cache")
        console.log("lookup session cache")
        handleLookupAjaxSuccess(cache, query, doneCallback)
    }

}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("successful")
    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)
    sessionStorage.setItem(query, data);


    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])

        // pass the value of the input box to the handler function
    window.location.replace('/Napflix/api/movie?movieID='+suggestion["data"]["movieID"]+'&titleID='+suggestion["value"] +'&sort=m.title ASC,r.rating DESC&page=1&limit=10');

}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    minChars:3
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})



