const PAGE = 0;
const SIZE = 6;
const serverBaseUrl = `http://localhost:8080`;
const bookApiBasePath = `/api/books`;
const createBookApiPath = `/create`;
const unauthorizedPath = `/error/401Unauthorized`;
const forbiddenPath = `/error/403Forbidden`;
const notFoundPath = `/error/404NotFound`;
const internalServerErrorPath = `/error/500InternalServerError`;
const logOutApiUrl = `${serverBaseUrl}/logOut`;
const loginPageUrl = `${serverBaseUrl}/logIn`;

// authentication
const logOutLink = document.getElementById(`logOutLink`);

// view book detail
const bookDetailImagePreview = document.getElementById(`bookDetailImagePreview`);
const bookDetailISBN = document.getElementById(`bookDetailISBN`);
const bookDetailCategory = document.getElementById(`bookDetailCategory`);
const bookDetailAuthor = document.getElementById(`bookDetailAuthor`);
const bookDetailPublisher = document.getElementById(`bookDetailPublisher`);
const bookDetailPrice = document.getElementById(`bookDetailPrice`);
const bookDetailStock = document.getElementById(`bookDetailStock`);
const bookDetailSoldQuantity = document.getElementById(`bookDetailSoldQuantity`);
const bookDetailDescription = document.getElementById(`bookDetailDescription`);

// create book
const createBookModalContainer = document.getElementById(`createBookModalContainer`);
const createBookDescriptionAccordion = document.getElementById(`createBookDescriptionAccordion`);
const createBookBtn = document.getElementById(`createBookBtn`);
const createBookImagePreview = document.getElementById(`createBookImagePreview`);
const createBookNameInput = document.getElementById(`createBookNameInput`);
const createBookAuthorInput = document.getElementById(`createBookAuthorInput`);
const createBookPublisherInput = document.getElementById(`createBookPublisherInput`);
const createBookISBNInput = document.getElementById(`createBookISBNInput`);
const createBookStockInput = document.getElementById(`createBookStockInput`);
const createBookPriceInput = document.getElementById(`createBookPriceInput`);
const createBookImageInput = document.getElementById(`createBookImageInput`);
const createBookDescriptionTextarea = document.getElementById(`createBookDescriptionTextarea`);
const createBookCategorySelect = document.getElementById(`createBookCategorySelect`);
// create book error
const createBookNameError = document.getElementById(`createBookNameError`);
const createBookAuthorError = document.getElementById(`createBookAuthorError`);
const createBookPublisherError = document.getElementById(`createBookPublisherError`);
const createBookISBNError = document.getElementById(`createBookISBNError`);
const createBookStockError = document.getElementById(`createBookStockError`);
const createBookPriceError = document.getElementById(`createBookPriceError`);
const createBookImageError = document.getElementById(`createBookImageError`);
const createBookDescriptionError = document.getElementById(`createBookDescriptionError`);

// update book
const updateBookModalContainer = document.getElementById(`updateBookModalContainer`);
const updateBookDescriptionAccordion = document.getElementById(`updateBookDescriptionAccordion`);
const updateBookBtn = document.getElementById(`updateBookBtn`);
const updateBookImagePreview = document.getElementById(`updateBookImagePreview`);
const updateBookNameInput = document.getElementById(`updateBookNameInput`);
const updateBookAuthorInput = document.getElementById(`updateBookAuthorInput`);
const updateBookPublisherInput = document.getElementById(`updateBookPublisherInput`);
const updateBookISBNInput = document.getElementById(`updateBookISBNInput`);
const updateBookStockInput = document.getElementById(`updateBookStockInput`);
const updateBookPriceInput = document.getElementById(`updateBookPriceInput`);
const updateBookImageInput = document.getElementById(`updateBookImageInput`);
const updateBookDescriptionTextarea = document.getElementById(`updateBookDescriptionTextarea`);
const updateBookCategorySelect = document.getElementById(`updateBookCategorySelect`);
// update book error
const updateBookNameError = document.getElementById(`updateBookNameError`);
const updateBookAuthorError = document.getElementById(`updateBookAuthorError`);
const updateBookPublisherError = document.getElementById(`updateBookPublisherError`);
const updateBookISBNError = document.getElementById(`updateBookISBNError`);
const updateBookStockError = document.getElementById(`updateBookStockError`);
const updateBookPriceError = document.getElementById(`updateBookPriceError`);
const updateBookImageError = document.getElementById(`updateBookImageError`);
const updateBookDescriptionError = document.getElementById(`updateBookDescriptionError`);

// delete book
const deleteBookModalContainer = document.getElementById(`deleteBookModalContainer`);
const deleteBookBtn = document.getElementById(`deleteBookBtn`);

// pagination bar
const paginationBar = document.getElementById(`paginationBar`);
const pageNumberBtns = document.querySelectorAll(`.page-number`);
const prev = document.getElementById(`prev`);
const next = document.getElementById(`next`);

// book table
const bookTableBody = document.getElementById(`bookTableBody`);

// book sorter
const bookSorter = document.getElementById(`bookSorter`);

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

    // Các hàm xử lý sự kiện
    handleCreateBookImageInputEvent();
    handleOpenBookDetailModalBtnsEvent(document.querySelectorAll(`.openBookDetailModalBtn`));
    handleCreateBookBtnEvent();
    handleOpenUpdateBookModalBtnsEvent(document.querySelectorAll(`.openUpdateBookModalBtn`));
    handleBookDescriptionAccordionOnOpenComplete(updateBookDescriptionAccordion);
    handleBookDescriptionTextareaOnInputEvent(updateBookDescriptionTextarea);
    handleUpdateBookImageInputEvent();
    handleBookDescriptionTextareaOnInputEvent(createBookDescriptionTextarea);
    handleUpdateBookBtnEvent();
    handleLogOutLinkEvent();
    handleOpenDeleteBookModalBtnsEvent(document.querySelectorAll(`.openDeleteBookModalBtn`));
    handleDeleteBookBtnEvent();
    handlePageNumberBtnsEvent(pageNumberBtns);
    handleNextBtnEvent(next);
    handlePrevBtnEvent(prev);
    handleBookSorterEvent();
  });
}

function handleCreateBookImageInputEvent() {
  const createBookImageInput = document.getElementById(`createBookImageInput`);
  createBookImageInput.addEventListener(`change`, (event) => {
    event.preventDefault();

    const bookCoverImageFile = createBookImageInput.files[0];
    if (!bookCoverImageFile) return;

    if (!bookCoverImageFile.type.startsWith(`image/`)) {
      createBookImageError.innerText = `Vui lòng chọn một file ảnh!`;
      createBookImageInput.classList.add(`is-invalid`);
      createBookImagePreview.src = ``;
      return;
    }

    createBookImageError.innerText = ``;
    createBookImageInput.classList.remove(`is-invalid`);

    const fileReader = new FileReader();
    fileReader.onload = (e) => {
      createBookImagePreview.src = e.target.result;
    };
    fileReader.readAsDataURL(bookCoverImageFile);
  });
}

function handleOpenBookDetailModalBtnsEvent(openBookDetailModalBtns) {
  openBookDetailModalBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();

      // TODO
      const bookId = btn.dataset.bookId;
      const getBookByIdApiUrl = `${serverBaseUrl}${bookApiBasePath}/${bookId}`;
      const book = await callGetBookByIdApi(getBookByIdApiUrl);
      if (book === null) return;
      renderBookDetailData(book);
    })
  );
}

async function callGetBookByIdApi(getBookByIdApiUrl) {
  const response = await fetch(getBookByIdApiUrl, { method: `GET` });
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

function renderBookDetailData(book) {
  bookDetailImagePreview.src = `${serverBaseUrl}${book.image.replace(/\\/g, `/`)}`;
  bookDetailISBN.innerText = book.isbn;
  bookDetailCategory.innerText = book.category.name;
  bookDetailAuthor.innerText = book.author;
  bookDetailPublisher.innerText = book.publisher;
  bookDetailPrice.innerText = book.price.toLocaleString(`vi-VN`) + ` ₫`;
  bookDetailStock.innerText = book.stockQuantity;
  bookDetailSoldQuantity.innerText = book.soldQuantity;
  bookDetailDescription.innerText = book.description;
}

function handleCreateBookBtnEvent() {
  createBookBtn.addEventListener(`click`, async (event) => {
    event.preventDefault();
    const bookDto = {
      isbn: createBookISBNInput.value,
      name: createBookNameInput.value,
      author: createBookAuthorInput.value,
      publisher: createBookPublisherInput.value,
      stockQuantity: createBookStockInput.value,
      price: createBookPriceInput.value,
      description: createBookDescriptionTextarea.value,
      category: {
        id: createBookCategorySelect.value,
      },
    };
    const json = JSON.stringify(bookDto);
    const bookCoverImageFile = createBookImageInput.files[0];
    const formData = new FormData();
    const blobObject = new Blob([json], { type: `application/json` });
    formData.append(`json`, blobObject);
    formData.append(`bookCoverImage`, bookCoverImageFile);
    const createBookApiUrl = `${serverBaseUrl}${bookApiBasePath}${createBookApiPath}`;
    await callCreateBookApi(formData, createBookApiUrl);

    const queryString = new URLSearchParams({ page: PAGE, size: SIZE, sort: bookSorter.value }).toString();
    const getAllBooksApiUrl = `${serverBaseUrl}${bookApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllBooksApiUrl);
    const books = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderBookTableRows(books);
    renderPaginationBar(paginationMetadata);
  });
}

async function callCreateBookApi(formData, createBookApiUrl) {
  const response = await fetch(createBookApiUrl, { method: `POST`, body: formData });
  const responseObj = await response.json();
  switch (response.status) {
    case 201:
      createBookModalContainer.querySelectorAll(`.is-invalid`).forEach((e) => e.classList.remove(`is-invalid`));
      successToastMessage.innerText = `Tạo sách mới thành công!`;
      showToast(successToast);
      break;
    case 400:
      errorToastMessage.innerText = `Tạo sách mới thất bại!`;
      showToast(errorToast);
      createBookModalContainer.querySelectorAll(`.is-invalid`).forEach((e) => e.classList.remove(`is-invalid`));
      const errorDetail = responseObj.detail;
      if (Object.hasOwn(errorDetail, `name`)) {
        createBookNameInput.classList.add(`is-invalid`);
        createBookNameError.innerText = errorDetail.name;
      }

      if (Object.hasOwn(errorDetail, `author`)) {
        createBookAuthorInput.classList.add(`is-invalid`);
        createBookAuthorError.innerText = errorDetail.author;
      }

      if (Object.hasOwn(errorDetail, `publisher`)) {
        createBookPublisherInput.classList.add(`is-invalid`);
        createBookPublisherError.innerText = errorDetail.publisher;
      }

      if (Object.hasOwn(errorDetail, `isbn`)) {
        createBookISBNInput.classList.add(`is-invalid`);
        createBookISBNError.innerText = errorDetail.isbn;
      }

      if (Object.hasOwn(errorDetail, `stockQuantity`)) {
        createBookStockInput.classList.add(`is-invalid`);
        createBookStockError.innerText = errorDetail.stockQuantity;
      }

      if (Object.hasOwn(errorDetail, `price`)) {
        createBookPriceInput.classList.add(`is-invalid`);
        createBookPriceError.innerText = errorDetail.price;
      }

      if (Object.hasOwn(errorDetail, `description`)) {
        createBookDescriptionTextarea.classList.add(`is-invalid`);
        createBookDescriptionError.innerText = errorDetail.description;
      }

      if (Object.hasOwn(errorDetail, `bookCoverImage`)) {
        createBookImageInput.classList.add(`is-invalid`);
        createBookImageError.innerText = errorDetail.bookCoverImage;
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

function handleOpenUpdateBookModalBtnsEvent(openUpdateBookModalBtns) {
  openUpdateBookModalBtns.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();
      updateBookBtn.dataset.bookId = btn.dataset.bookId;
      const getBookByIdApiUrl = `${serverBaseUrl}${bookApiBasePath}/${btn.dataset.bookId}`;
      const book = await callGetBookByIdApi(getBookByIdApiUrl);
      if (book === null) return;
      await renderDataIntoUpdateBookModal(book);
    })
  );
}

async function renderDataIntoUpdateBookModal(book) {
  updateBookNameInput.value = book.name;
  updateBookAuthorInput.value = book.author;
  updateBookPublisherInput.value = book.publisher;
  updateBookISBNInput.value = book.isbn;
  updateBookStockInput.value = book.stockQuantity;
  updateBookPriceInput.value = book.price;
  updateBookImagePreview.src = `${serverBaseUrl}${book.image.replace(/\\/g, `/`)}`;
  await populateFileInputFromUrl(updateBookImageInput, updateBookImagePreview.src, updateBookImagePreview.src.substring(updateBookImagePreview.src.lastIndexOf(`/`) + 1));
  updateBookDescriptionTextarea.value = book.description;
  if (updateBookDescriptionAccordion.classList.contains("show")) {
    autoResizeTextarea(updateBookDescriptionTextarea);
  }
  updateBookCategorySelect.value = book.category.id;
}

function handleUpdateBookBtnEvent() {
  updateBookBtn.addEventListener(`click`, async (event) => {
    event.preventDefault();

    const bookId = updateBookBtn.dataset.bookId;
    const bookDto = {
      isbn: updateBookISBNInput.value,
      name: updateBookNameInput.value,
      author: updateBookAuthorInput.value,
      publisher: updateBookPublisherInput.value,
      stockQuantity: updateBookStockInput.value,
      price: updateBookPriceInput.value,
      description: updateBookDescriptionTextarea.value,
      category: {
        id: updateBookCategorySelect.value,
      },
    };
    const json = JSON.stringify(bookDto);
    const bookCoverImageFile = updateBookImageInput.files[0];
    const formData = new FormData();
    const blobObject = new Blob([json], { type: `application/json` });
    formData.append(`json`, blobObject);
    formData.append(`bookCoverImage`, bookCoverImageFile);

    const updateBookByIdApiUrl = `${serverBaseUrl}${bookApiBasePath}/${bookId}`;
    const updatedBook = await callUpdateBookByIdApi(formData, updateBookByIdApiUrl);
    if (updatedBook === null) return;
    renderBookRow(bookId, updatedBook);
    hideModal(updateBookModalContainer);
  });
}

async function callUpdateBookByIdApi(formData, updateBookByIdApiUrl) {
  const response = await fetch(updateBookByIdApiUrl, { method: `PUT`, body: formData });
  const responseObj = await response.json();
  switch (response.status) {
    case 200:
      updateBookModalContainer.querySelectorAll(`.is-invalid`).forEach((e) => e.classList.remove(`is-invalid`));
      successToastMessage.innerText = `Cập nhật sách thành công!`;
      showToast(successToast);
      return Object.hasOwn(responseObj, `data`) ? responseObj.data : null;
    case 400:
      errorToastMessage.innerText = `Cập nhật sách thất bại!`;
      showToast(errorToast);
      updateBookModalContainer.querySelectorAll(`.is-invalid`).forEach((e) => e.classList.remove(`is-invalid`));
      const errorDetail = responseObj.detail;
      if (Object.hasOwn(errorDetail, `name`)) {
        updateBookNameInput.classList.add(`is-invalid`);
        updateBookNameError.innerText = errorDetail.name;
      }

      if (Object.hasOwn(errorDetail, `author`)) {
        updateBookAuthorInput.classList.add(`is-invalid`);
        updateBookAuthorError.innerText = errorDetail.author;
      }

      if (Object.hasOwn(errorDetail, `publisher`)) {
        updateBookPublisherInput.classList.add(`is-invalid`);
        updateBookPublisherError.innerText = errorDetail.publisher;
      }

      if (Object.hasOwn(errorDetail, `isbn`)) {
        updateBookISBNInput.classList.add(`is-invalid`);
        updateBookISBNError.innerText = errorDetail.isbn;
      }

      if (Object.hasOwn(errorDetail, `stockQuantity`)) {
        updateBookStockInput.classList.add(`is-invalid`);
        updateBookStockError.innerText = errorDetail.stockQuantity;
      }

      if (Object.hasOwn(errorDetail, `price`)) {
        updateBookPriceInput.classList.add(`is-invalid`);
        updateBookPriceError.innerText = errorDetail.price;
      }

      if (Object.hasOwn(errorDetail, `description`)) {
        updateBookDescriptionTextarea.classList.add(`is-invalid`);
        updateBookDescriptionError.innerText = errorDetail.description;
      }

      if (Object.hasOwn(errorDetail, `bookCoverImage`)) {
        updateBookImageInput.classList.add(`is-invalid`);
        updateBookImageError.innerText = errorDetail.bookCoverImage;
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

function autoResizeTextarea(textarea) {
  textarea.style.height = `auto`;
  textarea.style.height = textarea.scrollHeight + `px`;
}

function handleBookDescriptionAccordionOnOpenComplete(bookDescriptionAccordion) {
  bookDescriptionAccordion.addEventListener(`shown.bs.collapse`, (event) => {
    autoResizeTextarea(updateBookDescriptionTextarea);
  });
}

function handleBookDescriptionTextareaOnInputEvent(bookDescriptionTextarea) {
  bookDescriptionTextarea.addEventListener(`input`, (event) => {
    autoResizeTextarea(bookDescriptionTextarea);
  });
}

function handleUpdateBookImageInputEvent() {
  updateBookImageInput.addEventListener(`change`, (event) => {
    event.preventDefault();

    const bookCoverImageFile = updateBookImageInput.files[0];
    if (!bookCoverImageFile) return;

    if (!bookCoverImageFile.type.startsWith(`image/`)) {
      updateBookImageError.innerText = `Vui lòng chọn một file ảnh!`;
      updateBookImageInput.classList.add(`is-invalid`);
      updateBookImagePreview.src = ``;
      return;
    }

    updateBookImageError.innerText = ``;
    updateBookImageInput.classList.remove(`is-invalid`);

    const fileReader = new FileReader();
    fileReader.onload = (e) => {
      updateBookImagePreview.src = e.target.result;
    };
    fileReader.readAsDataURL(bookCoverImageFile);
  });
}

async function populateFileInputFromUrl(fileInput, serverImageUrl, fileName = "bookCoverImage.png") {
  const response = await fetch(serverImageUrl);
  const blobObject = await response.blob();
  const fileObject = new File([blobObject], fileName, { type: blobObject.type });
  const dataTransfer = new DataTransfer();
  dataTransfer.items.add(fileObject);
  fileInput.files = dataTransfer.files;
}

function renderBookRow(bookId, book) {
  document.getElementById(`bookImageCell-${bookId}`).src = `${serverBaseUrl}${book.image.replace(/\\/g, `/`)}`;
  document.getElementById(`bookISBNCell-${bookId}`).innerText = book.isbn;
  document.getElementById(`bookNameCell-${bookId}`).innerText = book.name;
  document.getElementById(`bookAuthorCell-${bookId}`).innerText = book.author;
  document.getElementById(`bookPublisherCell-${bookId}`).innerText = book.publisher;
  document.getElementById(`bookPriceCell-${bookId}`).innerText = book.price.toLocaleString(`vi-VN`) + ` ₫`;
}

function hideModal(modalElement) {
  const modalInstance = bootstrap.Modal.getInstance(modalElement);
  modalInstance.hide();
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

function handleOpenDeleteBookModalBtnsEvent(openDeleteBookModalBtns) {
  openDeleteBookModalBtns.forEach((btn) =>
    btn.addEventListener(`click`, (event) => {
      event.preventDefault();
      deleteBookBtn.dataset.bookId = btn.dataset.bookId;
    })
  );
}

function handleDeleteBookBtnEvent() {
  deleteBookBtn.addEventListener(`click`, async (event) => {
    event.preventDefault();
    const bookId = deleteBookBtn.dataset.bookId;
    const deleteBookByIdApiUrl = `${serverBaseUrl}${bookApiBasePath}/${bookId}`;
    await callDeleteBookByIdApi(deleteBookByIdApiUrl);
    hideModal(deleteBookModalContainer);

    const queryString = new URLSearchParams({ page: PAGE, size: SIZE, sort: bookSorter.value }).toString();
    const getAllBooksApiUrl = `${serverBaseUrl}${bookApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllBooksApiUrl);
    const books = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderBookTableRows(books);
    renderPaginationBar(paginationMetadata);
  });
}

async function callDeleteBookByIdApi(deleteBookByIdApiUrl) {
  const response = await fetch(deleteBookByIdApiUrl, { method: `DELETE` });
  switch (response.status) {
    case 200:
      successToastMessage.innerText = `Xóa sách thành công!`;
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

      const queryString = new URLSearchParams({ page: btn.dataset.page, size: SIZE, sort: bookSorter.value }).toString();
      const getAllBooksApiUrl = `${serverBaseUrl}${bookApiBasePath}?${queryString}`;
      const responseObj = await callGetAllBooksApi(getAllBooksApiUrl);
      const books = responseObj.data;
      const paginationMetadata = responseObj.paginationMetadata;
      renderBookTableRows(books);
      renderPaginationBar(paginationMetadata);
    })
  );
}

function handlePrevBtnEvent(prev) {
  prev.addEventListener(`click`, async (event) => {
    event.preventDefault();

    if (prev.dataset.prevPage < 0) return;

    const queryString = new URLSearchParams({ page: prev.dataset.prevPage, size: SIZE, sort: bookSorter.value }).toString();
    const getAllBooksApiUrl = `${serverBaseUrl}${bookApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllBooksApiUrl);
    const books = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderBookTableRows(books);
    renderPaginationBar(paginationMetadata);
  });
}

function handleNextBtnEvent(next) {
  next.addEventListener(`click`, async (event) => {
    event.preventDefault();

    if (next.dataset.nextPage < 0) return;

    const queryString = new URLSearchParams({ page: next.dataset.nextPage, size: SIZE, sort: bookSorter.value }).toString();
    const getAllBooksApiUrl = `${serverBaseUrl}${bookApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllBooksApiUrl);
    const books = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderBookTableRows(books);
    renderPaginationBar(paginationMetadata);
  });
}

function renderBookTableRows(books) {
  bookTableBody.innerHTML = ``;
  if (books === null || books.length === 0) {
    const tableRow = document.createElement(`tr`);
    tableRow.innerHTML = `<td colspan="8" class="text-center">Chưa có cuốn sách nào.</td>`;
    bookTableBody.appendChild(tableRow);
  } else {
    books.forEach((book, index) => {
      const tableRow = document.createElement(`tr`);
      tableRow.innerHTML = `
      <th scope="row" class="text-center">${index + 1}</th>
                  <td>
                    <div class="d-flex align-items-center">
                      <img src="${serverBaseUrl}${book.image.replace(/\\/g, "/")}" id="bookImageCell-${book.id}" class="img-thumbnail w-100 h-100 rounded object-fit-cover" style="aspect-ratio: 3/4; overflow: hidden" alt="${book.name}" />
                    </div>
                  </td>
                  <td class="text-center text-nowrap" id="bookISBNCell-${book.id}">${book.isbn}</td>
                  <td class="text-wrap" id="bookNameCell-${book.id}" style="text-align: left">${book.name}</td>
                  <td class="text-nowrap" id="bookAuthorCell-${book.id}" style="text-align: left">${book.author}</td>
                  <td class="text-nowrap" id="bookPublisherCell-${book.id}" style="text-align: left">${book.publisher}</td>
                  <td class="text-center text-nowrap" id="bookPriceCell-${book.id}">${book.price.toLocaleString("vi-VN")} ₫</td>
                  <td class="text-nowrap">
                    <button type="button" class="openBookDetailModalBtn btn btn-info text-nowrap text-white" data-book-id="${book.id}" data-bs-toggle="modal" data-bs-target="#bookDetailModalContainer">
                      <i class="fa-solid fa-eye"></i>
                    </button>
                    <button type="button" class="openUpdateBookModalBtn btn btn-success text-nowrap border-0" data-book-id="${book.id}" data-bs-toggle="modal" data-bs-target="#updateBookModalContainer">
                      <i class="fa-regular fa-pen-to-square"></i>
                    </button>
                    <button type="button" class="openDeleteBookModalBtn btn btn-danger text-nowrap" data-book-id="${book.id}" data-bs-toggle="modal" data-bs-target="#deleteBookModalContainer">
                      <i class="fa-solid fa-trash"></i>
                    </button>
                  </td>
      `;
      bookTableBody.appendChild(tableRow);
    });
    handleOpenBookDetailModalBtnsEvent(document.querySelectorAll(`.openBookDetailModalBtn`));
    handleOpenUpdateBookModalBtnsEvent(document.querySelectorAll(`.openUpdateBookModalBtn`));
    handleOpenDeleteBookModalBtnsEvent(document.querySelectorAll(`.openDeleteBookModalBtn`));
  }
}

async function callGetAllBooksApi(apiUrl) {
  const response = await fetch(apiUrl, { method: `GET` });
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

function handleBookSorterEvent() {
  bookSorter.addEventListener(`change`, async (event) => {
    event.preventDefault();
    const queryString = new URLSearchParams({ page: PAGE, size: SIZE, sort: bookSorter.value }).toString();
    const getAllBooksApiUrl = `${serverBaseUrl}${bookApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllBooksApiUrl);
    const books = responseObj.data;
    const paginationMetadata = responseObj.paginationMetadata;
    renderBookTableRows(books);
    renderPaginationBar(paginationMetadata);
  });
}

function handleBookSearchBoxEvent() {
  bookSearchBox.addEventListener(`keydown`, async (event) => {
    if (event.key !== `Enter`) return;

    event.preventDefault(); // đang làm ở đây

    const urlSearchParams = new URLSearchParams();
    urlSearchParams.append(`page`, `${PAGE}`);
    urlSearchParams.append(`size`, `${SIZE}`);
    const sortByField = document.getElementById(`sortByField`);
    urlSearchParams.append(`sort`, `${sortByField.value}`);
    const beingSelectedCategory = document.querySelector(`.category.beingSelected`);
    if (beingSelectedCategory && !beingSelectedCategory.classList.contains(`all-categories`)) urlSearchParams.append(`category`, `${beingSelectedCategory.dataset.id}`);
    const beingCheckedRate = document.querySelector(`input[name="rate"]:checked`);
    if (beingCheckedRate) urlSearchParams.append(`rate`, `${beingCheckedRate.value}`);
    urlSearchParams.append(`keyword`, `${bookSearchBox.value}`);
    const queryString = urlSearchParams.toString();
    const getAllBooksApiUrl = `${serverBaseUrl}${bookApiBasePath}?${queryString}`;
    const responseObj = await callGetAllBooksApi(getAllBooksApiUrl);
    const books = responseObj.data;
    renderBookGrid(books);
    const paginationMetadata = responseObj.paginationMetadata;
    renderPaginationBar(paginationMetadata);
  });
}
