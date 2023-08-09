const apiUrl = "http://localhost:8081/exam_war_exploded/";

$(document).ready(function() {
    $("#login-form").submit(function(event) {
        event.preventDefault(); // Prevent the default form submission

        // Get form data
        var formData = $(this).serialize();

        // Make the AJAX POST request
        $.ajax({
            type: "POST",
            url: apiUrl + "api/login",
            data: formData,
            success: function(response) {
                //get role and login from headers
                window.sessionStorage.setItem('user', JSON.stringify(response));
                // Redirect the user to the app page
                window.location.href = "app.html";
            },
            error: function(xhr, status, error) {
                //get status code
                if (xhr.status == 401) {
                    $("#error-msg").text("Incorrect username or password");
                }
                else {
                    $("#error-msg").text("Server error");
                }
            }
        });
    });
});
