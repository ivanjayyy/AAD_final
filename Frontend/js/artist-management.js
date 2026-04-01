const baseUrl = "http://localhost:8080/api/v1/artist";
const sidebar = new bootstrap.Offcanvas(document.getElementById('adminSidebar'));
const placeholderSVG = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='150' height='150' viewBox='0 0 24 24' fill='none' stroke='%23adb5bd' stroke-width='1' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'%3E%3C/path%3E%3Ccircle cx='12' cy='7' r='4'%3E%3C/circle%3E%3C/svg%3E";

$('#admin-sidebar-sensor').on('mouseenter', () => sidebar.show());

$(document).ready(() => {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
    }
    fetchArtists()
});

function fetchArtists() {
    $.ajax({
        url: `${baseUrl}/get-all`,
        type: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        success: function (response) {
            const tbody = $('#artistTable tbody').empty();
            response.data.forEach(artist => {
                tbody.append(`
            <tr>
              <td class="ps-3 text-muted">#${artist.id}</td>
              <td>
                <img src="${baseUrl}/profile-pic/${artist.id}" class="profile-img-table"
                     onerror="this.src='${placeholderSVG}'">
              </td>
              <td class="fw-bold text-dark">${artist.name}</td>
              <td class="text-secondary small bio-truncate">${artist.bio}</td>
              <td class="text-end pe-3">
                <button class="btn btn-sm btn-outline-primary border-0" onclick="updateArtist(${artist.id}, \`${artist.name}\`, \`${artist.bio}\`)">
                  <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger border-0 ms-1" onclick="deleteArtist(${artist.id})">
                  <i class="bi bi-trash"></i>
                </button>
              </td>
            </tr>
          `);
            });
        }
    });
}

function saveArtist() {
    const name = $('#artistName').val();
    const bio = $('#artistBio').val();
    const pic = $('#profilePic')[0].files[0];

    if(!name || !bio) return alert("Fill in name and biography.");

    let formData = new FormData();
    formData.append("name", name);
    formData.append("bio", bio);
    if(pic) formData.append("profilePic", pic);

    $.ajax({
        url: `${baseUrl}/add`,
        type: "POST",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        data: formData,
        processData: false,
        contentType: false,
        success: (response) => {
            alert("Artist added successfully!");
            $('#artistName, #artistBio, #profilePic').val('');
            $('#previewImage').attr('src', placeholderSVG);
            $('#addArtistModal').modal('hide');
            fetchArtists();
        },
        error: (error) => {
            let msg = "Add failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function updateArtist(id, name, bio) {
    $('#updateArtistId').val(id);
    $('#updateArtistName').val(name);
    $('#updateArtistBio').val(bio);
    // Load existing picture into the preview
    $('#updatePreviewImage').attr('src', `${baseUrl}/profile-pic/${id}?t=${new Date().getTime()}`);
    new bootstrap.Modal(document.getElementById('updateArtistModal')).show();
}

function submitUpdate() {
    const id = $('#updateArtistId').val();
    let formData = new FormData();
    formData.append("name", $('#updateArtistName').val());
    formData.append("bio", $('#updateArtistBio').val());
    const pFile = $('#updateProfilePic')[0].files[0];
    if (pFile) formData.append("profilePic", pFile);

    $.ajax({
        url: `${baseUrl}/update/${id}`,
        type: "PUT",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        data: formData,
        processData: false,
        contentType: false,
        success: () => {
            alert("Artist updated successfully!");
            bootstrap.Modal.getInstance(document.getElementById('updateArtistModal')).hide();
            $('#updateProfilePic').val('');
            fetchArtists();
        },
        error: (error) => {
            let msg = "Update failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function deleteArtist(id) {
    if (confirm("Delete this artist?")) {
        $.ajax({ url: `${baseUrl}/delete/${id}`, type: "DELETE", headers: {
                "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
            },success: () => {
                alert("Artist deleted successfully!");
                fetchArtists()
            },
            error: (error) => {
                let msg = "Delete failed"
                if (error.responseJSON) {
                    msg = error.responseJSON.data || error.responseJSON.message;
                }
                alert(msg);
            }});
    }
}

// Real-time Search
$('#artistSearch').on('keyup', function() {
    const value = $(this).val().toLowerCase();
    $('#artistTable tbody tr').filter(function() {
        $(this).toggle($(this).find('td:eq(2)').text().toLowerCase().indexOf(value) > -1);
    });
});

// Image Preview Handler
function handleImagePreview(input, targetImgId) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = (e) => $(targetImgId).attr('src', e.target.result);
        reader.readAsDataURL(input.files[0]);
    }
}

$('#addArtistModal').on('hidden.bs.modal', function () {
    $('#artistName').val('');
    $('#artistBio').val('');
    $('#profilePic').val('');
    $('#previewImage').attr('src', placeholderSVG);
});

function logout() {
    localStorage.clear();
    window.location.href = "sign-in.html";
}