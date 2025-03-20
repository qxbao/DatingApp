$(document).ready(function() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const isFromRegistration = urlParams.get('fromRegister');
    if (isFromRegistration) {
        $('#registrationSuccessModalToggle').modal('show');
    }
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = {};
        const fields = ['name', 'email', 'preference', 'dob', 'gender', 'password'];
        const validGender = ['male', 'female'];
        const validPreference = ['male', 'female', 'both'];
        fields.forEach(field => {
            formData[field] = document.querySelector('#registerForm').querySelector('[name=reg_' + field +']').value;
        });
        if (Object.values(formData).some(value => value == null || value === ""))
            return displayError("Please fill in all fields");
        if (formData.name.length < 3)
            return displayError("Name must be at least 3 characters long");
        if (validGender.includes(formData.gender) === false || validPreference.includes(formData.preference) === false)
            return displayError("Invalid gender or preference");
        const parsedDob = Date.parse(formData.dob);
        if (isNaN(parsedDob) || parsedDob > Date.now() || parsedDob < Date.now() - 100 * 365 * 24 * 60 * 60 * 1000)
            return displayError("Date of birth is not valid");
        if (formData.password.length < 8)
            return displayError("Password must be at least 8 characters long");
        $.ajax({
            type: "POST",
            url:  "/user/register",
            data: JSON.stringify(formData),
            contentType: "application/json",
            error: function (xhr, status, error) {
                displayError("An error occurred while processing your request.");
            }
        }).then(data => {
            console.log(data)
            if (data.error) {
                displayError(data.errorMessage);
            } else {
                $('#registrationModalToggle').modal('hide');
                const regInputs = document.querySelectorAll('#registerForm input');
                for (const input of regInputs) {
                    input.value = "";
                }
                $('#registrationSuccessModalToggle').modal('show');
            }
        })
    })
});


const displayError = (message) => {
    document.querySelector("#errorDetail").innerText = message;
    $('#errorModalToggle').modal('show');
}