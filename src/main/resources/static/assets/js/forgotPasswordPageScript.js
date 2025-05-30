// update password button script
let updatePasswordButton = document.getElementById(`update-password-btn`);
updatePasswordButton.addEventListener(`click`, async (event) => {
  event.preventDefault();

  let token = document.getElementById(`token`).value;
  let password = document.getElementById(`password`).value;
  let confirmPassword = document.getElementById(`confirm`).value;
  let requestBody = {
    token: token,
    password: password,
    confirmPassword: confirmPassword,
  };
  let jsonRequestBody = JSON.stringify(requestBody);

  let response = await fetch(`http://localhost:8080/resetPassword`, {
    method: `PUT`,
    headers: {
      "Content-Type": `application/json;charset=utf-8`,
    },
    body: jsonRequestBody,
  });
  let responseBody = await response.json();

  let successMessage = document.querySelector(`.success-message`);
  let passwordError = document.querySelector(`.password-error`);
  let confirmError = document.querySelector(`.confirm-error`);

  if (response.status === 200) {
    passwordError.innerText = ``;
    confirmError.innerText = ``;
    sessionStorage.setItem(`resetPasswordSuccessMessage`, responseBody.message);
    window.location.assign(`http://localhost:8080/logIn`);
  } else if (response.status === 400) {
    successMessage.innerText = ``;
    if (`token` in responseBody.detail) {
      sessionStorage.setItem(`resetPasswordTokenError`, responseBody.detail.token);
      window.location.assign(`http://localhost:8080/logIn`);
      return;
    }

    if (`password` in responseBody.detail) {
      passwordError.innerText = responseBody.detail.password;
    } else {
      passwordError.innerText = ``;
    }

    if (`confirmPassword` in responseBody.detail) {
      confirmError.innerText = responseBody.detail.confirmPassword;
    } else {
      confirmError.innerText = ``;
    }
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

let confirmPasswordField = document.getElementById(`confirm`);
let toggleConfirmPassword = document.querySelector(`.toggle-confirm`);
toggleConfirmPassword.addEventListener(`click`, (event) => {
  event.preventDefault();

  let faSolid = document.querySelector(`.toggle-confirm .fa-solid`);
  if (faSolid.classList.contains(`fa-eye-slash`)) {
    toggleConfirmPassword.innerHTML = `<i class="fa-solid fa-eye"></i>Hiện`;
    confirmPasswordField.setAttribute(`type`, `text`);
  } else if (faSolid.classList.contains(`fa-eye`)) {
    toggleConfirmPassword.innerHTML = `<i class="fa-solid fa-eye-slash"></i>Ẩn`;
    confirmPasswordField.setAttribute(`type`, `password`);
  }
});
