let role;

let configData = $('#configData');


let validator = {
    message: 'This value is not valid',
    feedbackIcons: {
        valid: 'glyphicon glyphicon-ok',
        invalid: 'glyphicon glyphicon-remove',
        validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
        code: {
            message: '角色编号不能为空',
            validators: {
                notEmpty: {
                    message: '角色编号不能为空'
                },
                remote: {
                    url: '/authRole/existCode',
                    data: {'code': $("#code").val()},
                    message: '角色编号不能重复',
                    delay: 1500,
                    type: 'post'
                }
            }
        },
        name: {
            validators: {
                notEmpty: {
                    message: '角色名称不能为空'
                },
                remote: {
                    url: '/authRole/existName',
                    data: {'name': $("#name").val()},
                    message: '角色名称不能重复',
                    delay: 1500,
                    type: 'post'
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
    let item = sessionStorage.getItem('role');
    if (item !== undefined && item !== null)
    {
        delete validator.fields.code.validators.remote;
        delete validator.fields.name.validators.remote;
    }
    return validator;

}

$(function () {
    var roleId;
    let item = sessionStorage.getItem('role');
    if (item !== undefined && item !== null && item !== '')
    {
        role = JSON.parse(item);
        $('.query-cond').each(function () {
            let attr = $(this).attr('name');
            $(this).val(role[attr]);
        });
        $('.read-cond').each(function () {
            $(this).attr('readonly',true);
        })
        roleId = role.id
    }
    initRolePermission(roleId);
});

function doSubmit() {
    var submitData = Plain.getParam('.query-cond');
    var permissionIds = '';
    $('input[name="permissionIds"]:checked').each(function(){
        permissionIds += $(this).val() + ",";
    });
    if (permissionIds != ''){
        submitData.permissionIds = permissionIds.substring(0, permissionIds.length-1);
    }
    $.ajax({
        url: '/authRole/save',
        data:submitData,
        method:'post',
        dataType:'json',
        success:function (data) {
            if (data.code === 0)
            {
                sessionStorage.removeItem('role');
                window.location.href='/authRole/index';
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

function initRolePermission(roleId){
    var permissionTable = $('#permisssionTable');
    // var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');
    $.ajax({
        url : '/authRole/getRolePermissionList',
        method : 'post',
        dataType : 'json',
        data : { 'roleId' : roleId},
        success:function (data) {
            if (data.code === 0) {
                var permissionTd = '';
                for (var i in data.data){
                    var checked = "";
                    var permissionJson = data.data[i];
                    if (permissionJson.checked == "1"){
                        checked = 'checked="checked"';
                    }
                    permissionTd += '<tr><td style="text-align: center;"><input type="checkbox" name="permissionIds" value="'+permissionJson.id+'" '+checked+'/></td>' +
                        '<td style="text-align: center;"><span>'+permissionJson.text+'</span></td></tr>'
                }
                permissionTable.html(permissionTd);
            } else {
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
    sessionStorage.removeItem('role');
    window.location.href='/authRole/index';
});