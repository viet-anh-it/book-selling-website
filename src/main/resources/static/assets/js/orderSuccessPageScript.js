// constants
const orderCodeSpan = document.getElementById(`orderCode`);
const createdAtSpan = document.getElementById(`createdAt`);

handleDOMContentLoadedEvent();

function handleDOMContentLoadedEvent() {
  document.addEventListener(`DOMContentLoaded`, (event) => {
    event.preventDefault();

    orderCodeSpan.innerText = sessionStorage.getItem(`orderCode`);
    createdAtSpan.innerText = new Date(sessionStorage.getItem(`createdAt`)).toLocaleString(`vi-VN`);

    // window.sessionStorage.removeItem(`orderCode`);
    // window.sessionStorage.removeItem(`createdAt`);
  });
}
