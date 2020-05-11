layer.config({extend: 'moon/style.css', skin: 'layer-ext-moon'});

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
                layer.msg(data.message,{icon:2,shift:6});
            }

        },
        error:function (data) {

            console.log(data);

            swal("系统异常", {button: false,icon:"error",timer:1000});
        }
    })
});

// //初始化菜单
// $.ajax({
//
//     url:'/authPermission/getMenus',
//     dataType: 'json',
//     async:false,//此步操作很重要
//     success:function (data) {
//         if (data.code === 0)
//         {
//             let result = data.data;
//
//             Render.userName($('.login-user'),result.userName);
//             Render.navigation($('#side-menu'),result.menus);
//
//             $('.secret-change').each(function () {
//
//                 let attr = $(this).attr('id');
//                 $(this).val(result[attr]);
//             });
//         }
//         else
//         {
//             layer.msg(data.message,{icon:2,shift:6});
//         }
//     },
//     error:function (data) {
//
//         console.log(data);
//         layer.msg('系统异常',{icon:2,shift:6});
//     }
// });

$('.modify-secret').on('click',function () {
    $('#modify-dialog').modal('show');
});

$("#saveModify").on('click',function () {


    let param = Plain.getParam('.secret-change');

    if (param['currentPassword'] === undefined
        || param['newPassword'] === undefined
        ||  param['confirmNewPassword'] === undefined)
    {
        layer.msg('输入不全，请补全参数',{icon:2,shift:6});

        return;
    }

    if (param['currentPassword'] === param['newPassword']
        || param['currentPassword'] === param['confirmNewPassword'])
    {
        layer.msg('新密码不能与旧密码相同',{icon:2,shift:6});
        return;
    }

    if (param['newPassword'] !== param['confirmNewPassword'])
    {
        layer.msg('两次输入密码不一致',{icon:2,shift:6});
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
                layer.msg('密码修改成功',{icon:1});
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

});

