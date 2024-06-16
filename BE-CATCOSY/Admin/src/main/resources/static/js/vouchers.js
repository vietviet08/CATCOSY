$('document').ready(function () {

    $('table #btnUpdate').on('click', function (e) {
        e.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (voucher, status) {
            $('#idVoucher').val(voucher.id);
            $('#updateCodeVoucher').val(voucher.codeVoucher);
            $('#updatePrice').val(voucher.price);
            $('#updatePercentOfTotalPrice').val(voucher.percentOfTotalPrice);
            $('#UpdateMinimumPrice').val(voucher.minimumPrice);
            $('#updateMinimumTotalProduct').val(voucher.minimumTotalProduct);
            $('#updateUsageLimits').val(voucher.usageLimits);
            $('#updateExpiryDate').val(voucher.expiryDate);
            $('#updateForEmailCustomer').val(voucher.forEmailCustomer);
        });

        var updateCategoryModal = new bootstrap.Modal(document.getElementById('updateCategory'));
        updateCategoryModal.show();
    });

    $('table #btnSendMailDetail').on('click', function (e) {
        e.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (voucher, status) {
            $('#sendMailDetailId').val(voucher.id)
            $('#sendMailDetailCodeVoucher').val(voucher.codeVoucher);
        });

        var updateCategoryModal = new bootstrap.Modal(document.getElementById('sendMailDetail'));
        updateCategoryModal.show();
    });

})
