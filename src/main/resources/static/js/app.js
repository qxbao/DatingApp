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
                const avatar = card.querySelector('.userAvatar');
                const name = card.querySelector('#name');
                const age = card.querySelector('#age');
                const religion = card.querySelector('#religion');
                const height = card.querySelector('#height');
                const education = card.querySelector('#education');
                const occupation = card.querySelector('#occupation');
                const bio = card.querySelector('#bio');

                // Set profile ID for next swipe action
                card.dataset.profileId = data.id;

                // Update profile photo
                avatar.style.backgroundImage = `url('${data.mainPhotoUrl}')`;
                name.textContent = data.name;
                age.textContent = data.age;
                handleNullableElements(height, data.height, " cm", "badge bg-secondary text-light");
                handleNullableElements(religion, data.religion, "", "badge bg-success text-light");
                if (data.occupation) {
                    occupation.className = "small mb-1";
                    occupation.innerHTML = `Working at <span class="fw-bold">${data.occupation}</span>`;
                } else {
                    occupation.className = "small mb-1 d-none";
                }
                if (data.education) {
                    education.className = "small mb-1";
                    education.innerHTML = `Studying at <span class="fw-bold">${data.education}</span>`;
                } else {
                    education.className = "small mb-1 d-none";
                }
                bio.textContent = data.bio || 'No bio';
            })
            .catch(error => {
                console.error('Error loading next profile:', error);
            });
    }

    function showMatchNotification(profileId) {
        document.querySelector("#goToChatButton").href = "/chat/" + profileId;
        $("#matchModalToggle").modal("show");

    }

    function handleNullableElements(element, value, postfix, rootClasses) {
        if (value) {
            element.className = rootClasses;
            element.textContent = value + postfix;
        } else {
            element.className = `${rootClasses} d-none`;
        }
    }
});