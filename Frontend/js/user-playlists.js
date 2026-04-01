const token = localStorage.getItem("token");
const payload = token ? JSON.parse(atob(token.split('.')[1])) : { sub: "Guest" };
const username = payload.sub;
const userId = payload.id;
const sidebar = new bootstrap.Offcanvas(document.getElementById('navSidebar'));

$('#sidebar-sensor').on('mouseenter', () => sidebar.show());

$(document).ready(() => {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }
    loadPlaylists()
});

function loadPlaylists(){
    $.ajax({
        url: "http://localhost:8080/api/v1/playlist/load/" + userId,
        method: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        success: function(response){
            const container = $("#playlistContainer");
            container.empty();

            if(!response.data || response.data.length === 0) {
                container.html(`
                        <div class="col-12 text-center py-5">
                            <i class="bi bi-music-note-list display-1 text-muted opacity-25"></i>
                            <p class="text-muted mt-3">Start your journey by creating a playlist.</p>
                        </div>
                    `);
                return;
            }

            response.data.forEach(pl => {
                container.append(`
                        <div class="col-6 col-md-4 col-lg-3 col-xl-2">
                            <div class="card h-100 shadow-sm text-center p-4 playlist-card position-relative">

                                <!-- Action Buttons -->
                                <div class="position-absolute top-0 end-0 m-2 d-flex gap-2">
                                    <i class="bi bi-pencil-square text-primary cursor-pointer"
                                       onclick="event.stopPropagation(); showUpdateModal(${pl.id}, '${pl.playlistName}')"></i>

                                    <i class="bi bi-trash text-danger cursor-pointer"
                                       onclick="event.stopPropagation(); deletePlaylist(${pl.id})"></i>
                                </div>

                                <div onclick="openPlaylist(${pl.id}, '${pl.playlistName}')">
                                    <div class="icon-box">
                                        <i class="bi bi-music-note-list"></i>
                                    </div>
                                    <div class="playlist-title text-truncate">${pl.playlistName}</div>
                                    <div class="playlist-subtitle">Playlist</div>
                                </div>

                            </div>
                        </div>
                    `);
            });
        },
        error: function() {
            $("#playlistContainer").html('<p class="text-danger text-center">Failed to load library.</p>');
        }
    });
}

function createPlaylist(){
    const name = $("#playlistName").val();
    if(!name) return alert("Please enter a name");

    $.ajax({
        url: "http://localhost:8080/api/v1/playlist/create",
        method: 'POST',
        contentType: 'application/json',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        data: JSON.stringify({ "playlistName": name, "userId": userId }),
        success: function () {
            $("#playlistName").val('');
            bootstrap.Modal.getInstance(document.getElementById('createModal')).hide();
            loadPlaylists();
        },
        error: (error) => {
            let msg = "Create playlist failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function updatePlaylist() {
    const newName = $("#updatePlaylistName").val();

    if (!newName) return alert("Enter a name");

    $.ajax({
        url: "http://localhost:8080/api/v1/playlist/update/" + currentPlaylistId,
        method: "PUT",
        contentType: "application/json",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: JSON.stringify({
            playlistName: newName,
            userIds: userId
        }),
        success: function () {
            bootstrap.Modal.getInstance(document.getElementById('updateModal')).hide();
            loadPlaylists();
        },
        error: (error) => {
            let msg = "Update playlist failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function deletePlaylist(id) {
    if (!confirm("Are you sure you want to delete this playlist?")) return;

    $.ajax({
        url: "http://localhost:8080/api/v1/playlist/delete/" + id,
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        success: function () {
            loadPlaylists();
        }
    });
}

function openPlaylist(id, name) {
    localStorage.setItem("selectedPlaylistId", id);
    localStorage.setItem("selectedPlaylistName", name);
    window.location.href = "user-playlist-songs.html";
}

let currentPlaylistId = null;

function showUpdateModal(id, name) {
    currentPlaylistId = id;
    $("#updatePlaylistName").val(name);
    new bootstrap.Modal(document.getElementById('updateModal')).show();
}

function logout() {
    localStorage.clear();
    window.location.href = "sign-in.html";
}
