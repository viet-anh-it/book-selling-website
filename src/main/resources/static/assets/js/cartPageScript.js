(function ($) {
  "use strict";

  // Back to top button
  $(window).scroll(function () {
    if ($(this).scrollTop() > 300) {
      $(".back-to-top").fadeIn("slow");
    } else {
      $(".back-to-top").fadeOut("slow");
    }
  });

  $(".back-to-top").click(function () {
    $("html, body").animate({ scrollTop: 0 }, 1500, "easeInOutExpo");
    return false;
  });
})(jQuery);

document.addEventListener(`DOMContentLoaded`, (event) => {
  event.preventDefault();

  // TODO: các hàm xử lý sự kiện
  handleCartIconEvent();
  handleLogOutIconEvent();
});

function handleCartIconEvent() {
  const cartIcon = document.getElementById(`cartIcon`);
  cartIcon.addEventListener(`click`, async (event) => {
    event.preventDefault();
    const response = await fetch(`http://localhost:8080/cart`, {
      method: `GET`,
    });
    // const responseObj = await response.json();
    if (response.status === 200) {
      window.location.assign(`http://localhost:8080/cart`);
    } else if (response.status === 401) {
      // switch (responseObj.type) {
      //   case `TOKEN_EXPIRED`:
      //     // TODO: gọi API refresh token
      //     await fetchRefreshTokenApi();
      //     break;
      //   case `AUTHENTICATION`:
      //     // TODO: gọi API revoke refresh token và điều hướng đến trang 401 Unauthorized
      //     await fetchRevokeRefreshTokenApi();
      //     window.location.assign(`http://localhost:8080/error/401Unauthorized`);
      //     break;
      //   case `INVALID_TOKEN`:
      //     // TODO: gọi API logout
      //     await fetchLogOutApi();
      //     break;
      //   default:
      //     break;
      // }
      await fetchRevokeRefreshTokenApi();
      window.location.assign(`http://localhost:8080/error/401Unauthorized`);
    } else if (response.status === 403) {
      window.location.assign(`http://localhost:8080/error/403Forbidden`);
    }
  });
}

async function fetchRevokeRefreshTokenApi() {
  await fetch(`http://localhost:8080/revokeRefreshToken`, { method: `DELETE` });
}

function handleLogOutIconEvent() {
  const logOutIcon = document.getElementById(`logOutIcon`);
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
