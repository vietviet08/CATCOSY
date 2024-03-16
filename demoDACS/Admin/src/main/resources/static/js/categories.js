$('document').ready(function (){

    $('table #updateBtn').on('click', function (e){
        e.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (category, status){
            $('#updateId').val(category.id);
            $('#updateName').val(category.name);
        });
        $('#updateCategory').modal();
    });

    }
)