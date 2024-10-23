

let img = document.querySelector(".img-product");

img.onmousemove = function(e) {
    // e.target.style.setProperty('--x',(100*e.offsetX/e.target.offsetWidth)+'%');
    // e.target.style.setProperty('--y',(100*e.offsetY/e.target.offsetHeight)+'%');

    // requestAnimationFrame(() => {
    //     e.target.style.setProperty('--x', (100 * e.offsetX / e.target.offsetWidth) + '%');
    //     e.target.style.setProperty('--y', (100 * e.offsetY / e.target.offsetHeight) + '%');
    // });

}
function roundToHalf(num) {
    return Math.round(num * 2) / 2;
}



$(document).ready(function () {

    // $('.img-product').easyZoom();

    $('.container-img-product img').on('click', function(){
        var src = $(this).attr('src');
        $('.img-main-detail img').css('opacity', '0');
        setTimeout(function() {
            $('.img-main-detail img').attr('src', src).css('opacity', '1');
        }, 130);
    });


        // var zoomImageSrc = $('#productImage').attr('src');
        // $('#productImage').attr('data-cloudzoom', "zoomImage: '" + zoomImageSrc + "', easeTime: 0, zoomPosition: 'inside', autoInside: true, easing: 0, disableOnScreenWidth:1024");

    $('.slick-slider-nav-img').slick({
        slidesToShow: 5,
        slidesToScroll: 1,
        asNavFor: '.slick-slider-for-img',
    });

    $('.slick-slider-for-img').slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        fade: true,
        asNavFor: '.slick-slider-nav-img',
        focusOnSelect: true,
        centerMode: true,
    });

    $(".decrease").click(function() {
        var value = parseInt($("#quantity").val());
        if (value > 1) {
            $("#quantity").val(value - 1);
        }
    });

    $(".increase").click(function() {
        var value = parseInt($("#quantity").val());
        $("#quantity").val(value + 1);
    });


    $('.container-product-same').slick({
        slidesToShow: 5,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 2000,
    });

});



