const PAGE = 0;
const SIZE = 6;
const serverBaseUrl = `http://localhost:8080`;
const categoryApiBasePath = `/api/categories`;
const unauthorizedPath = `/error/401Unauthorized`;
const forbiddenPath = `/error/403Forbidden`;
const notFoundPath = `/error/404NotFound`;
const internalServerErrorPath = `/error/500InternalServerError`;
const categoryNameInput = document.getElementById(`categoryNameInput`);
const categoryDescriptionTextarea = document.getElementById(`categoryDescriptionTextarea`);
const categoryNameError = document.getElementById(`categoryNameError`);
const categoryDescriptionError = document.getElementById(`categoryDescriptionError`);
const popCreateCategoryModalBtn = document.getElementById(`popCreateCategoryModalBtn`);
const updateCategoryModalContainer = document.getElementById(`updateCategoryModalContainer`);
const openUpdateCategoryModalBtns = document.querySelectorAll(`.openUpdateCategoryModalBtn`);
const updateCategoryBtn = document.getElementById(`updateCategoryBtn`);
const updateCategoryNameInput = document.getElementById(`updateCategoryNameInput`);
const updateCategoryDescriptionTextarea = document.getElementById(`updateCategoryDescriptionTextarea`);
const updateCategoryNameError = document.getElementById(`updateCategoryNameError`);
const updateCategoryDescriptionError = document.getElementById(`updateCategoryDescriptionError`);
const deleteCategoryModalContainer = document.getElementById(`deleteCategoryModalContainer`);
const openDeleteCategoryModalBtns = document.querySelectorAll(`.openDeleteCategoryModalBtn`);
const deleteCategoryBtn = document.getElementById(`deleteCategoryBtn`);
const createCategoryBtn = document.getElementById(`createCategoryBtn`);
const successToast = document.getElementById(`successToast`);
const successToastMessage = document.getElementById(`successToastMessage`);
const errorToast = document.getElementById(`errorToast`);
const errorToastMessage = document.getElementById(`errorToastMessage`);
const bookTableBody = document.getElementById(`categoryTableBody`);
const paginationBar = document.getElementById(`paginationBar`);
const pageNumberBtns = document.querySelectorAll(`.page-number`);
const categorySorter = document.getElementById(`categorySorter`);
const prev = document.getElementById(`prev`);
const next = document.getElementById(`next`);
const categorySearchBox = document.getElementById(`categorySearchBox`);

// authentication
const logOutLink = document.getElementById(`logOutLink`);
const logOutApiUrl = `${serverBaseUrl}/logOut`;
const loginPageUrl = `${serverBaseUrl}/logIn`;

handleDOMContentLoadedEvent();

function handleDOMContentLoadedEvent() {
  document.addEventListener(`DOMContentLoaded`, (event) => {
    event.preventDefault();

    // Các hàm xử lý sự kiện
    handleCreateCategoryBtnEvent();
    handleOpenUpdateCategoryModalBtnsEvent(openUpdateCategoryModalBtns);
    handleUpdateCategoryBtnEvent();
    handleOpenDeleteCategoryModalBtnsEvent(openDeleteCategoryModalBtns);
    handleDeleteCategoryBtn();
    handlePageNumberBtnsEvent(pageNumberBtns);
    handleCategorySorterEvent();
    handlePrevBtnEvent(prev);
    handleNextBtnEvent(next);
    handleSearchBoxEvent(categorySearchBox);
    handleLogOutLinkEvent();
  });
}

function handleCreateCategoryBtnEvent() {
  createCategoryBtn.addEventListener(`click`, async (event) => {
    event.preventDefault();

    // tạo request body
    const name = document.getElementById(`categoryNameInput`).value;
    const description = document.getElementById(`categoryDescriptionTextarea`).value;

    const categoryDto = {
      name: name,
      description: description,
    };

    const json = JSON.stringify(categoryDto);

    // tạo request URL
    const requestUrl = `http://localhost:8080/api/categories`;

    // gọi API
    await fetchCreateCategoryApi(json, requestUrl);

    // gọi API lấy tất cả danh mục
    const getAllCategoriesApiUrl = `${serverBaseUrl}${categoryApiBasePath}`;
    const responseObj = await callGetAllBooksApi(getAllCategoriesApiUrl);
    const categories = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;

    // TODO: render categories
    renderCategoryTableRows(categories);

    //TODO: render pagination bar
    renderPaginationBar(paginationMetadata);
  });
}

async function fetchCreateCategoryApi(json, requestUrl) {
  const response = await fetch(requestUrl, {
    method: `POST`,
    headers: { "Content-Type": `application/json;charset=utf-8` },
    body: json,
  });
  const responseObj = await response.json();
  const responseStatus = response.status;
  switch (responseStatus) {
    case 201:
      if (categoryNameInput.classList.contains(`is-invalid`)) {
        categoryNameInput.classList.remove(`is-invalid`);
      }

      if (categoryDescriptionTextarea.classList.contains(`is-invalid`)) {
        categoryDescriptionTextarea.classList.remove(`is-invalid`);
      }
      successToastMessage.innerText = `Tạo danh mục thành công!`;
      showToast(successToast);
      break;
    case 400:
      if (categoryNameInput.classList.contains(`is-invalid`)) {
        categoryNameInput.classList.remove(`is-invalid`);
      }

      if (categoryDescriptionTextarea.classList.contains(`is-invalid`)) {
        categoryDescriptionTextarea.classList.remove(`is-invalid`);
      }

      if (Object.hasOwn(responseObj.detail, `name`)) {
        categoryNameInput.classList.add(`is-invalid`);
        categoryNameError.innerText = responseObj.detail.name;
      }

      if (Object.hasOwn(responseObj.detail, `description`)) {
        categoryDescriptionTextarea.classList.add(`is-invalid`);
        categoryDescriptionError.innerText = responseObj.detail.description;
      }
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

async function callGetAllBooksApi(apiUrl) {
  const response = await fetch(apiUrl, { method: `GET` });
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

function renderCategoryTableRows(categories) {
  bookTableBody.innerHTML = ``;
  if (categories === null || categories.length === 0) {
    const tableRow = document.createElement(`tr`);
    tableRow.innerHTML = `<td colspan="8" class="text-center">Chưa có danh mục nào.</td>`;
    bookTableBody.appendChild(tableRow);
  } else {
    categories.forEach((category, index) => {
      const tableRow = document.createElement(`tr`);
      tableRow.innerHTML = `
      <th scope="row" class="text-center">${index + 1}</th>
                  <td id="categoryNameCell-${category.id}" style="text-align: justify">${category.name}</td>
                  <td>
                    <div class="accordion accordion-flush" id="categoryDescriptionAccordion-${index}">
                      <div class="accordion-item">
                        <h2 class="accordion-header">
                          <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse-${index}" aria-controls="collapse-${index}" aria-expanded="false">Mở rộng</button>
                        </h2>
                        <div id="collapse-${index}" class="accordion-collapse collapse" data-bs-parent="#categoryDescriptionAccordion-${index}">
                          <div class="accordion-body">
                            <p id="categoryDescriptionCell-${category.id}" style="white-space: pre-line; text-align: justify">${category.description}</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td class="text-nowrap">
                    <button type="button" class="openUpdateCategoryModalBtn btn btn-success text-nowrap border-0" data-category-id="${category.id}" data-bs-toggle="modal" data-bs-target="#updateCategoryModalContainer">
                      <i class="fa-regular fa-pen-to-square"></i>
                    </button>
                    <button type="button" class="openDeleteCategoryModalBtn btn btn-danger text-nowrap" data-category-id="${category.id}" data-bs-toggle="modal" data-bs-target="#deleteCategoryModalContainer">
                      <i class="fa-solid fa-trash"></i>
                    </button>
                  </td>
      `;
      bookTableBody.appendChild(tableRow);
    });
    handleOpenUpdateCategoryModalBtnsEvent(document.querySelectorAll(`.openUpdateCategoryModalBtn`));
    handleOpenDeleteCategoryModalBtnsEvent(document.querySelectorAll(`.openDeleteCategoryModalBtn`));
  }
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

async function fetchRevokeRefreshTokenApi() {
  await fetch(`${serverBaseUrl}/revokeRefreshToken`, { method: `DELETE` });
}

function handleUpdateCategoryBtnEvent() {
  updateCategoryBtn.addEventListener(`click`, async (event) => {
    event.preventDefault();

    // TODO
    // build url và call update category api
    const id = updateCategoryBtn.dataset.categoryId;
    const name = updateCategoryNameInput.value;
    const description = updateCategoryDescriptionTextarea.value;
    const categoryDto = {
      id: id,
      name: name,
      description: description,
    };
    const json = JSON.stringify(categoryDto);
    const apiUrl = `${serverBaseUrl}${categoryApiBasePath}`;
    await fetchUpdateCategoryApi(json, apiUrl);
    hideModal(updateCategoryModalContainer);
    const singleCategory = await fetchGetCategoryByIdApi(`${serverBaseUrl}${categoryApiBasePath}/${id}`);
    document.getElementById(`categoryNameCell-${id}`).innerText = singleCategory.name;
    document.getElementById(`categoryDescriptionCell-${id}`).innerText = singleCategory.description;
  });
}

function handleOpenUpdateCategoryModalBtnsEvent(openUpdateCategoryModalBtns) {
  openUpdateCategoryModalBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();

      // TODO
      // gọi api lấy danh mục theo id và đưa thông tin danh mục đang muốn cập nhật lên form cập nhật
      const categoryId = btn.dataset.categoryId;
      const apiUrl = serverBaseUrl + categoryApiBasePath + `/${categoryId}`;
      const category = await fetchGetCategoryByIdApi(apiUrl);
      updateCategoryNameInput.value = category.name;
      updateCategoryDescriptionTextarea.value = category.description;
      updateCategoryBtn.dataset.categoryId = category.id;
    })
  );
}

async function fetchGetCategoryByIdApi(apiUrl) {
  const response = await fetch(apiUrl, { method: `GET` });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      if (responseObj.data == null) {
        handle404NotFound();
        return;
      }
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

async function fetchUpdateCategoryApi(json, apiUrl) {
  const response = await fetch(apiUrl, {
    method: `PUT`,
    headers: { "Content-Type": `application/json;charset=utf-8` },
    body: json,
  });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      if (updateCategoryNameInput.classList.contains(`is-invalid`)) {
        updateCategoryNameInput.classList.remove(`is-invalid`);
      }

      if (updateCategoryDescriptionTextarea.classList.contains(`is-invalid`)) {
        updateCategoryDescriptionTextarea.classList.remove(`is-invalid`);
      }
      successToastMessage.innerText = `Cập nhật danh mục thành công!`;
      showToast(successToast);
      break;
    case 400:
      if (updateCategoryNameInput.classList.contains(`is-invalid`)) {
        updateCategoryNameInput.classList.remove(`is-invalid`);
      }

      if (updateCategoryDescriptionTextarea.classList.contains(`is-invalid`)) {
        updateCategoryDescriptionTextarea.classList.remove(`is-invalid`);
      }

      if (Object.hasOwn(responseObj.detail, `name`)) {
        updateCategoryNameInput.classList.add(`is-invalid`);
        updateCategoryNameError.innerText = responseObj.detail.name;
      }

      if (Object.hasOwn(responseObj.detail, `description`)) {
        updateCategoryDescriptionTextarea.classList.add(`is-invalid`);
        updateCategoryDescriptionError.innerText = responseObj.detail.description;
      }
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

function hideModal(modalElement) {
  const modalInstance = bootstrap.Modal.getInstance(modalElement);
  modalInstance.hide();
}

function handleOpenDeleteCategoryModalBtnsEvent(openDeleteCategoryModalBtns) {
  openDeleteCategoryModalBtns.forEach((btn) =>
    btn.addEventListener(`click`, (event) => {
      event.preventDefault();
      deleteCategoryBtn.dataset.categoryId = btn.dataset.categoryId;
    })
  );
}

function handleDeleteCategoryBtn() {
  deleteCategoryBtn.addEventListener(`click`, async (event) => {
    event.preventDefault();
    const categoryId = deleteCategoryBtn.dataset.categoryId;
    const apiUrl = `${serverBaseUrl}${categoryApiBasePath}/${categoryId}`;
    await fetchDeleteCategoryByIdApi(apiUrl);
    const getAllCategoriesApiUrl = `${serverBaseUrl}${categoryApiBasePath}`;
    const responseObj = await callGetAllBooksApi(getAllCategoriesApiUrl);
    const categories = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderCategoryTableRows(categories);
    renderPaginationBar(paginationMetadata);
  });
}

async function fetchDeleteCategoryByIdApi(apiUrl) {
  const response = await fetch(apiUrl, { method: `DELETE` });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      successToastMessage.innerText = responseObj.message;
      showToast(successToast);
      hideModal(deleteCategoryModalContainer);
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

function handlePageNumberBtnsEvent(pageNumberBtns) {
  pageNumberBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();

      if (btn.classList.contains(`active`)) return;

      const queryString = new URLSearchParams({ page: btn.dataset.page, size: SIZE, sort: categorySorter.value }).toString();
      const getAllCategoriesApiUrl = `${serverBaseUrl}${categoryApiBasePath}?${queryString}`;
      const responseObj = await callGetAllBooksApi(getAllCategoriesApiUrl);
      const categories = responseObj.data;
      const paginationMetadata = responseObj.paginationMetadata;
      renderCategoryTableRows(categories);
      renderPaginationBar(paginationMetadata);
    })
  );
}

function handleCategorySorterEvent() {
  categorySorter.addEventListener(`change`, async (event) => {
    event.preventDefault();
    const queryString = new URLSearchParams({ page: PAGE, size: SIZE, sort: categorySorter.value }).toString();
    const getAllCategoriesApiUrl = `${serverBaseUrl}${categoryApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllCategoriesApiUrl);
    const categories = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderCategoryTableRows(categories);
    renderPaginationBar(paginationMetadata);
  });
}

function handlePrevBtnEvent(prev) {
  prev.addEventListener(`click`, async (event) => {
    event.preventDefault();

    if (prev.dataset.prevPage < 0) return;

    const queryString = new URLSearchParams({ page: prev.dataset.prevPage, size: SIZE, sort: categorySorter.value }).toString();
    const getAllCategoriesApiUrl = `${serverBaseUrl}${categoryApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllCategoriesApiUrl);
    const categories = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderCategoryTableRows(categories);
    renderPaginationBar(paginationMetadata);
  });
}

function handleNextBtnEvent(next) {
  next.addEventListener(`click`, async (event) => {
    event.preventDefault();

    if (next.dataset.nextPage < 0) return;

    const queryString = new URLSearchParams({ page: next.dataset.nextPage, size: SIZE, sort: categorySorter.value }).toString();
    const getAllCategoriesApiUrl = `${serverBaseUrl}${categoryApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllCategoriesApiUrl);
    const categories = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderCategoryTableRows(categories);
    renderPaginationBar(paginationMetadata);
  });
}

function handleSearchBoxEvent(categorySearchBox) {
  categorySearchBox.addEventListener(`keydown`, async (event) => {
    if (event.key !== `Enter`) return;
    event.preventDefault();

    const queryParams = {
      page: PAGE,
      size: SIZE,
      sort: categorySorter.value,
      name: categorySearchBox.value,
    };
    const queryString = new URLSearchParams(queryParams).toString();
    const getAllCategoriesApiUrl = `${serverBaseUrl}${categoryApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllCategoriesApiUrl);
    const categories = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderCategoryTableRows(categories);
    renderPaginationBar(paginationMetadata);
  });
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
