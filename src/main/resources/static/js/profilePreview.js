document.addEventListener("DOMContentLoaded", function() {
    initPhotoCarousel();
    function initPhotoCarousel() {
        const carousel = document.getElementById('photoCarousel');
        if (!carousel) return;

        const photoUrls = JSON.parse(carousel.dataset.photos);
        updateCarouselPhoto(carousel);

        const indicatorsContainer = carousel.querySelector('.photo-indicators');
        photoUrls.forEach((_, index) => {
            const indicator = document.createElement('span');
            indicator.className = `indicator ${index === 0 ? 'active' : ''}`;
            indicator.dataset.index = index;
            indicator.addEventListener('click', () => {
                carousel.dataset.currentIndex = index;
                updateCarouselPhoto(carousel);
            });
            indicatorsContainer.appendChild(indicator);
        });

        const prevBtn = carousel.querySelector('.prev-photo');
        const nextBtn = carousel.querySelector('.next-photo');

        prevBtn.addEventListener('click', () => {
            const currentIndex = parseInt(carousel.dataset.currentIndex);
            const photos = JSON.parse(carousel.dataset.photos);
            const newIndex = (currentIndex - 1 + photos.length) % photos.length;
            carousel.dataset.currentIndex = newIndex;
            updateCarouselPhoto(carousel);
        });

        nextBtn.addEventListener('click', () => {
            const currentIndex = parseInt(carousel.dataset.currentIndex);
            const photos = JSON.parse(carousel.dataset.photos);
            const newIndex = (currentIndex + 1) % photos.length;
            carousel.dataset.currentIndex = newIndex;
            updateCarouselPhoto(carousel);
        });
    }

    function updateCarouselPhoto(carousel) {
        const photos = JSON.parse(carousel.dataset.photos);
        const currentIndex = parseInt(carousel.dataset.currentIndex);

        carousel.style.backgroundImage = `url('${photos[currentIndex]}')`;

        const indicators = carousel.querySelectorAll('.indicator');
        indicators.forEach((indicator, index) => {
            indicator.classList.toggle('active', index === currentIndex);
        });
    }
})