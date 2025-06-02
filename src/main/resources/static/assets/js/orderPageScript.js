// constants
const serverBaseUrl = `http://localhost:8080`;
const cartItemApiBasePath = `/api/cartitems`;
const cartApiBasePath = `/api/carts`;
const bookApiBasePath = `/api/books`;
const orderApiBasePath = `/api/orders`;
const unauthorizedPath = `/error/401Unauthorized`;
const forbiddenPath = `/error/403Forbidden`;
const notFoundPath = `/error/404NotFound`;
const internalServerErrorPath = `/error/500InternalServerError`;
const orderSuccessPath = `/orders/success`;

const confirmOrderBtn = document.getElementById(`confirmOrderBtn`);
const name = document.getElementById(`name`);
const phone = document.getElementById(`phone`);
const province = document.getElementById(`province`);
const district = document.getElementById(`district`);
const ward = document.getElementById(`ward`);
const home = document.getElementById(`home`);
const note = document.getElementById(`note`);
const orderItemTableBody = document.getElementById(`orderItemTableBody`);
const orderItemTableRows = orderItemTableBody.querySelectorAll(`tr`);
const cartGrandTotal = parseInt(document.getElementById(`cartGrandtotal`).innerText.replace(/[^\d]/, ``));
const orderForm = document.getElementById(`orderForm`);

// toast
// success toast
const successToast = document.getElementById(`successToast`);
const successToastMessage = document.getElementById(`successToastMessage`);
//error toast
const errorToast = document.getElementById(`errorToast`);
const errorToastMessage = document.getElementById(`errorToastMessage`);

handleDOMContentLoadedEvent();

function handleDOMContentLoadedEvent() {
  document.addEventListener(`DOMContentLoaded`, (event) => {
    event.preventDefault();

    // TODO
    handleConfirmOrderBtnEvent();
    handleLogOutIconEvent();
  });
}

function handleConfirmOrderBtnEvent() {
  confirmOrderBtn.addEventListener(`click`, async (event) => {
    event.preventDefault();

    if (orderItemTableRows.length === 0) {
      errorToastMessage.innerText = `Chưa có sản phẩm nào!`;
      showToast(errorToast);
      return;
    }

    let orderItems = new Array();
    orderItemTableRows.forEach((row) => {
      let itemImageUrl = row.querySelector(`.itemImage`).src;
      let orderItem = {
        cartItemId: row.querySelector(`.ids`).dataset.cartItemId,
        bookId: row.querySelector(`.ids`).dataset.bookId,
        image: itemImageUrl.substring(itemImageUrl.indexOf(`/book-covers`)),
        name: row.querySelector(`.itemName`).innerText,
        price: parseInt(row.querySelector(`.itemPrice`).innerText.replace(/[^\d]/g, ``)),
        quantity: parseInt(row.querySelector(`.itemQuantity`).innerText),
        total: parseInt(row.querySelector(`.itemSubtotal`).innerText.replace(/[^\d]/g, ``)),
      };
      orderItems.push(orderItem);
    });

    // TODO: build dto -> json -> request body
    const orderDto = {
      name: name.value,
      phone: phone.value,
      province: province.value,
      district: district.value,
      ward: ward.value,
      home: home.value,
      note: note.value,
      orderItems: orderItems,
      total: cartGrandTotal,
    };
    const json = JSON.stringify(orderDto);
    const createOrderApiUrl = `${serverBaseUrl}${orderApiBasePath}`;
    await callCreateOrderApi(createOrderApiUrl, json);
  });
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

async function callCreateOrderApi(url, json) {
  const response = await fetch(url, { method: `POST`, headers: { "Content-Type": `application/json;charset=utf-8` }, body: json });
  const responseObj = await response.json();
  switch (response.status) {
    case 201:
      orderForm.querySelectorAll(`.is-invalid`).forEach((element) => element.classList.remove(`is-invalid`));
      sessionStorage.setItem(`orderCode`, responseObj.data.code);
      sessionStorage.setItem(`createdAt`, responseObj.data.orderedAt);
      window.location.assign(`${serverBaseUrl}${orderSuccessPath}`);
      break;
    case 400:
      const validationMessage = responseObj.detail;
      renderValidationMessageIntoOrderForm(validationMessage);
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

function renderValidationMessageIntoOrderForm(validationMessage) {
  orderForm.querySelectorAll(`.is-invalid`).forEach((element) => element.classList.remove(`is-invalid`));
  orderForm.querySelectorAll(`input`).forEach((input) => {
    if (Object.hasOwn(validationMessage, input.id)) {
      input.classList.add(`is-invalid`);
      input.nextElementSibling.innerText = validationMessage[`${input.id}`];
    }
  });
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
