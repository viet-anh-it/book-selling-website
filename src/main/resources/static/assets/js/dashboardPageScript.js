const serverBaseUrl = `http://localhost:8080`;
const logOutApiUrl = `${serverBaseUrl}/logOut`;
const loginPageUrl = `${serverBaseUrl}/logIn`;

handleDOMContentLoadedEvent();

function handleDOMContentLoadedEvent() {
  document.addEventListener(`DOMContentLoaded`, (event) => {
    event.preventDefault();

    // TODO
    handleLogOutLinkEvent();
  });
}

async function handle401Unauthorized() {
  await fetchRevokeRefreshTokenApi();
  window.location.assign(`${serverBaseUrl}${unauthorizedPath}`);
}

function handle403Forbidden() {
  window.location.assign(`${serverBaseUrl}${forbiddenPath}`);
}

function handle500InternalServerError() {
  window.location.assign(`${serverBaseUrl}${internalServerErrorPath}`);
}

function handle404NotFound() {
  window.location.assign(`${serverBaseUrl}${notFoundPath}`);
}

async function fetchRevokeRefreshTokenApi() {
  await fetch(`${serverBaseUrl}/revokeRefreshToken`, { method: `DELETE` });
}

function handleLogOutLinkEvent() {
  logOutLink.addEventListener(`click`, async (event) => {
    event.preventDefault();
    await callLogOutApi(logOutApiUrl);
  });
}

async function callLogOutApi(logOutApiUrl) {
  const response = await fetch(logOutApiUrl, { method: `DELETE` });
  switch (response.status) {
    case 200:
      window.location.assign(loginPageUrl);
      break;
    case 401:
      await handle401Unauthorized();
      break;
    case 403:
      handle403Forbidden();
      break;
    case 500:
      handle500InternalServerError();
      break;
    default:
      break;
  }
}
