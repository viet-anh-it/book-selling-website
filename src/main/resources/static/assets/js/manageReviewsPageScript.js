// Định nghĩa formatter để tái sử dụng
const vnDateTimeFormatter = new Intl.DateTimeFormat("vi-VN", {
  day: "2-digit",
  month: "2-digit",
  year: "numeric",
  hour: "2-digit",
  minute: "2-digit",
  hour12: false,
});

document.addEventListener(`DOMContentLoaded`, (event) => {
  event.preventDefault();

  handleApproveBookReviewButtonEvent();
  handleDeleteReviewButtonEvent();
  handleFilterByApprovedAndSortByPostedAtSelectEvent();
  handlePaginationBarPreviousAndNextButtonEvent();
  handlePaginationBarPageNumberButtonEvent();
});

function handleApproveBookReviewButtonEvent() {
  const approveBookReviewButtons =
    document.querySelectorAll(`.approveReviewButton`);
  approveBookReviewButtons.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();
      const reviewId = parseInt(btn.dataset.reviewId);
      await fetchApproveReviewApi(reviewId);
      const page = parseInt(btn.dataset.currentPage);
      await fetchAllReviewsApi(page);
    })
  );
}

function handleDeleteReviewButtonEvent() {
  const deleteReviewButtons = document.querySelectorAll(`.deleteReviewButton`);
  deleteReviewButtons.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();
      const reviewId = parseInt(btn.dataset.reviewId);
      await fetchDeleteReviewApi(reviewId);
      const page = parseInt(btn.dataset.currentPage);
      await fetchAllReviewsApi(page);
    })
  );
}

function handleReviewDetailButtonEvent() {}

function handleFilterByApprovedAndSortByPostedAtSelectEvent() {
  const select = document.getElementById(`filterByApprovedAndSortByPostedAt`);
  select.addEventListener(`change`, async (event) => {
    event.preventDefault();
    const page = parseInt(select.dataset.currentPage);
    await fetchAllReviewsApi(page);
  });
}

function handlePaginationBarPreviousAndNextButtonEvent() {
  const prev = document.getElementById(`prev`);
  const next = document.getElementById(`next`);
  [prev, next].forEach((btn) => {
    if (btn !== null) {
      btn.addEventListener(`click`, async (event) => {
        if (btn.classList.contains(`disabled`)) return;
        event.preventDefault();
        if (btn.id === `prev`) {
          await fetchAllReviewsApi(prev.dataset.prevPage);
        } else if (btn.id === `next`) {
          await fetchAllReviewsApi(next.dataset.nextPage);
        }
      });
    }
  });
}

function handlePaginationBarPageNumberButtonEvent() {
  const pageNumberButtons = document.querySelectorAll(`.page-number`);
  pageNumberButtons.forEach((btn) =>
    btn.addEventListener(`click`, async (event) => {
      event.preventDefault();
      const page = parseInt(btn.dataset.page);
      await fetchAllReviewsApi(page);
    })
  );
}

async function fetchApproveReviewApi(reviewId) {
  const apiUrl = `http://localhost:8080/api/reviews/approve/${reviewId}`;
  await fetch(apiUrl, { method: `PATCH` });
}

async function fetchAllReviewsApi(page) {
  const select = document.getElementById(`filterByApprovedAndSortByPostedAt`);
  const value = select.value;
  const values = value.split(`,`);
  const approved = values[0];
  const sort = `postedAt,` + values[1];
  const size = 6;
  const urlSearchParams = new URLSearchParams();
  urlSearchParams.append(`approved`, `${approved}`);
  urlSearchParams.append(`page`, `${page}`);
  urlSearchParams.append(`size`, `${size}`);
  urlSearchParams.append(`sort`, `${sort}`);
  const queryString = `?${urlSearchParams.toString()}`;
  const apiUrl = `http://localhost:8080/api/reviews${queryString}`;
  const response = await fetch(apiUrl, { method: `GET` });
  const responseObj = await response.json();
  if (
    response.status === 200 &&
    Object.hasOwn(responseObj, `data`) &&
    Array.isArray(responseObj.data)
  ) {
    renderReviewsTableRows(responseObj.data, responseObj.paginationMetadata);
    renderPaginationBar(responseObj.data, responseObj.paginationMetadata);
  }
}

/**
 * Sinh các <tr> cho bảng đánh giá
 * @param {Array} data - mảng reviewDto từ API
 */
function renderReviewsTableRows(data, paginationMetadata) {
  const tbody = document.getElementById("reviewsTableBody");
  // Nếu không có dữ liệu, có thể hiển thị thông báo
  if (!data || data.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="8" class="text-center">Chưa có đánh giá nào.</td>
      </tr>`;
    // document.querySelector(".pagination").innerHTML = "";
    return;
  }

  // Sinh từng dòng
  const rowsHtml = data
    .map((reviewDto, index) => {
      const {
        reviewId,
        reviewerComment,
        givenRatingPoint,
        postedAt,
        approved,
        user: { email },
        book: { name: bookName },
      } = reviewDto;

      // Sinh sao vàng / xám
      const starsHtml = Array.from({ length: 5 }, (_, i) => {
        return `<i class="fa fa-star ${
          i < givenRatingPoint ? "text-warning" : "text-info"
        }" aria-hidden="true"></i>`;
      }).join("");

      // Định dạng thời gian
      const formattedDate = vnDateTimeFormatter.format(new Date(postedAt));

      return `
      <tr>
        <th scope="row" class="text-center">${index + 1}</th>
        <td>${email}</td>
        <td>${bookName}</td>
        <td><div class="d-flex">${starsHtml}</div></td>
        <td>${reviewerComment}</td>
        <td class="text-center">${formattedDate}</td>
        <td class="text-center">${approved ? "DUYỆT" : "CHỜ"}</td>
        <td class="text-nowrap">
          ${
            !approved
              ? `<button type="button" class="approveReviewButton btn btn-warning text-nowrap" data-review-id="${reviewId}" data-current-page="${paginationMetadata.currentPosition}"><i class="fa-solid fa-square-check"></i></button>`
              : ""
          }
          <button type="button" class="deleteReviewButton btn btn-danger text-nowrap" data-review-id="${reviewId}" data-current-page="${
        paginationMetadata.currentPosition
      }"><i class="fa-solid fa-trash"></i></button>
        </td>
      </tr>`;
    })
    .join("");

  tbody.innerHTML = rowsHtml;

  // Thêm lại sự kiện cho các nút sau khi xóa HTML gốc và gán lại HTML thông qua innerHTML
  handleApproveBookReviewButtonEvent();
  handleDeleteReviewButtonEvent();
}

/**
 * Sinh thanh phân trang
 * @param {Object} meta - paginationMetadata từ API
 *   { currentPosition, totalNumberOfPages, hasPreviousPage, hasNextPage, previousPosition, nextPosition }
 */
function renderPaginationBar(data, meta) {
  if (meta.totalNumberOfPages === 0) {
    document.querySelector(".pagination").innerHTML = "";
    return;
  }

  const { currentPosition, totalNumberOfPages, hasPreviousPage, hasNextPage } =
    meta;
  const ul = document.querySelector(".pagination");

  // Nếu không tìm thấy pagination container
  if (!ul) return;

  let itemsHtml = "";

  // Nút Prev
  itemsHtml += `
    <li data-prev-page="${
      meta.currentPosition - 1
    }" id="prev" class="page-item ${hasPreviousPage ? "" : "disabled"}">
      <a class="page-link" href="#" data-page="${
        hasPreviousPage ? meta.previousPosition : currentPosition
      }">&laquo;</a>
    </li>`;

  // Các trang
  for (let i = 0; i < totalNumberOfPages; i++) {
    itemsHtml += `
      <li data-page="${i}" class="page-item  page-number ${
      i === currentPosition ? "active" : ""
    }">
        <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
      </li>`;
  }

  // Nút Next
  itemsHtml += `
    <li data-next-page="${
      meta.currentPosition + 1
    }" id="next" class="page-item ${hasNextPage ? "" : "disabled"}">
      <a class="page-link" href="#" data-page="${
        hasNextPage ? meta.nextPosition : currentPosition
      }">&raquo;</a>
    </li>`;

  ul.innerHTML = itemsHtml;

  handlePaginationBarPreviousAndNextButtonEvent();
  handlePaginationBarPageNumberButtonEvent();
}

async function fetchDeleteReviewApi(reviewId) {
  const apiUrl = `http://localhost:8080/api/reviews/${reviewId}`;
  await fetch(apiUrl, { method: `DELETE` });
}
