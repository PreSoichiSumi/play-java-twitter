//initially disable the button
$("#tweet-button").prop("disabled", true);

//When the textarea value is changed
$("textarea").on("input", function () {
    if ($(this).val().length > 0) {
        $("#tweet-button").prop("disabled", false);
    } else {
        $("#tweet-button").prop("disabled", true);
    }
});

