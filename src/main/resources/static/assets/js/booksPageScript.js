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

function buildBookNameTooltips() {
  // Chọn tất cả phần tử có data-bs-toggle="tooltip"
  var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
  // Khởi tạo tooltip cho mỗi phần tử
  tooltipTriggerList.map(function (el) {
    return new bootstrap.Tooltip(el);
  });
}

function handlePriceSliderEvent() {
  const rangeInput = document.getElementById(`rangeInput`);
  rangeInput.addEventListener(`input`, async (event) => {
    event.preventDefault();
    const resultsContainer = document.getElementById("resultsContainer");
    const paginationContainer = document.getElementById("paginationContainer");
    const minPrice = rangeInput.min;
    const maxPrice = rangeInput.value;
    const queryString = new URLSearchParams();
    queryString.append(`price`, `${minPrice},${maxPrice}`);
    // const activePageNumberButton = document.querySelector(`.pagNumBtn.active`);
    // const currentPage = activePageNumberButton.innerText;
    // queryString.append(`page`, currentPage - 1);
    const sortByField = document.getElementById(`sortByField`);
    const fieldAndDirection = sortByField.value;
    queryString.append(`sort`, `${fieldAndDirection}`);

    const beingSelectedCategory = document.querySelector(`.category.beingSelected`);
    if (beingSelectedCategory && !beingSelectedCategory.classList.contains(`all-categories`)) queryString.append(`category`, `${beingSelectedCategory.dataset.id}`);
    const checkedRate = document.querySelector(`input[name="rate"]:checked`);
    if (checkedRate) queryString.append(`rate`, `${checkedRate.value}`);
    queryString.append(`keyword`, `${bookSearchBox.value}`);

    const apiUrl = `http://localhost:8080/api/books?${queryString.toString()}`;

    const bookFilteringResponse = await fetch(apiUrl, { method: `GET` });
    const bookFilteringResponseBody = await bookFilteringResponse.json();

    if (bookFilteringResponse.status === 200) {
      // NHIỆM VỤ 1: Render danh sách các cuốn sách
      resultsContainer.innerHTML = "";
      const books = bookFilteringResponseBody.data;
      if (books.length > 0) {
        const bookFilteringResultMessage = document.getElementById(`bookFilteringResultMessage`);
        bookFilteringResultMessage.classList.add(`d-none`);
        books.forEach((book) => {
          const bookHTML = `
            <div class="col-md-6 col-lg-6 col-xl-4 bookItem">
              <div class="rounded position-relative fruite-item">
                <div class="fruite-img">
                  <img src="${book.image}" class="img-fluid w-100 h-100 object-fit-cover rounded-top image" alt="${book.name}" />
                </div>
                <div class="p-4 border border-secondary border-top-0 rounded-bottom">
                  <p class="bookId d-none">${book.id}</p>
                  <h4 class="text-truncate name" style="max-width: 100%" data-bs-toggle="tooltip" title="${book.name}">${book.name}</h4>
                  <p class="author">${book.author}</p>
                  <div class="d-flex justify-content-between flex-lg-wrap">
                    <p class="text-dark fs-5 fw-bold mb-0 price">${book.price.toLocaleString("vi-VN")}₫</p>
                  </div>
                </div>
              </div>
            </div>
          `;
          resultsContainer.insertAdjacentHTML("beforeend", bookHTML);
        });
      } else {
        const bookFilteringResultMessage = document.getElementById(`bookFilteringResultMessage`);
        bookFilteringResultMessage.classList.remove(`d-none`);
      }

      // NHIỆM VỤ 2: Render thanh phân trang
      paginationContainer.innerHTML = "";
      const pagination = bookFilteringResponseBody.paginationMetadata;

      if (books.length > 0) {
        // Prev
        const prevClass = !pagination.hasPreviousPage ? " disabled" : "";
        const prev = `<a id="pagBarPrevBtn" href="#!" onclick="event.preventDefault();" class="rounded${prevClass}" aria-label="Previous" aria-disabled="${!pagination.hasPreviousPage}">&laquo;</a>`;
        paginationContainer.insertAdjacentHTML("beforeend", prev);

        // Page numbers
        for (let i = 0; i < pagination.totalNumberOfPages; i++) {
          const activeClass = i === pagination.currentPosition ? " active" : "";
          const page = `<a href="#!" onclick="event.preventDefault();" class="rounded mx-1${activeClass} pagNumBtn"><span>${i + 1}</span></a>`;
          paginationContainer.insertAdjacentHTML("beforeend", page);
        }

        // Next
        const nextClass = !pagination.hasNextPage ? " disabled" : "";
        const next = `<a id="pagBarNextBtn" href="#!" onclick="event.preventDefault();" class="rounded${nextClass}" aria-label="Next" aria-disabled="${!pagination.hasNextPage}">&raquo;</a>`;
        paginationContainer.insertAdjacentHTML("beforeend", next);

        handleBookItemEvent();
        handlePrevAndNextButtonEvent();
        handlePageNumberButtonEvent();
      }
      // 2.4: Nếu không có cuốn sách nào, không render thanh phân trang
    }
  });
}

function handlePageNumberButtonEvent() {
  const pageNumberButtons = document.querySelectorAll(`.pagNumBtn`);
  pageNumberButtons.forEach((pageNumberButton) =>
    pageNumberButton.addEventListener(`click`, async (event) => {
      event.preventDefault();
      if (pageNumberButton.classList.contains(`active`)) return;
      const clickedPageNumberButton = event.target;
      const demandedPage = clickedPageNumberButton.innerText;
      const queryString = new URLSearchParams();
      queryString.append(`page`, demandedPage - 1);
      const rangeInput = document.getElementById(`rangeInput`);
      const minPrice = rangeInput.min;
      const maxPrice = rangeInput.value;
      queryString.append(`price`, `${minPrice},${maxPrice}`);
      const sortByField = document.getElementById(`sortByField`);
      const fieldAndDirection = sortByField.value;
      queryString.append(`sort`, `${fieldAndDirection}`);

      const beingSelectedCategory = document.querySelector(`.category.beingSelected`);
      if (beingSelectedCategory && !beingSelectedCategory.classList.contains(`all-categories`)) queryString.append(`category`, `${beingSelectedCategory.dataset.id}`);
      const beingCheckedRate = document.querySelector(`input[name="rate"]:checked`);
      if (beingCheckedRate) queryString.append(`rate`, `${beingCheckedRate.value}`);
      queryString.append(`keyword`, `${bookSearchBox.value}`);

      const apiUrl = `http://localhost:8080/api/books?${queryString.toString()}`;
      const pageNavigationResponse = await fetch(apiUrl, {
        method: `GET`,
      });
      const pageNavigationResponseBody = await pageNavigationResponse.json();
      const resultsContainer = document.getElementById("resultsContainer");
      const paginationContainer = document.getElementById("paginationContainer");
      const prevBtn = document.getElementById("pagBarPrevBtn");
      const nextBtn = document.getElementById("pagBarNextBtn");

      if (pageNavigationResponse.status === 200) {
        // NHIỆM VỤ 1: Render lại danh sách các cuốn sách
        // renderBookList(pageNavigationResponseBody.data, resultsContainer);
        renderBookGrid(pageNavigationResponseBody.data);

        // NHIỆM VỤ 2: Cập nhật trạng thái thanh phân trang
        // updatePagination(pageNavigationResponseBody.paginationMetadata, prevBtn, nextBtn, paginationContainer);
        renderPaginationBar(pageNavigationResponseBody.paginationMetadata);

        handleBookItemEvent();
      }
    })
  );

  /**
   * Định dạng số thành chuỗi tiền Việt
   */
  function formatPrice(price) {
    return new Intl.NumberFormat("vi-VN").format(price) + " ₫";
  }

  /**
   * Tạo HTML cho một cuốn sách
   */
  function createBookHTML(book) {
    return `
      <div class="col-md-6 col-lg-6 col-xl-4 bookItem">
        <div class="rounded position-relative fruite-item">
          <div class="fruite-img">
            <img src="${book.image}" class="img-fluid w-100 h-100 object-fit-cover rounded-top image" alt="${book.name}" />
          </div>
          <div class="p-4 border border-secondary border-top-0 rounded-bottom">
            <p class="bookId d-none">${book.id}</p>
            <h4 class="text-truncate name" style="max-width: 100%" data-bs-toggle="tooltip" title="${book.name}">${book.name}</h4>
            <p class="author">${book.author}</p>
            <div class="d-flex justify-content-between flex-lg-wrap">
              <p class="text-dark fs-5 fw-bold mb-0 price">${formatPrice(book.price)}</p>
            </div>
          </div>
        </div>
      </div>`;
  }

  /**
   * Render danh sách sách vào container
   */
  function renderBookList(books, container) {
    container.innerHTML = ""; // Xóa nội dung cũ
    books.forEach((book) => {
      container.insertAdjacentHTML("beforeend", createBookHTML(book));
    });
  }

  /**
   * Cập nhật trạng thái prev/next và active page
   */
  function updatePagination(metadata, prevBtn, nextBtn, container) {
    // Prev button
    if (!metadata.hasPreviousPage) {
      prevBtn.classList.add("disabled");
      prevBtn.setAttribute("aria-disabled", "true");
    } else {
      prevBtn.classList.remove("disabled");
      prevBtn.setAttribute("aria-disabled", "false");
    }

    // Next button
    if (!metadata.hasNextPage) {
      nextBtn.classList.add("disabled");
      nextBtn.setAttribute("aria-disabled", "true");
    } else {
      nextBtn.classList.remove("disabled");
      nextBtn.setAttribute("aria-disabled", "false");
    }

    // Highlight nút trang đang active
    const pageBtns = container.querySelectorAll(".pagNumBtn");
    pageBtns.forEach((btn) => {
      const pageIndex = parseInt(btn.innerText, 10) - 1;
      if (pageIndex === metadata.currentPosition) {
        btn.classList.add("active");
      } else {
        btn.classList.remove("active");
      }
    });
  }
}

function handlePrevAndNextButtonEvent() {
  const previousButton = document.getElementById(`pagBarPrevBtn`);
  const nextButton = document.getElementById(`pagBarNextBtn`);
  const buttons = [previousButton, nextButton];

  buttons.forEach((button) =>
    button.addEventListener(`click`, async (event) => {
      event.preventDefault();
      const activePageNumberButton = document.querySelector(`.pagNumBtn.active`);
      const currentPage = parseInt(activePageNumberButton.innerText, 10);
      const queryString = new URLSearchParams();

      if (event.target.id === `pagBarPrevBtn`) {
        queryString.append(`page`, currentPage - 2); // vì currentPage là 1-based, API nhận 0-based
      } else {
        queryString.append(`page`, currentPage); // next: page = (currentPage+1) - 1
      }

      const rangeInput = document.getElementById(`rangeInput`);
      const minPrice = rangeInput.min;
      const maxPrice = rangeInput.value;
      queryString.append(`price`, `${minPrice},${maxPrice}`);
      const sortByField = document.getElementById(`sortByField`);
      const fieldAndDirection = sortByField.value;
      queryString.append(`sort`, `${fieldAndDirection}`);

      const beingSelectedCategory = document.querySelector(`.category.beingSelected`);
      if (beingSelectedCategory && !beingSelectedCategory.classList.contains(`all-categories`)) queryString.append(`category`, `${beingSelectedCategory.dataset.id}`);
      const beingCheckedRate = document.querySelector(`input[name="rate"]:checked`);
      if (beingCheckedRate) queryString.append(`rate`, `${beingCheckedRate.value}`);
      queryString.append(`keyword`, `${bookSearchBox.value}`);

      const apiUrl = `http://localhost:8080/api/books?${queryString.toString()}`;
      const pageNavigationResponse = await fetch(apiUrl, { method: `GET` });
      const pageNavigationResponseBody = await pageNavigationResponse.json();

      if (pageNavigationResponse.status === 200) {
        // NHIỆM VỤ 1: Render lại danh sách các cuốn sách
        // renderBookList(pageNavigationResponseBody.data);
        renderBookGrid(pageNavigationResponseBody.data);

        // NHIỆM VỤ 2: Cập nhật trạng thái thanh phân trang
        // updatePagination(pageNavigationResponseBody.paginationMetadata);
        renderPaginationBar(pageNavigationResponseBody.paginationMetadata);

        handleBookItemEvent();
      }
    })
  );

  /**
   * Định dạng số thành chuỗi tiền Việt
   */
  function formatPrice(price) {
    return new Intl.NumberFormat("vi-VN").format(price) + " ₫";
  }

  /**
   * Tạo HTML cho một cuốn sách
   */
  function createBookHTML(book) {
    return `
      <div class="col-md-6 col-lg-6 col-xl-4 bookItem">
        <div class="rounded position-relative fruite-item">
          <div class="fruite-img">
            <img src="${book.image}" class="img-fluid w-100 h-100 object-fit-cover rounded-top image" alt="${book.name}" />
          </div>
          <div class="p-4 border border-secondary border-top-0 rounded-bottom">
            <p class="bookId d-none">${book.id}</p>
            <h4 class="text-truncate name" style="max-width: 100%" data-bs-toggle="tooltip" title="${book.name}">${book.name}</h4>
            <p class="author">${book.author}</p>
            <div class="d-flex justify-content-between flex-lg-wrap">
              <p class="text-dark fs-5 fw-bold mb-0 price">${formatPrice(book.price)}</p>
            </div>
          </div>
        </div>
      </div>`;
  }

  /**
   * Render danh sách sách vào container
   */
  function renderBookList(books) {
    const container = document.getElementById("resultsContainer");
    container.innerHTML = "";
    books.forEach((book) => {
      container.insertAdjacentHTML("beforeend", createBookHTML(book));
    });
  }

  /**
   * Cập nhật trạng thái prev/next và active page
   */
  function updatePagination(metadata) {
    const prevBtn = document.getElementById("pagBarPrevBtn");
    const nextBtn = document.getElementById("pagBarNextBtn");
    const pageBtns = document.querySelectorAll(".pagNumBtn");

    if (!metadata.hasPreviousPage) {
      prevBtn.classList.add("disabled");
      prevBtn.setAttribute("aria-disabled", "true");
    } else {
      prevBtn.classList.remove("disabled");
      prevBtn.setAttribute("aria-disabled", "false");
    }

    if (!metadata.hasNextPage) {
      nextBtn.classList.add("disabled");
      nextBtn.setAttribute("aria-disabled", "true");
    } else {
      nextBtn.classList.remove("disabled");
      nextBtn.setAttribute("aria-disabled", "false");
    }

    pageBtns.forEach((btn) => {
      const pageIndex = parseInt(btn.innerText, 10) - 1;
      if (pageIndex === metadata.currentPosition) {
        btn.classList.add("active");
      } else {
        btn.classList.remove("active");
      }
    });
  }
}

function handleSortByFieldEvent() {
  const sortByField = document.getElementById(`sortByField`);
  sortByField.addEventListener(`change`, async (event) => {
    event.preventDefault();
    const rangeInput = document.getElementById(`rangeInput`);
    const minPrice = rangeInput.min;
    const maxPrice = rangeInput.value;
    const fieldAndDirection = event.target.value;
    // const activePageNumberButton = document.querySelector(`.pagNumBtn.active`);
    // const currentPage = activePageNumberButton.innerText;
    const queryString = new URLSearchParams();
    // queryString.append(`page`, currentPage - 1);
    queryString.append(`sort`, `${fieldAndDirection}`);
    queryString.append(`price`, `${minPrice},${maxPrice}`);

    const beingSelectedCategory = document.querySelector(`.category.beingSelected`);
    if (beingSelectedCategory && !beingSelectedCategory.classList.contains(`all-categories`)) queryString.append(`category`, `${beingSelectedCategory.dataset.id}`);
    const checkedRate = document.querySelector(`input[name="rate"]:checked`);
    if (checkedRate) queryString.append(`rate`, `${checkedRate.value}`);
    queryString.append(`keyword`, `${bookSearchBox.value}`);

    const apiUrl = `http://localhost:8080/api/books?${queryString.toString()}`;
    const sortResponse = await fetch(apiUrl, {
      method: `GET`,
    });
    const sortResponseBody = await sortResponse.json();
    if (sortResponse.status === 200) {
      // renderBooks(sortResponseBody.data);
      // updatePagination(sortResponseBody.paginationMetadata);
      renderBookGrid(sortResponseBody.data);
      renderPaginationBar(sortResponseBody.paginationMetadata);
      handleBookItemEvent();
    }
  });

  /**
   * Chuyển số nguyên thành chuỗi tiền Việt, ví dụ 54720 → "54.720 ₫"
   */
  function formatVND(amount) {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
      maximumFractionDigits: 0,
    }).format(amount);
  }

  /**
   * Xóa sạch và dựng lại lưới sách từ mảng dữ liệu
   */
  function renderBooks(books) {
    const container = document.getElementById("resultsContainer");
    container.innerHTML = "";

    books.forEach((book) => {
      const col = document.createElement("div");
      col.className = "col-md-6 col-lg-6 col-xl-4";
      col.innerHTML = `
      <div class="rounded position-relative fruite-item bookItem">
        <div class="fruite-img">
          <img src="${book.image}" class="img-fluid w-100 h-100 object-fit-cover rounded-top image" alt="${book.name}" />
        </div>
        <div class="p-4 border border-secondary border-top-0 rounded-bottom">
        <p class="bookId d-none">${book.id}</p>
          <h4 class="text-truncate name"
              style="max-width: 100%"
              data-bs-toggle="tooltip"
              title="${book.name}">
            ${book.name}
          </h4>
          <p class="author">${book.author}</p>
          <div class="d-flex justify-content-between flex-lg-wrap">
            <p class="text-dark fs-5 fw-bold mb-0 price">
              ${formatVND(book.price)}
            </p>
          </div>
        </div>
      </div>
    `;
      container.appendChild(col);
    });

    // Khởi tạo lại tooltip cho các tên sách bị truncate
    const tooltipTriggerList = Array.from(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.forEach((el) => new bootstrap.Tooltip(el));
  }

  /**
   * Cập nhật trạng thái cho các nút phân trang: active, disable prev/next
   */
  function updatePagination(meta) {
    const prevBtn = document.getElementById("pagBarPrevBtn");
    const nextBtn = document.getElementById("pagBarNextBtn");
    const pageBtns = Array.from(document.querySelectorAll(".pagNumBtn"));

    // Active trang hiện tại
    pageBtns.forEach((btn, index) => {
      btn.classList.toggle("active", index === meta.currentPosition);
    });

    // Disable/enable nút Prev
    prevBtn.classList.toggle("disabled", !meta.hasPreviousPage);
    prevBtn.setAttribute("aria-disabled", String(!meta.hasPreviousPage));

    // Disable/enable nút Next
    nextBtn.classList.toggle("disabled", !meta.hasNextPage);
    nextBtn.setAttribute("aria-disabled", String(!meta.hasNextPage));
  }
}

function handleBookItemEvent() {
  const bookItems = document.querySelectorAll(`.bookItem`);
  bookItems.forEach((bookItem) =>
    bookItem.addEventListener(`click`, (event) => {
      event.preventDefault();
      const bookId = bookItem.querySelector(`.bookId`).innerText;
      window.location.assign(`http://localhost:8080/books/${bookId}`);
    })
  );
}

// document.addEventListener("DOMContentLoaded", (event) => {
//   event.preventDefault();
//   const filterLink = document.getElementById(`filterLink`);
//   filterLink.addEventListener(`click`, async (event) => {
//     event.preventDefault();
//     const resultsContainer = document.getElementById("resultsContainer");
//     const paginationContainer = document.getElementById("paginationContainer");
//     const rangeInput = document.getElementById(`rangeInput`);
//     const minPrice = rangeInput.min;
//     const maxPrice = rangeInput.value;
//     const queryString = new URLSearchParams();
//     queryString.append(`price`, `${minPrice},${maxPrice}`);
//     const apiUrl = `http://localhost:8080/api/books?${queryString.toString()}`;
//     const bookFilteringResponse = await fetch(apiUrl, {
//       method: `GET`,
//     });
//     const bookFilteringResponseBody = await bookFilteringResponse.json();
//     if (bookFilteringResponse.status === 200) {
//       // NHIỆM VỤ 1: Render danh sách các cuốn sách
//       // 1.1. Format hiển thị giá tiền theo kiểu Việt Nam
//       // NHIỆM VỤ 2: Render thanh phân trang
//       // 2.1. Nếu đang ở trang 1, disable nút prev
//       // 2.2. Nếu đang ở trang cuối, disable nút next
//       // 2.3. Nếu chỉ có 1 trang, disable cả 2 nút
//       // 2.4. Nếu không có cuốn sách nào, không render thanh phân trang
//       // Lưu ý:
//       // 1. Dữ liệu phân trang là 0-based.
//       // 2. Chỉ viết code render, còn lại không viết code gì thêm.
//     }
//   });
// });

document.addEventListener(`DOMContentLoaded`, (event) => {
  event.preventDefault();
  handleBookItemEvent();
  buildBookNameTooltips();
  handleSortByFieldEvent();
  handlePriceSliderEvent();
  handlePageNumberButtonEvent();
  handlePrevAndNextButtonEvent();
  handleLogOutIconEvent();
  handleCartIconEvent();
  handleCategoryLinkEvent();
  handleRateRadioButtonEvent();
  handleBookSearchBoxEvent();
});

window.addEventListener(`pageshow`, (event) => {
  // TODO
  const slider = document.getElementById("rangeInput");
  slider.value = slider.min;
  const beingCheckedRate = document.querySelector(`input[name="rate"]:checked`);
  if (beingCheckedRate) beingCheckedRate.checked = false;
});

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

// ==========

const PAGE = 0;
const SIZE = 6;
const serverBaseUrl = `http://localhost:8080`;
const bookApiBasePath = `/api/books`;
const createBookApiPath = `/create`;
const unauthorizedPath = `/error/401Unauthorized`;
const forbiddenPath = `/error/403Forbidden`;
const notFoundPath = `/error/404NotFound`;
const internalServerErrorPath = `/error/500InternalServerError`;

// category
const categories = document.querySelectorAll(`.category`);
const categoryNames = document.querySelectorAll(`.categoryName`);

// rate
const rates = document.querySelectorAll(`input[name="rate"]`);

// book grid
const resultsContainer = document.getElementById(`resultsContainer`);
const bookFilteringResultMessage = document.getElementById(`bookFilteringResultMessage`);
const bookSearchBox = document.getElementById(`bookSearchBox`);

// pagination bar
const paginationContainer = document.getElementById(`paginationContainer`);

function handleCategoryLinkEvent() {
  categories.forEach((category) =>
    category.addEventListener(`click`, async (event) => {
      event.preventDefault();

      const beingSelectedCategory = document.querySelector(`.category.beingSelected`);
      if (beingSelectedCategory) beingSelectedCategory.classList.remove(`beingSelected`);
      category.classList.add(`beingSelected`);

      const sortByField = document.getElementById(`sortByField`);
      const rangeInput = document.getElementById(`rangeInput`);
      const checkedRateRadioButton = document.querySelector(`input[name="rate"]:checked`);

      // TODO:
      // build url -> query params: category, price, rate, sort
      const urlSearchParams = new URLSearchParams();
      urlSearchParams.append(`page`, `${PAGE}`);
      urlSearchParams.append(`size`, `${SIZE}`);
      urlSearchParams.append(`sort`, `${sortByField.value}`);
      if (!category.classList.contains(`all-categories`)) urlSearchParams.append(`category`, `${category.dataset.id}`);
      urlSearchParams.append(`price`, `${rangeInput.min},${rangeInput.value}`);
      if (checkedRateRadioButton) urlSearchParams.append(`rate`, `${checkedRateRadioButton.value}`);
      urlSearchParams.append(`keyword`, `${bookSearchBox.value}`);
      const queryString = urlSearchParams.toString();
      const getAllBooksApiUrl = `${serverBaseUrl}${bookApiBasePath}?${queryString}`;

      // TODO:
      // call api get all books
      const responseObj = await callGetAllBooksApi(getAllBooksApiUrl);

      // TODO:
      // render: book grid, pagination bar
      const books = responseObj.data;
      const paginationMetadata = responseObj.paginationMetadata;
      renderBookGrid(books);
      renderPaginationBar(paginationMetadata);
    })
  );
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

function renderBookGrid(books) {
  resultsContainer.innerHTML = ``;
  if (books.length === 0) {
    bookFilteringResultMessage.classList.remove(`d-none`);
    bookFilteringResultMessage.classList.add(`d-block`);
  } else {
    bookFilteringResultMessage.classList.remove(`d-block`);
    bookFilteringResultMessage.classList.add(`d-none`);
    books.forEach((book, index) => {
      // TODO
      const bookItem = document.createElement(`div`);
      bookItem.classList.add(`col-md-6`, `col-lg-6`, `col-xl-4`, `bookItem`);
      bookItem.innerHTML = `
                          <div class="rounded position-relative fruite-item">
                      <div class="fruite-img">
                        <img src="${serverBaseUrl}${book.image.replace(/\\/g, "/")}" class="img-fluid w-100 h-100 object-fit-cover rounded-top image" alt="${book.name}" />
                      </div>
                      <div class="p-4 border border-secondary border-top-0 rounded-bottom">
                        <p class="bookId d-none">${book.id}</p>
                        <h4 class="text-truncate name" style="max-width: 100%" data-bs-toggle="tooltip" title="${book.name}">${book.name}</h4>
                        <p class="author">${book.author}</p>
                        <div class="d-flex justify-content-between flex-lg-wrap">
                          <p class="text-dark fs-5 fw-bold mb-0 price">${book.price.toLocaleString("vi-VN")} ₫</p>
                        </div>
                      </div>
                    </div>
      `;
      resultsContainer.appendChild(bookItem);
    });

    // TODO:
    // reattach event handler
    buildBookNameTooltips();
    handleBookItemEvent();
  }
}

function renderPaginationBar(paginationMetadata) {
  paginationContainer.innerHTML = ``;
  if (paginationMetadata.totalNumberOfPages > 0) {
    // TODO:
    // render prev
    const prev = document.createElement(`a`);
    prev.setAttribute(`id`, `pagBarPrevBtn`);
    prev.setAttribute(`href`, `#`);
    prev.setAttribute(`arial-label`, `Previous`);
    prev.setAttribute(`arial-disabled`, `${!paginationMetadata.hasPreviousPage}`);
    prev.classList.add(`rounded`);
    if (!paginationMetadata.hasPreviousPage) prev.classList.add(`disabled`);
    prev.innerHTML = `&laquo;`;
    paginationContainer.appendChild(prev);
    // render pages
    for (let i = 1; i <= paginationMetadata.totalNumberOfPages; i++) {
      const page = document.createElement(`a`);
      page.setAttribute(`href`, `#`);
      page.classList.add(`rounded`, `mx-1`, `pagNumBtn`);
      page.innerText = `${i}`;
      page.classList.toggle(`active`, i - 1 == paginationMetadata.currentPosition);
      paginationContainer.appendChild(page);
    }

    // render next
    const next = document.createElement(`a`);
    next.setAttribute(`id`, `pagBarNextBtn`);
    next.setAttribute(`href`, `#`);
    next.setAttribute(`arial-label`, `Next`);
    next.setAttribute(`arial-disabled`, `${!paginationMetadata.hasNextPage}`);
    next.classList.add(`rounded`);
    if (!paginationMetadata.hasNextPage) next.classList.add(`disabled`);
    next.innerHTML = `&raquo;`;
    paginationContainer.appendChild(next);

    handlePrevAndNextButtonEvent();
    handlePageNumberButtonEvent();
  }
}

function handleRateRadioButtonEvent() {
  rates.forEach((rate) =>
    rate.addEventListener(`click`, async (event) => {
      // TODO

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
    })
  );
}

function handleBookSearchBoxEvent() {
  bookSearchBox.addEventListener(`keydown`, async (event) => {
    if (event.key !== `Enter`) return;

    event.preventDefault();

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
