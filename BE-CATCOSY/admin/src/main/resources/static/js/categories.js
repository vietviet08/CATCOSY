$('document').ready(function (){

        $('table #btnUpdate').on('click', function (e){
            e.preventDefault();
            var href = $(this).attr('href');
            $.get(href, function (category, status){
                $('#updateId').val(category.id);
                $('#updateName').val(category.name);
            });

            var updateCategoryModal = new bootstrap.Modal(document.getElementById('updateCategory'));
            updateCategoryModal.show();
        });

    }
)


