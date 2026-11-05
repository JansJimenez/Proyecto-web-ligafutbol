(() => {
  const body = document.body;
  const loader = document.querySelector(".navigation-loader");
  const showLoader = () => {
    if (loader) {
      loader.classList.add("is-visible");
      loader.setAttribute("aria-hidden", "false");
    }
    body.classList.add("is-navigating");
  };

  document.querySelectorAll(".site-menu-toggle").forEach((toggle) => {
    const menu = document.getElementById(toggle.getAttribute("aria-controls"));
    if (!menu) return;
    toggle.addEventListener("click", () => {
      const expanded = toggle.getAttribute("aria-expanded") === "true";
      toggle.setAttribute("aria-expanded", String(!expanded));
      toggle.setAttribute("aria-label", expanded ? "Abrir navegación" : "Cerrar navegación");
      menu.classList.toggle("is-open", !expanded);
    });
    menu.querySelectorAll("a").forEach((link) => {
      if (link.pathname === window.location.pathname) link.setAttribute("aria-current", "page");
    });
  });

  document.querySelectorAll(".table-responsive table").forEach((table) => {
    const headers = Array.from(table.querySelectorAll("thead th"), (header) => header.textContent.trim());
    if (!headers.length) return;

    table.querySelectorAll("tbody tr").forEach((row) => {
      Array.from(row.cells).forEach((cell, index) => {
        cell.dataset.label = headers[index] || "Acciones";
      });
    });

    if (headers.length <= 7) table.closest(".table-responsive").classList.add("mobile-card-table");
  });

  document.querySelectorAll('input[type="file"]').forEach((input) => {
    input.addEventListener("change", () => {
      const file = input.files && input.files[0];
      input.setCustomValidity("");
      if (!file) return;
      if (file.size > 5 * 1024 * 1024) {
        input.value = "";
        input.setCustomValidity("El archivo no puede superar los 5 MB.");
        input.reportValidity();
        return;
      }

      const previewSelector = input.dataset.imagePreview;
      if (previewSelector && file.type.startsWith("image/")) {
        let preview = document.querySelector(previewSelector);
        if (!preview) {
          preview = document.createElement("img");
          preview.id = previewSelector.slice(1);
          preview.className = "upload-preview mt-2";
          preview.alt = "Vista previa del archivo seleccionado";
          input.insertAdjacentElement("afterend", preview);
        }
        if (preview.dataset.objectUrl) URL.revokeObjectURL(preview.dataset.objectUrl);
        preview.dataset.objectUrl = URL.createObjectURL(file);
        preview.src = preview.dataset.objectUrl;
      }

      const removeCheckbox = input.form && input.form.querySelector(
        `input[type="checkbox"][name="remove${input.name.charAt(0).toUpperCase()}${input.name.slice(1).replace("File", "")}"]`
      );
      if (removeCheckbox) removeCheckbox.checked = false;
    });
  });

  document.addEventListener("click", (event) => {
    const link = event.target.closest("a[href]");
    if (!link || event.defaultPrevented || event.button !== 0
      || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey
      || link.target || link.hasAttribute("download")
      || link.getAttribute("href").startsWith("#")) return;
    const destination = new URL(link.href, window.location.href);
    if (destination.origin === window.location.origin
      && (destination.pathname !== window.location.pathname || destination.search !== window.location.search)) {
      showLoader();
    }
  });

  document.addEventListener("submit", (event) => {
    const form = event.target;
    if (form instanceof HTMLFormElement && (!form.target || form.target === "_self")) showLoader();
  });
})();
