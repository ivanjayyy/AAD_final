const token = localStorage.getItem("token");
const payload = token ? JSON.parse(atob(token.split('.')[1])) : { sub: "Guest", id: null };
const userId = payload.id;

const sidebar = new bootstrap.Offcanvas(document.getElementById('navSidebar'));

// Reveal sidebar on left edge hover
$('#sidebar-sensor').on('mouseenter', () => sidebar.show());

$(document).ready(() => {
    if (localStorage.getItem("token") == null) {
        window.location.href = "sign-in.html";
        return;
    }
    fetchFollowingArtists();
});

function fetchArtists() {
    $.ajax({
        url: "http://localhost:8080/api/v1/artist/get-all",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        success: function(response) {
            renderArtists(response.data);
            localStorage.setItem("artistList", JSON.stringify(response.data));
        },
        error: (error) => {
            let msg = "Load Artists failed"
            if (error.responseJSON) {
                msg = error.responseJSON.data || error.responseJSON.message;
            }
            alert(msg);
        }
    });
}

function fetchFollowingArtists() {
    $.ajax({
        url: "http://localhost:8080/api/v1/follow-artist/get-all/" + userId,
        type: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token") // Is this line here?
        },
        success: function(response) {
            const followedIds = response.data.map(artist => artist.id);
            localStorage.setItem("followedArtists", JSON.stringify(followedIds));

            // fetchFamousArtists();
            fetchArtists();
        }
    });
}

function fetchFamousArtists() {
    return $.ajax({
        url: "http://localhost:8080/api/v1/follow-artist/get-famous-artists",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        success: function(response) {
            const famousIds = response.data.map(artist => artist.id);
            localStorage.setItem("famousArtists", JSON.stringify(famousIds));

            fetchArtists();
        }
    });
}

function renderArtists(artists) {
    const allContainer = $("#artistContainer");
    const followedContainer = $("#followedContainer");
    // const famousContainer = $("#famousContainer");

    allContainer.empty();
    followedContainer.empty();
    // famousContainer.empty();

    const followedIds = JSON.parse(localStorage.getItem("followedArtists")) || [];

    let hasFollowed = false;

    artists.forEach(artist => {
        const artistData = JSON.stringify(artist).replace(/'/g, "&apos;");

        const card = `
        <div class="col-12 col-sm-6 col-lg-4 col-xl-3">
            <div class="card h-100 artist-card shadow-sm text-center p-4"
                 style="cursor: pointer;"
                 onclick='viewProfile(${artistData})'>
                <div class="mb-3">
                    <img src="http://localhost:8080/api/v1/artist/profile-pic/${artist.id}"
                         class="rounded-circle profile-img mx-auto"
                         onerror="this.src='https://via.placeholder.com/150?text=Artist'">
                </div>
                <h5 class="fw-bold mb-1">${artist.name}</h5>
                <p class="bio-text mb-3 px-2">${artist.bio}</p>
                <div class="mt-auto">
                    <span class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle px-3 py-2">
                         View Profile
                    </span>
                </div>
            </div>
        </div>
        `;

        // If followed → go to top section
        if (followedIds.includes(artist.id)) {
            followedContainer.append(card);
            hasFollowed = true;
        } else {
            allContainer.append(card);
        }
    });

    // Show section only if user has followed artists
    if (hasFollowed) {
        $("#followedSection").removeClass("d-none");
    }
}

// Helper function to save to localStorage and redirect
function viewProfile(artist) {
    localStorage.setItem("selectedArtist", JSON.stringify(artist));
    localStorage.setItem("backLocation", "artists-page.html");
    window.location.href = "artist-profile.html";
}

function logout() {
    localStorage.clear();
    window.location.href = "sign-in.html";
}