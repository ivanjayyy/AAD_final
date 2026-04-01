const audio = document.getElementById("audioPlayer");
const sidebar = new bootstrap.Offcanvas(document.getElementById('navSidebar'));
const apiBase = "http://localhost:8080/api/v1";

const token = localStorage.getItem("token");
const payload = token ? JSON.parse(atob(token.split('.')[1])) : { sub: "Guest", id: null };
const username = payload.sub;
const userId = payload.id;

let musicList = [];
let followedArtistsSongs = []; // Global list
let recentListGlobal = [];
let likedListPreview = [];
let shuffledList = [];
let shuffleMode = false;
let currentMusicId = null;
let currentIndex = -1;
let currentActiveListName = "all"; // To track if playing from liked or all

$('#sidebar-sensor').on('mouseenter', () => sidebar.show());

$(document).ready(() => {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }

    startAI(userId);

    fetchInitialData();
    fetchFollowedArtistsSongs();
    fetchRecentSongs();
    fetchLikedSongs();
    fetchFollowingArtists()

    // Initial Greeting
    document.getElementById("messages").innerHTML +=
        "<p><b>HarmoniQ AI:</b> Hello! How can I help you Today?</p>";

    $("#input").on("keydown", function(e) {
        if(e.which === 13) {
            send();
        } else if (e.which === 27) {
            toggleChat();
        }
    });
});

function fetchInitialData() {
    // Fetch All Music
    $.ajax({
        url: `${apiBase}/music/get-all`,
        type: "GET",
        headers: { "Authorization": "Bearer " + token },
        success: function(response) {
            musicList = response.data;
            renderList("#musicContainer", musicList, "all");
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

function renderFollowedArtistsSongs(songs) {
    followedArtistsSongs = songs || [];
    renderList("#followedArtistsSongsContainer", followedArtistsSongs, "followed");
}

function fetchFollowedArtistsSongs() {
    if (!userId) return;

    $.ajax({
        url: `${apiBase}/follow-artist/random-songs/${userId}`, // adjust endpoint
        type: "GET",
        headers: { "Authorization": "Bearer " + token },
        contentType: "application/json",
        success: function(response) {
            renderFollowedArtistsSongs(response.data || []);
        },
        error: (error) => {
            let msg = "Get following artists' songs failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function fetchLikedSongs() {
    $.ajax({
        url: `${apiBase}/liked-song/get-by-user/${userId}`,
        type: "GET",
        headers: { "Authorization": "Bearer " + token },
        success: function(response) {
            likedListPreview = response.data || [];

            // Shuffle the list to pick random songs each time
            let randomPreview = [...likedListPreview];
            shuffleArray(randomPreview);

            likedListPreview = randomPreview.slice(0, 6);

            // Show only first 3 random songs for preview
            renderList("#likedSongsPreviewContainer", likedListPreview, "liked");
        },
        error: (error) => {
            let msg = "Get liked songs failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

// Toggle Chat Visibility
function toggleChat() {
    const chat = document.getElementById("chatWindow");
    chat.style.display = (chat.style.display === "none") ? "flex" : "none";
}

function startAI(userId) {
    fetch("http://localhost:8080/api/v1/chat/start", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId: userId }),
        error: (error) => {
            let msg = "Start AI failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function send() {
    let inputField = document.getElementById("input");
    let input = inputField.value;
    if (!input.trim()) return;

    const messagesDiv = document.getElementById("messages");

    // Show user message
    messagesDiv.innerHTML += `<p><b>${username}:</b> ${input}</p>`;
    inputField.value = "";

    // Placeholder for AI
    const aiMessageId = "ai-resp-" + Date.now();
    messagesDiv.innerHTML += `<p><b>HarmoniQ AI:</b> <span id="${aiMessageId}">Typing...</span></p>`;

    $.ajax({
        url: "http://localhost:8080/api/v1/chat/response",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ message: input }),

        success: function(response) {
            messagesDiv.scrollTop = messagesDiv.scrollHeight;

            // ✅ Detect [ACTION:PLAY_SONG(id_number)]
            const matchSong = response.match(/\[ACTION:PLAY_SONG\((\d+)\)\]/);
            const matchCreateWithSongs = response.match(/\[ACTION:CREATE_PLAYLIST_WITH_SONGS\((.+?)\|([\d,]+)\)\]/);
            const matchPlaylist = response.match(/\[ACTION:CREATE_PLAYLIST\(([^)]+)\)\]/);

            if (matchSong) {
                const songId = matchSong[1]; // extracted number
                console.log("PLAY SONG ID:", songId);
                playFromSpecificList(parseInt(songId) - 1, "all")
                document.getElementById(aiMessageId).innerText = "Playing song...";
            } else if (matchCreateWithSongs) {

                const playlistName = matchCreateWithSongs[1].trim();

                const songList = matchCreateWithSongs[2]
                    .split(',')
                    .map(id => parseInt(id.trim()));

                console.log("Playlist Name:", playlistName);
                console.log("Song IDs:", songList);

                createPlaylistUsingAI(playlistName, songList);
                document.getElementById(aiMessageId).innerText = "Creating playlist with songs...";

            } else if (matchPlaylist) {
                const playlistName = matchPlaylist[1].trim(); // extracted name
                console.log("CREATE PLAYLIST:", playlistName);
                const songList = [];
                createPlaylistUsingAI(playlistName, songList);
                document.getElementById(aiMessageId).innerText = "Creating playlist...";

            } else {
                document.getElementById(aiMessageId).innerText = response;
            }
        },
        error: (error) => {
            let msg = "Send msg to AI failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
            document.getElementById(aiMessageId).innerText = "Error occurred.";
        }
    });
}

function createPlaylistUsingAI(playlistName, songList){
    $.ajax({
        url: "http://localhost:8080/api/v1/playlist/create",
        method: 'POST',
        contentType: 'application/json',
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        data: JSON.stringify({ "playlistName": playlistName, "userId": userId }),
        success: function () {
            alert("Playlist created successfully!");

            if (songList) {
                getPlaylistId(playlistName, songList);
            }
        },
        error: (error) => {
            let msg = "Create playlist using AI failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function createAIPlaylistWithSongs(playlistId, songList) {
    songList.forEach(songId => {
        $.ajax({
            url: `${apiBase}/playlist-song/add`,
            type: "POST",
            headers: { "Authorization": "Bearer " + token },
            contentType: "application/json",
            data: JSON.stringify({
                playlistId: playlistId,
                musicId: songId
            }),
            error: (error) => {
                let msg = "Create playlist with songs using AI failed"
                if (error.responseJSON) {
                    msg = error.responseJSON.data || error.responseJSON.message;
                }
                alert(msg);
            }
        });
    });
}

function getPlaylistId(playlistName, songList) {
    return $.ajax({
        url: `${apiBase}/playlist/get-id/` + playlistName,
        type: "GET",
        headers: { "Authorization": "Bearer " + token },
        success: function(response) {
            const playlistId = response.data;
            console.log("Playlist ID:", playlistId);
            createAIPlaylistWithSongs(playlistId, songList);
        },
        error: (error) => {
            let msg = "Get playlist failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
}

// Update the play function to handle the preview list correctly
function playFromSpecificList(index, listType) {
    currentActiveListName = listType;

    if (listType === "liked") {
        // Since the preview is randomized and sliced, we play specifically from the preview items
        // But we keep the global likedListPreview as the queue
        const songToPlay = $(event.currentTarget); // Not ideal, better to use the specific random list
    }

    if (listType === "recent") {
        const songToPlay = $(event.currentTarget);
    }

    if (listType === "followed") {
        const songToPlay = $(event.currentTarget);
    }

    let targetList;

    if (listType === "liked") {
        targetList = likedListPreview;
    } else if (listType === "recent") {
        targetList = recentListGlobal; // we’ll define this
    } else if (listType === "followed")  {
        targetList = followedArtistsSongs;
    } else {
        targetList = musicList;
    }

    if (shuffleMode) {
        const musicId = targetList[index].id;
        shuffledList = [...targetList].sort(() => Math.random() - 0.5);
        const shuffledIndex = shuffledList.findIndex(m => m.id === musicId);
        playSong(shuffledIndex);
    } else {
        playSong(index);
    }
}

function renderList(selector, list, listType) {
    const container = $(selector);
    container.empty();
    if(list.length === 0) {
        container.append('<div class="col-12 text-muted small ps-3">No tracks found.</div>');
        return;
    }
    list.forEach((music, index) => {
        container.append(`
                <div class="col-12 col-md-6 col-lg-4">
                    <div class="d-flex align-items-center p-2 rounded music-card hover-bg cursor-pointer" onclick="playFromSpecificList(${index}, '${listType}')">
                        <img src="${apiBase}/music/thumbnail/${music.id}" class="rounded me-3 border" style="width: 45px; height: 45px; object-fit: cover;">
                        <div class="overflow-hidden">
                            <div class="fw-bold text-dark text-truncate small">${music.musicTitle}</div>
                            <div class="text-muted" style="font-size: 11px;">${music.musicArtist}</div>
                        </div>
                    </div>
                </div>
            `);
    });
}

let listenedSeconds = 0;
let lastTime = 0;
let recentSaved = false;

function playSong(index) {
    const activeList = shuffleMode ? shuffledList : (currentActiveListName === "liked" ? likedListPreview : (currentActiveListName === "followed" ? followedArtistsSongs : (currentActiveListName === "recent" ? recentListGlobal :  musicList)));
    if (index < 0 || index >= activeList.length) return;

    currentIndex = index;
    const music = activeList[index];
    currentMusicId = music.id;

    listenedSeconds = 0;
    lastTime = 0;
    recentSaved = false;

    audio.src = `${apiBase}/music/stream/${music.id}`;
    audio.play();

    $("#playerTitle").text(music.musicTitle);
    $("#playerArtist").text(music.musicArtist);
    $("#playerThumbnail").attr("src", `${apiBase}/music/thumbnail/${music.id}`);

    $("#bottomPlayer").addClass("active");
    audio.play();
    $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill");

    checkLikeStatus();
}

function checkLikeStatus() {
    if (!token || currentIndex === -1) return;
    const activeList = shuffleMode ? shuffledList : (currentActiveListName === "liked" ? likedListPreview : (currentActiveListName === "followed" ? followedArtistsSongs : (currentActiveListName === "recent" ? recentListGlobal :  musicList)));
    const musicId = activeList[currentIndex].id;

    $.ajax({
        url: `${apiBase}/liked-song/check-like`,
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
        },
        error: (error) => {
            let msg = "Check like status failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
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

function likeSong() {
    if (!token || currentIndex === -1) return;
    const activeList = shuffleMode ? shuffledList : (currentActiveListName === "liked" ? likedListPreview : (currentActiveListName === "followed" ? followedArtistsSongs : (currentActiveListName === "recent" ? recentListGlobal :  musicList)));
    const musicId = activeList[currentIndex].id;

    $.ajax({
        url: `${apiBase}/liked-song/add-or-remove`,
        type: "POST",
        headers: { "Authorization": "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify({ userId: userId, musicId: musicId }),
        success: function(response) {
            const btn = $('#likeBtn');
            if (response.data === "Liked Song") {
                btn.removeClass("bi-heart text-muted").addClass("bi-heart-fill text-danger liked");
            } else {
                btn.removeClass("bi-heart-fill text-danger liked").addClass("bi-heart text-muted");
            }
            fetchLikedSongs();
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

function toggleShuffle() {
    shuffleMode = !shuffleMode;
    const btn = $("#shuffleBtn");
    const baseList = currentActiveListName === "liked" ? likedListPreview : (currentActiveListName === "followed" ? followedArtistsSongs : (currentActiveListName === "recent" ? recentListGlobal :  musicList));

    if (shuffleMode) {
        btn.addClass("text-primary-important").removeClass("text-muted");
        shuffledList = [...baseList].sort(() => Math.random() - 0.5);
        if (currentMusicId) currentIndex = shuffledList.findIndex(m => m.id === currentMusicId);
    } else {
        btn.removeClass("text-primary-important").addClass("text-muted");
        if (currentMusicId) currentIndex = baseList.findIndex(m => m.id === currentMusicId);
    }
}

function nextSong() {
    const activeList = shuffleMode ? shuffledList : (currentActiveListName === "liked" ? likedListPreview : (currentActiveListName === "followed" ? followedArtistsSongs : (currentActiveListName === "recent" ? recentListGlobal :  musicList)));
    let nextIdx = (currentIndex + 1) % activeList.length;
    playSong(nextIdx);
}

function prevSong() {
    const activeList = shuffleMode ? shuffledList : (currentActiveListName === "liked" ? likedListPreview : (currentActiveListName === "followed" ? followedArtistsSongs : (currentActiveListName === "recent" ? recentListGlobal :  musicList)));
    let prevIdx = (currentIndex - 1 + activeList.length) % activeList.length;
    playSong(prevIdx);
}

audio.onended = () => nextSong();

// Player UI Helpers
$("#playPauseBtn").on("click", () => {
    if(audio.paused) {
        audio.play();
        $("#playPauseBtn").removeClass("bi-play-circle-fill").addClass("bi-pause-circle-fill");
    } else {
        audio.pause();
        $("#playPauseBtn").removeClass("bi-pause-circle-fill").addClass("bi-play-circle-fill");
    }
});

audio.onseeked = () => {
    // Reset lastTime so skip doesn't count as listening
    lastTime = audio.currentTime;
};

audio.onpause = () => {
    lastTime = audio.currentTime;
}

audio.onplay = () => {
    lastTime = audio.currentTime;
};

audio.ontimeupdate = () => {
    if(!audio.duration) return;

    let current = audio.currentTime;
    // Only count small continuous playback (no skipping)
    let delta = current - lastTime;

    if (delta > 0 && delta < 1) {
        listenedSeconds += delta;
    }

    lastTime = current;

    // UI update
    let pct = (audio.currentTime / audio.duration) * 100;
    $("#progressBar").css("width", pct + "%");

    let mins = Math.floor(audio.currentTime / 60);
    let secs = Math.floor(audio.currentTime % 60);
    $("#currentTime").text(`${mins}:${secs < 10 ? '0' : ''}${secs}`);

    if (!recentSaved && listenedSeconds >= audio.duration / 2) {
        saveRecentSong(userId, currentMusicId);
        recentSaved = true;
    }
};

function seek(e) {
    const percent = e.offsetX / e.currentTarget.offsetWidth;
    audio.currentTime = percent * audio.duration;
}

// Popover Logic
const playlistPopover = new bootstrap.Popover(document.getElementById('playlistBtn'), {
    html: true,
    sanitize: false,
    content: function() { return `<div id="playlistPopoverContent" class="text-center p-2"><div class="spinner-border spinner-border-sm text-primary"></div></div>`; }
});

document.getElementById('playlistBtn').addEventListener('shown.bs.popover', function () {
    $.ajax({
        url: `${apiBase}/playlist/load/${userId}`,
        type: "GET",
        headers: { "Authorization": "Bearer " + token },
        success: function(response) {
            let html = '<div class="list-group list-group-flush">';
            if(!response.data || response.data.length === 0) {
                html += '<div class="p-3 text-muted small">No playlists found</div>';
            } else {
                response.data.forEach(pl => {
                    html += `<div class="playlist-item border-bottom" onclick="addToPlaylist(${pl.id})">${pl.playlistName}</div>`;
                });
            }
            html += '</div>';
            const popoverBody = document.querySelector('.popover-body');
            if (popoverBody) popoverBody.innerHTML = html;
        },
        error: (error) => {
            let msg = "Check like status failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
});

function addToPlaylist(playlistId) {
    if (!currentMusicId) return;
    $.ajax({
        url: `${apiBase}/playlist-song/add`,
        type: "POST",
        headers: { "Authorization": "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify({ playlistId: playlistId, musicId: currentMusicId }),
        success: function() {
            alert("Song added to playlist!");
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

let lastSavedMusicId = null;

function saveRecentSong(userId, musicId) {
    if (!userId || !musicId || lastSavedMusicId === musicId) return;

    lastSavedMusicId = musicId;

    $.ajax({
        url: `${apiBase}/recent-song/add`,
        type: "POST",
        headers: { "Authorization": "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify({ userId, musicId }),
        success: function() {
            fetchRecentSongs();
        },
        error: (error) => {
            let msg = "Save recent song failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function fetchRecentSongs() {
    if (!userId) return;

    $.ajax({
        url: `${apiBase}/recent-song/load-all/${userId}`,
        type: "GET",
        headers: { "Authorization": "Bearer " + token },
        success: function(response) {

            // 👉 THIS is what I meant by "update inside fetch"
            recentListGlobal = response.data || [];

            // show only few (optional)
            const limitedList = recentListGlobal.slice(0, 6);

            renderList("#recentSongContainer", limitedList, "recent");
        },
        error: (error) => {
            let msg = "Load recent songs failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function renderFollowingArtists(artists) {
    const container = $("#followingArtistContainer");
    container.empty();

    artists.forEach(a => {
        // We stringify the artist object so it can be stored in localStorage
        const artistData = JSON.stringify(a).replace(/'/g, "&apos;");

        container.append(`
                <div class="col-6 col-md-3 col-lg-2">
                <div class="artist-item" onclick='viewProfile(${artistData})'>
                <img src="${apiBase}/artist/profile-pic/${a.id}" class="artist-img" onerror="this.src='https://via.placeholder.com/100?text=Artist'">
                <div class="fw-bold small text-truncate">${a.name}</div>
        </div>
        </div>
        `);
    });
}

function fetchFollowingArtists() {
    $.ajax({
        url: "http://localhost:8080/api/v1/follow-artist/get-all/" + userId,
        type: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        success: function(response) {
            renderFollowingArtists(response.data.slice(0,6));
            localStorage.setItem("artistList",JSON.stringify(response.data));
        },
        error: (error) => {
            let msg = "Get following artists failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function viewProfile(artist) {
    localStorage.setItem("selectedArtist", JSON.stringify(artist));
    localStorage.setItem("backLocation", "user-home.html");
    window.location.href = "artist-profile.html";
}

function closePlayer() { audio.pause(); $("#bottomPlayer").removeClass("active"); }

function logout() { localStorage.clear(); window.location.href = "sign-in.html"; }