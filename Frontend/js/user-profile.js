const token = localStorage.getItem("token");
const sidebar = new bootstrap.Offcanvas(document.getElementById('navSidebar'));
const defaultProfileSVG = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='150' height='150' viewBox='0 0 24 24' fill='none' stroke='%23adb5bd' stroke-width='1'%3E%3Ccircle cx='12' cy='7' r='4'/%3E%3Cpath d='M5.5 21a6.5 6.5 0 0 1 13 0'/%3E%3C/svg%3E";

if(!token) {
    window.location.href="signin.html";
}

// Parse Token
const payload = JSON.parse(atob(token.split('.')[1]));
const userId = payload.id;
const userRole = payload.role; // Ensure your backend includes 'role' in JWT payload

// Initialize Sidebar Hover
$('#sidebar-sensor').on('mouseenter', () => sidebar.show());
let otpModal = new bootstrap.Modal(document.getElementById('otpModal'));

$(document).ready(() => {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }
    setupNavbar();
    loadUser();
    loadProfilePhoto();
});

function setupNavbar() {
    const navContainer = $("#dynamicNavLinks");
    navContainer.empty();

    if (userRole === "ADMIN") {
        $("#navRoleLabel").text("Admin 🛠️");
        navContainer.append(`
        <li><a href="admin-dashboard.html" class="nav-link p-3 text-dark"><i class="bi bi-speedometer2 me-3"></i> Dashboard</a></li>
        <li><a href="music-management.html" class="nav-link p-3 text-dark"><i class="bi bi-music-note-beamed me-3"></i> Music Management</a></li>
        <li><a href="artist-management.html" class="nav-link p-3 text-dark"><i class="bi bi-mic me-3"></i> Artists</a></li>
        <li><a href="user-management.html" class="nav-link p-3 text-dark"><i class="bi bi-people me-3"></i> User Directory</a></li>
        <li><a href="genre-management.html" class="nav-link p-3 text-dark"><i class="bi bi-tags me-3"></i> Genres</a></li>
        <li><a href="user-profile.html" class="nav-link p-3 text-dark"><i class="bi bi-person-circle me-3"></i> Profile</a></li>
      `);
    } else {
        $("#navRoleLabel").text("User 🎶");
        navContainer.append(`
        <li><a class="nav-link text-dark p-3 fw-medium" href="user-home.html">🏠 Home</a></li>
        <li><a class="nav-link text-dark p-3 fw-medium" href="search-page.html">🔍 Search</a></li>
        <li><a class="nav-link text-dark p-3 fw-medium" href="artists-page.html">🎙️ Artists</a></li>
        <li><a class="nav-link text-dark p-3 fw-medium" href="user-playlists.html">📜 Playlists</a></li>
        <li><a class="nav-link text-dark p-3 fw-medium" href="user-profile.html">👤 Profile</a></li>
      `);
    }
}

function loadUser() {
    $.ajax({
        url: `http://localhost:8080/api/v1/user/get/${userId}`,
        type: "GET",
        headers: {"Authorization": "Bearer " + token},
        success: function (response) {
            const user = response.data;
            $("#username").val(user.username);
            $("#email").val(user.email);
            $("#role").val(user.role);

            $("#displayUsername").text(user.username);
            $("#displayEmail").text(user.email);
            $("#displayRole").text(user.role);
        },
        error: (error) => {
            let msg = "Session expired. Please login again."
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
            logout();
        }
    });
}

function updateUser() {
    const updateData = {
        id: userId,
        username: $("#username").val(),
        email: $("#email").val(),
        role: $("#role").val()
    };

    $.ajax({
        url: "http://localhost:8080/api/v1/user/update",
        type: "PUT",
        headers: { "Authorization": "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify(updateData),
        success: function() {
            alert("Profile Updated Successfully! Please Sign-In again.");
            window.location.href = "sign-in.html";
        },
        error: (error) => {
            let msg = "Update user failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function deleteAccount() {
    if(!confirm("Are you sure? This will permanently delete your account and all data.")) return;
    $.ajax({
        url: "http://localhost:8080/api/v1/user/delete/" + userId,
        type: "DELETE",
        headers: { "Authorization": "Bearer " + token },
        success: function() {
            alert("Account deleted.");
            logout();
        },
        error: (error) => {
            let msg = "Delete user failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function handleUserProfilePreview(input) {
    if (input.files && input.files[0]) {

        const reader = new FileReader();

        reader.onload = function(e) {
            $('#userProfilePreview').attr('src', e.target.result);
        };

        reader.readAsDataURL(input.files[0]);

        // AUTO upload after selecting
        uploadUserProfilePic(input.files[0]);
    }
}

function uploadUserProfilePic(file) {

    let formData = new FormData();
    formData.append("userId", userId);
    formData.append("profilePic", file);

    $.ajax({
        url: "http://localhost:8080/api/v1/user/upload-profile-pic",
        type: "POST", // or PUT depending on backend
        headers: {
            "Authorization": "Bearer " + token
        },
        data: formData,
        processData: false,
        contentType: false,

        success: function () {
            // refresh with real image (avoid cache)
            $('#userProfilePreview').attr(
                'src',
                `http://localhost:8080/api/v1/user/get-profile-pic/${userId}`
            );
        },

        error: (error) => {
            let msg = "upload profile pic failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function loadProfilePhoto() {
    $("#userProfilePreview")
        .attr("src", `http://localhost:8080/api/v1/user/get-profile-pic/${userId}?t=${new Date().getTime()}`)
        .on("error", function () {
            $(this).off("error"); // prevent loop
            $(this).attr("src", defaultProfileSVG);
        });
}

function getUserEmail() {
    const email = $('#email').val();

    if (!email) {
        alert("Enter email first");
        return;
    }

    $.ajax({
        url: `http://localhost:8080/api/v1/user/get/${userId}`,
        type: "GET",
        headers: {"Authorization": "Bearer " + token},
        success: function (response) {
            const user = response.data;
            if (user.email === email) {
                updateUser();
            } else {
                sendOtp();
            }
        },
        error: () => {
            alert("Session expired. Please login again.");
            logout();
        }
    });
}

function sendOtp() {
    const email = $('#email').val();

    if (!email) {
        alert("Enter email first");
        return;
    }

    $('#update-user').prop('disabled', true);

    // Call backend API (change URL if needed)
    $.ajax({
        url: "http://localhost:8080/api/v1/auth/send-otp",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({ email: email }),
        success: function () {
            alert("OTP sent to your email!");
            startOtpTimer();
            otpModal.show();
        },
        error: function () {
            alert("Failed to send OTP");
            $('#update-user').prop('disabled', false);
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
            $('#update-user').prop('disabled', false);
        }
    }, 1000);
}

function verifyOtp() {
    const otp = $('#otpInput').val();
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
            otpModal.hide();
            updateUser();
        },
        error: function(error) {
            const msg = error.responseJSON?.message || "OTP validation failed";
            alert(msg);
        }
    });
}

function logout() {
    localStorage.clear();
    window.location.href = "sign-in.html";
}