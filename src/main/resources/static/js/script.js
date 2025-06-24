// Enhanced JavaScript for Random Videos 139 Website

document.addEventListener('DOMContentLoaded', function() {
    // Theme Toggle Functionality
    const themeToggle = document.getElementById('themeToggle');
    const body = document.body;
    
    // Load saved theme or default to dark mode
    const savedTheme = localStorage.getItem('theme') || 'dark';
    body.className = savedTheme + '-mode';
    
    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            if (body.classList.contains('dark-mode')) {
                body.className = 'light-mode';
                localStorage.setItem('theme', 'light');
            } else {
                body.className = 'dark-mode';
                localStorage.setItem('theme', 'dark');
            }
        });
    }
    
    // Mobile Navigation Toggle
    const navToggle = document.getElementById('navToggle');
    const navMenu = document.getElementById('navMenu');
    
    if (navToggle && navMenu) {
        navToggle.addEventListener('click', function() {
            navToggle.classList.toggle('active');
            navMenu.classList.toggle('active');
        });
        
        // Close mobile menu when clicking on a link
        const navLinks = navMenu.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            link.addEventListener('click', function() {
                navToggle.classList.remove('active');
                navMenu.classList.remove('active');
            });
        });
        
        // Close mobile menu when clicking outside
        document.addEventListener('click', function(event) {
            if (!navToggle.contains(event.target) && !navMenu.contains(event.target)) {
                navToggle.classList.remove('active');
                navMenu.classList.remove('active');
            }
        });
    }
    
    // Set active navigation link based on current page
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === currentPath || 
            (currentPath === '/' && link.getAttribute('href') === '/')) {
            link.classList.add('active');
        }
    });
    
    // Video Search Functionality (for all-videos page)
    const searchInput = document.getElementById('videoSearch');
    const sortSelect = document.getElementById('sortSelect');
    const viewToggle = document.getElementById('viewToggle');
    const videosContainer = document.getElementById('videosContainer');
    
    if (searchInput && videosContainer) {
        let searchTimeout;
        
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                filterVideos();
            }, 300);
        });
    }
    
    if (sortSelect) {
        sortSelect.addEventListener('change', filterVideos);
    }
    
    if (viewToggle) {
        viewToggle.addEventListener('click', function() {
            const icon = this.querySelector('i');
            if (videosContainer.classList.contains('list-view')) {
                videosContainer.classList.remove('list-view');
                videosContainer.classList.add('grid-view');
                icon.className = 'fas fa-list';
                this.setAttribute('aria-label', 'Switch to list view');
            } else {
                videosContainer.classList.remove('grid-view');
                videosContainer.classList.add('list-view');
                icon.className = 'fas fa-th-large';
                this.setAttribute('aria-label', 'Switch to grid view');
            }
        });
    }
    
    function filterVideos() {
        const searchTerm = searchInput ? searchInput.value.toLowerCase() : '';
        const sortBy = sortSelect ? sortSelect.value : 'date';
        const videoCards = videosContainer ? videosContainer.querySelectorAll('.video-card') : [];
        
        // Convert NodeList to Array for sorting
        const videoArray = Array.from(videoCards);
        
        // Filter videos based on search term
        videoArray.forEach(card => {
            const title = card.querySelector('.video-title').textContent.toLowerCase();
            const description = card.querySelector('.video-description') ? 
                card.querySelector('.video-description').textContent.toLowerCase() : '';
            
            if (title.includes(searchTerm) || description.includes(searchTerm)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
        
        // Sort visible videos
        const visibleVideos = videoArray.filter(card => card.style.display !== 'none');
        
        visibleVideos.sort((a, b) => {
            switch (sortBy) {
                case 'title':
                    const titleA = a.querySelector('.video-title').textContent;
                    const titleB = b.querySelector('.video-title').textContent;
                    return titleA.localeCompare(titleB);
                    
                case 'views':
                    const viewsA = parseInt(a.dataset.views || '0');
                    const viewsB = parseInt(b.dataset.views || '0');
                    return viewsB - viewsA;
                    
                case 'likes':
                    const likesA = parseInt(a.dataset.likes || '0');
                    const likesB = parseInt(b.dataset.likes || '0');
                    return likesB - likesA;
                    
                case 'comments':
                    const commentsA = parseInt(a.dataset.comments || '0');
                    const commentsB = parseInt(b.dataset.comments || '0');
                    return commentsB - commentsA;
                    
                case 'date':
                default:
                    const dateA = new Date(a.dataset.publishedAt || '0');
                    const dateB = new Date(b.dataset.publishedAt || '0');
                    return dateB - dateA;
            }
        });
        
        // Reorder DOM elements
        visibleVideos.forEach(card => {
            videosContainer.appendChild(card);
        });
    }
    
    // Smooth scrolling for anchor links
    const anchorLinks = document.querySelectorAll('a[href^="#"]');
    anchorLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);
            
            if (targetElement) {
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // Lazy loading for images
    const images = document.querySelectorAll('img[loading="lazy"]');
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src || img.src;
                    img.classList.remove('lazy');
                    observer.unobserve(img);
                }
            });
        });
        
        images.forEach(img => imageObserver.observe(img));
    }
    
    // Keyboard navigation support
    document.addEventListener('keydown', function(e) {
        // ESC key closes mobile menu
        if (e.key === 'Escape' && navMenu && navMenu.classList.contains('active')) {
            navToggle.classList.remove('active');
            navMenu.classList.remove('active');
        }
        
        // Enter key on theme toggle
        if (e.key === 'Enter' && e.target === themeToggle) {
            themeToggle.click();
        }
    });
    
    // Add loading states for video thumbnails
    const videoThumbnails = document.querySelectorAll('.video-thumbnail img');
    videoThumbnails.forEach(img => {
        img.addEventListener('load', function() {
            this.style.opacity = '1';
        });
        
        img.addEventListener('error', function() {
            this.src = '/images/placeholder-video.jpg'; // Fallback image
        });
    });
    
    // Performance optimization: Debounce scroll events
    let scrollTimeout;
    window.addEventListener('scroll', function() {
        clearTimeout(scrollTimeout);
        scrollTimeout = setTimeout(() => {
            // Add scroll-based animations or effects here if needed
        }, 100);
    });
    
    // Add focus management for accessibility
    const focusableElements = document.querySelectorAll(
        'a, button, input, textarea, select, [tabindex]:not([tabindex="-1"])'
    );
    
    // Trap focus in mobile menu when open
    if (navMenu) {
        navMenu.addEventListener('keydown', function(e) {
            if (e.key === 'Tab' && this.classList.contains('active')) {
                const focusableInMenu = this.querySelectorAll('a, button');
                const firstFocusable = focusableInMenu[0];
                const lastFocusable = focusableInMenu[focusableInMenu.length - 1];
                
                if (e.shiftKey) {
                    if (document.activeElement === firstFocusable) {
                        e.preventDefault();
                        lastFocusable.focus();
                    }
                } else {
                    if (document.activeElement === lastFocusable) {
                        e.preventDefault();
                        firstFocusable.focus();
                    }
                }
            }
        });
    }
    
    console.log('Random Videos 139 Website - JavaScript loaded successfully');
});


// Featured Videos Carousel Initialization
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Swiper carousel for featured videos
    const featuredCarousel = document.querySelector('.featured-carousel');
    if (featuredCarousel) {
        const swiper = new Swiper('.featured-carousel', {
            slidesPerView: 1,
            spaceBetween: 20,
            loop: true,
            autoplay: {
                delay: 5000,
                disableOnInteraction: false,
            },
            pagination: {
                el: '.swiper-pagination',
                clickable: true,
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
            },
            breakpoints: {
                640: {
                    slidesPerView: 2,
                    spaceBetween: 20,
                },
                768: {
                    slidesPerView: 3,
                    spaceBetween: 30,
                },
                1024: {
                    slidesPerView: 4,
                    spaceBetween: 30,
                },
            },
            // Accessibility
            a11y: {
                prevSlideMessage: 'Previous video',
                nextSlideMessage: 'Next video',
                paginationBulletMessage: 'Go to video {{index}}',
            },
        });
        
        // Pause autoplay on hover
        featuredCarousel.addEventListener('mouseenter', () => {
            swiper.autoplay.stop();
        });
        
        featuredCarousel.addEventListener('mouseleave', () => {
            swiper.autoplay.start();
        });
    }
    
    // Mobile Menu Toggle with new navbar structure
    const mobileMenuToggle = document.getElementById('mobileMenuToggle');
    const mobileMenuOverlay = document.getElementById('mobileMenuOverlay');
    
    if (mobileMenuToggle && mobileMenuOverlay) {
        mobileMenuToggle.addEventListener('click', function() {
            mobileMenuOverlay.classList.toggle('active');
            this.classList.toggle('active');
            
            // Animate hamburger lines
            const lines = this.querySelectorAll('.hamburger-line');
            if (this.classList.contains('active')) {
                lines[0].style.transform = 'rotate(45deg) translate(5px, 5px)';
                lines[1].style.opacity = '0';
                lines[2].style.transform = 'rotate(-45deg) translate(7px, -6px)';
            } else {
                lines[0].style.transform = 'none';
                lines[1].style.opacity = '1';
                lines[2].style.transform = 'none';
            }
        });
        
        // Close mobile menu when clicking overlay
        mobileMenuOverlay.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('active');
                mobileMenuToggle.classList.remove('active');
                
                // Reset hamburger lines
                const lines = mobileMenuToggle.querySelectorAll('.hamburger-line');
                lines[0].style.transform = 'none';
                lines[1].style.opacity = '1';
                lines[2].style.transform = 'none';
            }
        });
        
        // Close mobile menu when clicking on a link
        const mobileNavLinks = mobileMenuOverlay.querySelectorAll('.mobile-nav-link');
        mobileNavLinks.forEach(link => {
            link.addEventListener('click', function() {
                mobileMenuOverlay.classList.remove('active');
                mobileMenuToggle.classList.remove('active');
                
                // Reset hamburger lines
                const lines = mobileMenuToggle.querySelectorAll('.hamburger-line');
                lines[0].style.transform = 'none';
                lines[1].style.opacity = '1';
                lines[2].style.transform = 'none';
            });
        });
    }
    
    // Loading skeleton animation
    function showLoadingSkeletons(container, count = 8) {
        if (!container) return;
        
        container.innerHTML = '';
        for (let i = 0; i < count; i++) {
            const skeleton = document.createElement('div');
            skeleton.className = 'skeleton-video-card';
            skeleton.innerHTML = `
                <div class="skeleton-thumbnail skeleton"></div>
                <div class="skeleton-info">
                    <div class="skeleton-title skeleton"></div>
                    <div class="skeleton-meta skeleton"></div>
                </div>
            `;
            container.appendChild(skeleton);
        }
    }
    
    // Function to hide loading skeletons and show actual content
    function hideLoadingSkeletons(container, actualContent) {
        if (!container) return;
        
        setTimeout(() => {
            container.innerHTML = actualContent;
        }, 1000); // Simulate loading time
    }
    
    // Smooth animations for video cards
    const videoCards = document.querySelectorAll('.video-card, .featured-video-card');
    if ('IntersectionObserver' in window) {
        const cardObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        });
        
        videoCards.forEach(card => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            cardObserver.observe(card);
        });
    }
    
    // Enhanced theme toggle with smooth transitions
    const themeToggle = document.getElementById('themeToggle');
    const themeIcon = document.getElementById('themeIcon');
    
    if (themeToggle && themeIcon) {
        themeToggle.addEventListener('click', function() {
            const body = document.body;
            const isLight = body.classList.contains('light-mode');
            
            // Add transition class for smooth theme change
            body.classList.add('theme-transitioning');
            
            if (isLight) {
                body.className = 'dark-mode theme-transitioning';
                themeIcon.className = 'fas fa-moon';
                localStorage.setItem('theme', 'dark');
            } else {
                body.className = 'light-mode theme-transitioning';
                themeIcon.className = 'fas fa-sun';
                localStorage.setItem('theme', 'light');
            }
            
            // Remove transition class after animation
            setTimeout(() => {
                body.classList.remove('theme-transitioning');
            }, 300);
        });
        
        // Set initial icon based on current theme
        const currentTheme = localStorage.getItem('theme') || 'dark';
        if (currentTheme === 'light') {
            themeIcon.className = 'fas fa-sun';
        } else {
            themeIcon.className = 'fas fa-moon';
        }
    }
});

// Add CSS for theme transition
const style = document.createElement('style');
style.textContent = `
    .theme-transitioning * {
        transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease !important;
    }
`;
document.head.appendChild(style);

