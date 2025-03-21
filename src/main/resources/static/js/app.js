document.addEventListener('DOMContentLoaded', function() {
    const profileCard = document.getElementById('profile-card');
    const swipeLeftBtn = document.getElementById('swipe-left');
    const swipeRightBtn = document.getElementById('swipe-right');
    let touchStartX = 0;
    let touchEndX = 0;
    let isDragging = false;
    let currentX = 0;

    swipeLeftBtn.addEventListener('click', function() {
        swipeLeft();
    });

    swipeRightBtn.addEventListener('click', function() {
        swipeRight();
    });

    profileCard.addEventListener('touchstart', function(e) {
        touchStartX = e.changedTouches[0].screenX;
    }, false);

    profileCard.addEventListener('touchend', function(e) {
        touchEndX = e.changedTouches[0].screenX;
        handleSwipeGesture();
    }, false);

    profileCard.addEventListener('mousedown', function(e) {
        isDragging = true;
        touchStartX = e.clientX;
        profileCard.style.transition = 'none';
    });

    document.addEventListener('mousemove', function(e) {
        if (!isDragging) return;
        currentX = e.clientX - touchStartX;

        // Limit the drag
        if (Math.abs(currentX) > 200) {
            currentX = currentX > 0 ? 200 : -200;
        }

        profileCard.style.transform = `translateX(${currentX}px) rotate(${currentX * 0.1}deg)`;

        // Change opacity based on drag distance
        if (currentX > 0) {
            // Right swipe - green for like
            profileCard.style.boxShadow = `0 0 ${Math.abs(currentX) / 10}px rgba(40, 167, 69, ${Math.abs(currentX) / 200})`;
        } else {
            // Left swipe - red for dislike
            profileCard.style.boxShadow = `0 0 ${Math.abs(currentX) / 10}px rgba(220, 53, 69, ${Math.abs(currentX) / 200})`;
        }
    });

    document.addEventListener('mouseup', function() {
        if (!isDragging) return;
        isDragging = false;
        touchEndX = currentX + touchStartX;
        handleSwipeGesture();
    });

    loadNextProfile();

    function handleSwipeGesture() {
        const swipeDistance = touchEndX - touchStartX;

        if (swipeDistance > 100) {
            swipeRight();
        } else if (swipeDistance < -100) {
            swipeLeft();
        } else {
            resetCard();
        }
    }

    function swipeLeft() {
        profileCard.style.transition = 'transform 0.5s ease';
        profileCard.style.transform = 'translateX(-1000px) rotate(-30deg)';
        sendSwipeToServer(false);
        setTimeout(loadNextProfile, 500);
    }

    function swipeRight() {
        profileCard.style.transition = 'transform 0.5s ease';
        profileCard.style.transform = 'translateX(1000px) rotate(30deg)';
        sendSwipeToServer(true);
        setTimeout(loadNextProfile, 500);
    }

    function resetCard() {
        profileCard.style.transition = 'transform 0.5s ease';
        profileCard.style.transform = 'translateX(0) rotate(0)';
        profileCard.style.boxShadow = 'none';
    }

    function sendSwipeToServer(isLike) {
        const profileId = profileCard.dataset.profileId || 1;
        fetch('/api/swipe', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                targetUserId: profileId,
                like: isLike
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.match && isLike) {
                    showMatchNotification(profileId);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
    function showNoMoreProfilesMessage() {
        const container = document.querySelector('.container');
        container.innerHTML = `
        <div class="text-center h-100 d-flex flex-column justify-content-center align-items-center">
            <i class="bi bi-search-heart display-2 mb-3"></i>
            <h3>No more profiles available</h3>
            <p>We're finding more matches for you!</p>
        </div>
    `;
    }

    function loadNextProfile() {
        fetch('/api/next-profile')
            .then(response => {
                if (response.status === 204) {
                    showNoMoreProfilesMessage();
                    return null;
                }
                return response.json();
            })
            .then(data => {
                if (!data) return;
                resetCard();
                // Update card with new profile data
                const card = document.getElementById('profile-card');
                card.dataset.profileId = data.id;
                document.querySelector(".userAvatar").style.backgroundImage = `url('${data.mainPhotoUrl}')`;
                writeAllElements("target_name", data.name);
                writeAllElements("target_age", data.age);

                if (data.height) writeAllElements("target_height", data.height + " cm");
                else hideAllElements("target_height");

                if (data.religion) writeAllElements("target_religion", data.religion);
                else hideAllElements("target_religion");

                if (data.occupation) writeAllElements("target_occupation", `Work at <b>${data.occupation}</b>`)
                else hideAllElements("target_occupation");

                if (data.education) writeAllElements("target_education", `Study at <b>${data.education}</b>`);
                else hideAllElements("target_education");
                writeAllElements("target_bio", '<i class="bi bi-quote me-2 lh-1"></i>' + (data.bio || 'No bio')
            )
                ;
            })
            .catch(error => {
                console.error('Error loading next profile:', error);
            });
    }

    function showMatchNotification(profileId) {
        document.querySelector("#goToChatButton").href = "/chat/" + profileId;
        $("#matchModalToggle").modal("show");

    }

    function writeAllElements(className, content) {
        const elements = document.getElementsByClassName(className);
        for (let i = 0; i < elements.length; i++) {
            elements[i].classList.remove("d-none");
            elements[i].innerHTML = content;
        }
    }

    function hideAllElements(className) {
        const elements = document.getElementsByClassName(className);
        for (let i = 0; i < elements.length; i++) {
            elements[i].classList.add("d-none");
        }
    }
});