const baseUrl = "http://localhost:8080/api/v1/user";
const sidebar = new bootstrap.Offcanvas(document.getElementById('adminSidebar'));

$('#admin-sidebar-sensor').on('mouseenter', () => sidebar.show());

$(document).ready(() => {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }
    fetchUsers()
});

function fetchUsers() {
    $.ajax({
        url: `${baseUrl}/get-all`,
        method: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        success: function(response) {
            const adminsTbody = $("#adminTable tbody").empty();
            const usersTbody = $("#userTable tbody").empty();

            response.data.forEach(user => {
                const row = `
                        <tr>
                            <td class="ps-3 text-muted">#${user.id}</td>
                            <td class="fw-bold">${user.username}</td>
                            <td>${user.email}</td>
                            <td class="text-end pe-3">
                                <button class="btn btn-sm btn-outline-primary" onclick="goToEmailPage('${user.email}')"><i class="bi bi-envelope-fill"></i> Email</button>
                                <button class="btn btn-sm btn-outline-primary" onclick="viewUser('${user.id}')"><i class="bi bi-pencil"></i> Edit</button>
                                <button class="btn btn-sm btn-outline-danger ms-2" onclick="deleteUser('${user.id}')"><i class="bi bi-trash"></i></button>
                            </td>
                        </tr>`;

                if (user.role === "ADMIN") adminsTbody.append(row);
                else usersTbody.append(row);
            });
        }
    });
}



function goToEmailPage(email) {
    localStorage.setItem("email",email);
    window.location.href = "email-page.html";
}

function viewUser(id) {
    $.ajax({
        url: `${baseUrl}/get/${id}`,
        type: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        success: function(response) {
            const user = response.data;
            $("#modalUserId").val(user.id);
            $("#modalUsername").val(user.username);
            $("#modalEmail").val(user.email);
            $("#modalRole").val(user.role);
            new bootstrap.Modal(document.getElementById("userModal")).show();
        }
    });
}

function updateUser() {
    $.ajax({
        url: `${baseUrl}/update-role/${$("#modalUserId").val()}`,
        type: "PUT",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        contentType: "application/json",
        data: JSON.stringify({ role: $("#modalRole").val() }),
        success: function() {
            bootstrap.Modal.getInstance(document.getElementById("userModal")).hide();
            fetchUsers();
        }
    });
}

function deleteUser(id) {
    if (!confirm("Delete user?")) return;
    $.ajax({
        url: `${baseUrl}/delete/${id}`,
        type: "DELETE",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        success: () => fetchUsers()
    });
}

function logout() {
    localStorage.clear();
    window.location.href = "sign-in.html";
}