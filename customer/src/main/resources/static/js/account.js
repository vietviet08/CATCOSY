$('document').ready(function () {

        var idProductNeedRate;
        var idOrderDetail;
        var totalStarRate = 5;

        /*Order */
        $('.btnDetailOrder').on('click', function (e) {
            e.preventDefault();
            var href = $(this).attr('href');
            $.get(href, function (orderDetails, status) {
                $("#listOrderDetail").empty();
                const VND = new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND'
                });
                $.each(orderDetails, function (index, orderDetail) {
                    var allowComment = orderDetail.isAllowComment;
                    console.log(orderDetail)
                    console.log(allowComment)
                    $("#listOrderDetail").append(
                        '<tr>' +
                        '<th scope="row">' +
                        '<a href="/product-detail/' + orderDetail.idProduct + '" >' +
                        '<img src="' + orderDetail.image + '" style="width: 60px; height: 60px;">' +
                        '</a>' +
                        '</th>' +
                        '<td>' +
                        '<a href="/product-detail/' + orderDetail.idProduct + '" style="text-decoration: none; color: #333333">' + orderDetail.nameProduct + '</a>' +
                        '</td>' +
                        '<td class="price-product">' + VND.format(orderDetail.unitPrice) + '</td>' +
                        '<td>' + orderDetail.quantityAndSize + '</td>' +
                        '<td class="price-product">' + VND.format(orderDetail.totalPrice) + '</td>' +
                        '<td class="">' +
                        (orderDetail.allowComment ? '<button type="button" class="btn btn-primary btnRateProduct btnRateRate"  data-bs-toggle="modal" data-bs-target="#rateProduct">Rate </button>' : '<button class="btn btn-secondary btnRateRate" disabled>Rate </button>') +

                        '</td>' +

                        '</tr>'
                    );
                    var currentRateButton = $("#listOrderDetail").find(".btnRateRate").eq(index);
                    currentRateButton.click(function () {
                        var idProductNeedRate = orderDetail.idProduct;
                        var idOrderDetail = orderDetail.id;
                        $('#idProductRate').val(idProductNeedRate);
                        $('#idOrderDetail').val(idOrderDetail);
                        console.log(idProductNeedRate)
                    });
                });
                // var btnRates = $('.btnRateRate');
                // btnRates.each(function (index, element) {
                //
                //
                // });
            });

            var updateCategoryModal = new bootstrap.Modal(document.getElementById('detailOrder'));
            updateCategoryModal.show();
        });


        /*rate product*/

        const stars = $(".stars i");
        // Loop through the "stars" NodeList
        stars.each(function (index1) {
            // Add an event listener that runs a function when the "click" event is triggered
            $(this).on("click", function () {

                // Loop through the "stars" NodeList Again
                stars.each(function (index2) {
                    // Add the "active" class to the clicked star and any stars with a lower index
                    // and remove the "active" class from any stars with a higher index
                    if (index1 >= index2) {
                        $(this).addClass("active-rate");
                    } else {
                        $(this).removeClass("active-rate");
                    }
                });

                totalStarRate =  $('.active-rate').length
                $('#star-need-rate').val(totalStarRate);
            });
        });

        $('.input-images').imageUploader({
            imagesInputName: 'photos',
            preloadedInputName: 'old',
            maxSize: 2 * 1024 * 1024,
            maxFiles: 5,
            label: 'Drag & Drop files here or click to browse (Maximum 5 image)'
        });

        totalStarRate =  $('.active-rate').length
        $('#star-need-rate').val(totalStarRate);


        // $('.btnRateProduct').on('click', function (e) {
        //     e.preventDefault();
        //
        //     var rateProductModal = new bootstrap.Modal(document.getElementById('rateProduct'));
        //     console.log(rateProductModal);
        //     rateProductModal.show();
        // });
    }
);