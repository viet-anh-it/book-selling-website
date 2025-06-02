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

// order item table
const orderSorter = document.getElementById(`orderSorter`);
const orderSearchBox = document.getElementById(`orderSearchBox`);
const orderStatusFilter = document.getElementById(`orderStatusFilter`);
const orderItemTableBody = document.getElementById(`orderItemTableBody`);

// modals
const orderDetailModalContainer = document.getElementById(`orderDetailModalContainer`);

// pagination bar
const paginationBar = document.getElementById(`paginationBar`);
const pageNumberBtns = document.querySelectorAll(`.page-number`);
const prev = document.getElementById(`prev`);
const next = document.getElementById(`next`);

handleDOMContentLoadedEvent();

function handleDOMContentLoadedEvent() {
  document.addEventListener(`DOMContentLoaded`, (event) => {
    event.preventDefault();

    // TODO
    handleOpenOrderDetailModalBtnsEvent(document.querySelectorAll(`.openOrderDetailModalBtn`));
    handlePageNumberBtnsEvent(document.querySelectorAll(`.page-number`));
    handlePrevBtnEvent(document.getElementById(`prev`));
    handleNextBtnEvent(document.getElementById(`next`));
    handleOrderSorterEvent();
    handleOrderStatusFilterEvent();
    handleOrderSearchBoxEvent();
    handleLogOutLinkEvent();
  });
}

function handleOpenOrderDetailModalBtnsEvent(openOrderDetailModalBtns) {
  openOrderDetailModalBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();
      const url = `${serverBaseUrl}${orderApiBasePath}/${btn.dataset.orderId}`;
      const order = await callGetOrderByIdApi(url);
      if (order === null) return;
      renderOrderDetailDataIntoModal(order);
    })
  );
}

async function callGetOrderByIdApi(url) {
  const response = await fetch(url, { method: `GET` });
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

function renderOrderDetailDataIntoModal(order) {
  // TODO
  orderedAt.innerText = format_DDMMYYYY_hhMM(order.orderedAt);
  name.innerText = order.name;
  phone.innerText = order.phone;
  address.innerText = `${order.province}, ${order.district}, ${order.ward}, ${order.home}`;
  grandtotal.innerText = `${order.total.toLocaleString(`vi-VN`)} ₫`;
  paymentMethod.innerText = order.paymentMethod;
  orderStatus.innerText = order.status;
  const orderItems = order.orderItems;
  orderItemTableBody.innerHTML = ``;
  orderItems.forEach((orderItem, index) => {
    const orderItemTableRow = document.createElement(`tr`);
    orderItemTableRow.innerHTML = `
    <th scope="row" class="text-center">${index + 1}</th>
                        <td class="text-nowrap text-center">
                          <img src="${serverBaseUrl}${orderItem.image}" class="img-fluid img-thumbnail rounded overflow-hidden object-fit-cover d-inline-block" style="height: 80px; aspect-ratio: 3/4"/>
                        </td>
                        <td class="text-left text-wrap">${orderItem.name}</td>
                        <td class="text-center text-nowrap" style="text-align: left">${orderItem.price.toLocaleString(`vi-VN`)} ₫</td>
                        <td class="text-center text-nowrap" style="text-align: left">${orderItem.quantity}</td>
                        <td class="text-center text-nowrap" style="text-align: left">${orderItem.total.toLocaleString(`vi-VN`)} ₫</td>
    `;
    orderItemTableBody.appendChild(orderItemTableRow);
  });
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

function renderPaginationBar(paginationMetadata) {
  paginationBar.innerHTML = ``;
  if (paginationMetadata.totalNumberOfPages >= 1) {
    const prev = document.createElement(`li`);
    prev.setAttribute(`id`, `prev`);
    prev.setAttribute(`data-prev-page`, paginationMetadata.previousPosition);
    prev.classList.add(`page-item`);
    prev.classList.toggle(`disabled`, !paginationMetadata.hasPreviousPage);
    prev.innerHTML = `
    <a class="page-link" href="#" aria-label="Previous">
      <span aria-hidden="true">&laquo;</span>
    </a>
    `;
    paginationBar.appendChild(prev);

    for (let i = 1; i <= paginationMetadata.totalNumberOfPages; i++) {
      const page = document.createElement(`li`);
      page.classList.add(`page-item`);
      page.classList.add(`page-number`);
      page.classList.toggle(`active`, i - 1 === paginationMetadata.currentPosition);
      page.setAttribute(`data-page`, i - 1);
      page.innerHTML = `
      <a class="page-link" href="#">${i}</a>
      `;
      paginationBar.appendChild(page);
    }

    const next = document.createElement(`li`);
    next.setAttribute(`id`, `next`);
    next.setAttribute(`data-next-page`, paginationMetadata.nextPosition);
    next.classList.add(`page-item`);
    next.classList.toggle(`disabled`, !paginationMetadata.hasNextPage);
    next.innerHTML = `
    <a class="page-link" href="#" aria-label="Next">
      <span aria-hidden="true">&raquo;</span>
    </a>
    `;
    paginationBar.appendChild(next);

    handlePageNumberBtnsEvent(document.querySelectorAll(`.page-number`));
    handlePrevBtnEvent(document.getElementById(`prev`));
    handleNextBtnEvent(document.getElementById(`next`));
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

      const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
      const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
      const orders = responseObj.data;
      renderOrderTable(orders);
      const paginationMetadata = responseObj.paginationMetadata;
      renderPaginationBar(paginationMetadata);
    })
  );
}

async function callGetAllOrdersApi(url) {
  const response = await fetch(url, { method: `GET` });
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

function renderOrderTable(orders) {
  // TODO
  orderTableBody.innerHTML = ``;
  if (orders === null || orders.length === 0) {
    const tableRow = document.createElement(`tr`);
    tableRow.innerHTML = `<td colspan="10" class="text-center">Chưa có đơn hàng nào.</td>`;
    orderTableBody.appendChild(tableRow);
  } else {
    orders.forEach((order, index) => {
      const tableRow = document.createElement(`tr`);
      tableRow.innerHTML = `
       <th scope="row" class="text-center">${index + 1}</th>
                  <td class="text-center text-nowrap">${order.code}</td>
                  <td class="text-center text-nowrap">${order.name}</td>
                  <td class="text-center text-nowrap">${order.phone}</td>
                  <td class="text-wrap" style="text-align: left">${order.province}, ${order.district}, ${order.ward}, ${order.home}</td>
                  <td class="text-center text-nowrap" style="text-align: left">${format_DDMMYYYY_hhMM(order.orderedAt)}</td>
                  <td class="text-center text-nowrap">${order.total.toLocaleString("vi-VN")} ₫</td>
                  <td class="text-center text-nowrap">${order.paymentMethod}</td>
                  <td class="text-center text-nowrap">${order.status}</td>
                  <td class="text-center text-nowrap">
                    <button type="button" class="openOrderDetailModalBtn btn btn-info text-nowrap text-white" data-order-id="${order.id}" data-bs-toggle="modal" data-bs-target="#orderDetailModalContainer">
                      <i class="fa-solid fa-eye"></i>
                    </button>
                    <button type="button" class="openUpdateBookModalBtn btn btn-success text-nowrap border-0" data-order-id="${order.id}" data-bs-toggle="modal" data-bs-target="#updateBookModalContainer">
                      <i class="fa-regular fa-pen-to-square"></i>
                    </button>
                  </td>
      `;
      orderTableBody.appendChild(tableRow);
    });
    handleOpenOrderDetailModalBtnsEvent(document.querySelectorAll(`.openOrderDetailModalBtn`));
  }
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

    const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
    const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
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

    const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
    const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
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
    // urlSearchParams.append(`page`, `${btn.dataset.page}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    urlSearchParams.append(`sort`, `${orderSorter.value}`);
    urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
    const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
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
    // urlSearchParams.append(`page`, `${btn.dataset.page}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    urlSearchParams.append(`sort`, `${orderSorter.value}`);
    urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
    const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
    const orders = responseObj.data;
    renderOrderTable(orders);
    const paginationMetadata = responseObj.paginationMetadata;
    renderPaginationBar(paginationMetadata);
  });
}

function handleOrderSearchBoxEvent() {
  orderSearchBox.addEventListener(`keydown`, async (event) => {
    // TODO
    if (event.key !== `Enter`) return;

    const urlSearchParams = new URLSearchParams();
    // urlSearchParams.append(`page`, `${btn.dataset.page}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    urlSearchParams.append(`sort`, `${orderSorter.value}`);
    urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
    const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
    const orders = responseObj.data;
    renderOrderTable(orders);
    const paginationMetadata = responseObj.paginationMetadata;
    renderPaginationBar(paginationMetadata);
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
