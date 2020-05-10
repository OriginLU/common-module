
$(function () {
    $.ajaxSetup({
        dataType : 'json',
        cache:false,
        data : {
            un : function() {
                // 加上时间戳，解决浏览器缓存问题（如果同一个请求参数没变，浏览器默认取的是缓存数据，不会去请求后台）
                return new Date().getTime() + "_" + Math.round(Math.random() * 10000)
            }
        }
    });



    let ajax = $.ajax;
    $.ajax = function(s) {
        let superSuccess = s.success;
        if (superSuccess !== undefined)
        {
            s.success = function(data, textStatus, jqXHR) {

                if (data &&  data.code !== undefined && data.code !== null && data.code === -500)
                {
                    window.location = '/login';
                    return;
                }
                superSuccess(data, textStatus, jqXHR);
            };
        }
        return ajax(s);
    };
});

