// script when login page is fully loaded
window.addEventListener(`load`, async (event) => {
  event.preventDefault();

  let commonError = document.querySelector(`.common-error`);
  let successMessage = document.querySelector(`.success-message`);

  if (sessionStorage.getItem(`resetPasswordTokenError`)) {
    commonError.innerText = sessionStorage.getItem(`resetPasswordTokenError`);
    sessionStorage.removeItem(`resetPasswordTokenError`);
  } else if (sessionStorage.getItem(`resetPasswordSuccessMessage`)) {
    successMessage.innerText = sessionStorage.getItem(`resetPasswordSuccessMessage`);
    sessionStorage.removeItem(`resetPasswordSuccessMessage`);
  } else if (sessionStorage.getItem(`logOutSuccess`)) {
    successMessage.innerText = sessionStorage.getItem(`logOutSuccess`);
    sessionStorage.removeItem(`logOutSuccess`);
  }
});

// log in button script
let logInBtn = document.querySelector(`.log-in-btn`);
logInBtn.addEventListener(`click`, async (event) => {
  event.preventDefault();

  let email = document.getElementById(`username`).value;
  let password = document.getElementById(`password`).value;

  let logInRequestDTO = {
    email: email,
    password: password,
  };

  let requestBody = JSON.stringify(logInRequestDTO);

  let response = await fetch(`http://localhost:8080/api/auth/logIn`, {
    method: `POST`,
    headers: {
      "Content-Type": `application/json;charset=utf-8`,
    },
    body: requestBody,
  });

  let responseBody = await response.json();

  let commonError = document.querySelector(`.common-error`);
  let emailError = document.querySelector(`.email-error`);
  let passwordError = document.querySelector(`.password-error`);

  if (response.status === 200) {
    switch (responseBody.data.role) {
      case `ROLE_CUSTOMER`:
        window.location.assign(`http://localhost:8080/books`);
        break;
      case `ROLE_STAFF`:
      case `ROLE_MANAGER`:
      case `ROLE_ADMIN`:
        window.location.assign(`http://localhost:8080/dashboard`);
        break;
      default:
        break;
    }
  } else if (response.status === 400) {
    commonError.innerText = ``;
    let errorDetail = responseBody.detail;
    if (`email` in errorDetail) {
      emailError.innerText = errorDetail.email;
    } else {
      emailError.innerText = ``;
    }

    if (`password` in errorDetail) {
      passwordError.innerText = errorDetail.password;
    } else {
      passwordError.innerText = ``;
    }
  } else if (response.status === 401) {
    commonError.innerText = responseBody.detail;
    emailError.innerText = ``;
    passwordError.innerText = ``;
  }
});

// show/hide password field script
let passwordField = document.getElementById(`password`);
let togglePassword = document.querySelector(`.toggle-password`);
togglePassword.addEventListener(`click`, (event) => {
  event.preventDefault();

  let faSolid = document.querySelector(`.fa-solid`);
  if (faSolid.classList.contains(`fa-eye-slash`)) {
    togglePassword.innerHTML = `<i class="fa-solid fa-eye"></i>Hiện`;
    passwordField.setAttribute(`type`, `text`);
  } else if (faSolid.classList.contains(`fa-eye`)) {
    togglePassword.innerHTML = `<i class="fa-solid fa-eye-slash"></i>Ẩn`;
    passwordField.setAttribute(`type`, `password`);
  }
});

// forgot password link script
let forgotPasswordLink = document.getElementById(`forgot-password-link`);
forgotPasswordLink.addEventListener(`click`, async (event) => {
  event.preventDefault();

  let email = document.getElementById(`username`).value;
  let requestBody = {
    email: email,
  };
  let jsonRequestBody = JSON.stringify(requestBody);

  let response = await fetch(`http://localhost:8080/sendForgotPasswordEmail`, {
    method: `POST`,
    headers: {
      "Content-Type": `application/json;charset=utf-8`,
    },
    body: jsonRequestBody,
  });
  let responseBody = await response.json();

  let successMessage = document.querySelector(`.success-message`);
  let commonError = document.querySelector(`.common-error`);
  let emailError = document.querySelector(`.email-error`);
  let passwordError = document.querySelector(`.password-error`);

  if (response.status === 200) {
    successMessage.innerText = responseBody.message;
    emailError.innerText = ``;
    passwordError.innerText = ``;
  } else if (response.status === 400) {
    commonError.innerText = ``;
    emailError.innerText = responseBody.detail.email;
    passwordError.innerText = ``;
  }
});
