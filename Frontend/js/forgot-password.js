function sendResetOtp() {
    const email = $('#email').val();

    if (!email) {
        alert("Enter email first");
        return;
    }

    // Disable button immediately
    $('#sendOtpBtn').prop('disabled', true);

    // Call backend API (change URL if needed)
    $.ajax({
        url: "http://localhost:8080/api/v1/auth/send-otp",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({ email: email }),
        success: function () {
            alert("OTP sent to your email!");
            startOtpTimer();
        },
        error: function () {
            alert("Failed to send OTP");
            $('#sendOtpBtn').prop('disabled', false);
        }
    });
}

function startOtpTimer() {
    countdown = 60;

    $('#otpTimer').text(`Resend available in ${countdown}s`);

    timerInterval = setInterval(() => {
        countdown--;

        $('#otpTimer').text(`Resend available in ${countdown}s`);

        if (countdown <= 0) {
            clearInterval(timerInterval);
            $('#otpTimer').text("");
            $('#sendOtpBtn').prop('disabled', false);
        }
    }, 1000);
}

function validateOtp() {
    const otp = $('#otp').val();
    const email = $('#email').val();

    if (!otp || !email) {
        alert("Please enter both OTP and email");
        return false;
    }

    $.ajax({
        url: "http://localhost:8080/api/v1/auth/validate-otp",
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            "email": email,
            "otp": otp
        }),
        success: function (response) {
            console.log("OTP validation successful:", response);
            resetPassword();
        },
        error: function(error) {
            const msg = error.responseJSON?.message || "OTP validation failed";
            alert(msg);
        }
    });
}

function resetPassword() {
    const username = document.getElementById("newUsername").value;
    const password = document.getElementById("newPassword").value;
    const email = document.getElementById("email").value;

    if (!username || !password || !email) {
        alert("Please fill all fields");
        return;
    }

    $.ajax({
        url: "http://localhost:8080/api/v1/auth/reset-password",
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            "username": username,
            "email": email,
            "password": password,
        }),
        success: function (response) {
            if (response.status === 200 || response.status === "SUCCESS") {
                alert("Password reset successful!");
                window.location.href = "sign-in.html";
            }
        },
        error: (error) => {
            let msg = "Password reset failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}