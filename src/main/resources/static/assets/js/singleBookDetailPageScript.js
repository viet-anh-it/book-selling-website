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

handleDomContentLoadedEvent();

function handleDomContentLoadedEvent() {
  document.addEventListener(`DOMContentLoaded`, (event) => {
    event.preventDefault();

    handleAddToCartButtonClickEvent();
    handleAddToCartQuantityBtnEvent();
    handleRatingStarClickEvent();
    postReviewBtnClickEvent();
    handleLogOutIconEvent();
    handleCartIconEvent();
  });
}

function handleAddToCartButtonClickEvent() {
  const addToCartButton = document.getElementById(`addToCartButton`);
  if (!addToCartButton) return;
  addToCartButton.addEventListener(`click`, async (event) => {
    event.preventDefault();
    const addToCartSuccessToast = document.getElementById(`addToCartSuccessToast`);
    const addToCartErrorToast = document.getElementById(`errorToast`);
    const bookId = document.getElementById(`bookId`).innerText;
    const addToCartQuantity = document.getElementById(`addToCartQuantityInput`).value;
    const addToCartDTO = {
      bookId: bookId,
      addToCartQuantity: addToCartQuantity,
    };
    const addToCartRequestJsonData = JSON.stringify(addToCartDTO);
    const addToCartResponse = await fetch(`http://localhost:8080/api/carts/cartitem`, {
      method: `POST`,
      headers: { "Content-Type": `application/json;charset=utf-8` },
      body: addToCartRequestJsonData,
    });
    const addToCartResponseData = await addToCartResponse.json();
    if (addToCartResponse.status === 201) {
      // Cập nhật số lượng sản phẩm trên icon giỏ hàng
      document.getElementById(`addToCartSuccessMessage`).innerText = addToCartResponseData.message;
      showToast(addToCartSuccessToast);
    } else if (addToCartResponse.status === 400) {
      if (addToCartResponseData.type === `DESERIALIZATION`) {
        document.getElementById(`errorToastMessage`).innerText = `Tải lại trang và thử lại!`;
      } else if (addToCartResponseData.type === `VALIDATION`) {
        if (`bookId` in addToCartResponseData.detail) {
          document.getElementById(`errorToastMessage`).innerText = `Tải lại trang và thử lại!`;
        } else if (`addToCartQuantity` in addToCartResponseData.detail) {
          document.getElementById(`errorToastMessage`).innerText = addToCartResponseData.detail.addToCartQuantity;
        }
      }
      showToast(addToCartErrorToast);
    } else if (addToCartResponse.status === 401) {
      if (addToCartResponseData.type === `TOKEN_EXPIRED`) {
        const refreshTokenResponse = await fetch(`http://localhost:8080/refreshToken`, { method: `PUT` });
        if (refreshTokenResponse.status === 200) {
          const retryAddToCartResponse = await fetch(`http://localhost:8080/cart/item`, { method: `PUT` });
          const retryAddToCartResponseData = retryAddToCartResponse.json();
          if (retryAddToCartResponse.status === 201) {
            // update cart number
            document.getElementById(`addToCartSuccessMessage`).innerText = retryAddToCartResponseData.message;
            showToast(addToCartSuccessToast);
          } else if (retryAddToCartResponse.status === 400) {
            if (retryAddToCartResponseData.type === `DESERIALIZATION`) {
              document.getElementById(`errorToastMessage`).innerText = `Tải lại trang và thử lại!`;
            } else if (retryAddToCartResponseData.type === `VALIDATION`) {
              if (`bookId` in retryAddToCartResponseData.detail) {
                document.getElementById(`errorToastMessage`).innerText = `Tải lại trang và thử lại!`;
              } else if (`addToCartQuantity` in retryAddToCartResponseData.detail) {
                document.getElementById(`errorToastMessage`).innerText = retryAddToCartResponseData.detail.addToCartQuantity;
              }
            }
            showToast(addToCartErrorToast);
          }
        } else if (refreshTokenResponse.status === 400) {
          const logOutResponse = await fetch(`http://localhost:8080/logOut`, {
            method: `DELETE`,
          });
          document.getElementById(`errorToastMessage`).innerText = `Phiên đăng nhập của bạn đã hết hạn! Vui lòng đăng nhập lại!`;
          showToast(addToCartErrorToast);
        }
      } else if (addToCartResponseData.type === `AUTHENTICATION`) {
        await fetch(`http://localhost:8080/revokeRefreshToken`, {
          method: `DELETE`,
        });
        // document.getElementById(
        //   `errorToastMessage`
        // ).innerText = `Vui lòng đăng nhập để thêm vào giỏ!`;
        // showToast(addToCartErrorToast);
        window.location.assign(`http://localhost:8080/error/401Unauthorized`);
      }
    } else if (addToCartResponse.status === 403) {
      // TODO: bật toast thông báo bạn không có quyền thực hiện hành động này
      // document.getElementById(
      //   `errorToastMessage`
      // ).innerText = `Bạn không có quyền thực hiện hành động này!`;
      // showToast(addToCartErrorToast);
      window.location.assign(`http://localhost:8080/error/403Forbidden`);
    }
  });
}

function showToast(toastEl) {
  console.log(toastEl);
  const progressBar = toastEl.querySelector(".progress-bar");
  console.log(progressBar);

  // 2. Khởi tạo Bootstrap Toast (tắt autohide để tự kiểm soát)
  toastEl.classList.remove("d-none");
  const myToast = new bootstrap.Toast(toastEl, {
    autohide: false,
  });

  // Reset lại progress về 100%
  progressBar.style.transition = "none";
  progressBar.style.width = "100%";

  // Buộc reflow để reset transition áp dụng
  void progressBar.offsetWidth;

  // 4. Thiết lập transition cho progress bar: 5s linear
  progressBar.style.transition = "width 5s linear";

  // 3. Hiển thị toast
  myToast.show();

  // 5. Kích hoạt animation: từ 100% xuống 0%
  requestAnimationFrame(function () {
    progressBar.style.width = "0%";
  });

  // Khi transition kết thúc, ẩn toast và cleanup listener
  const onEnd = () => {
    toastEl.classList.add("d-none");
    myToast.hide();
    progressBar.removeEventListener("transitionend", onEnd);
  };
  progressBar.addEventListener("transitionend", onEnd, { once: true });
}

function handleAddToCartQuantityBtnEvent() {
  // (1) Khởi tạo tham chiếu
  const minusBtn = document.getElementById("addToCartQuantityMinusBtn");
  const plusBtn = document.getElementById("addToCartQuantityPlusBtn");
  const inputField = document.getElementById("addToCartQuantityInput");

  // (2) Hàm cập nhật trạng thái nút “-”
  function updateMinusState() {
    const value = parseInt(inputField.value, 10);
    minusBtn.disabled = value <= 1;
  }

  // (3) Xử lý click
  minusBtn.addEventListener("click", () => {
    let v = parseInt(inputField.value, 10);
    v = isNaN(v) ? 1 : Math.max(v - 1, 1);
    inputField.value = v;
    updateMinusState();
  });
  plusBtn.addEventListener("click", () => {
    let v = parseInt(inputField.value, 10);
    v = isNaN(v) ? 1 : v + 1;
    inputField.value = v;
    updateMinusState();
  });

  // (4) Xử lý nhập
  inputField.addEventListener("input", () => {
    let v = parseInt(inputField.value, 10);
    if (isNaN(v) || v <= 0) v = 1;
    inputField.value = v;
    updateMinusState();
  });

  // (5) Xử lý paste
  inputField.addEventListener("paste", (e) => {
    e.preventDefault();
    const text = (e.clipboardData || window.clipboardData).getData("text");
    let filtered = text.replace(/\D+/g, "") || "1";
    filtered = filtered.replace(/^0+/, "") || "1";
    inputField.value = filtered;
    updateMinusState();
  });

  // Khởi tạo trạng thái ban đầu
  updateMinusState();
}

function handleRatingStarClickEvent() {
  const ratingStars = document.querySelectorAll(`.ratingStar`);
  ratingStars.forEach((ratingStar) =>
    ratingStar.addEventListener(`click`, (event) => {
      event.preventDefault();
      const givenRatingPoint = Number(event.currentTarget.dataset.rating);
      ratingStars.forEach((ratingStar) => {
        const starRatingPoint = Number(ratingStar.dataset.rating);
        if (starRatingPoint <= givenRatingPoint) {
          ratingStar.classList.add(`text-secondary`);
        } else {
          ratingStar.classList.remove(`text-secondary`);
        }
      });
    })
  );
}

function postReviewBtnClickEvent() {
  const postReviewBtn = document.getElementById(`postReviewBtn`);
  if (!postReviewBtn) return;
  postReviewBtn.addEventListener(`click`, async (event) => {
    event.preventDefault();
    await requestPostReviewApi();
  });
}

async function requestPostReviewApi() {
  const bookId = Number(document.getElementById(`bookId`).innerText);
  const reviewerDisplayName = document.getElementById(`reviewerDisplayName`).value;
  const reviewerEmail = document.getElementById(`reviewerEmail`).value;
  const reviewerComment = document.getElementById(`reviewerComment`).value;
  let givenRatingPoint = 5 - document.querySelectorAll(`.ratingStar:not(.text-secondary)`).length;
  givenRatingPoint = givenRatingPoint === 0 ? null : givenRatingPoint;
  const reviewDto = {
    bookId: bookId,
    reviewerDisplayName: reviewerDisplayName,
    reviewerEmail: reviewerEmail,
    reviewerComment: reviewerComment,
    givenRatingPoint: givenRatingPoint,
  };
  const jsonData = JSON.stringify(reviewDto);
  console.log(jsonData);
  const postReviewResponse = await fetch(`http://localhost:8080/api/reviews`, {
    method: `POST`,
    headers: { "Content-Type": `application/json;charset=utf-8` },
    body: jsonData,
  });
  const responseData = await postReviewResponse.json();
  const errorToast = document.getElementById(`errorToast`);
  if (postReviewResponse.status === 201) {
    document.querySelectorAll(`.is-invalid`).forEach((element) => element.classList.remove(`is-invalid`));
    showPostReviewSuccessModal();
  } else if (postReviewResponse.status === 400) {
    switch (responseData.type) {
      case `VALIDATION`:
        if (`bookId` in responseData.detail) {
          document.querySelectorAll(`.is-invalid`).forEach((element) => element.classList.remove(`is-invalid`));
          document.getElementById(`errorToastMessage`).innerText = `Tải lại trang và thử lại!`;
          showToast(errorToast);
        } else {
          document.querySelectorAll(`.is-invalid`).forEach((element) => element.classList.remove(`is-invalid`));
          Object.entries(responseData.detail).forEach(([key, value]) => {
            const element = document.getElementById(key);
            const invalidFeedback = element.nextElementSibling;
            element.classList.add(`is-invalid`);
            invalidFeedback.innerText = value;
          });
        }
        break;
      case `DESERIALIZATION`:
        if (`bookId` in responseData.detail) {
          document.querySelectorAll(`.is-invalid`).forEach((element) => element.classList.remove(`is-invalid`));
          document.getElementById(`errorToastMessage`).innerText = `Tải lại trang và thử lại!`;
          showToast(errorToast);
        }
        break;
      default:
        break;
    }
  } else if (postReviewResponse.status === 401) {
    if (responseData.type === `AUTHENTICATION`) {
      await requestRevokeRefreshTokenApi();
      // document.getElementById(
      //   `errorToastMessage`
      // ).innerText = `Vui lòng đăng nhập để gửi đánh giá!`;
      // showToast(errorToast);
      window.location.assign(`http://localhost:8080/error/401Unauthorized`);
    } else if (responseData.type === `TOKEN_EXPIRED`) {
      const refreshTokenResponse = await requestRefreshTokenApi();
      switch (refreshTokenResponse.status) {
        case 200:
          await requestPostReviewApi();
          break;
        case 400:
          await requestLogoutApi();
          break;
        default:
          break;
      }
    }
  } else if (postReviewResponse.status === 403) {
    // TODO: bật toast thông báo bạn không có quyền thực hiện hành động này
    // document
    //   .querySelectorAll(`.is-invalid`)
    //   .forEach((element) => element.classList.remove(`is-invalid`));
    // document.getElementById(
    //   `errorToastMessage`
    // ).innerText = `Bạn không có quyền thực hiện hành động này!`;
    // showToast(errorToast);
    window.location.assign(`http://localhost:8080/error/403Forbidden`);
  }
}

async function requestRefreshTokenApi() {
  const response = await fetch(`http://localhost:8080/refreshToken`, {
    method: `PUT`,
  });
  return response;
}

async function requestLogoutApi() {
  const response = await fetch(`http://localhost:8080/logOut`, {
    method: `DELETE`,
  });
  return response;
}

async function requestRevokeRefreshTokenApi() {
  const response = await fetch(`http://localhost:8080/revokeRefreshToken`, {
    method: `DELETE`,
  });
  return response;
}

function showPostReviewSuccessModal() {
  const modalElement = document.getElementById(`postReviewSuccessModal`);
  const modalInstance = new bootstrap.Modal(modalElement);
  modalInstance.show();
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

async function fetchRevokeRefreshTokenApi() {
  await fetch(`http://localhost:8080/revokeRefreshToken`, { method: `DELETE` });
}

function handleCartIconEvent() {
  const cartIcon = document.getElementById(`cartIcon`);
  if (!cartIcon) return;
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
