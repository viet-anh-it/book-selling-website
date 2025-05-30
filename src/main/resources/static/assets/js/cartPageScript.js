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

// constants
const serverBaseUrl = `http://localhost:8080`;
const cartItemApiBasePath = `/api/cartitems`;
const cartApiBasePath = `/api/carts`;
const bookApiBasePath = `/api/books`;
const unauthorizedPath = `/error/401Unauthorized`;
const forbiddenPath = `/error/403Forbidden`;
const notFoundPath = `/error/404NotFound`;
const internalServerErrorPath = `/error/500InternalServerError`;

// cart item table
const cartItemTableBody = document.getElementById(`cartItemTableBody`);
const cartValuePane = document.getElementById(`cartValuePane`);
const cartSubtotal = document.getElementById(`cartSubtotal`);
const cartGrandtotal = document.getElementById(`cartGrandtotal`);

// toast
// success toast
const successToast = document.getElementById(`successToast`);
const successToastMessage = document.getElementById(`successToastMessage`);
//error toast
const errorToast = document.getElementById(`errorToast`);
const errorToastMessage = document.getElementById(`errorToastMessage`);

document.addEventListener(`DOMContentLoaded`, (event) => {
  event.preventDefault();

  // TODO: các hàm xử lý sự kiện
  handleCartIconEvent();
  handleLogOutIconEvent();
  handleDeleteCartItemBtnsEvent(document.querySelectorAll(`.deleteCartItemBtn`));
  handleIncreaseAddToCartByOneBtnsEvent(document.querySelectorAll(`.increaseAddToCartQuantityByOneBtn`));
  handleDecreaseAddToCartByOneBtnsEvent(document.querySelectorAll(`.decreaseAddToCartQuantityByOneBtn`));
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

function handleDeleteCartItemBtnsEvent(deleteCartItemBtns) {
  deleteCartItemBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();
      const cartItemId = btn.dataset.cartItemId;
      const deleteCartItemByIdApiUrl = `${serverBaseUrl}${cartItemApiBasePath}/${cartItemId}`;
      await callDeleteCartItemByIdApi(deleteCartItemByIdApiUrl);
      // TODO: render cart item rows
      const getAllCartItemsApiUrl = `${serverBaseUrl}${cartItemApiBasePath}`;
      const getAllCartItemsResponse = await callGetAllCartItemsApi(getAllCartItemsApiUrl);
      const cartItems = getAllCartItemsResponse.data;
      renderCartItemTableRows(cartItems);
      // TODO: update cart value
      const getCartApiUrl = `${serverBaseUrl}${cartApiBasePath}`;
      const getCartResponse = await callGetCartApi(getCartApiUrl);
      const cart = getCartResponse.data;
      updateCartValuePane(cart);
    })
  );
}

async function callDeleteCartItemByIdApi(deleteCartItemByIdApiUrl) {
  const response = await fetch(deleteCartItemByIdApiUrl, { method: `DELETE` });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      successToastMessage.innerText = responseObj.message;
      showToast(successToast);
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

function showToast(toastEl) {
  const progressBar = toastEl.querySelector(".progress-bar");

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

async function callGetAllCartItemsApi(getAllCartItemsApiUrl) {
  const response = await fetch(getAllCartItemsApiUrl, { method: `GET` });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      return responseObj;
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

function renderCartItemTableRows(cartItems) {
  cartItemTableBody.innerHTML = ``;
  if (!cartItems || cartItems.length === 0) {
    const tr = document.createElement(`tr`);
    tr.innerHTML = `
    <td colspan="8" class="text-center">Chưa có sản phẩm nào.</td>
    `;
    cartItemTableBody.appendChild(tr);
  } else {
    cartItems.forEach((cartItem, index) => {
      const tr = document.createElement(`tr`);
      tr.innerHTML = `
      <th scope="row" class="text-nowrap">
                  <div class="d-flex align-items-center justify-content-center">
                    <img src="${serverBaseUrl}${cartItem.image.replace(/\\/g, "/")}" class="img-thumbnail object-fit-cover overflow-hidden rounded" style="height: 80px; aspect-ratio: 3/4" alt="${cartItem.name}" />
                  </div>
                </th>
                <td class="text-wrap">
                  <p>${cartItem.name}</p>
                </td>
                <td class="text-center text-nowrap">
                  <p class="cartItemPrice">${cartItem.price.toLocaleString("vi-VN")} ₫</p>
                </td>
                <td>
                  <div class="input-group quantity mx-auto" style="width: 100px">
                    <div class="input-group-btn">
                      <button class="decreaseAddToCartQuantityByOneBtn btn btn-sm btn-minus rounded-circle bg-light border" data-book-id="${cartItem.bookId}">
                        <i class="fa fa-minus"></i>
                      </button>
                    </div>
                    <input type="number" class="addToCartQuantityInput form-control form-control-sm text-center border-0" value="${cartItem.addToCartQuantity}" readonly/>
                    <div class="input-group-btn">
                      <button class="increaseAddToCartQuantityByOneBtn btn btn-sm btn-plus rounded-circle bg-light border" data-book-id="${cartItem.bookId}">
                        <i class="fa fa-plus"></i>
                      </button>
                    </div>
                  </div>
                </td>
                <td class="text-center text-nowrap">
                  <p class="cartItemRowSubtotal">${cartItem.totalPrice.toLocaleString("vi-VN")} ₫</p>
                </td>
                <td class="text-center">
                  <button data-cart-item-id="${cartItem.cartItemId}" class="deleteCartItemBtn btn btn-md rounded-circle bg-light border">
                    <i class="fa fa-times text-danger"></i>
                  </button>
                </td>
      `;
      cartItemTableBody.appendChild(tr);
    });
    handleDeleteCartItemBtnsEvent(document.querySelectorAll(`.deleteCartItemBtn`));
    handleIncreaseAddToCartByOneBtnsEvent(document.querySelectorAll(`.increaseAddToCartQuantityByOneBtn`));
    handleDecreaseAddToCartByOneBtnsEvent(document.querySelectorAll(`.decreaseAddToCartQuantityByOneBtn`));
  }
}

async function callGetCartApi(getCartApiUrl) {
  const response = await fetch(getCartApiUrl, { method: `GET` });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      return responseObj;
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

function updateCartValuePane(cart) {
  cartSubtotal.innerText = `${cart.totalPrice.toLocaleString(`vi-VN`)} ₫`;
  cartGrandtotal.innerText = `${cart.totalPrice.toLocaleString(`vi-VN`)} ₫`;
}

function handleIncreaseAddToCartByOneBtnsEvent(increaseAddToCartQuantityByOneBtns) {
  increaseAddToCartQuantityByOneBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();
      // xử lý tăng số lượng
      const closestInputGroup = btn.closest(`.input-group`);
      const addToCartQuantityInput = closestInputGroup.querySelector(`.addToCartQuantityInput`);
      const addToCartQuantity = Number(addToCartQuantityInput.value);
      if (Number.isNaN(addToCartQuantity)) return;
      if (addToCartQuantity < 0) return;
      if (!Number.isInteger(addToCartQuantity)) return;
      // gọi API check số lượng tồn kho
      const bookId = btn.dataset.bookId;
      const apiUrl = `${serverBaseUrl}${bookApiBasePath}/${bookId}/stockquantity`;
      const stockQuantity = await callGetStockQuantityByBookIdApi(apiUrl);
      if (stockQuantity <= 0) {
        errorToastMessage.innerText = `Chỉ còn ${stockQuantity} sản phẩm!`;
        showToast(errorToast);
        // btn.setAttribute(`disabled`, `true`);
        return;
      }
      addToCartQuantityInput.value = addToCartQuantity + 1;
      // xử lý tăng thành tiền của dòng
      const closestTableRowElement = btn.closest(`tr`);
      const cartItemPrice = closestTableRowElement.querySelector(`.cartItemPrice`);
      const price = Number(cartItemPrice.innerText.replace(/[^\d]/g, ``));
      const subtotal = price * Number(addToCartQuantityInput.value);
      const cartItemRowSubtotal = closestTableRowElement.querySelector(`.cartItemRowSubtotal`);
      cartItemRowSubtotal.innerText = `${subtotal.toLocaleString("vi-VN")} ₫`;
      // gọi API update số lượng và thành tiền của cart item
      // gọi API update tổng tiền giỏ hàng
      // gọi API lấy thông tin giỏ hàng và update lại lên pane
      const cartItemId = btn.dataset.cartItemId;
      const updateCartItemByIdApiUrl = `${serverBaseUrl}${cartItemApiBasePath}/${cartItemId}`;
      const cartItemDto = {
        addToCartQuantity: addToCartQuantityInput.value,
        totalPrice: subtotal,
      };
      const json = JSON.stringify(cartItemDto);
      const cartDto = await callUpdateCartItemByIdApi(updateCartItemByIdApiUrl, json);
      if (cartDto === null) {
        errorToastMessage.innerText = `Vui lòng tải lại trang và thử lại!`;
        showToast(errorToast);
        return;
      }
      cartSubtotal.innerText = `${cartDto.totalPrice.toLocaleString("vi-VN")} ₫`;
      cartGrandtotal.innerText = `${cartDto.totalPrice.toLocaleString("vi-VN")} ₫`;
    })
  );
}

function handleDecreaseAddToCartByOneBtnsEvent(decreaseAddToCartQuantityByOneBtns) {
  decreaseAddToCartQuantityByOneBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();
      const closestInputGroup = btn.closest(`.input-group`);
      const addToCartQuantityInput = closestInputGroup.querySelector(`.addToCartQuantityInput`);
      const addToCartQuantity = Number(addToCartQuantityInput.value);
      if (Number.isNaN(addToCartQuantity)) return;
      if (addToCartQuantity <= 1) {
        errorToastMessage.innerText = `Số lượng tối thiểu là 1!`;
        showToast(errorToast);
        // btn.setAttribute(`disabled`, `true`);
        return;
      }
      if (!Number.isInteger(addToCartQuantity)) return;
      addToCartQuantityInput.value = addToCartQuantity - 1;
      // xử lý giảm thành tiền của dòng
      const closestTableRowElement = btn.closest(`tr`);
      const cartItemPrice = closestTableRowElement.querySelector(`.cartItemPrice`);
      const price = Number(cartItemPrice.innerText.replace(/[^\d]/g, ``));
      const subtotal = price * Number(addToCartQuantityInput.value);
      const cartItemRowSubtotal = closestTableRowElement.querySelector(`.cartItemRowSubtotal`);
      cartItemRowSubtotal.innerText = `${subtotal.toLocaleString("vi-VN")} ₫`;
      // gọi API update số lượng và thành tiền của cart item
      // gọi API update tổng tiền giỏ hàng
      // gọi API lấy thông tin giỏ hàng và update lại lên pane
      const cartItemId = btn.dataset.cartItemId;
      const updateCartItemByIdApiUrl = `${serverBaseUrl}${cartItemApiBasePath}/${cartItemId}`;
      const cartItemDto = {
        addToCartQuantity: addToCartQuantityInput.value,
        totalPrice: subtotal,
      };
      const json = JSON.stringify(cartItemDto);
      const cartDto = await callUpdateCartItemByIdApi(updateCartItemByIdApiUrl, json);
      if (cartDto === null) {
        errorToastMessage.innerText = `Vui lòng tải lại trang và thử lại!`;
        showToast(errorToast);
        return;
      }
      cartSubtotal.innerText = `${cartDto.totalPrice.toLocaleString("vi-VN")} ₫`;
      cartGrandtotal.innerText = `${cartDto.totalPrice.toLocaleString("vi-VN")} ₫`;
    })
  );
}

async function callGetStockQuantityByBookIdApi(apiUrl) {
  const response = await fetch(apiUrl, { method: `GET` });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      return responseObj.data;
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

async function callUpdateCartItemByIdApi(url, json) {
  const response = await fetch(url, { method: `PATCH`, headers: { "Content-Type": `application/json;charset=utf-8` }, body: json });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      return Object.hasOwn(responseObj, `data`) ? responseObj.data : null;
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
