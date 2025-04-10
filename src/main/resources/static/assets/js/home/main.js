// log out button script
let logOutBtn = document.getElementById(`log-out-btn`);
logOutBtn.addEventListener(`click`, async (event) => {
  event.preventDefault();

  let logOutResponse = await fetch(`http://localhost:8080/log-out`, {
    method: `DELETE`,
  });

  if (logOutResponse.status === 200) {
    window.location.assign(`http://localhost:8080/log-in`);
  } else if (
    logOutResponse.status === 401 &&
    logOutResponse.headers.get(`X-Error-Type`) === `TOKEN_EXPIRED`
  ) {
    let refreshTokenResponse = await fetch(
      `http://localhost:8080/refresh-token`,
      {
        method: `PUT`,
      }
    );

    if (refreshTokenResponse.status === 200) {
      await fetch(`http://localhost:8080/log-out`, {
        method: `DELETE`,
      });
      window.location.assign(`http://localhost:8080/log-in`);
    } else if (refreshTokenResponse.status === 400) {
      window.location.assign(`http://localhost:8080/log-in`);
    }
  } else if (
    logOutResponse.status === 401 &&
    (logOutResponse.headers.get(`X-Error-Type`) === `INVALID_TOKEN` ||
      logOutResponse.headers.get(`X-Error-Type`) === `AUTHENTICATION`)
  ) {
    await fetch(`http://localhost:8080/revoke-refresh-token`, {
      method: `DELETE`,
    });
    window.location.assign(`http://localhost:8080/log-in`);
  }
});

// cart link script
let cartLink = document.getElementById(`cart-link`);
cartLink.addEventListener(`click`, async (event) => {
  event.preventDefault();
  let cartLinkHrefAttributeValue = cartLink.href;

  let response = await fetch(`http://localhost:8080/cart`, {
    method: `GET`,
  });

  if (response.status === 200) {
    window.location.assign(cartLinkHrefAttributeValue);
  } else if (
    response.status === 401 &&
    response.headers.get(`X-Error-Type`) === `TOKEN_EXPIRED`
  ) {
    let refreshTokenResponse = await fetch(
      `http://localhost:8080/refresh-token`,
      {
        method: `PUT`,
      }
    );

    if (refreshTokenResponse.status === 200) {
      window.location.assign(cartLinkHrefAttributeValue);
    } else if (refreshTokenResponse.status === 400) {
      window.location.assign(`http://localhost:8080/log-in`);
    }
  } else if (
    response.status === 401 &&
    (response.headers.get(`X-Error-Type`) === `INVALID_TOKEN` ||
      response.headers.get(`X-Error-Type`) === `AUTHENTICATION`)
  ) {
    await fetch(`http://localhost:8080/revoke-refresh-token`, {
      method: `DELETE`,
    });
    window.location.assign(`http://localhost:8080/log-in`);
  }
});
