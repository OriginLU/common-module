let Render = {

    userName(selector,userName) {

        if (userName === undefined || userName === '')
        {
            return;
        }
        selector.text(userName);
    },
    navigation(selector,menus) {

        if (menus === undefined || menus.length === 0)
        {
            return;
        }
        selector.children('li:gt(1)').remove();
        let nav = selector;

        let rid;
        let sysId,menuId;
        let oneMenu,twoMenu,threeMenu;
        for (let i = 0; i < menus.length; i++)
        {

            let menu = menus[i];

            if (menu.parentId === undefined)
            {
                rid = menu.id;
                continue;
            }

            if (menu.parentId === rid)
            {
                sysId = menu.id;
                if (oneMenu !== undefined)
                {
                    oneMenu = undefined;
                    twoMenu = undefined;
                    threeMenu = undefined;
                }
                oneMenu = Render.firstMenu(menu);
                nav.append(oneMenu);
            }
            else if (sysId !== undefined && sysId === menu.parentId)
            {
                menuId = menu.id;
                if (twoMenu !== undefined)
                {
                    twoMenu.append(Render.menuItem(menu));
                }
                else
                {
                    twoMenu = Render.secondMenu().append(Render.menuItem(menu));
                    oneMenu.children("a").removeAttr('class').append(Render.arrow());
                    oneMenu.append(twoMenu);
                }
            }
            else if (menuId !== undefined && menuId === menu.parentId)
            {
                if (threeMenu === undefined)
                {
                    threeMenu = Render.thirdMenu().append(Render.menuItem(menu));
                    twoMenu.children('li:last').children("a").removeAttr('class').append(Render.arrow());
                    twoMenu.children('li:last').append(threeMenu);
                }
                else
                {
                    threeMenu.append(Render.menuItem(menu));
                }
            }

        }
    },
    icon(icon) {

        if (icon === undefined || icon === '')
        {
            return 'fa-chart';
        }
        return icon;
    },
    anchor(anchor){
        if (anchor === undefined || anchor === '')
        {
            return '#';
        }
        return anchor;
    },
    firstMenu(menu) {

          let anchor =  $('<a class="J_menuItem" href=\"' + Render.anchor(menu.resourceLink) + '\" ></a>')
            .append('<i class=\"fa ' + Render.icon(menu.img) + '\"></i>')
            .append('<span class="nav-label">' + menu.name + '</span>');

          return $('<li></li>').append(anchor);

    },
    secondMenu() {
        return $('<ul class="nav nav-second-level"></ul>');
    },
    thirdMenu() {
        return $('<ul class="nav nav-third-level"></ul>');
    },
    arrow() {
        return $('<span class="fa arrow"></span>');
    },
    menuItem(menu) {
        return  $('<li><a class="J_menuItem" href=\"' + Render.anchor(menu.resourceLink) + '\">' + menu.name + '</a>');
    }
};