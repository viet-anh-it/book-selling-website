// server
const PAGE = 0;
const SIZE = 6;
const serverBaseUrl = `http://localhost:8080`;
const bookApiBasePath = `/api/books`;
const createBookApiPath = `/create`;
const orderApiBasePath = `/api/orders`;
const unauthorizedPath = `/error/401Unauthorized`;
const forbiddenPath = `/error/403Forbidden`;
const notFoundPath = `/error/404NotFound`;
const internalServerErrorPath = `/error/500InternalServerError`;
const logOutApiUrl = `${serverBaseUrl}/logOut`;
const loginPageUrl = `${serverBaseUrl}/logIn`;

// order table
const orderTableBody = document.getElementById(`orderTableBody`);
const openOrderDetailModalBtns = document.querySelectorAll(`.openOrderDetailModalBtn`);
const orderedAt = document.getElementById(`orderedAt`);
const name = document.getElementById(`name`);
const phone = document.getElementById(`phone`);
const address = document.getElementById(`address`);
const grandtotal = document.getElementById(`grandtotal`);
const paymentMethod = document.getElementById(`paymentMethod`);
const orderStatus = document.getElementById(`status`);
const orderNote = document.getElementById(`note`);

// order item table
const orderSorter = document.getElementById(`orderSorter`);
const orderSearchBox = document.getElementById(`orderSearchBox`);
const orderStatusFilter = document.getElementById(`orderStatusFilter`);
const orderItemTableBody = document.getElementById(`orderItemTableBody`);

// modals
const orderDetailModalContainer = document.getElementById(`orderDetailModalContainer`);
const approveOrderModalContainer = document.getElementById(`approveOrderModalContainer`);
const rejectOrderModalContainer = document.getElementById(`rejectOrderModalContainer`);

// pagination bar
const paginationBar = document.getElementById(`paginationContainer`);
const pageNumberBtns = document.querySelectorAll(`.pagNumBtn`);
const prev = document.getElementById(`pagBarPrevBtn`);
const next = document.getElementById(`pagBarNextBtn`);

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
    handleOrderSearchBoxEvent();
    handlePageNumberBtnsEvent(document.querySelectorAll(`.pagNumBtn`));
    handlePrevBtnEvent(prev);
    handleNextBtnEvent(next);
    handleOrderSorterEvent();
    handleOrderStatusFilterEvent();
    handleLogOutIconEvent();
  });
}

function handleOrderSearchBoxEvent() {
  orderSearchBox.addEventListener(`keydown`, async (event) => {
    // TODO
    if (event.key !== `Enter`) return;

    const urlSearchParams = new URLSearchParams();
    urlSearchParams.append(`page`, `${PAGE}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    urlSearchParams.append(`sort`, `${orderSorter.value}`);
    urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    const url = `${serverBaseUrl}${orderApiBasePath}/personal?${queryString}`;
    const responseObj = await callGetPersonalOrdersApi(url);
    const orders = responseObj.data;
    renderOrderTable(orders);
    const paginationMetadata = responseObj.paginationMetadata;
    renderPaginationBar(paginationMetadata);
  });
}

function handleOrderSorterEvent() {
  orderSorter.addEventListener(`change`, async (event) => {
    // TODO
    const urlSearchParams = new URLSearchParams();
    urlSearchParams.append(`page`, `${PAGE}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    urlSearchParams.append(`sort`, `${orderSorter.value}`);
    urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    const url = `${serverBaseUrl}${orderApiBasePath}/personal?${queryString}`;
    const responseObj = await callGetPersonalOrdersApi(url);
    const orders = responseObj.data;
    renderOrderTable(orders);
    const paginationMetadata = responseObj.paginationMetadata;
    renderPaginationBar(paginationMetadata);
  });
}

function handleOrderStatusFilterEvent() {
  orderStatusFilter.addEventListener(`change`, async (event) => {
    // TODO
    const urlSearchParams = new URLSearchParams();
    urlSearchParams.append(`page`, `${PAGE}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    urlSearchParams.append(`sort`, `${orderSorter.value}`);
    urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();
    const url = `${serverBaseUrl}${orderApiBasePath}/personal?${queryString}`;
    const responseObj = await callGetPersonalOrdersApi(url);
    const orders = responseObj.data;
    renderOrderTable(orders);
    const paginationMetadata = responseObj.paginationMetadata;
    renderPaginationBar(paginationMetadata);
  });
}

function renderOrderTable(orders) {
  // TODO
  orderTableBody.innerHTML = ``;
  if (orders === null || orders.length === 0) {
    const tableRow = document.createElement(`tr`);
    tableRow.innerHTML = `<td colspan="6" class="text-center">Chưa có đơn hàng nào.</td>`;
    orderTableBody.appendChild(tableRow);
  } else {
    orders.forEach((order, index) => {
      const tableRow = document.createElement(`tr`);
      tableRow.innerHTML = `
      <th scope="row" class="text-nowrap text-start">${index + 1}</th>
      <td class="text-nowrap text-start"><a href="${serverBaseUrl}/orders/${order.id}/detail">${order.code}</a></td>
      <td class="text-start text-nowrap">${format_DDMMYYYY_hhMM(order.orderedAt)}</td>
      <td class="text-start text-nowrap">${order.total.toLocaleString(`vi-VN`)} ₫</td>
      <td class="text-start text-nowrap">${order.status}</td>
      `;
      orderTableBody.appendChild(tableRow);
      // <td class="text-start">
      //   <button data-order-id="${order.id}" class="cancelOrderBtn btn btn-md rounded-circle bg-light border">
      //     <i class="fa fa-times text-danger"></i>
      //   </button>
      // </td>
    });
  }
}

function renderPaginationBar(paginationMetadata) {
  // TODO
  paginationBar.innerHTML = ``;
  if (paginationMetadata.totalNumberOfPages >= 1) {
    const prev = document.createElement(`a`);
    prev.setAttribute(`id`, `pagBarPrevBtn`);
    prev.setAttribute(`data-prev-page`, paginationMetadata.previousPosition);
    prev.setAttribute(`arial-disabled`, !paginationMetadata.hasPreviousPage);
    prev.setAttribute(`arial-label`, `Previous`);
    prev.setAttribute(`href`, `#`);
    prev.classList.add(`rounded`);
    prev.classList.toggle(`disabled`, !paginationMetadata.hasPreviousPage);
    prev.innerHTML = `&laquo;`;
    paginationBar.appendChild(prev);

    for (let i = 1; i <= paginationMetadata.totalNumberOfPages; i++) {
      const page = document.createElement(`a`);
      page.setAttribute(`href`, `#`);
      page.setAttribute(`data-page`, `${i - 1}`);
      page.innerText = `${i}`;
      page.classList.add(`rounded`, `mx-1`, `pagNumBtn`);
      page.classList.toggle(`active`, i - 1 === paginationMetadata.currentPosition);
      paginationBar.appendChild(page);
    }

    const next = document.createElement(`a`);
    next.setAttribute(`id`, `pagBarNextBtn`);
    next.setAttribute(`data-next-page`, paginationMetadata.nextPosition);
    next.setAttribute(`arial-disabled`, !paginationMetadata.hasNextPage);
    next.setAttribute(`arial-label`, `Next`);
    next.setAttribute(`href`, `#`);
    next.classList.add(`rounded`);
    next.classList.toggle(`disabled`, !paginationMetadata.hasNextPage);
    next.innerHTML = `&raquo;`;
    paginationBar.appendChild(next);

    handlePageNumberBtnsEvent(document.querySelectorAll(`.pagNumBtn`));
    handleNextBtnEvent(document.getElementById(`pagBarNextBtn`));
    handlePrevBtnEvent(document.getElementById(`pagBarPrevBtn`));
  }
}

function handlePageNumberBtnsEvent(pageNumberBtns) {
  pageNumberBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();

      if (btn.classList.contains(`active`)) return;

      const urlSearchParams = new URLSearchParams();
      urlSearchParams.append(`page`, `${btn.dataset.page}`);
      urlSearchParams.append(`size`, `${SIZE}`);
      urlSearchParams.append(`sort`, `${orderSorter.value}`);
      urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
      urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
      const queryString = urlSearchParams.toString();

      const url = `${serverBaseUrl}${orderApiBasePath}/personal?${queryString}`;
      const responseObj = await callGetPersonalOrdersApi(url);
      const orders = responseObj.data;
      renderOrderTable(orders);
      const paginationMetadata = responseObj.paginationMetadata;
      renderPaginationBar(paginationMetadata);
    })
  );
}

function handlePrevBtnEvent(prev) {
  prev.addEventListener(`click`, async (event) => {
    event.preventDefault();

    if (prev.dataset.prevPage < 0) return;

    const urlSearchParams = new URLSearchParams();
    urlSearchParams.append(`page`, `${prev.dataset.prevPage}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    urlSearchParams.append(`sort`, `${orderSorter.value}`);
    urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    const url = `${serverBaseUrl}${orderApiBasePath}/personal?${queryString}`;
    const responseObj = await callGetPersonalOrdersApi(url);
    const orders = responseObj.data;
    renderOrderTable(orders);
    const paginationMetadata = responseObj.paginationMetadata;
    renderPaginationBar(paginationMetadata);
  });
}

function handleNextBtnEvent(next) {
  next.addEventListener(`click`, async (event) => {
    event.preventDefault();

    if (next.dataset.nextPage < 0) return;

    const urlSearchParams = new URLSearchParams();
    urlSearchParams.append(`page`, `${next.dataset.nextPage}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    urlSearchParams.append(`sort`, `${orderSorter.value}`);
    urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    const url = `${serverBaseUrl}${orderApiBasePath}/personal?${queryString}`;
    const responseObj = await callGetPersonalOrdersApi(url);
    const orders = responseObj.data;
    renderOrderTable(orders);
    const paginationMetadata = responseObj.paginationMetadata;
    renderPaginationBar(paginationMetadata);
  });
}

async function callGetPersonalOrdersApi(url) {
  const response = await fetch(url, { method: `GET` });
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

// Hàm format theo dd/MM/yyyy hh:mm
function format_DDMMYYYY_hhMM(iso) {
  var d = new Date(iso);
  if (isNaN(d)) return ""; // trả về rỗng nếu không parse được

  // Lấy ngày, tháng, năm
  var day = String(d.getDate()).padStart(2, "0"); // 1 → "01", 31 → "31"
  var month = String(d.getMonth() + 1).padStart(2, "0"); // 0→1, pad: 5 → "05"
  var year = d.getFullYear(); // Ví dụ: 2025

  // Lấy giờ (24h) và phút
  var hours = String(d.getHours()).padStart(2, "0"); // 0–23 → "00"–"23"
  var minutes = String(d.getMinutes()).padStart(2, "0"); // 0–59 → "00"–"59"

  return day + "/" + month + "/" + year + " " + hours + ":" + minutes;
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
