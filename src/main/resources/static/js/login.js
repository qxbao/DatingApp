$(document).ready(function() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const isFromRegistration = urlParams.get('fromRegister');
    if (isFromRegistration) {
        $('#registrationSuccessModalToggle').modal('show');
    }
});