// const VND = new Intl.NumberFormat('vi-VN', {
//     style: 'currency', currency: 'VND',
// });
// const nodePriceProduct = document.querySelectorAll(".price-product");
// nodePriceProduct.forEach((nodePrice, index) => {
//     nodePrice.textContent = VND.format(nodePrice.textContent);
// })

// const VND = new Intl.NumberFormat('vi-VN', {
//     style: 'currency', currency: 'VND',
// });
//
// $(".price-product").each(function (index) {
//     $(this).text(VND.format($(this).text()));
// });


function getAll(){
    $.ajax({
        url: "/cart/v1",
        success: function (items){

        }
    })
}

function upQuantity() {
    $.ajax({
        type: "POST",
        url: "/update-cart?up=true",

    })
}


