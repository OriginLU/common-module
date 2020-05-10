let systemMail;

let configData = $('#configData');


let validator = {
    message: 'This value is not valid',
    feedbackIcons: {
        valid: 'glyphicon glyphicon-ok',
        invalid: 'glyphicon glyphicon-remove',
        validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
        'system.mailServerHost': {
            validators: {
                notEmpty: {
                    message: '邮箱服务器地址不能为空'
                }
            }
        },
        'system.mailServerPort': {
            validators: {
                notEmpty: {
                    message: '邮件服务器端口不能为空'
                }
            }
        },
        'system.mailUserName': {
            validators: {
                email: {
                    message: '邮箱用户名非法'
                },
                notEmpty: {
                    message: '邮箱用户名不能为空'
                }
            }
        },
        'system.mailPassword': {
            validators: {
                notEmpty: {
                    message: '邮箱密码不能为空'
                }
            }
        }
    }
};

configData.bootstrapValidator(getValidator()).on('success.form.bv', function(e) {
    e.preventDefault();

    console.log("验证通过了......");
    doSubmit();
});

function getValidator(){

    let item = sessionStorage.getItem('systemMail');
    if (item !== undefined && item !== null)
    {
        delete validator.fields.serverHost.validators.remote;
        delete validator.fields.serverPort.validators.remote;
        delete validator.fields.userName.validators.remote;
        delete validator.fields.password.validators.remote;
    }
    return validator;

}

$(function () {
    // alert("${#request.getParameter('mailTest')}")
    // let item = sessionStorage.getItem('user');
    // if (item !== undefined && item !== null && item !== '')
    // {
    //     user = JSON.parse(item);
    //     $('.query-cond').each(function () {
    //         let attr = $(this).attr('name');
    //         $(this).val(user[attr]);
    //     });
    //     $('.read-cond').each(function () {
    //         $(this).attr('readonly',true);
    //     })
    // }
});

function doSubmit() {
    $.ajax({
        url: "/systemMail/saveMailParam",
        data:Plain.getParam('.query-cond'),
        method:'post',
        dataType:'json',
        success:function (data) {
            if (data.code === 0) {
            }
            else {
                layer.msg(data.message,{icon:2,shift:6});
            }
        },
        error:function (data) {
            console.log(data);
            layer.msg('系统异常',{icon:2,shift:6});
        }
    });

}

$("#testSendButton").on('click',function () {
    clearTestResult();
    $('#testSendButton').attr("disabled","disabled");
    // if ($("#serverHost").valid()==0||$("#serverPort").valid()==0||$("#userName").valid()==0||$("#password").valid()==0)
    // {
    //     $('#${formId} #testSendButton').attr("disabled",false);
    //     return;
    // }
    $.ajax({
        url:"/systemMail/testSendMail",
        data:Plain.getParam('.query-cond'),
        method:'post',
        dataType:'json',
        success:function(data){
            $('#testSendButton').attr("disabled",false);
            var text = "<font color='red'>发送失败</font>";
            if (data && data.code === 0){
                text = "<font color='green'>发送成功</font>";
            }else if(data && data.message){
                text = "<font color='red'>"+data.message+"</font>";
            }
            $("#testResultText").html(text);
        },
        error:function(){
            $('#testSendButton').attr("disabled",false);
            document.getElementById("testResultText").innerText="发送失败";
        }
    });
});

$(".query-cond").on('change',function () {
    clearTestResult();
});

function clearTestResult(){
    $("#testResultText").html("");
}