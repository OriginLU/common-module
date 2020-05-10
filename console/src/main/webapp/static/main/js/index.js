

$(function(){
    //菜单点击
    $(".J_menuItem").on('click',function(){
        let url = $(this).attr('href');
        $("#J_iframe").attr('src',url);
        return false;
    });
});

