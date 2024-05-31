$('document').ready(function () {


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
                        '<img src="data:image/jpeg;base64,' + orderDetail.image + '" style="width: 60px; height: 60px;">' +
                        '</a>' +
                        '</th>' +
                        '<td>' +
                        '<a href="/product-detail/' + orderDetail.idProduct + '" style="text-decoration: none; color: #333333">' + orderDetail.nameProduct + '</a>' +
                        '</td>' +
                        '<td class="price-product">' + VND.format(orderDetail.unitPrice) + '</td>' +
                        '<td>' + orderDetail.quantityAndSize + '</td>' +
                        '<td class="price-product">' + VND.format(orderDetail.totalPrice) + '</td>' +
                        '<td class="">' +
                        (orderDetail.allowComment ? '<button class="btn btn-primary ">Rate </button>' : '<button class="btn btn-secondary" disabled>Rate </button>') +

                        '</td>' +

                        '</tr>'
                    );
                });
            });

            var updateCategoryModal = new bootstrap.Modal(document.getElementById('detailOrder'));
            updateCategoryModal.show();
        });


    }
);