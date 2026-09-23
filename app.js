/* Mẹo Vặt site: language switching (vi / en) + mobile nav. */
(function () {
  var KEY = "meovat-lang";
  var SUPPORTED = ["vi", "en"];

  function detect() {
    var param = new URLSearchParams(location.search).get("lang");
    if (SUPPORTED.indexOf(param) !== -1) return param;
    try {
      var saved = localStorage.getItem(KEY);
      if (SUPPORTED.indexOf(saved) !== -1) return saved;
    } catch (e) {}
    var nav = (navigator.language || "vi").toLowerCase();
    return nav.indexOf("vi") === 0 ? "vi" : "en";
  }

  function apply(lang) {
    document.documentElement.lang = lang;
    var titleEl = document.querySelector("title");
    if (titleEl && titleEl.dataset[lang]) document.title = titleEl.dataset[lang];
    var desc = document.querySelector('meta[name="description"]');
    if (desc && desc.dataset[lang]) desc.setAttribute("content", desc.dataset[lang]);
    document.querySelectorAll("[data-lang-btn]").forEach(function (b) {
      var on = b.dataset.langBtn === lang;
      b.classList.toggle("active", on);
      b.setAttribute("aria-pressed", on ? "true" : "false");
    });
    try { localStorage.setItem(KEY, lang); } catch (e) {}
  }

  // Set the language before first paint to avoid a flash of the wrong language.
  apply(detect());

  document.addEventListener("DOMContentLoaded", function () {
    apply(document.documentElement.lang);

    document.querySelectorAll("[data-lang-btn]").forEach(function (b) {
      b.addEventListener("click", function () { apply(b.dataset.langBtn); });
    });

    var menuBtn = document.querySelector(".menu-btn");
    var nav = document.querySelector(".nav");
    if (menuBtn && nav) {
      menuBtn.addEventListener("click", function () {
        var open = nav.classList.toggle("open");
        menuBtn.setAttribute("aria-expanded", open ? "true" : "false");
      });
      nav.querySelectorAll("a").forEach(function (a) {
        a.addEventListener("click", function () { nav.classList.remove("open"); });
      });
    }

    document.querySelectorAll("[data-year]").forEach(function (el) {
      el.textContent = new Date().getFullYear();
    });
  });
})();
