var inputPassword = document.querySelector(' .password-login');
var eyeShow = document.querySelector('.show-pass');
var eyeHide = document.querySelector('.hide-pass');

function showPassword(){
    if (inputPassword.type === 'password') {
        inputPassword.type = 'text';
        eyeShow.classList.add('hide');
        eyeHide.classList.add('show');
    } else {
        inputPassword.type = 'password';
        eyeShow.classList.remove('hide');
        eyeHide.classList.remove('show');
    }
}


var inputPasswords = document.querySelectorAll('.pass-register');
var eyeShow1 = document.querySelectorAll('.show-pass-1');
var eyeHide1 = document.querySelectorAll('.hide-pass-1');
function showPasswordRegister(){
    inputPasswords.forEach((e) => {
        if (e.type === 'password') {
            e.type = 'text';
            eyeShow1.forEach((e) => {
                e.classList.add('hide');
            })
            eyeHide1.forEach((e) => {
                e.classList.add('show');
            })
        } else {
            e.type = 'password';
            // eyeShow1.classList.remove('hide');
            // eyeHide1.classList.remove('show');
            eyeShow1.forEach((e) => {
                e.classList.remove('hide');
            })
            eyeHide1.forEach((e) => {
                e.classList.remove('show');
            })
        }


    })

}

