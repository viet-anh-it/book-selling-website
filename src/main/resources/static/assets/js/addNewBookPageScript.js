const createBookButton = document.getElementById(`createBookButton`);
createBookButton.addEventListener(`click`, async (event) => {
  event.preventDefault();

  const createBookRequestDTO = {
    isbn: document.getElementById(`isbn`).value,
    name: document.getElementById(`name`).value,
    author: document.getElementById(`author`).value,
    publisher: document.getElementById(`publisher`).value,
    price: document.getElementById(`price`).value,
    stockQuantity: document.getElementById(`stockQuantity`).value,
    description: document.getElementById(`description`).value,
  };

  let json = JSON.stringify(createBookRequestDTO);

  const bookCoverImageInput = document.getElementById(`bookCoverImage`);
  const fileObject = bookCoverImageInput.files[0];
  const formData = new FormData();
  const blobObject = new Blob([json], { type: `application/json` });
  formData.append(`json`, blobObject);
  formData.append(`bookCoverImage`, fileObject);

  let response = await fetch(`http://localhost:8080/api/books/create`, {
    method: `POST`,
    body: formData,
  });

  let responseBody = await response.json();

  document.querySelectorAll(".is-invalid").forEach((el) => {
    el.classList.remove("is-invalid");
  });
  document.querySelectorAll(".invalid-feedback").forEach((el) => {
    el.innerText = "";
  });

  if (
    response.status === 400 &&
    responseBody.type === "VALIDATION" &&
    responseBody.detail
  ) {
    Object.entries(responseBody.detail).forEach(([field, message]) => {
      const inputEl = document.getElementById(field);
      if (!inputEl) return; // nếu không tìm thấy input, bỏ qua

      // thêm class is-invalid vào input
      inputEl.classList.add("is-invalid");

      // tìm .invalid-feedback sát bên và set message
      // giả định .invalid-feedback là sibling ngay sau input
      const feedbackEl =
        inputEl.parentElement.querySelector(".invalid-feedback");
      if (feedbackEl) {
        feedbackEl.innerText = message;
      }
    });
  } else {
    // 5. Nếu không có lỗi, có thể xử lý tiếp (chuyển hướng, hiển thị thông báo thành công, v.v.)
    console.log("Thêm sách thành công:", responseBody);
  }
});
