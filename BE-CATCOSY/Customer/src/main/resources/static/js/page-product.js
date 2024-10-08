//
// const urlParams = new URLSearchParams(window.location.search);
// const sortPriceParam = urlParams.get('sortPrice');
//
// const selectElement = document.getElementById('sort-by');
// for (let option of selectElement.options) {
//     if (option.id === sortPriceParam) {
//         option.selected = true;
//     }
// }


const urlParams = new URLSearchParams(window.location.search);
const sortPriceParam = 'option-sort-' + urlParams.get('sortPrice');

// console.log(sortPriceParam);

//
// const option_sorts = document.querySelectorAll(".option-sort");

const selectElement = document.getElementById('sort-by');
for (let option of selectElement.options) {
    if (option.classList.contains(sortPriceParam)) {
        option.selected = true;
    }
}

document.getElementById('filter').addEventListener('change', function() {
    var leftPageProduct = document.querySelector('.left-page-product');
    var mainPage = document.querySelector('.page-product');
    if (this.checked) {
        leftPageProduct.style.height = '500px';
    } else {
        leftPageProduct.style.height = '0';
    }
});


//
// option_sorts.forEach((option) => {
//     if (option.classList.contains(sortPriceParam)) option.select = true;
// })


