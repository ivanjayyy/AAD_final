const audio = document.getElementById("audioPlayer");
const musicApi = "http://localhost:8080/api/v1/music";
const playlistApi = "http://localhost:8080/api/v1/playlist";
const playlistSongApi = "http://localhost:8080/api/v1/playlist-song";
const likedSongApi = "http://localhost:8080/api/v1/liked-song";

const token = localStorage.getItem("token");
const payload = token ? JSON.parse(atob(token.split('.')[1])) : { sub: "Guest", id: null };
const username = payload.sub;
const userId = payload.id;

let currentPlaylistSongs = [];
let shuffledSongs = [];
let isShuffle = false;
let currentPlayingMusicId = null;
let currentPlayingIndex = -1;
let activePlaylistId = null;

$(document).ready(function() {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }
    activePlaylistId = localStorage.getItem("selectedPlaylistId");
    const initialName = localStorage.getItem("selectedPlaylistName");
    if (!activePlaylistId) { window.location.href = "user-playlists.html"; return; }

    loadSidebarPlaylists(activePlaylistId);
    fetchPlaylistSongs(activePlaylistId, initialName);
});

// --- PLAYLIST MGMT ---
function loadSidebarPlaylists(activeId) {
    $.ajax({
        url: `${playlistApi}/load/${userId}`,
        type: "GET",
        headers: { "Authorization": "Bearer " + token },
        success: function(response) {
            const list = $("#sidebarPlaylistList").empty();
            response.data.forEach(pl => {
                const isActive = pl.id == activeId ? 'active' : '';
                list.append(`
            <div class="playlist-nav-item ${isActive}" onclick="switchPlaylist(${pl.id}, '${pl.playlistName}')">
              <span class="text-truncate"><i class="bi bi-music-note-list me-2"></i>${pl.playlistName}</span>
              <i class="bi bi-trash small delete-playlist-btn" onclick="event.stopPropagation(); deleteSpecificPlaylist(${pl.id})"></i>
            </div>
          `);
            });
        }
    });
}

function removeFromCurrentPlaylist() {
    if (!currentPlayingMusicId || !activePlaylistId) return;
    if (!confirm("Remove song from this playlist?")) return;
    $.ajax({
        url: `${playlistSongApi}/remove`,
        type: "DELETE",
        headers: { "Authorization": "Bearer " + token },
        contentType: 'application/json',
        data: JSON.stringify({ playlistId: activePlaylistId, musicId: currentPlayingMusicId }),
        success: function() {
            alert("Song removed.");
            fetchPlaylistSongs(activePlaylistId, localStorage.getItem("selectedPlaylistName"));
            closePlayer();
        },
        error: (error) => {
            let msg = "Remove song from playlist failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function deleteSpecificPlaylist(id) {
    if (!confirm("Delete this playlist?")) return;
    $.ajax({
        url: `${playlistApi}/delete/${id}`,
        type: "DELETE",
        headers: { "Authorization": "Bearer " + token },
        success: function() { window.location.href = "user-playlists.html"; }
    });
}

function switchPlaylist(id, name) {
    activePlaylistId = id;
    localStorage.setItem("selectedPlaylistId", id);
    localStorage.setItem("selectedPlaylistName", name);
    fetchPlaylistSongs(id, name);
    loadSidebarPlaylists(id);
}

// --- LIKE LOGIC ---
function checkLikeStatus() {
    if (!token || currentPlayingIndex === -1) return;
    const musicId = isShuffle ? shuffledSongs[currentPlayingIndex].id : currentPlaylistSongs[currentPlayingIndex].id;

    $.ajax({
        url: `${likedSongApi}/check-like`,
        type: "POST",
        headers: { "Authorization": "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify({ userId: userId, musicId: musicId }),
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
    if (!token || currentPlayingIndex === -1) return;
    const musicId = isShuffle ? shuffledSongs[currentPlayingIndex].id : currentPlaylistSongs[currentPlayingIndex].id;

    $.ajax({
        url: `${likedSongApi}/add-or-remove`,
        type: "POST",
        headers: { "Authorization": "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify({ userId: userId, musicId: musicId }),
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

// --- PLAYBACK LOGIC ---
function fetchPlaylistSongs(playlistId, playlistName) {
    $("#activePlaylistName").text(playlistName);
    $.ajax({
        url: `${playlistSongApi}/get-all/${playlistId}`,
        type: "GET",
        headers: { "Authorization": "Bearer " + token },
        success: function(response) {
            currentPlaylistSongs = response.data || [];
            const container = $("#musicContainer").empty();
            $("#songCount").text(`${currentPlaylistSongs.length} Tracks found`);

            if (currentPlaylistSongs.length === 0) {
                container.html('<div class="col-12 text-center py-5 text-muted"><p>Playlist is empty.</p></div>');
                return;
            }

            currentPlaylistSongs.forEach((music, index) => {
                container.append(`
            <div class="music-card" onclick="playFromGrid(${index})">
              <img src="${musicApi}/thumbnail/${music.id}" class="music-thumbnail" onerror="this.src='https://via.placeholder.com/180'">
              <div class="music-title text-truncate fw-bold">${music.musicTitle}</div>
              <div class="music-artist text-truncate text-muted small">${music.musicArtist}</div>
            </div>
          `);
            });
        }
    });
}

function playFromGrid(index) {
    if (isShuffle) {
        const musicId = currentPlaylistSongs[index].id;
        const shuffleIdx = shuffledSongs.findIndex(s => s.id === musicId);
        playSong(shuffleIdx);
    } else {
        playSong(index);
    }
}

function playSong(index) {
    const activeList = isShuffle ? shuffledSongs : currentPlaylistSongs;
    if (index < 0 || index >= activeList.length) return;

    currentPlayingIndex = index;
    const music = activeList[index];
    currentPlayingMusicId = music.id;

    audio.src = `${musicApi}/stream/${music.id}`;
    $("#playerTitle").text(music.musicTitle);
    $("#playerArtist").text(music.musicArtist);
    $("#playerThumbnail").attr("src", `${musicApi}/thumbnail/${music.id}`);

    $("#bottomPlayer").addClass("active");
    audio.play();
    $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill");

    checkLikeStatus();
}

function toggleShuffle() {
    isShuffle = !isShuffle;
    const btn = $("#shuffleBtn");
    if (isShuffle) {
        btn.addClass("shuffle-active").removeClass("text-muted");
        shuffledSongs = [...currentPlaylistSongs].sort(() => Math.random() - 0.5);
        if (currentPlayingMusicId) currentPlayingIndex = shuffledSongs.findIndex(s => s.id === currentPlayingMusicId);
    } else {
        btn.removeClass("shuffle-active").addClass("text-muted");
        if (currentPlayingMusicId) currentPlayingIndex = currentPlaylistSongs.findIndex(s => s.id === currentPlayingMusicId);
    }
}

function nextSong() {
    const activeList = isShuffle ? shuffledSongs : currentPlaylistSongs;
    let nextIdx = (currentPlayingIndex + 1) % activeList.length;
    playSong(nextIdx);
}

function prevSong() {
    const activeList = isShuffle ? shuffledSongs : currentPlaylistSongs;
    let prevIdx = (currentPlayingIndex - 1 + activeList.length) % activeList.length;
    playSong(prevIdx);
}

audio.onended = () => nextSong();

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

$("#playPauseBtn").on("click", () => {
    if(audio.paused) { audio.play(); $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill"); }
    else { audio.pause(); $("#playPauseBtn").removeClass("bi-pause-circle-fill").addClass("bi-play-circle-fill"); }
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

document.addEventListener("keydown", function(e) {
    if (e.key === "Escape") {
        window.location.href = "user-playlists.html";
        return;
    }
});

function seek(e) {
    const percent = e.offsetX / $(e.target).closest('.progress').width();
    audio.currentTime = percent * audio.duration;
}

function closePlayer() { audio.pause(); $("#bottomPlayer").removeClass("active"); }