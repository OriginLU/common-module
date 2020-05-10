let Plain = {

    getParam(selector,param){

        if (param === undefined)
        {
            param = {};
        }
        $(selector).each(function () {

            let $param = $(this);
            let name = $param.attr('name');
            if (name === undefined)
            {
                name = $param.attr('id');
            }
            if (name === undefined)
            {
                return;
            }

            param[name] = $param.val();
        });

        return param;
    }
};