$(document).ready(function() {
    console.log("HarmoniQ App Initialized");
    const token = localStorage.getItem("token");
    showSignIn();
});

// Professional Toast Configuration
const Toast = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
    background: '#1a0033',
    color: '#fff'
});

function showSignIn() {
    const html = `
        <div class="brand mb-3">HarmoniQ 🎧</div>
        <h5 class="mb-4">Welcome Back</h5>
        <div class="mb-3 text-start">
            <label class="form-label text-secondary small">Username</label>
            <input type="text" id="login-user" class="form-control" placeholder="Enter username">
        </div>
        <div class="mb-4 text-start">
            <label class="form-label text-secondary small">Password</label>
            <input type="password" id="login-pass" class="form-control" placeholder="Enter password">
        </div>
        <button class="btn btn-purple w-100 mb-3" onclick="handleSignIn()">Sign In</button>
        <p class="mb-0 small">New to HarmoniQ? <a href="javascript:void(0)" onclick="showSignUp()">Create Account</a></p>
    `;
    renderAuth(html);
}

function showSignUp() {
    const html = `
        <div class="brand mb-3">HarmoniQ 🎶</div>
        <h5 class="mb-4">Create Account</h5>
        <div class="mb-3 text-start">
            <label class="form-label text-secondary small">Username</label>
            <input type="text" id="reg-user" class="form-control" placeholder="Choose username">
        </div>
        <div class="mb-3 text-start">
            <label class="form-label text-secondary small">Email</label>
            <input type="email" id="reg-email" class="form-control" placeholder="Enter email address">
        </div>
        <div class="mb-4 text-start">
            <label class="form-label text-secondary small">Password</label>
            <input type="password" id="reg-pass" class="form-control" placeholder="Create password">
        </div>
        <button class="btn btn-purple w-100 mb-3" onclick="handleSignUp()">Sign Up</button>
        <p class="mb-0 small">Already have an account? <a href="javascript:void(0)" onclick="showSignIn()">Sign In</a></p>
    `;
    renderAuth(html);
}

function renderAuth(content) {
    const shell = $('#auth-shell');
    shell.fadeOut(150, function() {
        shell.html(content).fadeIn(250);
    });
}

function handleSignIn() {
    const username = $('#login-user').val();
    const password = $('#login-pass').val();

    if(!username || !password) {
        return Swal.fire({ icon: 'warning', title: 'Missing Fields', text: 'Please fill all fields', background: '#1c1c1c', color: '#fff' });
    }

    $.ajax({
        url: `${API_BASE}/auth/sign-in`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ username, password }),
        success: function(response) {
            if (response.status === 200) {
                localStorage.setItem("token", response.data.accessToken);
                Toast.fire({ icon: 'success', title: 'Signed in successfully' });
                // window.location.href = "dashboard.html";
            }
        },
        error: function(err) {
            Swal.fire({ icon: 'error', title: 'Login Failed', text: err.responseJSON?.message || "Invalid credentials", background: '#1c1c1c', color: '#fff' });
        }
    });
}

function handleSignUp() {
    const username = $('#reg-user').val();
    const email = $('#reg-email').val();
    const password = $('#reg-pass').val();

    if(!username || !email || !password) {
        return Swal.fire({ icon: 'warning', title: 'Missing Fields', text: 'Please fill all fields', background: '#1c1c1c', color: '#fff' });
    }

    $.ajax({
        url: `${API_BASE}/auth/sign-up`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ username, email, password, role: "ADMIN" }),
        success: function(response) {
            Swal.fire({ icon: 'success', title: 'Welcome!', text: 'Account created successfully', background: '#1c1c1c', color: '#fff' });
            showSignIn();
        },
        error: function(err) {
            Swal.fire({ icon: 'error', title: 'Sign Up Failed', text: err.responseJSON?.message || "Something went wrong", background: '#1c1c1c', color: '#fff' });
        }
    });
}