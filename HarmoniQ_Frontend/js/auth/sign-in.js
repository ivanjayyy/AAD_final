const baseUrl = "http://localhost:8080/api/v1/auth/sign-in";

function signIn() {
    const username=$('#username').val()
    const password=$('#password').val()

    $.ajax({
        url:baseUrl,
        method:'POST',
        contentType:'application/json',

        data:JSON.stringify({
            "username":username,
            "password":password
        }),
        success:function (response) {
            if (response.status === 200) {
                console.log(response.status)
                console.log(response.message)
                alert("User login Successful!")

                localStorage.setItem("token",response.data)
                window.location.href = "../../index.html";
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