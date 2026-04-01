const apiBase = "http://localhost:8080/api/v1";
const audio = document.getElementById("audioPlayer");
const token = localStorage.getItem("token");
const payload = token ? JSON.parse(atob(token.split('.')[1])) : { sub: "Guest", id: null };
const username = payload.sub;
const userId = payload.id;

let artistList = [];
let currentArtistIndex = -1;

let currentArtistId = null;
let artistSongs = [];
let shuffledList = [];
let shuffleMode = false;
let currentMusicId = null;
let currentIndex = -1;

$(document).ready(function() {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
    }
    const artistData = localStorage.getItem("selectedArtist");
    if (!artistData) {
        window.location.href = localStorage.getItem("backLocation" || "artists-page.html");
        return;
    }

    const backPage = localStorage.getItem("backLocation") || "artists-page.html";
    $(".back-btn").attr("href", backPage);

    const artist = JSON.parse(artistData);
    currentArtistId = artist.id;

    const storedList = localStorage.getItem("artistList");
    if (storedList) {
        artistList = JSON.parse(storedList);
        currentArtistIndex = artistList.findIndex(a => a.id === currentArtistId);
        if (currentArtistIndex === -1) {
            window.location.href = "artists-page.html";
        }
    }

    $("#artistName").text(artist.name);
    $("#artistBio").text(artist.bio);
    $("#artistImg").attr("src", `${apiBase}/artist/profile-pic/${artist.id}`);
    document.title = `HarmoniQ | ${artist.name}`;

    fetchArtistSongs(artist.id, artist.name);
    checkFollowStatus(); // Load follow status on page load
    initPlaylistPopover();
});

$.ajaxSetup({
    beforeSend: function(xhr) {
        if (token) xhr.setRequestHeader('Authorization', 'Bearer ' + token);
    }
});

// --- FOLLOW LOGIC ---
function checkFollowStatus() {
    if (!token || !currentArtistId) return;

    $.ajax({
        url: `${apiBase}/follow-artist/check`,
        type: "POST",
        contentType: "application/json",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: JSON.stringify({ userId: userId, artistId: currentArtistId }),
        success: function(response) {
            updateFollowUI(response.data === "Following");
        },
        error: function() {
            console.error("Failed to check follow status.");
        }
    });
}

function toggleFollow() {
    if (!token || !userId || !currentArtistId) {
        alert("Please sign in to follow artists.");
        return;
    }

    $.ajax({
        url: `${apiBase}/follow-artist/add`,
        type: "POST",
        contentType: "application/json",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: JSON.stringify({
            userId: userId,
            artistId: currentArtistId
        }),
        success: function(response) {
            const isFollowing = response.data === "Followed Artist";
            updateFollowUI(isFollowing);
        },
        error: (error) => {
            let msg = "Follow Artist failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function updateFollowUI(isFollowing) {
    const btn = $("#followBtn");
    if (isFollowing) {
        btn.removeClass("btn-outline-light").addClass("btn-light text-primary");
        btn.html('<i class="bi bi-person-check-fill me-2"></i>Following');
    } else {
        btn.removeClass("btn-light text-primary").addClass("btn-outline-light");
        btn.html('<i class="bi bi-person-plus-fill me-2"></i>Follow');
    }
}

function nextArtist() {
    if (artistList.length === 0) return;

    currentArtistIndex = (currentArtistIndex + 1) % artistList.length;
    switchArtist();
}

function prevArtist() {
    if (artistList.length === 0) return;

    currentArtistIndex = (currentArtistIndex - 1 + artistList.length) % artistList.length;
    switchArtist();
}

function switchArtist() {
    const artist = artistList[currentArtistIndex];

    currentArtistId = artist.id;

    $("#artistName").text(artist.name);
    $("#artistBio").text(artist.bio);
    $("#artistImg").attr("src", `${apiBase}/artist/profile-pic/${artist.id}`);

    fetchArtistSongs(artist.id, artist.name);
    checkFollowStatus();
}

// --- MUSIC FETCHING ---
function fetchArtistSongs(artistId, artistName) {
    $.get(`${apiBase}/music/get-all`, (response) => {
        artistSongs = response.data.filter(s => s.artistId == artistId || s.musicArtist === artistName);
        renderSongs(artistSongs);
    });
}

function renderSongs(songs) {
    const container = $("#songContainer");
    container.empty();
    if (songs.length === 0) {
        container.html('<div class="text-center text-muted py-5">No tracks found.</div>');
        return;
    }
    songs.forEach((song, index) => {
        container.append(`
                <div class="song-card d-flex align-items-center p-3 shadow-sm mb-2" onclick="playFromProfile(${index})">
                    <div class="me-3 text-muted fw-bold">${index + 1}</div>
                    <img src="${apiBase}/music/thumbnail/${song.id}" class="song-thumb me-3">
                    <div class="flex-grow-1">
                        <div class="fw-bold">${song.musicTitle}</div>
                        <div class="text-muted small">${song.musicArtist}</div>
                    </div>
                    <div class="text-primary fs-4"><i class="bi bi-play-circle-fill"></i></div>
                </div>
            `);
    });
}

// --- LIKE LOGIC ---
function checkLikeStatus() {
    if (!token || currentIndex === -1) return;
    const activeList = shuffleMode ? shuffledList : artistSongs;
    const musicId = activeList[currentIndex].id;

    $.ajax({
        url: `${apiBase}/liked-song/check-like`,
        type: "POST",
        contentType: "application/json",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
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
    if (!token || currentIndex === -1) return;
    const activeList = shuffleMode ? shuffledList : artistSongs;
    const musicId = activeList[currentIndex].id;

    $.ajax({
        url: `${apiBase}/liked-song/add-or-remove`,
        type: "POST",
        contentType: "application/json",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
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

// --- MUSIC PLAYER LOGIC ---
function playFromProfile(index) {
    if (shuffleMode) {
        const musicId = artistSongs[index].id;
        const shuffledIndex = shuffledList.findIndex(m => m.id === musicId);
        playSong(shuffledIndex);
    } else {
        playSong(index);
    }
}

function toggleShuffle() {
    shuffleMode = !shuffleMode;
    const btn = $("#shuffleBtn");
    if (shuffleMode) {
        btn.addClass("text-primary-important").removeClass("text-muted");
        shuffledList = [...artistSongs].sort(() => Math.random() - 0.5);
        if (currentMusicId) currentIndex = shuffledList.findIndex(m => m.id === currentMusicId);
    } else {
        btn.removeClass("text-primary-important").addClass("text-muted");
        if (currentMusicId) currentIndex = artistSongs.findIndex(m => m.id === currentMusicId);
    }
}

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

// Click icon to mute/unmute
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

function playSong(index) {
    const activeList = shuffleMode ? shuffledList : artistSongs;
    if (index < 0 || index >= activeList.length) return;

    currentIndex = index;
    const music = activeList[index];
    currentMusicId = music.id;

    audio.src = `${apiBase}/music/stream/${music.id}`;
    $("#playerTitle").text(music.musicTitle);
    $("#playerArtist").text(music.musicArtist);
    $("#playerThumbnail").attr("src", `${apiBase}/music/thumbnail/${music.id}`);

    $("#bottomPlayer").addClass("active");
    audio.play();
    $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill");

    checkLikeStatus();
}

function nextSong() {
    const activeList = shuffleMode ? shuffledList : artistSongs;
    let nextIdx = (currentIndex + 1) % activeList.length;
    playSong(nextIdx);
}

function prevSong() {
    const activeList = shuffleMode ? shuffledList : artistSongs;
    let prevIdx = (currentIndex - 1 + activeList.length) % activeList.length;
    playSong(prevIdx);
}

audio.onended = () => nextSong();

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

// --- PLAYLIST POPOVER ---
function initPlaylistPopover() {
    new bootstrap.Popover(document.getElementById('playlistBtn'), {
        html: true,
        sanitize: false,
        content: function() { return `<div class="text-center p-2"><div class="spinner-border spinner-border-sm text-primary"></div></div>`; }
    });

    document.getElementById('playlistBtn').addEventListener('shown.bs.popover', function () {
        $.get(`${apiBase}/playlist/load/${userId}`, (response) => {
            let html = '<div class="list-group list-group-flush">';
            if(!response.data || response.data.length === 0) {
                html += '<div class="p-3 text-muted small">No playlists found</div>';
            } else {
                response.data.forEach(pl => {
                    html += `<div class="playlist-item" onclick="addToPlaylist(${pl.id})">${pl.playlistName}</div>`;
                });
            }
            html += '</div>';
            document.querySelector('.popover-body').innerHTML = html;
        });
    });
}

function addToPlaylist(playlistId) {
    if (!currentMusicId) return;
    $.ajax({
        url: `${apiBase}/playlist-song/add`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ playlistId: playlistId, musicId: currentMusicId }),
        success: function() {
            alert("Song added!");
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

document.addEventListener("keydown", function(e) {
    if (e.key === "Escape") {
        window.location.href = localStorage.getItem("backLocation");
        return;
    }
});