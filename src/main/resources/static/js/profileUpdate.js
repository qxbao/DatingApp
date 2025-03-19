function openPreview(e) {
    const root = $(e);
    const parent = root.parent().get(0);
    const input = parent.querySelector("input");
    if (!input) return alert("No input found");
    document.querySelector("#previewImage").src = input.value;
    $('#imagePreviewModal').modal('show')
}

function uploadImage(e) {
    const root = $(e);
    const parent = root.parent().get(0);
    const input = parent.querySelector("input");
    if (!input) return alert("No input found");
    $.ajax({
        type: "POST",
        url:  "/profile/photo/add",
        data: JSON.stringify({ url : input.value}),
        contentType: "application/json",
        complete: function (data) {
            alert("Photo upload successfully!!!");
            location.reload();
        },
    });
}

function removeImage(id) {
    $.ajax({
        type: "POST",
        url:  "/profile/photo/remove",
        data: JSON.stringify({ id : id}),
        contentType: "application/json",
        complete: function (data) {
            alert("Photo removed!!!");
            location.reload();
        },
    });
}