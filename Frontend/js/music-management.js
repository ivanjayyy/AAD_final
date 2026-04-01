const baseUrl = "http://localhost:8080/api/v1/music";
const artistUrl = "http://localhost:8080/api/v1/artist/get-all";
const genreBaseUrl = "http://localhost:8080/api/v1/genre";
const sidebar = new bootstrap.Offcanvas(document.getElementById('adminSidebar'));
const placeholderThumb = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 24 24' fill='none' stroke='%23adb5bd' stroke-width='1'%3E%3Crect x='3' y='3' width='18' height='18' rx='2' ry='2'%3E%3C/rect%3E%3Ccircle cx='8.5' cy='8.5' r='1.5'%3E%3C/circle%3E%3Cpolyline points='21 15 16 10 5 21'%3E%3C/polyline%3E%3C/svg%3E";

let existingArtists = [];
let existingGenres = [];

$('#admin-sidebar-sensor').on('mouseenter', () => sidebar.show());

$(document).ready(() => {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }
    fetchMusic();
    fetchArtists();
    fetchGenres();

    $('#musicSearch').on('keyup', function() {
        const query = $(this).val().toLowerCase();
        $('#musicTable tbody tr').filter(function() {
            const title = $(this).find('td:nth-child(3)').text().toLowerCase();
            const artist = $(this).find('td:nth-child(4)').text().toLowerCase();
            $(this).toggle(title.indexOf(query) > -1 || artist.indexOf(query) > -1);
        });
    });
});

function fetchGenres() {
    $.ajax({
        url: `${genreBaseUrl}/get-all`,
        type: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        success: (response) => {
            existingGenres = response.data;
            const dropdowns = $('.genre-dropdown').empty().append('<option value="">Select Genre</option>');
            existingGenres.forEach(genre => {
                dropdowns.append(`<option value="${genre.id}">${genre.name}</option>`);
            });
        },
        error: (error) => {
            let msg = "Get genre failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function addGenre() {
    const name = $('#genreName').val().trim();
    if (!name) return alert("Please enter a genre name.");

    $.ajax({
        url: `${genreBaseUrl}/add`,
        type: "POST",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        contentType: "application/json",
        data: JSON.stringify({ name: name }),
        success: () => {
            alert("Genre added!");
            $('#genreModal').modal('hide');
            $('#genreName').val('');
            fetchGenres(); // Refresh dropdowns
        },
        error: (error) => {
            let msg = "Add genre failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function previewFile(input, targetImgId) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = (e) => $(targetImgId).attr('src', e.target.result);
        reader.readAsDataURL(input.files[0]);
    }
}

function fetchArtists() {
    $.ajax({
        url: artistUrl,
        type: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        success: (response) => {
            existingArtists = response.data.map(a => a.name);
            const list = $('#artistList').empty();
            existingArtists.forEach(name => list.append(`<option value="${name}">`));
        },
        error: (error) => {
            let msg = "Get artists failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function fetchMusic() {
    $.ajax({
        url: `${baseUrl}/get-all`,
        type: "GET",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        success: function(response) {
            const tbody = $('#musicTable tbody').empty();
            response.data.forEach(music => {
                tbody.append(`
          <tr>
              <td class="ps-3 text-muted">#${music.id}</td>
              <td><img src="${baseUrl}/thumbnail/${music.id}" class="rounded border" style="width: 40px; height: 40px; object-fit: cover;" onerror="this.src='${placeholderThumb}'"></td>
              <td class="fw-bold text-dark">${music.musicTitle}</td>
              <td class="text-secondary">${music.musicArtist}</td>
              <td class="text-end pe-3">
                  <button class="btn btn-sm btn-outline-primary border-0" onclick="updateMusic(${music.id}, '${music.musicTitle.replace(/'/g, "\\'")}', '${music.musicArtist.replace(/'/g, "\\'")}', ${music.genreId || 'null'})"><i class="bi bi-pencil"></i></button>
                  <button class="btn btn-sm btn-outline-danger border-0 ms-2" onclick="deleteMusic(${music.id})"><i class="bi bi-trash"></i></button>
              </td>
          </tr>
        `);
            });
        },
        error: (error) => {
            let msg = "Get songs failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function uploadMusic() {
    const artist = $('#musicArtist').val();
    const genreId = $('#musicGenre').val();

    if (!existingArtists.includes(artist)) return alert("Artist does not exist.");
    if (!genreId) return alert("Please select a genre.");

    let formData = new FormData();
    formData.append("musicFile", $('#musicFile')[0].files[0]);
    formData.append("thumbnail", $('#thumbnailFile')[0].files[0]);
    formData.append("musicTitle", $('#musicTitle').val());
    formData.append("musicArtist", artist);
    formData.append("genreId", genreId);

    $.ajax({
        url: `${baseUrl}/upload`,
        type: "POST",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        data: formData,
        processData: false,
        contentType: false,
        success: () => {
            $('#musicTitle').val('');
            $('#musicArtist').val('');
            $('#musicGenre').val('');
            $('#musicFile').val('');
            $('#thumbnailFile').val('');
            $('#uploadModal').modal('hide');
            fetchMusic();
        },
        error: (error) => {
            let msg = "Upload music failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function updateMusic(id, title, artist, genreId) {
    $('#updateMusicId').val(id);
    $('#updateMusicTitle').val(title);
    $('#updateMusicArtist').val(artist);
    $('#updateMusicGenre').val(genreId || "");
    $('#updateThumbPreview').attr('src', `${baseUrl}/thumbnail/${id}?t=${new Date().getTime()}`);
    new bootstrap.Modal(document.getElementById('updateModal')).show();
}

function submitUpdate() {
    const id = $('#updateMusicId').val();
    let formData = new FormData();
    formData.append("musicTitle", $('#updateMusicTitle').val());
    formData.append("musicArtist", $('#updateMusicArtist').val());
    formData.append("genreId", $('#updateMusicGenre').val());

    if ($('#updateMusicFile')[0].files[0]) formData.append("musicFile", $('#updateMusicFile')[0].files[0]);
    if ($('#updateThumbnailFile')[0].files[0]) formData.append("thumbnail", $('#updateThumbnailFile')[0].files[0]);

    $.ajax({
        url: `${baseUrl}/update/${id}`,
        type: "PUT",
        headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
        data: formData,
        processData: false,
        contentType: false,
        success: () => {
            bootstrap.Modal.getInstance(document.getElementById('updateModal')).hide();
            fetchMusic();
        },
        error: (error) => {
            let msg = "Update music failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function deleteMusic(id) {
    if (confirm("Delete this track?")) {
        $.ajax({
            url: `${baseUrl}/delete/${id}`,
            type: "DELETE",
            headers: { "Authorization": "Bearer " + localStorage.getItem("token") },
            success: () => fetchMusic(),
            error: (error) => {
                let msg = "Delete music failed"
                if (error.responseJSON) {
                    msg = error.responseJSON.data || error.responseJSON.message;
                }
                alert(msg);
            }
        });
    }
}

function logout() {
    localStorage.clear();
    window.location.href = "sign-in.html";
}