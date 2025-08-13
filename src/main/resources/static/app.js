document.addEventListener("DOMContentLoaded", () => {
  // DOM elements
  const activitiesList = document.getElementById("activities-list");
  const messageDiv = document.getElementById("message");
  const registrationModal = document.getElementById("registration-modal");
  const modalActivityName = document.getElementById("modal-activity-name");
  const signupForm = document.getElementById("signup-form");
  const activityInput = document.getElementById("activity");
  const closeRegistrationModal = document.querySelector(".close-modal");

  // Theme toggle elements
  const themeToggle = document.getElementById("theme-toggle");
  const themeIcon = document.querySelector(".theme-icon");
  const themeText = themeToggle.querySelector("span:last-child");

  // Search and filter elements
  const searchInput = document.getElementById("activity-search");
  const searchButton = document.getElementById("search-button");
  const categoryFilters = document.querySelectorAll(".category-filter");
  const dayFilters = document.querySelectorAll(".day-filter");
  const timeFilters = document.querySelectorAll(".time-filter");

  // Authentication elements
  const loginButton = document.getElementById("login-button");
  const userInfo = document.getElementById("user-info");
  const displayName = document.getElementById("display-name");
  const logoutButton = document.getElementById("logout-button");
  const loginModal = document.getElementById("login-modal");
  const loginForm = document.getElementById("login-form");
  const closeLoginModal = document.querySelector(".close-login-modal");
  const loginMessage = document.getElementById("login-message");

  // Activity categories - will be loaded from backend
  let activityTypes = {};
  let allCategories = {};

  // State for activities and filters
  let allActivities = {};
  let currentFilter = "all";
  let searchQuery = "";
  let currentDay = "";
  let currentTimeRange = "";

  // Theme management
  let isDarkMode = false;

  // Initialize theme from localStorage or system preference
  function initializeTheme() {
    const savedTheme = localStorage.getItem("darkMode");
    const systemPrefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    
    // Use saved preference if available, otherwise use system preference
    isDarkMode = savedTheme ? savedTheme === "true" : systemPrefersDark;
    
    applyTheme();
    updateThemeToggleUI();
  }

  // Apply the current theme
  function applyTheme() {
    if (isDarkMode) {
      document.body.classList.add("dark-mode");
    } else {
      document.body.classList.remove("dark-mode");
    }
  }

  // Update the theme toggle button UI
  function updateThemeToggleUI() {
    if (isDarkMode) {
      themeIcon.textContent = "☀️";
      themeText.textContent = "Claro";
    } else {
      themeIcon.textContent = "🌙";
      themeText.textContent = "Escuro";
    }
  }

  // Toggle theme function
  function toggleTheme() {
    isDarkMode = !isDarkMode;
    applyTheme();
    updateThemeToggleUI();
    
    // Save preference to localStorage
    localStorage.setItem("darkMode", isDarkMode.toString());
  }

  // Event listener for theme toggle
  themeToggle.addEventListener("click", toggleTheme);

  // Load activity categories from backend
  async function loadActivityCategories() {
    try {
      const response = await fetch('/categories');
      const categoriesData = await response.json();
      
      // Transform backend categories to frontend format
      activityTypes = {};
      allCategories = categoriesData;
      
      // Handle both map format {code: categoryDTO} and array format [categoryDTO]
      if (Array.isArray(categoriesData)) {
        categoriesData.forEach((category) => {
          const code = category.type.toLowerCase();
          activityTypes[code] = {
            label: category.label,
            color: category.backgroundColor,
            textColor: category.textColor,
            description: category.description
          };
        });
      } else {
        Object.entries(categoriesData).forEach(([code, category]) => {
          activityTypes[code.toLowerCase()] = {
            label: category.label,
            color: category.backgroundColor,
            textColor: category.textColor,
            description: category.description
          };
        });
      }
      
      console.log('Categorias carregadas do backend:', activityTypes);
      return activityTypes;
    } catch (error) {
      console.error('Erro ao carregar categorias:', error);
      throw error;
    }
  }


  // Update category filters in the DOM based on loaded categories
  function updateCategoryFilters() {
    const categoryFiltersContainer = document.getElementById('category-filters');
    if (!categoryFiltersContainer) return;
    
    // Clear existing filters
    categoryFiltersContainer.innerHTML = '';
    
    // Add "Todas" button
    const allBtn = document.createElement('button');
    allBtn.className = 'category-filter active';
    allBtn.setAttribute('data-category', 'all');
    allBtn.textContent = 'Todas';
    categoryFiltersContainer.appendChild(allBtn);
    
    // Add category buttons based on loaded categories
    Object.entries(activityTypes).forEach(([code, config]) => {
      const button = document.createElement('button');
      button.className = 'category-filter';
      button.setAttribute('data-category', code);
      button.textContent = config.label;
      button.style.borderColor = config.color;
      categoryFiltersContainer.appendChild(button);
    });
    
    // Re-attach event listeners
    attachCategoryFilterListeners();
  }

  // Attach event listeners to category filter buttons
  function attachCategoryFilterListeners() {
    const categoryFilters = document.querySelectorAll('.category-filter');
    categoryFilters.forEach((button) => {
      button.addEventListener('click', () => {
        // Update active class
        categoryFilters.forEach((btn) => btn.classList.remove('active'));
        button.classList.add('active');

        // Update current filter and display filtered activities
        currentFilter = button.dataset.category;
        displayFilteredActivities();
      });
    });
  }

  // Authentication state
  let currentUser = null;

  // Time range mappings for the dropdown
  const timeRanges = {
    morning: { start: "06:00", end: "08:00" }, // Before school hours
    afternoon: { start: "15:00", end: "18:00" }, // After school hours
    weekend: { days: ["Saturday", "Sunday"] }, // Weekend days
  };

  // Initialize filters from active elements
  function initializeFilters() {
    // Initialize day filter
    const activeDayFilter = document.querySelector(".day-filter.active");
    if (activeDayFilter) {
      currentDay = activeDayFilter.dataset.day;
    }

    // Initialize time filter
    const activeTimeFilter = document.querySelector(".time-filter.active");
    if (activeTimeFilter) {
      currentTimeRange = activeTimeFilter.dataset.time;
    }
  }

  // Function to set day filter
  function setDayFilter(day) {
    currentDay = day;

    // Update active class
    dayFilters.forEach((btn) => {
      if (btn.dataset.day === day) {
        btn.classList.add("active");
      } else {
        btn.classList.remove("active");
      }
    });

    fetchActivities();
  }

  // Function to set time range filter
  function setTimeRangeFilter(timeRange) {
    currentTimeRange = timeRange;

    // Update active class
    timeFilters.forEach((btn) => {
      if (btn.dataset.time === timeRange) {
        btn.classList.add("active");
      } else {
        btn.classList.remove("active");
      }
    });

    fetchActivities();
  }

  // Check if user is already logged in (from localStorage)
  function checkAuthentication() {
    const savedUser = localStorage.getItem("currentUser");
    if (savedUser) {
      try {
        currentUser = JSON.parse(savedUser);
        updateAuthUI();
        // Verify the stored user with the server
        validateUserSession(currentUser.username);
      } catch (error) {
        console.error("Error parsing saved user", error);
        logout(); // Clear invalid data
      }
    }

    // Set authentication class on body
    updateAuthBodyClass();
  }

  // Validate user session with the server
  async function validateUserSession(username) {
    try {
      const response = await fetch(
        `/auth/check-session?username=${encodeURIComponent(username)}`
      );

      if (!response.ok) {
        // Session invalid, log out
        logout();
        return;
      }

      // Session is valid, update user data
      const userData = await response.json();
      currentUser = userData;
      localStorage.setItem("currentUser", JSON.stringify(userData));
      updateAuthUI();
    } catch (error) {
      console.error("Error validating session:", error);
    }
  }

  // Update UI based on authentication state
  function updateAuthUI() {
    if (currentUser) {
      loginButton.classList.add("hidden");
      userInfo.classList.remove("hidden");
      displayName.textContent = currentUser.displayName;
    } else {
      loginButton.classList.remove("hidden");
      userInfo.classList.add("hidden");
      displayName.textContent = "";
    }

    updateAuthBodyClass();
    // Refresh the activities to update the UI
    fetchActivities();
  }

  // Update body class for CSS targeting
  function updateAuthBodyClass() {
    if (currentUser) {
      document.body.classList.remove("not-authenticated");
    } else {
      document.body.classList.add("not-authenticated");
    }
  }

  // Login function
  async function login(username, password) {
    try {
      const response = await fetch(
        `/auth/login?username=${encodeURIComponent(
          username
        )}&password=${encodeURIComponent(password)}`,
        {
          method: "POST",
        }
      );

      const data = await response.json();

      if (!response.ok) {
        showLoginMessage(
          data.detail || "Usuário ou senha inválidos",
          "error"
        );
        return false;
      }

      // Login successful
      currentUser = data;
      console.log('Debug: Login successful, currentUser =', currentUser);
      localStorage.setItem("currentUser", JSON.stringify(data));
      updateAuthUI();
      closeLoginModalHandler();
      showMessage(`Bem-vindo(a), ${currentUser.displayName}!`, "success");
      return true;
    } catch (error) {
      console.error("Error during login:", error);
      showLoginMessage("Falha no login. Por favor, tente novamente.", "error");
      return false;
    }
  }

  // Logout function
  function logout() {
    currentUser = null;
    localStorage.removeItem("currentUser");
    updateAuthUI();
    showMessage("Você foi desconectado.", "info");
  }

  // Show message in login modal
  function showLoginMessage(text, type) {
    loginMessage.textContent = text;
    loginMessage.className = `message ${type}`;
    loginMessage.classList.remove("hidden");
  }

  // Open login modal
  function openLoginModal() {
    loginModal.classList.remove("hidden");
    loginModal.classList.add("show");
    loginMessage.classList.add("hidden");
    loginForm.reset();
  }

  // Close login modal
  function closeLoginModalHandler() {
    loginModal.classList.remove("show");
    setTimeout(() => {
      loginModal.classList.add("hidden");
      loginForm.reset();
    }, 300);
  }

  // Event listeners for authentication
  loginButton.addEventListener("click", openLoginModal);
  logoutButton.addEventListener("click", logout);
  closeLoginModal.addEventListener("click", closeLoginModalHandler);

  // Close login modal when clicking outside
  window.addEventListener("click", (event) => {
    if (event.target === loginModal) {
      closeLoginModalHandler();
    }
  });

  // Handle login form submission
  loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    await login(username, password);
  });

  // Show loading skeletons
  function showLoadingSkeletons() {
    activitiesList.innerHTML = "";

    // Create more skeleton cards to fill the screen since they're smaller now
    for (let i = 0; i < 9; i++) {
      const skeletonCard = document.createElement("div");
      skeletonCard.className = "skeleton-card";
      skeletonCard.innerHTML = `
        <div class="skeleton-line skeleton-title"></div>
        <div class="skeleton-line"></div>
        <div class="skeleton-line skeleton-text short"></div>
        <div style="margin-top: 8px;">
          <div class="skeleton-line" style="height: 6px;"></div>
          <div class="skeleton-line skeleton-text short" style="height: 8px; margin-top: 3px;"></div>
        </div>
        <div style="margin-top: auto;">
          <div class="skeleton-line" style="height: 24px; margin-top: 8px;"></div>
        </div>
      `;
      activitiesList.appendChild(skeletonCard);
    }
  }

  // Format schedule for display
  function formatSchedule(details) {
    if (details.scheduleDetails) {
      const days = details.scheduleDetails.days.join(", ");

      // Convert 24h time format to 12h AM/PM format for display
      const formatTime = (time24) => {
        const [hours, minutes] = time24.split(":").map((num) => parseInt(num));
        const period = hours >= 12 ? "PM" : "AM";
        const displayHours = hours % 12 || 12; // Convert 0 to 12 for 12 AM
        return `${displayHours}:${minutes
          .toString()
          .padStart(2, "0")} ${period}`;
      };

      const startTime = formatTime(details.scheduleDetails.startTime);
      const endTime = formatTime(details.scheduleDetails.endTime);

      return `${days}, ${startTime} - ${endTime}`;
    }

    return "Horário não especificado";
  }

  // Function to get activity type from backend data
  function getActivityType(activityName, description, categoryFromBackend = null) {
    // Use category from backend if available
    if (categoryFromBackend) {
      return categoryFromBackend.toLowerCase();
    }
    
    // Default to academic if no category provided
    return "academic";
  }

  // Function to fetch activities from API with optional filters
  async function fetchActivities() {
    // Show loading skeletons first
    showLoadingSkeletons();

    try {
      // Build query string with filters if they exist
      let queryParams = [];

      // Handle day filter
      if (currentDay) {
        queryParams.push(`day=${encodeURIComponent(currentDay)}`);
      }

      // Handle category filter - send to backend if not "all"
      if (currentFilter && currentFilter !== "all") {
        queryParams.push(`category=${encodeURIComponent(currentFilter.toUpperCase())}`);
      }

      // Handle time range filter
      if (currentTimeRange) {
        const range = timeRanges[currentTimeRange];

        // Handle weekend special case
        if (currentTimeRange === "weekend") {
          // Don't add time parameters for weekend filter
          // Weekend filtering will be handled on the client side
        } else if (range) {
          // Add time parameters for before/after school
          queryParams.push(`start_time=${encodeURIComponent(range.start)}`);
          queryParams.push(`end_time=${encodeURIComponent(range.end)}`);
        }
      }

      const queryString =
        queryParams.length > 0 ? `?${queryParams.join("&")}` : "";
      const response = await fetch(`/activities${queryString}`);
      const activities = await response.json();

      // Save the activities data
      allActivities = activities;

      // Apply search and filter, and handle weekend filter in client
      displayFilteredActivities();
    } catch (error) {
      activitiesList.innerHTML =
        "<p>Falha ao carregar atividades. Por favor, tente novamente mais tarde.</p>";
      console.error("Error fetching activities:", error);
    }
  }

  // Function to display filtered activities
  function displayFilteredActivities() {
    // Clear the activities list
    activitiesList.innerHTML = "";

    // Apply client-side filtering - this handles search and weekend filter
    let filteredActivities = {};

    Object.entries(allActivities).forEach(([name, details]) => {
      // Get activity type - use backend data if available
      const activityType = getActivityType(name, details.description, details.category);

      // Apply category filter (only if backend didn't already filter)
      if (currentFilter !== "all" && activityType !== currentFilter) {
        return;
      }

      // Apply weekend filter if selected
      if (currentTimeRange === "weekend") {
        const scheduleDetails = details.scheduleDetails;
        if (scheduleDetails) {
          const activityDays = scheduleDetails.days;
          const isWeekendActivity = activityDays.some((day) =>
            timeRanges.weekend.days.includes(day)
          );

          if (!isWeekendActivity) {
            return;
          }
        }
      }

      // Apply search filter
      const searchableContent = [
        name.toLowerCase(),
        details.description.toLowerCase(),
        formatSchedule(details).toLowerCase(),
      ].join(" ");

      if (
        searchQuery &&
        !searchableContent.includes(searchQuery.toLowerCase())
      ) {
        return;
      }

      // Activity passed all filters, add to filtered list
      filteredActivities[name] = details;
    });

    // Check if there are any results
    if (Object.keys(filteredActivities).length === 0) {
      activitiesList.innerHTML = `
        <div class="no-results">
          <h4>Nenhuma atividade encontrada</h4>
          <p>Tente ajustar seus critérios de busca ou filtro</p>
        </div>
      `;
      return;
    }

    // Display filtered activities
    Object.entries(filteredActivities).forEach(([name, details]) => {
      renderActivityCard(name, details);
    });
  }

  // Function to check if current user can manage this activity
  function canManageActivity(details) {
    if (!currentUser) {
      console.log('Debug: No currentUser');
      return false;
    }
    
    console.log('Debug: currentUser =', currentUser);
    console.log('Debug: currentUser.role =', currentUser.role);
    console.log('Debug: role check =', currentUser.role && currentUser.role.toLowerCase() === "admin");
    
    // Admin can manage any activity - bypass teacher list validation
    if (currentUser.role && currentUser.role.toLowerCase() === "admin") {
      console.log('Debug: User is admin, returning true');
      return true;
    }
    
    // For regular teachers, check if they are in the list of responsible teachers
    if (details.teacherDetails && details.teacherDetails.length > 0) {
      // Use embedded teacher data
      const isResponsible = details.teacherDetails.some(teacher => teacher.username === currentUser.username);
      console.log('Debug: Teacher check with embedded data =', isResponsible);
      return isResponsible;
    } else if (details.assignedTeachers && details.assignedTeachers.length > 0) {
      // Fallback to username list
      const isResponsible = details.assignedTeachers.includes(currentUser.username);
      console.log('Debug: Teacher check with username list =', isResponsible);
      return isResponsible;
    }
    
    console.log('Debug: No teacher data found, returning false');
    return false;
  }

  // Function to render a single activity card
  function renderActivityCard(name, details) {
    const activityCard = document.createElement("div");
    activityCard.className = "activity-card";

    // Calculate spots and capacity
    const totalSpots = details.maxParticipants;
    const takenSpots = details.participants ? details.participants.length : 0;
    const currentCount = details.currentParticipantCount || takenSpots;
    const spotsLeft = totalSpots - currentCount;
    const capacityPercentage = (currentCount / totalSpots) * 100;
    const isFull = spotsLeft <= 0;

    // Check if current user can manage this activity
    const canManage = canManageActivity(details);

    // Determine capacity status class
    let capacityStatusClass = "capacity-available";
    if (isFull) {
      capacityStatusClass = "capacity-full";
    } else if (capacityPercentage >= 75) {
      capacityStatusClass = "capacity-near-full";
    }

    // Get activity type and styling
    let typeInfo = {
      label: "Atividade",
      color: "#007bff",
      textColor: "#ffffff"
    };
    
    if (details.categoryDetails) {
      // Use backend category details if available
      typeInfo = {
        label: details.categoryDetails.label,
        color: details.categoryDetails.backgroundColor,
        textColor: details.categoryDetails.textColor
      };
    } else if (details.category && activityTypes[details.category.toLowerCase()]) {
      // Use category from activityTypes
      const categoryType = activityTypes[details.category.toLowerCase()];
      typeInfo = {
        label: categoryType.label,
        color: categoryType.color,
        textColor: categoryType.textColor
      };
    }

    // Format the schedule using the helper function
    const formattedSchedule = formatSchedule(details);

    // Create activity tag
    const tagHtml = `
      <span class="activity-tag" style="background-color: ${typeInfo.color}; color: ${typeInfo.textColor}">
        ${typeInfo.label}
      </span>
    `;

    // Create capacity indicator
    const capacityIndicator = `
      <div class="capacity-container ${capacityStatusClass}">
        <div class="capacity-bar-bg">
          <div class="capacity-bar-fill" style="width: ${capacityPercentage}%"></div>
        </div>
        <div class="capacity-text">
          <span>${currentCount} inscritos</span>
          <span>${spotsLeft} vagas restantes</span>
        </div>
      </div>
    `;

    // Create participants list with safe handling
    const participants = details.participants || [];
    const participantsList = participants.length > 0 ? 
      participants.map(email => `
        <li>
          ${email}
          ${canManage ? `
            <span class="delete-participant tooltip" data-activity="${name}" data-email="${email}">
              ✖
              <span class="tooltip-text">Desinscrever este estudante</span>
            </span>
          ` : ""}
        </li>
      `).join("") : 
      "<li>Nenhum participante ainda</li>";

    // Create teachers list using embedded data when available
    let teachersInfo = "";
    if (details.teacherDetails && details.teacherDetails.length > 0) {
      // Use embedded teacher data for better performance
      const teachersList = details.teacherDetails.map(teacher => {
        const roleLabel = teacher.role === "admin" ? "Administrador" : "Professor";
        return `<li class="teacher-item">
          <span class="teacher-name">${teacher.displayName}</span>
          <span class="teacher-role">(${roleLabel})</span>
        </li>`;
      }).join("");
      
      teachersInfo = `
        <div class="teachers-list">
          <h5>Professores Responsáveis:</h5>
          <ul class="teachers-ul">
            ${teachersList}
          </ul>
        </div>
      `;
    } else if (details.assignedTeachers && details.assignedTeachers.length > 0) {
      // Fallback to username list if embedded data not available
      const teachersList = details.assignedTeachers.map(username => 
        `<li class="teacher-item">
          <span class="teacher-name">${username}</span>
        </li>`
      ).join("");
      
      teachersInfo = `
        <div class="teachers-list">
          <h5>Professores Responsáveis:</h5>
          <ul class="teachers-ul">
            ${teachersList}
          </ul>
        </div>
      `;
    } else {
      teachersInfo = `
        <div class="teachers-list">
          <h5>Professores Responsáveis:</h5>
          <p class="no-teachers">Nenhum professor atribuído ainda</p>
        </div>
      `;
    }

    activityCard.innerHTML = `
      ${tagHtml}
      <h4>${name}</h4>
      <p>${details.description}</p>
      <p class="tooltip">
        <strong>Horário:</strong> ${formattedSchedule}
        <span class="tooltip-text">Encontros regulares neste horário durante todo o semestre</span>
      </p>
      ${capacityIndicator}
      ${teachersInfo}
      <div class="participants-list">
        <h5>Participantes Atuais:</h5>
        <ul>
          ${participantsList}
        </ul>
      </div>
      <div class="activity-card-actions">
        ${
          canManage
            ? `
          <button class="register-button" data-activity="${name}" ${
                isFull ? "disabled" : ""
              }>
            ${isFull ? "Atividade Lotada" : "Inscrever Estudante"}
          </button>
        `
            : currentUser
            ? `
          <div class="auth-notice">
            ${currentUser.role && currentUser.role.toLowerCase() === "admin" 
              ? "Acesso administrativo disponível para esta atividade." 
              : "Apenas professores responsáveis por esta atividade podem inscrever estudantes."}
          </div>
        `
            : `
          <div class="auth-notice">
            Professores podem inscrever estudantes.
          </div>
        `
        }
      </div>
    `;

    // Add click handlers for delete buttons
    const deleteButtons = activityCard.querySelectorAll(".delete-participant");
    deleteButtons.forEach((button) => {
      button.addEventListener("click", handleUnregister);
    });

    // Add click handler for register button (only when user can manage this activity)
    if (canManage) {
      const registerButton = activityCard.querySelector(".register-button");
      if (!isFull && registerButton) {
        registerButton.addEventListener("click", () => {
          openRegistrationModal(name);
        });
      }
    }

    activitiesList.appendChild(activityCard);
  }

  // Event listeners for search and filter
  searchInput.addEventListener("input", (event) => {
    searchQuery = event.target.value;
    displayFilteredActivities();
  });

  searchButton.addEventListener("click", (event) => {
    event.preventDefault();
    searchQuery = searchInput.value;
    displayFilteredActivities();
  });

  // Add event listeners to category filter buttons (will be called after loading categories)
  // This replaces the old static event listeners

  // Add event listeners to day filter buttons
  dayFilters.forEach((button) => {
    button.addEventListener("click", () => {
      // Update active class
      dayFilters.forEach((btn) => btn.classList.remove("active"));
      button.classList.add("active");

      // Update current day filter and fetch activities
      currentDay = button.dataset.day;
      fetchActivities();
    });
  });

  // Add event listeners for time filter buttons
  timeFilters.forEach((button) => {
    button.addEventListener("click", () => {
      // Update active class
      timeFilters.forEach((btn) => btn.classList.remove("active"));
      button.classList.add("active");

      // Update current time filter and fetch activities
      currentTimeRange = button.dataset.time;
      fetchActivities();
    });
  });

  // Open registration modal
  function openRegistrationModal(activityName) {
    modalActivityName.textContent = activityName;
    activityInput.value = activityName;
    registrationModal.classList.remove("hidden");
    // Add slight delay to trigger animation
    setTimeout(() => {
      registrationModal.classList.add("show");
    }, 10);
  }

  // Close registration modal
  function closeRegistrationModalHandler() {
    registrationModal.classList.remove("show");
    setTimeout(() => {
      registrationModal.classList.add("hidden");
      signupForm.reset();
    }, 300);
  }

  // Event listener for close button
  closeRegistrationModal.addEventListener(
    "click",
    closeRegistrationModalHandler
  );

  // Close modal when clicking outside of it
  window.addEventListener("click", (event) => {
    if (event.target === registrationModal) {
      closeRegistrationModalHandler();
    }
  });

  // Create and show confirmation dialog
  function showConfirmationDialog(message, confirmCallback) {
    // Create the confirmation dialog if it doesn't exist
    let confirmDialog = document.getElementById("confirm-dialog");
    if (!confirmDialog) {
      confirmDialog = document.createElement("div");
      confirmDialog.id = "confirm-dialog";
      confirmDialog.className = "modal hidden";
      confirmDialog.innerHTML = `
        <div class="modal-content">
          <h3>Confirmar Ação</h3>
          <p id="confirm-message"></p>
          <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
            <button id="cancel-button" class="cancel-btn">Cancelar</button>
            <button id="confirm-button" class="confirm-btn">Confirmar</button>
          </div>
        </div>
      `;
      document.body.appendChild(confirmDialog);

      // Style the buttons
      const cancelBtn = confirmDialog.querySelector("#cancel-button");
      const confirmBtn = confirmDialog.querySelector("#confirm-button");

      cancelBtn.style.backgroundColor = "#f1f1f1";
      cancelBtn.style.color = "#333";

      confirmBtn.style.backgroundColor = "#dc3545";
      confirmBtn.style.color = "white";
    }

    // Set the message
    const confirmMessage = document.getElementById("confirm-message");
    confirmMessage.textContent = message;

    // Show the dialog
    confirmDialog.classList.remove("hidden");
    setTimeout(() => {
      confirmDialog.classList.add("show");
    }, 10);

    // Handle button clicks
    const cancelButton = document.getElementById("cancel-button");
    const confirmButton = document.getElementById("confirm-button");

    // Remove any existing event listeners
    const newCancelButton = cancelButton.cloneNode(true);
    const newConfirmButton = confirmButton.cloneNode(true);
    cancelButton.parentNode.replaceChild(newCancelButton, cancelButton);
    confirmButton.parentNode.replaceChild(newConfirmButton, confirmButton);

    // Add new event listeners
    newCancelButton.addEventListener("click", () => {
      confirmDialog.classList.remove("show");
      setTimeout(() => {
        confirmDialog.classList.add("hidden");
      }, 300);
    });

    newConfirmButton.addEventListener("click", () => {
      confirmCallback();
      confirmDialog.classList.remove("show");
      setTimeout(() => {
        confirmDialog.classList.add("hidden");
      }, 300);
    });

    // Close when clicking outside
    confirmDialog.addEventListener("click", (event) => {
      if (event.target === confirmDialog) {
        confirmDialog.classList.remove("show");
        setTimeout(() => {
          confirmDialog.classList.add("hidden");
        }, 300);
      }
    });
  }

  // Handle unregistration with confirmation
  async function handleUnregister(event) {
    // Check if user is authenticated
    if (!currentUser) {
      showMessage(
        "Você deve estar logado como professor para desinscrever estudantes.",
        "error"
      );
      return;
    }

    const activity = event.target.dataset.activity;
    const email = event.target.dataset.email;

    // Get activity details to check authorization
    const activityDetails = allActivities[activity];
    if (!canManageActivity(activityDetails)) {
      const message = currentUser.role && currentUser.role.toLowerCase() === "admin" 
        ? "Erro inesperado: administradores devem ter acesso total."
        : "Você não tem permissão para desinscrever estudantes desta atividade. Apenas professores responsáveis ou administradores podem fazê-lo.";
      showMessage(message, "error");
      return;
    }

    // Show confirmation dialog
    showConfirmationDialog(
      `Tem certeza de que deseja desinscrever ${email} de ${activity}?`,
      async () => {
        try {
          const response = await fetch(
            `/activities/${encodeURIComponent(
              activity
            )}/unregister?email=${encodeURIComponent(
              email
            )}&teacher_username=${encodeURIComponent(currentUser.username)}`,
            {
              method: "POST",
            }
          );

          const result = await response.json();

          if (response.ok) {
            showMessage(result.message, "success");
            // Refresh the activities list
            fetchActivities();
          } else {
            showMessage(result.detail || "Ocorreu um erro", "error");
          }
        } catch (error) {
          showMessage("Falha ao desinscrever. Por favor, tente novamente.", "error");
          console.error("Error unregistering:", error);
        }
      }
    );
  }

  // Show message function
  function showMessage(text, type) {
    messageDiv.textContent = text;
    messageDiv.className = `message ${type}`;
    messageDiv.classList.remove("hidden");

    // Hide message after 5 seconds
    setTimeout(() => {
      messageDiv.classList.add("hidden");
    }, 5000);
  }

  // Handle form submission
  signupForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    // Check if user is authenticated
    if (!currentUser) {
      showMessage(
        "Você deve estar logado como professor para inscrever estudantes.",
        "error"
      );
      return;
    }

    const email = document.getElementById("email").value;
    const activity = activityInput.value;

    // Get activity details to check authorization
    const activityDetails = allActivities[activity];
    if (!canManageActivity(activityDetails)) {
      const message = currentUser.role && currentUser.role.toLowerCase() === "admin" 
        ? "Erro inesperado: administradores devem ter acesso total."
        : "Você não tem permissão para inscrever estudantes nesta atividade. Apenas professores responsáveis ou administradores podem fazê-lo.";
      showMessage(message, "error");
      return;
    }

    try {
      const response = await fetch(
        `/activities/${encodeURIComponent(
          activity
        )}/signup?email=${encodeURIComponent(
          email
        )}&teacher_username=${encodeURIComponent(currentUser.username)}`,
        {
          method: "POST",
        }
      );

      const result = await response.json();

      if (response.ok) {
        showMessage(result.message, "success");
        closeRegistrationModalHandler();
        // Refresh the activities list after successful signup
        fetchActivities();
      } else {
        showMessage(result.detail || "Ocorreu um erro", "error");
      }
    } catch (error) {
      showMessage("Falha na inscrição. Por favor, tente novamente.", "error");
      console.error("Error signing up:", error);
    }
  });

  // Expose filter functions to window for future UI control
  window.activityFilters = {
    setDayFilter,
    setTimeRangeFilter,
  };

  // Initialize app with backend categories
  async function initializeApp() {
    try {
      // Initialize theme first
      initializeTheme();
      
      // Load categories from backend first
      await loadActivityCategories();
      
      // Update category filters in DOM
      updateCategoryFilters();
      
      // Check authentication
      checkAuthentication();
      
      // Initialize other filters
      initializeFilters();
      
      // Load and display activities
      await fetchActivities();
      
      console.log('App inicializado com categorias do backend');
    } catch (error) {
      console.error('Erro na inicialização:', error);
      throw error;
    }
  }

  // Initialize app
  initializeApp();
});
