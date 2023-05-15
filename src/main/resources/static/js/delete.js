$(document).ready(function () {
    $("#deleteEntity").on("click", function (event) {
        event.preventDefault();
        const form = $(this).parents('form');
        swal.fire({
            title: "Conferma eliminazione",
            text: "Sei sicuro?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6",
            confirmButtonText: "Elimina",
            cancelButtonText: "Annulla"
        }).then((result) => {
            if (result.isConfirmed) {
                form.submit();
            }
        });
    });
});
