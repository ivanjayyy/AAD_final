const apiBase = "http://localhost:8080/api/v1";
const audio = document.getElementById("audioPlayer");
const sidebar = new bootstrap.Offcanvas(document.getElementById('navSidebar'));

const token = localStorage.getItem("token");
const payload = token ? JSON.parse(atob(token.split('.')[1])) : { sub: "Guest", id: null };
const username = payload.sub;
const userId = payload.id;

$.ajaxSetup({
    beforeSend: function(xhr) {
        if (token) xhr.setRequestHeader('Authorization', 'Bearer ' + token);
    }
});

let currentPlayingMusicId = null;
let allArtists = [];
let allSongs = [];

$('#sidebar-sensor').on('mouseenter', () => sidebar.show());

$(document).ready(function() {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }

    Promise.all([
        $.get(`${apiBase}/artist/get-all`),
        $.get(`${apiBase}/music/get-all`)
    ]).then(([artistRes, musicRes]) => {
        allArtists = artistRes.data;
        allSongs = musicRes.data;
    }).catch(err => {
        if(err.status === 403 || err.status === 401) logout();
    });

    $('#mainSearch').on('input', function() {
        const query = $(this).val().toLowerCase().trim();
        if (query === "") { resetUI(); return; }
        filterAndRender(query);
    });
});

// Volume Control Logic
$('#volumeSlider').on('input', function() {
    const vol = $(this).val();
    audio.volume = vol;

    // Update icon based on volume level
    const icon = $('#volumeIcon');
    if (vol == 0) {
        icon.removeClass('bi-volume-up bi-volume-down').addClass('bi-volume-mute');
    } else if (vol < 0.5) {
        icon.removeClass('bi-volume-up bi-volume-mute').addClass('bi-volume-down');
    } else {
        icon.removeClass('bi-volume-down bi-volume-mute').addClass('bi-volume-up');
    }
});

// Optional: Click icon to mute/unmute
$('#volumeIcon').on('click', function() {
    if (audio.volume > 0) {
        $(this).data('last-vol', audio.volume);
        audio.volume = 0;
        $('#volumeSlider').val(0);
        $(this).removeClass('bi-volume-up bi-volume-down').addClass('bi-volume-mute');
    } else {
        const lastVol = $(this).data('last-vol') || 1;
        audio.volume = lastVol;
        $('#volumeSlider').val(lastVol);
        $(this).removeClass('bi-volume-mute').addClass(lastVol < 0.5 ? 'bi-volume-down' : 'bi-volume-up');
    }
});

function resetUI() {
    $('#artistSection, #songSection, #noResults').hide();
    $('#emptyPrompt').show();
    $('#artistResults, #songResults').empty();
}

function filterAndRender(query) {
    const filteredArtists = allArtists.filter(a => a.name.toLowerCase().includes(query));
    localStorage.setItem("artistList",JSON.stringify(filteredArtists));
    const filteredSongs = allSongs.filter(s =>
        s.musicTitle.toLowerCase().includes(query) ||
        s.musicArtist.toLowerCase().includes(query)
    );

    $('#emptyPrompt').hide();

    if (filteredArtists.length > 0) {
        $('#artistSection').show();
        let html = '';
        filteredArtists.forEach((a) => {
            const artistData = JSON.stringify(a).replace(/'/g, "&apos;");
            html += `
                    <div class="col-6 col-md-3 col-lg-2">
                        <div class="artist-item" onclick='viewArtistProfile(${artistData})'>
                            <img src="${apiBase}/artist/profile-pic/${a.id}" class="artist-img" onerror="this.src='https://via.placeholder.com/100?text=Artist'">
                            <div class="fw-bold small text-truncate">${a.name}</div>
                        </div>
                    </div>`;
        });
        $('#artistResults').html(html);
    } else { $('#artistSection').hide(); }

    if (filteredSongs.length > 0) {
        $('#songSection').show();
        let html = '';
        filteredSongs.forEach(s => {
            html += `
                    <div class="song-row d-flex align-items-center" onclick="playSong('${s.id}', '${s.musicTitle.replace(/'/g, "\\'")}', '${s.musicArtist.replace(/'/g, "\\'")}')">
                        <img src="${apiBase}/music/thumbnail/${s.id}" class="song-img me-3" onerror="this.src='https://via.placeholder.com/60?text=Song'">
                        <div class="flex-grow-1">
                            <div class="fw-bold mb-0 text-dark">${s.musicTitle}</div>
                            <div class="text-muted small">${s.musicArtist}</div>
                        </div>
                        <div class="text-primary fs-4 opacity-75"><i class="bi bi-play-circle-fill"></i></div>
                    </div>`;
        });
        $('#songResults').html(html);
    } else { $('#songSection').hide(); }

    if (filteredArtists.length === 0 && filteredSongs.length === 0) { $('#noResults').show(); }
    else { $('#noResults').hide(); }
}

function viewArtistProfile(artist) {
    localStorage.setItem("selectedArtist", JSON.stringify(artist));
    localStorage.setItem("backLocation", "search-page.html");
    window.location.href = "artist-profile.html";
}

// --- NEW: LIKE FUNCTIONS ---
function checkLikeStatus() {
    if (!token || !currentPlayingMusicId) return;
    $.ajax({
        url: `${apiBase}/liked-song/check-like`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ userId: userId, musicId: currentPlayingMusicId }),
        success: function(response) {
            const btn = $('#likeBtn');
            if (response.data === "Liked") {
                btn.removeClass("bi-heart text-muted").addClass("bi-heart-fill text-danger liked");
            } else {
                btn.removeClass("bi-heart-fill text-danger liked").addClass("bi-heart text-muted");
            }
        }
    });
}

function likeSong() {
    if (!token || !currentPlayingMusicId) return;
    $.ajax({
        url: `${apiBase}/liked-song/add-or-remove`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ userId: userId, musicId: currentPlayingMusicId }),
        success: function(response) {
            if (response.status === 200 || response.code === 200) {
                const btn = $('#likeBtn');
                if (response.data === "Liked Song") {
                    btn.removeClass("bi-heart text-muted").addClass("bi-heart-fill text-danger liked");
                } else {
                    btn.removeClass("bi-heart-fill text-danger liked").addClass("bi-heart text-muted");
                }
            }
        },
        error: (error) => {
            let msg = "Like song failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function playSong(id, title, artist) {
    currentPlayingMusicId = id;
    audio.src = `${apiBase}/music/stream/${id}`;
    $("#playerTitle").text(title);
    $("#playerArtist").text(artist);
    $("#playerThumbnail").attr("src", `${apiBase}/music/thumbnail/${id}`);
    $("#bottomPlayer").addClass("active");
    audio.play();
    $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill");

    checkLikeStatus(); // Check if liked when song starts
}

// Popover Logic
const playlistPopover = new bootstrap.Popover(document.getElementById('playlistBtn'), {
    html: true,
    content: function() {
        return `<div id="playlistPopoverContent" class="text-center p-2"><div class="spinner-border spinner-border-sm text-primary"></div></div>`;
    }
});

document.getElementById('playlistBtn').addEventListener('shown.bs.popover', function () {
    $.get(`${apiBase}/playlist/load/${userId}`, (response) => {
        let html = '<div class="list-group list-group-flush" style="width: 200px; height: 200px; overflow-y: auto;">';
        if(!response.data || response.data.length === 0) {
            html += '<div class="p-3 text-muted small">No playlists found</div>';
        } else {
            response.data.forEach(pl => {
                html += `<div class="playlist-item p-2 hover-bg cursor-pointer border-bottom small" onclick="addToPlaylist(${pl.id})">${pl.playlistName}</div>`;
            });
        }
        html += '</div>';
        const popoverBody = document.querySelector('.popover-body');
        if (popoverBody) popoverBody.innerHTML = html;
    });
});

function addToPlaylist(playlistId) {
    if (!currentPlayingMusicId) return;
    $.ajax({
        url: `${apiBase}/playlist-song/add`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ playlistId: playlistId, musicId: currentPlayingMusicId }),
        success: function() {
            alert("Song added to playlist! 🎶");
            bootstrap.Popover.getInstance(document.getElementById('playlistBtn')).hide();
        },
        error: (error) => {
            let msg = "Add song to playlist failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function closePlayer() {
    audio.pause();
    $("#bottomPlayer").removeClass("active");
}

$("#playPauseBtn").on("click", () => {
    if(audio.paused) {
        audio.play();
        $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill");
    } else {
        audio.pause();
        $("#playPauseBtn").removeClass("bi-pause-circle-fill").addClass("bi-play-circle-fill");
    }
});

audio.ontimeupdate = () => {
    if(audio.duration) {
        let pct = (audio.currentTime / audio.duration) * 100;
        $("#progressBar").css("width", pct + "%");
        let mins = Math.floor(audio.currentTime / 60);
        let secs = Math.floor(audio.currentTime % 60);
        $("#currentTime").text(`${mins}:${secs < 10 ? '0' : ''}${secs}`);
    }
};

function seek(e) {
    const percent = e.offsetX / e.currentTarget.offsetWidth;
    audio.currentTime = percent * audio.duration;
}

function logout() {
    localStorage.clear();
    window.location.href = "sign-in.html";
}