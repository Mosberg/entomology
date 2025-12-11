// Mobile nav toggle
const navToggle = document.getElementById("navToggle");
const navLinks = document.getElementById("navLinks");

if (navToggle && navLinks) {
  navToggle.addEventListener("click", () => {
    navLinks.classList.toggle("open");
  });

  navLinks.querySelectorAll("a").forEach((link) => {
    link.addEventListener("click", () => navLinks.classList.remove("open"));
  });
}

// Fake download interaction
const downloadBtn = document.getElementById("downloadBtn");
const downloadNote = document.getElementById("downloadNote");

if (downloadBtn && downloadNote) {
  downloadBtn.addEventListener("click", (e) => {
    e.preventDefault();
    downloadNote.textContent = "https://github.com/Mosberg/entomology.git";
  });
}
