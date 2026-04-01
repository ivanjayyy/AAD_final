const apiBase = "http://localhost:8080/api/v1";
const audio = document.getElementById("audioPlayer");
const token = localStorage.getItem("token");
const payload = token ? JSON.parse(atob(token.split('.')[1])) : { id: null, sub: "Guest" };
const userId = payload.id;
const username = payload.sub; // Needed for playlist loading

let likedSongs = [];
let currentIndex = -1;

$.ajaxSetup({
    beforeSend: function(xhr) {
        if (token) xhr.setRequestHeader('Authorization', 'Bearer ' + token);
    }
});

$(document).ready(function() {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }

    fetchLikedSongs();
    initPlaylistPopover();
});

function fetchLikedSongs() {
    $.get(`${apiBase}/liked-song/get-by-user/${userId}`, (response) => {
        likedSongs = response.data || [];
        $("#songCount").text(`${likedSongs.length} songs`);
        renderSongs();
    });
}

// --- PLAYLIST FEATURE LOGIC ---
function initPlaylistPopover() {
    const playlistBtn = document.getElementById('playlistBtn');
    const popover = new bootstrap.Popover(playlistBtn, {
        html: true,
        sanitize: false,
        content: function() {
            return `<div id="playlistPopoverContent" class="text-center p-2"><div class="spinner-border spinner-border-sm text-primary"></div></div>`;
        }
    });

    playlistBtn.addEventListener('shown.bs.popover', function () {
        $.get(`${apiBase}/playlist/load/${userId}`, (response) => {
            let html = '<div class="list-group list-group-flush" style="width: 200px; height: 200px; overflow-y: auto;">';
            if(!response.data || response.data.length === 0) {
                html += '<div class="p-2 text-muted small">No playlists found</div>';
            } else {
                response.data.forEach(pl => {
                    html += `<div class="playlist-item" onclick="addToPlaylist(${pl.id})">${pl.playlistName}</div>`;
                });
            }
            html += '</div>';
            const popoverBody = document.querySelector('.popover-body');
            if (popoverBody) popoverBody.innerHTML = html;
        });
    });
}

function addToPlaylist(playlistId) {
    if (currentIndex === -1) {
        alert("Please play a song first!");
        return;
    }
    const musicId = likedSongs[currentIndex].id;

    $.ajax({
        url: `${apiBase}/playlist-song/add`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ playlistId: playlistId, musicId: musicId }),
        success: function() {
            alert("Added to playlist! 🎶");
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

// --- VOLUME & PLAYER LOGIC ---
$('#volumeSlider').on('input', function() {
    const vol = $(this).val();
    audio.volume = vol;
    const icon = $('#volumeIcon');
    if (vol == 0) icon.removeClass('bi-volume-up bi-volume-down').addClass('bi-volume-mute');
    else if (vol < 0.5) icon.removeClass('bi-volume-up bi-volume-mute').addClass('bi-volume-down');
    else icon.removeClass('bi-volume-down bi-volume-mute').addClass('bi-volume-up');
});

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

function renderSongs() {
    const container = $("#likedSongsContainer");
    container.empty();
    if(likedSongs.length === 0) {
        container.html('<div class="text-center py-5 text-muted">You haven\'t liked any songs yet.</div>');
        return;
    }
    likedSongs.forEach((song, index) => {
        container.append(`
                <div class="song-row d-flex align-items-center p-3 mb-2" onclick="playSong(${index})">
                    <div class="col-1 text-muted">${index + 1}</div>
                    <div class="col-6 d-flex align-items-center">
                        <img src="${apiBase}/music/thumbnail/${song.id}" class="song-img me-3">
                        <span class="fw-bold">${song.musicTitle}</span>
                    </div>
                    <div class="col-4 text-muted">${song.musicArtist}</div>
                    <div class="col-1 text-end text-primary fs-5">
                        <i class="bi bi-play-circle-fill"></i>
                    </div>
                </div>
            `);
    });
}

function playSong(index) {
    if (index < 0 || index >= likedSongs.length) return;
    currentIndex = index;
    const song = likedSongs[index];

    audio.src = `${apiBase}/music/stream/${song.id}`;
    $("#playerTitle").text(song.musicTitle);
    $("#playerArtist").text(song.musicArtist);
    $("#playerThumbnail").attr("src", `${apiBase}/music/thumbnail/${song.id}`);

    $("#bottomPlayer").addClass("active");
    audio.play();
    $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill");
}

function playAll() { if (likedSongs.length > 0) playSong(0); }
function nextSong() { playSong((currentIndex + 1) % likedSongs.length); }
function prevSong() { playSong((currentIndex - 1 + likedSongs.length) % likedSongs.length); }

function likeSong() {
    if (currentIndex === -1) return;
    const musicId = likedSongs[currentIndex].id;
    $.ajax({
        url: `${apiBase}/liked-song/add-or-remove`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ userId: userId, musicId: musicId }),
        success: function() {
            fetchLikedSongs();
            closePlayer();
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

$("#playPauseBtn").on("click", () => {
    if(audio.paused) { audio.play(); $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill"); }
    else { audio.pause(); $("#playPauseBtn").removeClass("bi-pause-circle-fill").addClass("bi-play-circle-fill"); }
});

audio.ontimeupdate = () => {
    if(audio.duration) {
        let pct = (audio.currentTime / audio.duration) * 100;
        $("#progressBar").css("width", pct + "%");
    }
};

function seek(e) {
    const percent = e.offsetX / e.currentTarget.offsetWidth;
    audio.currentTime = percent * audio.duration;
}

document.addEventListener("keydown", function(e) {
    if (e.key === "Escape") {
        window.location.href = "user-home.html";
    }
});

function closePlayer() { audio.pause(); $("#bottomPlayer").removeClass("active"); }
audio.onended = () => nextSong();