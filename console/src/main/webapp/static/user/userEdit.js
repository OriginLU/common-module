let user;

let configData = $('#configData');


let validator = {
    message: 'This value is not valid',
    feedbackIcons: {
        valid: 'glyphicon glyphicon-ok',
        invalid: 'glyphicon glyphicon-remove',
        validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
        loginName: {
            message: '登录名不能为空',
            validators: {
                notEmpty: {
                    message: '登录名不能为空'
                },
                remote: {
                    url: '/authUser/duplicateName',
                    data: {'loginName': $("#loginName").val()},
                    message: '登录名不能重复',
                    delay: 1500,
                    type: 'post'
                }
            }
        },
        email: {
            validators: {
                email: {
                    message: '邮箱非法'
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

    let item = sessionStorage.getItem('user');
    if (item !== undefined && item !== null)
    {
        delete validator.fields.loginName.validators.remote;
        delete validator.fields.email.validators.remote;
    }
    return validator;

}

$(function () {
    var isSuperuser;
    var userId;
    let item = sessionStorage.getItem('user');
    if (item !== undefined && item !== null && item !== '')
    {
        user = JSON.parse(item);
        $('.query-cond').each(function () {
            let attr = $(this).attr('name');
            $(this).val(user[attr]);
        });
        $('.read-cond').each(function () {
            $(this).attr('readonly',true);
        })
        userId = user.id;
        isSuperuser = user.isSuperuser;
        // 初始化用户的超级用户状态不可编辑
        if (user.initFlag == 1){
            $("#isSuperuser").attr("disabled","disabled");
        }
    }

    // 超级用户
    if (isSuperuser == 1){
        $("#isSuperuser").prop("checked", true);
        $("#selectRoleIds").attr("disabled","disabled");
    }

    $.ajax({
        url : '/authRole/getRoleSelectList',
        method : 'post',
        dataType : 'json',
        data : { 'userId' : userId},
        success:function (data) {
            if (data.code === 0) {
                var selectedValue = new Array();
                for (var i in data.data){
                    var selectRole = data.data[i];
                    if (selectRole.selected == true){
                        selectedValue.push(selectRole.value);
                    }
                    $("#selectRoleIds").append("<option value=" + selectRole.value + ">" + selectRole.text + "</option>");
                }
                $('#selectRoleIds').selectpicker('val', selectedValue);
                $('#selectRoleIds').selectpicker('refresh');
            } else {
                layer.msg(data.message,{icon:2,shift:6});
            }
        },
        error:function (data) {
            console.log(data);
            layer.msg('系统异常',{icon:2,shift:6});
        }
    });
});

function doSubmit() {
    var submitData = Plain.getParam('.query-cond');
    if ($("#isSuperuser").is(':checked')){
        submitData.isSuperuser = $("#isSuperuser").val();
    } else {
        var roleIdArray = $("#selectRoleIds").val();
        if (roleIdArray != null && roleIdArray != undefined){
            submitData.roleIds = roleIdArray.join(",");
        }
        submitData.isSuperuser = 0;
    }
    $.ajax({
        url:'/authUser/save',
        data:submitData,
        method:'post',
        dataType:'json',
        success:function (data) {

            if (data.code === 0)
            {
                sessionStorage.removeItem('user');
                window.location.href='/authUser/index';
            }
            else
            {
                layer.msg(data.message,{icon:2,shift:6});
            }
        },
        error:function (data) {

            console.log(data);

            layer.msg('系统异常',{icon:2,shift:6});
        }
    });

}

$("#back").on('click',function () {

    sessionStorage.removeItem('user');

    window.location.href='/authUser/index';
});

$("#isSuperuser").on('change',function () {
    if (this.checked){
        $('#selectRoleIds').attr('disabled','disabled');
        $('#selectRoleIds').selectpicker('refresh');
    } else {
        $('#selectRoleIds').removeAttr('disabled');
        $('#selectRoleIds').selectpicker('refresh');
    }
});