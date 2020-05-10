
let URL = '/authUser/list';
let $table = $("#user-list");

layer.config({
    extend: 'moon/style.css', //加载新皮肤
    skin: 'layer-ext-moon' //一旦设定，所有弹层风格都采用此主题。
});
$(function () {
    sessionStorage.removeItem('user');
});

$("#search-btn").on('click',function () {
    $table.bootstrapTable('refresh', {'url': URL});
});

$("#reset-btn").on('click',function () {

    $(".query-cond").each(function () {
        $(this).val("");
    });
});

$('#createUser').on('click',function () {

    window.location.href ='/authUser/edit';
});

$('#deleteUser').on('click',function () {

    $.ajax({

        url:'/authUser/deleteUser',
        method:'post',
        data:{'id':$('#deleteId').val()},
        dataType:'json',
        success:function (data) {

            if (data.code === 0)
            {
                $table.bootstrapTable('refresh', {'url': URL});
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


$table.bootstrapTable({
    url: URL,
    dataType: "json",
    toolbar: "#toolbar",
    striped: true,
    pageSize: 20,
    pageList: [10, 20, 50, 100],
    pagination: true,
    sidePagination: "server",
    queryParams: function (params) {
        return Plain.getParam('.query-cond',params);
    },
    responseHandler: function (res) {
        return res.data;
    },
    columns: [
        {
            title: '序号',
            align: 'center',
            width: '5%',
            formatter: function (cellValue, record,index) {
                let options = $table.bootstrapTable('getOptions');
                let pageSize = options.pageSize;
                let pageNum = options.pageNumber;

                return pageSize * (pageNum - 1) + index + 1;
            }
        },
        {
            title: '登陆账号',
            align: 'center',
            field: 'loginName',
        },
        {
            title: '用户姓名',
            align: 'center',
            field: 'name'
        },
        {
            title: '手机号码',
            align: 'center',
            field: 'phone',
        },
        {
            title: '邮箱',
            align: 'center',
            field: 'email'
        }
        ,{
            title: '操作',
            align: 'center',
            formatter:function () {

                let btn = [];
                let edit = "<button class=\'user-edit btn btn-primary btn-sm\'>编辑</button>";
                let deleteBtn= "<button class=\'user-delete btn btn-danger btn-sm\'>删除</button>";

                btn.push(edit);
                btn.push(deleteBtn);
                return btn.join(" ");
            },
            events: {
                'click .user-edit ': edit ,
                'click .user-delete': deleteUser
            }
        }],
    onLoadSuccess: function (data) {
    }
});

function edit(e, val, row) {

    let s = JSON.stringify(row);

    sessionStorage.setItem('user',s);

    window.location.href="/authUser/edit";
}


function deleteUser(e, val, row) {

    $('#deleteId').val(row.id);
    $('#deleteModel').modal('show');

}


