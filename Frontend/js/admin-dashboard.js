const sidebar = new bootstrap.Offcanvas(document.getElementById('adminSidebar'));
$('#admin-sidebar-sensor').on('mouseenter', () => sidebar.show());

if (!localStorage.getItem("token")) {
    window.location.href = "sign-in.html";
}

// Fetch stats
function fetchStats() {
    $.ajax({
        url: "http://localhost:8080/api/v1/music/get-all",
        method: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        success: (res) => $("#totalTracks").text(res.data.length)
    });

    $.ajax({
        url: "http://localhost:8080/api/v1/artist/get-all",
        method: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        success: (res) => $("#totalArtists").text(res.data.length)
    });

    $.ajax({
        url: "http://localhost:8080/api/v1/user/get-all",
        method: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        success: (res) => $("#totalUsers").text(res.data.length)
    });

    $.ajax({
        url: "http://localhost:8080/api/v1/genre/get-all",
        method: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        success: (res) => $("#totalGenres").text(res.data.length)
    });
}

// Fetch recent activity
function fetchRecentActivity() {
    $.ajax({
        url: "http://localhost:8080/api/v1/activity/recent",
        method: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        success: (res) => {
            const tbody = $("#recentActivityTable tbody").empty();
            res.data.forEach(a => {
                tbody.append(`
          <tr>
            <td class="ps-3 text-muted">#${a.id}</td>
            <td>${a.type}</td>
            <td>${a.item}</td>
            <td>${a.username}</td>
            <td class="text-end pe-3">${new Date(a.date).toLocaleString()}</td>
          </tr>
        `);
            });
        }
    });
}

function logout() {
    localStorage.clear();
    window.location.href = "sign-in.html";
}

$(document).ready(() => {
    fetchStats();
    fetchRecentActivity();
});