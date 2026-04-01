const baseUrl = "http://localhost:8080/api/v1/auth/sign-up";
let countdown = 60;
let timerInterval = null;

function sendOtp() {
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
            signUp();
        },
        error: function(error) {
            const msg = error.responseJSON?.message || "OTP validation failed";
            alert(msg);
        }
    });
}

function signUp() {
    const username = $('#username').val();
    const password = $('#password').val();
    const email = $('#email').val();
    const role = "ADMIN";

    if (!username || !email || !password) {
        alert("Please fill all fields");
        return;
    }

    $.ajax({
        url: baseUrl,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            "username": username,
            "email": email,
            "password": password,
            "role": role
        }),
        success: function (response) {
            // Adjusting to check standard success patterns
            if (response.status === 200 || response.status === "SUCCESS") {
                alert("Registration successful!");
                window.location.href = "sign-in.html";
            }
        },
        error: (error) => {
            let msg = "Sign-Up failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
            // alert("Add failed")
        }
    });
}

$('#password').on('input', function () {
    const password = $(this).val();
    let strength = 0;

    // Rules
    if (password.length >= 6) strength++;
    if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength++;
    if (password.match(/\d/)) strength++;
    if (password.match(/[^a-zA-Z\d]/)) strength++;

    let text = "";
    let color = "";
    let width = "0%";

    switch (strength) {
        case 0:
        case 1:
            text = "Weak";
            color = "bg-danger";
            width = "25%";
            break;
        case 2:
            text = "Fair";
            color = "bg-warning";
            width = "50%";
            break;
        case 3:
            text = "Good";
            color = "bg-info";
            width = "75%";
            break;
        case 4:
            text = "Strong";
            color = "bg-success";
            width = "100%";
            break;
    }

    $('#passwordStrength').text(text);
    $('#strengthBar')
        .removeClass("bg-danger bg-warning bg-info bg-success")
        .addClass(color)
        .css("width", width);
});