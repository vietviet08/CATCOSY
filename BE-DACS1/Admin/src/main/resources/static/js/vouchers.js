$('document').ready(function () {

    $('table #btnUpdate').on('click', function (e) {
        e.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (voucher, status) {
            $('#updateId').val(voucher.id);
            $('#updateName').val(voucher.name);
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
