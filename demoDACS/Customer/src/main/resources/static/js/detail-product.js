
$(document).ready(function () {

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
});