
let URL = '/authRole/list';
let $table = $("#role-list");

layer.config({
    extend: 'moon/style.css', //加载新皮肤
    skin: 'layer-ext-moon' //一旦设定，所有弹层风格都采用此主题。
});

$(function () {
    sessionStorage.removeItem('role');
});

$("#search-btn").on('click',function () {
    $table.bootstrapTable('refresh', {'url': URL});
});

$("#reset-btn").on('click',function () {
    $(".query-cond").each(function () {
        $(this).val("");
    });
});

$('#createRole').on('click',function () {
    window.location.href ='/authRole/edit';
});

$('#deleteRole').on('click',function () {
    $.ajax({
        url:'/authRole/delete',
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
            title: '角色编号',
            align: 'center',
            field: 'code',
        },
        {
            title: '角色名称',
            align: 'center',
            field: 'name'
        }
        ,{
            title: '操作',
            align: 'center',
            formatter:function () {
                let btn = [];
                let edit = "<button class=\'role-edit btn btn-primary btn-sm\'>编辑</button>";
                let deleteBtn= "<button class=\'role-delete btn btn-danger btn-sm\'>删除</button>";

                btn.push(edit);
                btn.push(deleteBtn);
                return btn.join(" ");
            },
            events: {
                'click .role-edit ': edit ,
                'click .role-delete': deleteRole
            }
        }],
    onLoadSuccess: function (data) {
    }
});

function edit(e, val, row) {

    let s = JSON.stringify(row);

    sessionStorage.setItem('role',s);

    window.location.href="/authRole/edit";
}


function deleteRole(e, val, row) {

    $('#deleteId').val(row.id);
    $('#deleteModel').modal('show');

}


