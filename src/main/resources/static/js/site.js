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

  // Sincronización de rangos de fechas (inicio <= fin)
  const startDateInput = document.getElementById("startDate");
  const endDateInput = document.getElementById("endDate");
  if (startDateInput && endDateInput) {
    const syncDates = () => {
      if (startDateInput.value) {
        endDateInput.min = startDateInput.value;
      } else {
        endDateInput.removeAttribute("min");
      }
    };
    startDateInput.addEventListener("change", syncDates);
    syncDates();
  }

  // Filtrado inteligente de equipos por torneo y validación local/visitante en formulario de partidos
  const competitionSelect = document.getElementById("competitionId");
  const homeTeamSelect = document.getElementById("homeTeamId");
  const awayTeamSelect = document.getElementById("awayTeamId");
  const matchStatusSelect = document.getElementById("status");
  const homeGoalsInput = document.getElementById("homeGoals");
  const awayGoalsInput = document.getElementById("awayGoals");

  if (competitionSelect && homeTeamSelect && awayTeamSelect) {
    const filterMatchTeams = () => {
      const compId = competitionSelect.value;
      [homeTeamSelect, awayTeamSelect].forEach((select) => {
        Array.from(select.options).forEach((option) => {
          if (!option.value) return;
          const teamComp = option.dataset.competitionId;
          const matchesComp = !compId || !teamComp || teamComp === compId;
          option.hidden = !matchesComp;
          option.disabled = !matchesComp;
        });
        const selectedOption = select.options[select.selectedIndex];
        if (selectedOption && selectedOption.disabled) {
          select.value = "";
        }
      });

      const homeVal = homeTeamSelect.value;
      const awayVal = awayTeamSelect.value;
      Array.from(awayTeamSelect.options).forEach((option) => {
        if (option.value && !option.hidden) {
          option.disabled = Boolean(homeVal && option.value === homeVal);
        }
      });
      Array.from(homeTeamSelect.options).forEach((option) => {
        if (option.value && !option.hidden) {
          option.disabled = Boolean(awayVal && option.value === awayVal);
        }
      });
    };

    competitionSelect.addEventListener("change", filterMatchTeams);
    homeTeamSelect.addEventListener("change", filterMatchTeams);
    awayTeamSelect.addEventListener("change", filterMatchTeams);
    filterMatchTeams();
  }

  if (matchStatusSelect && homeGoalsInput && awayGoalsInput) {
    const syncScoreInputs = () => {
      const status = matchStatusSelect.value;
      const isCancelled = status === "CANCELLED";
      const isFinalized = status === "FINALIZED";
      homeGoalsInput.disabled = isCancelled;
      awayGoalsInput.disabled = isCancelled;
      homeGoalsInput.required = isFinalized;
      awayGoalsInput.required = isFinalized;
      if (isCancelled) {
        homeGoalsInput.value = "";
        awayGoalsInput.value = "";
      }
    };
    matchStatusSelect.addEventListener("change", syncScoreInputs);
    syncScoreInputs();
  }

  // Filtrado inteligente de jugadores por equipo y activación de sustitución en el acta del partido
  const sheetTeamSelect = document.getElementById("teamId");
  const sheetPlayerSelect = document.getElementById("playerId");
  const sheetRelatedPlayerSelect = document.getElementById("relatedPlayerId");
  const sheetEventTypeSelect = document.getElementById("type");

  if (sheetTeamSelect && sheetPlayerSelect && sheetRelatedPlayerSelect) {
    const filterSheetPlayers = () => {
      const selectedTeamId = sheetTeamSelect.value;
      [sheetPlayerSelect, sheetRelatedPlayerSelect].forEach((select) => {
        Array.from(select.options).forEach((option) => {
          if (!option.value) return;
          const playerTeamId = option.dataset.teamId;
          const matchesTeam = !selectedTeamId || !playerTeamId || playerTeamId === selectedTeamId;
          option.hidden = !matchesTeam;
          option.disabled = !matchesTeam;
        });
        const current = select.options[select.selectedIndex];
        if (current && current.disabled) {
          select.value = "";
        }
      });
    };
    sheetTeamSelect.addEventListener("change", filterSheetPlayers);
    filterSheetPlayers();
  }

  if (sheetEventTypeSelect && sheetRelatedPlayerSelect) {
    const syncSubstitutionField = () => {
      const isSub = sheetEventTypeSelect.value === "SUBSTITUTION";
      sheetRelatedPlayerSelect.disabled = !isSub;
      sheetRelatedPlayerSelect.required = isSub;
      if (!isSub) {
        sheetRelatedPlayerSelect.value = "";
      }
    };
    sheetEventTypeSelect.addEventListener("change", syncSubstitutionField);
    syncSubstitutionField();
  }

  // Buscador instantáneo en listados y directorios
  document.querySelectorAll("[data-search-target]").forEach((searchInput) => {
    const targetSelector = searchInput.dataset.searchTarget;
    searchInput.addEventListener("input", () => {
      const query = searchInput.value.trim().toLowerCase();
      document.querySelectorAll(targetSelector).forEach((item) => {
        const text = (item.textContent || "").toLowerCase();
        item.style.display = !query || text.includes(query) ? "" : "none";
      });
    });
  });

  // Animación Scroll-Reveal escalonada para tarjetas, etapas y secciones del portal
  const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  const revealElements = document.querySelectorAll(
    ".club-card, .feature-card, .stage-step, .metric-card, .module-link, .section-heading, .table-responsive, .empty-state, .band-inner"
  );
  if (!prefersReducedMotion && "IntersectionObserver" in window) {
    const revealObserver = new IntersectionObserver(
      (entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("is-visible");
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.1, rootMargin: "0px 0px -30px 0px" }
    );

    revealElements.forEach((element, index) => {
      const siblingIndex = element.parentElement
        ? Array.from(element.parentElement.children).indexOf(element)
        : index;
      const delayMs = Math.min(Math.max(siblingIndex, 0) * 65, 320);
      element.style.setProperty("--reveal-delay", `${delayMs}ms`);
      element.classList.add("reveal-on-scroll");
      revealObserver.observe(element);
    });
  }

  // Efecto Spotlight e inclinación suave al pasar el cursor sobre tarjetas
  if (!prefersReducedMotion) {
    document.querySelectorAll(".club-card, .feature-card").forEach((card) => {
      card.addEventListener("mousemove", (event) => {
        const rect = card.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;
        card.style.setProperty("--mouse-x", `${x}px`);
        card.style.setProperty("--mouse-y", `${y}px`);
      });
    });
  }

  // Contadores numéricos animados para métricas de la liga
  if (!prefersReducedMotion && "IntersectionObserver" in window) {
    const counterObserver = new IntersectionObserver(
      (entries, observer) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          const el = entry.target;
          const targetValue = parseInt((el.textContent || "").trim(), 10);
          if (Number.isNaN(targetValue) || targetValue <= 0) {
            observer.unobserve(el);
            return;
          }
          const duration = 850;
          const startTime = performance.now();
          const tick = (now) => {
            const progress = Math.min((now - startTime) / duration, 1);
            const eased = 1 - Math.pow(1 - progress, 3);
            el.textContent = String(Math.round(targetValue * eased));
            if (progress < 1) {
              requestAnimationFrame(tick);
            } else {
              el.textContent = String(targetValue);
            }
          };
          requestAnimationFrame(tick);
          observer.unobserve(el);
        });
      },
      { threshold: 0.5 }
    );

    document.querySelectorAll(".metric-pill strong, .metric-card strong").forEach((counter) => {
      if (/^\d+$/.test((counter.textContent || "").trim())) {
        counterObserver.observe(counter);
      }
    });
  }

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
