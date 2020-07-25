$(".logout").on("click",function () {

    $.ajax({
        url:'/doLogout',
        dataType:'json',
        method:'post',
        success:function (data) {

            if (data.code === 0)
            {
                window.location.href='/';
            }
            else
            {
                swal(data.message, {button: false,icon:"error",timer:1000});
            }

        },
        error:function (data) {

            console.log(data);

            swal("系统异常", {button: false,icon:"error",timer:1000});
        }
    })
});

//初始化菜单
$.ajax({

    url:'/authPermission/getMenus',
    dataType: 'json',
    async:false,//此步操作很重要
    success:function (data) {
        if (data.code === 0)
        {
            let result = data.data;

            Render.userName($('.login-user'),result.userName);
            Render.navigation($('#side-menu'),result.menus);

            $('.secret-change').each(function () {

                let attr = $(this).attr('id');
                $(this).val(result[attr]);
            });
        }
        else
        {
            swal(data.message, {button: false,icon:"error",timer:1000});
        }
    },
    error:function (data) {

        console.log(data);
        swal('菜单初始化失败，请联系管理员！！！', {button: false,icon:"error",timer:1000});
    }
});

$('.modify-secret').on('click',function () {
    $('#modify-dialog').modal('show');
});

$("#saveModify").on('click',function () {


    let param = Plain.getParam('.secret-change');

    if (param['currentPassword'] === undefined
        || param['newPassword'] === undefined
        ||  param['confirmNewPassword'] === undefined)
    {
        swal('输入不全，请补全参数', {button: false,icon:"error",timer:1000});
        return;
    }

    if (param['currentPassword'] === param['newPassword']
        || param['currentPassword'] === param['confirmNewPassword'])
    {
        swal('新密码不能与旧密码相同', {button: false,icon:"error",timer:1000});
        return;
    }

    if (param['newPassword'] !== param['confirmNewPassword'])
    {
        swal('两次输入密码不一致', {button: false,icon:"error",timer:1000});
        return;
    }

    param['currentPassword'] = md5(param['currentPassword']);
    param['newPassword'] = md5(param['newPassword']);


    $.ajax({

        url:'/user/modifyPassword',
        method:'post',
        data:param,
        dataType:'json',
        success:function (data) {

            if (data.code === 0)
            {
                $('#modify-dialog').modal('hide');
                swal('密码修改成功', {button: false,icon:"success",timer:1000});
            }
            else
            {
                layer.msg(data.message,{icon:2,shift:6});
            }
        },
        error:function (data) {

            console.log(data);
            swal('系统异常', {button: false,icon:"error",timer:1000});
        }
    });

});

