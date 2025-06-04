const serverBaseUrl = `http://localhost:8080`;
const logOutApiUrl = `${serverBaseUrl}/logOut`;
const loginPageUrl = `${serverBaseUrl}/logIn`;

handleDOMContentLoadedEvent();

function handleDOMContentLoadedEvent() {
  document.addEventListener(`DOMContentLoaded`, (event) => {
    event.preventDefault();

    // TODO
    handleLogOutIconEvent();
  });
}

function handleLogOutIconEvent() {
  const logOutIcon = document.getElementById(`logOutIcon`);
  if (!logOutIcon) return;
  logOutIcon.addEventListener(`click`, async (event) => {
    event.preventDefault();
    // TODO: gọi API logout
    await fetchLogOutApi();
  });
}

async function fetchLogOutApi() {
  const response = await fetch(`http://localhost:8080/logOut`, {
    method: `DELETE`,
  });
  const responseObj = await response.json();
  if (response.status === 200) {
    sessionStorage.setItem(`logOutSuccess`, `Bạn đã đăng xuất!`);
    window.location.assign(`http://localhost:8080/logIn`);
  } else if (response.status === 401 && responseObj.type === `TOKEN_EXPIRED`) {
    // TODO: gọi API refresh token
    await fetchRefreshTokenApi();
  } else {
    await fetchRevokeRefreshTokenApi();
    sessionStorage.setItem(`logOutSuccess`, `Bạn đã đăng xuất!`);
    window.location.assign(`http://localhost:8080/logIn`);
  }
}

async function fetchRefreshTokenApi() {
  const response = await fetch(`http://localhost:8080/refreshToken`, {
    method: `PUT`,
  });
  if (response.status === 200) {
    // TODO: gọi lại API logout
    await fetchLogOutApi();
  } else if (response.status === 400) {
    // TODO: gọi API revoke refresh token
    await fetchRevokeRefreshTokenApi();
    sessionStorage.setItem(`logOutSuccess`, `Bạn đã đăng xuất!`);
    window.location.assign(`http://localhost:8080/logIn`);
  }
}

async function fetchRevokeRefreshTokenApi() {
  await fetch(`http://localhost:8080/revokeRefreshToken`, { method: `DELETE` });
}
