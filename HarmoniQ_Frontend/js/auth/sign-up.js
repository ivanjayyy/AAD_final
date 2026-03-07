const baseUrl = "http://localhost:8080/api/v1/auth/sign-up";

function signUp() {
    const username=$('#username').val()
    const password=$('#password').val()
    const email=$('#email').val()
    const role="ADMIN"

    $.ajax({
        url:baseUrl,
        method:'POST',
        contentType:'application/json',

        data:JSON.stringify({
            "username":username,
            "email":email,
            "password":password,
            "role":role
        }),
        success:function (response) {
            if (response.status === 200) {
                console.log(response.status)
                console.log(response.message)
                alert(response.data)
                window.location.href = "sign-in.html";
            }
        },
        error: function(error) {
            const msg = error.responseJSON?.message || "Something went wrong";
            const stts = error.responseJSON?.status
            console.log(stts);
            console.log(msg);
            alert(msg);
        }
    })
}