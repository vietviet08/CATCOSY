const navbar = document.querySelector(".nav");
window.addEventListener("scroll", function (e) {
    if (this.scrollY > 5) {
        navbar.classList.add("scrolled")
    } else {
        navbar.classList.remove("scrolled");
    }

});


const btnScroll = document.querySelector(".scroll-to-top");
window.addEventListener("scroll", function (e) {
    if (this.scrollY > 500) {
        btnScroll.classList.add("show");
    } else {
        btnScroll.classList.remove("show");
    }

});

btnScroll.addEventListener("click", function (e) {
    window.scrollTo({
        top: 0, behavior: "smooth"
    });
});


const VND = new Intl.NumberFormat('vi-VN', {
    style: 'currency', currency: 'VND',
});
const nodePriceProduct = document.querySelectorAll(".price-product");
nodePriceProduct.forEach((nodePrice, index) => {
    nodePrice.textContent = VND.format(nodePrice.textContent);
})


const nodeCard = document.querySelectorAll(".card");
const frontImg = document.querySelector(".front-img");
const backImg = document.querySelectorAll(".back-img");
const chooseOptions = document.querySelector(".card__main--choose-options");
const buyNow = document.querySelectorAll(".buy-now");
const addToCart = document.querySelectorAll(".add-to-cart");

nodeCard.forEach((card, index) => {
    card.addEventListener("mouseenter", (e) => {
        backImg[index].setAttribute("style", "  opacity: 100;")
        buyNow[index].setAttribute("style", " transform: translateY(-44px); opacity:100;")
        addToCart[index].setAttribute("style", " transform: translateY(-44px); opacity:100;")
    })
})


nodeCard.forEach((card, index) => {
    card.addEventListener("mouseleave", (e) => {

        setTimeout(function () {
            backImg[index].setAttribute("style", "opacity: 0;");
        }, 0.4);

        buyNow[index].setAttribute("style", " transform: translateY(44px); opacity:0;")
        addToCart[index].setAttribute("style", " transform: translateY(44px); opacity:0;")

    })
})