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
// Lấy giá trị của cartId
var idCart = document.querySelector('#cartId').value;

var inputElements = document.querySelectorAll('.inputQuantity');
var decreaseButtons = document.querySelectorAll('.decrease');
var increaseButtons = document.querySelectorAll('.increase');

var idCartItems = document.querySelectorAll('.idCartItem');

decreaseButtons.forEach(function(decreaseButton, index) {
    decreaseButton.addEventListener('click', function() {
        var input = inputElements[index];
        var value = parseInt(input.value);
        if (value > 1) {
            input.value = value - 1;
            var idCartItem = idCartItems[index].value;
            updateCartItem(idCartItem, input.value);
        }
    });
});

increaseButtons.forEach(function(increaseButton, index) {
    increaseButton.addEventListener('click', function() {
        var input = inputElements[index];
        var value = parseInt(input.value);
        if (value >= 0) {
            input.value = value + 1;
            var idCartItem = idCartItems[index].value;
            updateCartItem(idCartItem, input.value);
        }
    });
});

inputElements.forEach(function(input, index) {
    input.addEventListener('input', function() {
        var value = parseInt(input.value);
        if (value > 0) {
            var idCartItem = idCartItems[index].value;
            updateCartItem(idCartItem, value);
        }
    });

    input.addEventListener('blur', function() {
        var value = parseInt(input.value);
        if (value > 0) {
            var idCartItem = idCartItems[index].value;
            updateCartItem(idCartItem, value);
        }
    });

    input.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            var value = parseInt(input.value);
            if (value > 0) {
                var idCartItem = idCartItems[index].value;
                updateCartItem(idCartItem, value);
            }
        }
    });
});

function updateCartItem(idCartItem, quantity) {
    $.ajax({
        url: '/cart/update-quantity',
        type: 'POST',
        data: {
            idCart: idCart,
            idCartItem: idCartItem,
            quantity: quantity
        },
        success: function(price) {
            var formattedPrice = price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
            document.querySelector("#totalPrice").textContent = formattedPrice;
        },
        error: function(xhr, status, error) {
            console.error("Error updating cart item: ", error);
        }
    });
}


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


