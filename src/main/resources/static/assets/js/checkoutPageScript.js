// constants
const confirmOrderBtn = document.getElementById(`confirmOrderBtn`);
const name = document.getElementById(`name`);
const phone = document.getElementById(`phone`);
const province = document.getElementById(`province`);
const district = document.getElementById(`district`);
const ward = document.getElementById(`ward`);
const home = document.getElementById(`home`);
const note = document.getElementById(`note`);

handleDOMContentLoadedEvent();

function handleDOMContentLoadedEvent() {
  document.addEventListener(`DOMContentLoaded`, (event) => {
    event.preventDefault();

    // TODO
    handleConfirmOrderBtnEvent();
  });
}

function handleConfirmOrderBtnEvent() {
  confirmOrderBtn.addEventListener(`click`, (event) => {
    event.preventDefault();

    // TODO: build dto -> json -> request body
    const orderDto = {
      at: new Date().toLocaleString(`vi-VN`),
      name: name.value,
      phone: phone.value,
      province: province.value,
      district: district.value,
      ward: ward.value,
      home: home.value,
      note: note.value,
    };
    const json = JSON.stringify(orderDto);
  });
}
