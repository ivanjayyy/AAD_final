$(document).ready(function() {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
    }

    $('#to').val(localStorage.getItem("email"))
    localStorage.removeItem("email")

    $('#emailForm').on('submit', function(e) {
        e.preventDefault();

        const emailData = {
            to: $('#to').val(),
            subject: $('#subject').val(),
            body: $('#body').val()
        };

        $('#responseMsg').html('<div class="alert alert-info">Sending email...</div>');

        $.ajax({
            url: 'http://localhost:8080/api/v1/email/send',
            type: 'POST',
            contentType: 'application/json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            data: JSON.stringify(emailData),
            success: function(res) {
                $('#responseMsg').html('<div class="alert alert-success">Email sent successfully!</div>');
                $('#emailForm')[0].reset();
                window.location.href = "user-management.html";
            },
            error: function(xhr) {
                let errMsg = 'Failed to send email';
                try {
                    const err = JSON.parse(xhr.responseText);
                    if (err.message) errMsg = err.message;
                } catch(e) {}
                $('#responseMsg').html('<div class="alert alert-danger">Error: ' + errMsg + '</div>');
            }
        });
    });
});

function goBack() {
    window.location.href = "user-management.html";
}