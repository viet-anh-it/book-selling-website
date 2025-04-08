// sign up button script
let signUpBtn = document.querySelector(`.sign-up-btn`);
signUpBtn.addEventListener(`click`, async (event) => {
  event.preventDefault();

  let email = document.getElementById(`username`).value;
  let password = document.getElementById(`password`).value;

  let signUpRequestDTO = {
    email: email,
    password: password,
  };

  let requestBody = JSON.stringify(signUpRequestDTO);

  let response = await fetch(`http://localhost:8080/sign-up`, {
    method: `POST`,
    headers: {
      "Content-Type": `application/json;charset=utf-8`,
    },
    body: requestBody,
  });

  let responseBody = await response.json();

  let emailError = document.querySelector(`.email-error`);
  let passwordError = document.querySelector(`.password-error`);

  if (response.status === 201) {
    window.location.assign(`http://localhost:8080/log-in`);
  } else if (response.status === 400) {
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
