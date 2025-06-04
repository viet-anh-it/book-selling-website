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

// pagination bar
const paginationBar = document.getElementById(`paginationBar`);
const pageNumberBtns = document.querySelectorAll(`.page-number`);
const prev = document.getElementById(`prev`);
const next = document.getElementById(`next`);

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
    handlePageNumberBtnsEvent(document.querySelectorAll(`.page-number`));
    handlePrevBtnEvent(document.getElementById(`prev`));
    handleNextBtnEvent(document.getElementById(`next`));
  });
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
      //   urlSearchParams.append(`sort`, `${orderSorter.value}`);
      //   urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
      //   urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
      const queryString = urlSearchParams.toString();

      //   const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
      //   const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
      //   const orders = responseObj.data;
      //   renderOrderTable(orders);
      //   const paginationMetadata = responseObj.paginationMetadata;
      //   renderPaginationBar(paginationMetadata);
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
    // urlSearchParams.append(`sort`, `${orderSorter.value}`);
    // urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    // urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    // const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
    // const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
    // const orders = responseObj.data;
    // renderOrderTable(orders);
    // const paginationMetadata = responseObj.paginationMetadata;
    // renderPaginationBar(paginationMetadata);
  });
}

function handleNextBtnEvent(next) {
  next.addEventListener(`click`, async (event) => {
    event.preventDefault();

    if (next.dataset.nextPage < 0) return;

    const urlSearchParams = new URLSearchParams();
    urlSearchParams.append(`page`, `${next.dataset.nextPage}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    // urlSearchParams.append(`sort`, `${orderSorter.value}`);
    // urlSearchParams.append(`keyword`, `${orderSearchBox.value}`);
    // urlSearchParams.append(`status`, `${orderStatusFilter.value}`);
    const queryString = urlSearchParams.toString();

    // const getAllOrdersApiUrl = `${serverBaseUrl}${orderApiBasePath}?${queryString}`;
    // const responseObj = await callGetAllOrdersApi(getAllOrdersApiUrl);
    // const orders = responseObj.data;
    // renderOrderTable(orders);
    // const paginationMetadata = responseObj.paginationMetadata;
    // renderPaginationBar(paginationMetadata);
  });
}
