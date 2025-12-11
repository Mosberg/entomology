/**
 * Entomology Mod Homepage Script
 * Handles interactions and smooth scrolling
 */

document.addEventListener('DOMContentLoaded', () => {
  // Mobile navigation toggle
  const navToggle = document.getElementById('navToggle');
  const navLinks = document.getElementById('navLinks');
  
  if (navToggle && navLinks) {
    navToggle.addEventListener('click', () => {
      navLinks.classList.toggle('open');
      navToggle.setAttribute('aria-expanded', navLinks.classList.contains('open'));
    });
    
    // Close menu when clicking a link
    navLinks.querySelectorAll('a').forEach(link => {
      link.addEventListener('click', () => {
        navLinks.classList.remove('open');
        navToggle.setAttribute('aria-expanded', 'false');
      });
    });
    
    // Close menu when clicking outside
    document.addEventListener('click', (e) => {
      if (!navToggle.contains(e.target) && !navLinks.contains(e.target)) {
        navLinks.classList.remove('open');
        navToggle.setAttribute('aria-expanded', 'false');
      }
    });
  }
  
  // Smooth scroll for anchor links
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
      e.preventDefault();
      const target = document.querySelector(this.getAttribute('href'));
      
      if (target) {
        const headerOffset = 80;
        const elementPosition = target.getBoundingClientRect().top;
        const offsetPosition = elementPosition + window.pageYOffset - headerOffset;
        
        window.scrollTo({
          top: offsetPosition,
          behavior: 'smooth'
        });
      }
    });
  });
  
  // Active nav link highlighting
  const sections = document.querySelectorAll('section[id]');
  const navItems = document.querySelectorAll('.nav-links a');
  
  function highlightNav() {
    const scrollPos = window.scrollY + 100;
    
    sections.forEach(section => {
      const sectionTop = section.offsetTop;
      const sectionHeight = section.offsetHeight;
      const sectionId = section.getAttribute('id');
      
      if (scrollPos >= sectionTop && scrollPos < sectionTop + sectionHeight) {
        navItems.forEach(item => {
          item.classList.remove('active');
          if (item.getAttribute('href') === `#${sectionId}`) {
            item.classList.add('active');
          }
        });
      }
    });
  }
  
  window.addEventListener('scroll', highlightNav);
  highlightNav();
  
  // Download button interaction
  const downloadBtn = document.getElementById('downloadBtn');
  const downloadNote = document.getElementById('downloadNote');
  
  if (downloadBtn && downloadNote) {
    downloadBtn.addEventListener('click', () => {
      // Add visual feedback
      downloadBtn.style.transform = 'scale(0.95)';
      setTimeout(() => {
        downloadBtn.style.transform = '';
      }, 150);
      
      // Update note with repo link
      setTimeout(() => {
        downloadNote.innerHTML = `
          📦 Downloading... Check 
          <a href="https://github.com/Mosberg/entomology/releases" 
             target="_blank" 
             rel="noopener"
             style="color: var(--ento-primary);">releases page</a> 
          for all versions
        `;
      }, 500);
    });
  }
  
  // Intersection Observer for fade-in animations
  const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  };
  
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.style.opacity = '1';
        entry.target.style.transform = 'translateY(0)';
      }
    });
  }, observerOptions);
  
  // Observe cards for animation
  document.querySelectorAll('.overview-card, .feature-block, .arch-card, .step').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(20px)';
    el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
    observer.observe(el);
  });
  
  // Add copy functionality to code blocks
  document.querySelectorAll('.code-block').forEach(block => {
    const wrapper = block.closest('.code-block-wrapper');
    if (wrapper) {
      const copyBtn = document.createElement('button');
      copyBtn.className = 'copy-btn';
      copyBtn.innerHTML = `
        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
          <path d="M4 2h8a2 2 0 012 2v8a2 2 0 01-2 2H4a2 2 0 01-2-2V4a2 2 0 012-2z"/>
          <path d="M2 6V4a2 2 0 012-2h8" stroke="currentColor" fill="none"/>
        </svg>
        Copy
      `;
      copyBtn.style.cssText = `
        position: absolute;
        top: 8px;
        right: 8px;
        background: rgba(80, 250, 123, 0.1);
        border: 1px solid rgba(80, 250, 123, 0.3);
        color: var(--ento-primary);
        padding: 4px 8px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 12px;
        display: flex;
        align-items: center;
        gap: 4px;
        transition: all 0.2s ease;
      `;
      
      wrapper.style.position = 'relative';
      wrapper.appendChild(copyBtn);
      
      copyBtn.addEventListener('click', async () => {
        const text = block.textContent;
        try {
          await navigator.clipboard.writeText(text);
          copyBtn.innerHTML = `
            <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
              <path d="M13.5 2L6 9.5 2.5 6"/>
            </svg>
            Copied!
          `;
          setTimeout(() => {
            copyBtn.innerHTML = `
              <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                <path d="M4 2h8a2 2 0 012 2v8a2 2 0 01-2 2H4a2 2 0 01-2-2V4a2 2 0 012-2z"/>
                <path d="M2 6V4a2 2 0 012-2h8" stroke="currentColor" fill="none"/>
              </svg>
              Copy
            `;
          }, 2000);
        } catch (err) {
          console.error('Failed to copy:', err);
        }
      });
      
      copyBtn.addEventListener('mouseenter', () => {
        copyBtn.style.background = 'rgba(80, 250, 123, 0.2)';
      });
      
      copyBtn.addEventListener('mouseleave', () => {
        copyBtn.style.background = 'rgba(80, 250, 123, 0.1)';
      });
    }
  });
});
