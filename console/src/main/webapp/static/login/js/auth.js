let Auth = {
    vars: {
        lowin: document.querySelector('.lowin'),
        lowin_brand: document.querySelector('.lowin-brand'),
        lowin_wrapper: document.querySelector('.lowin-wrapper'),
        lowin_login: document.querySelector('.lowin-login'),
        lowin_wrapper_height: 0,
        login_back_link: document.querySelector('.login-back-link'),
        forgot_link: document.querySelector('.forgot-link'),
        login_btn: document.querySelector('.login-btn'),
        password_group: document.querySelector('.password-group'),
        password_group_height: 0,
        lowin_footer: document.querySelector('.lowin-footer'),
        box: document.getElementsByClassName('lowin-box'),
        option: {}
    },
    login(e) {
        Auth.vars.lowin_login.style.display = 'block';
        Auth.vars.lowin_login.classList.remove('lowin-animated');
        Auth.vars.lowin_login.className += ' lowin-animatedback';

        setTimeout(() => {
            Auth.vars.lowin_login.classList.remove('lowin-animatedback');
        }, 1000);

        Auth.setHeight(Auth.vars.lowin_login.offsetHeight + Auth.vars.lowin_footer.offsetHeight);

        e.preventDefault();
    },
    forgot(e) {
        Auth.vars.password_group.classList += ' lowin-animated';
        Auth.vars.login_back_link.style.display = 'block';

        setTimeout(() => {
            Auth.vars.login_back_link.style.opacity = 1;
            Auth.vars.password_group.style.height = 0;
            Auth.vars.password_group.style.margin = 0;
        }, 100);

        Auth.vars.login_btn.innerText = '忘记密码';

        Auth.setHeight(Auth.vars.lowin_wrapper_height - Auth.vars.password_group_height);

        Auth.vars.login_btn.removeEventListener("click", Auth.vars.option.eventLogin);

        Auth.vars.login_btn.addEventListener("click", Auth.vars.option.eventForget);

        e.preventDefault();
    },
    loginback(e) {
        Auth.vars.password_group.classList.remove('lowin-animated');
        Auth.vars.password_group.classList += ' lowin-animated-back';
        Auth.vars.password_group.style.display = 'block';

        setTimeout(() => {
            Auth.vars.login_back_link.style.opacity = 0;
            Auth.vars.password_group.style.height = Auth.vars.password_group_height + 'px';
            Auth.vars.password_group.style.marginBottom = 30 + 'px';
        }, 100);

        setTimeout(() => {
            Auth.vars.login_back_link.style.display = 'none';
            Auth.vars.password_group.classList.remove('lowin-animated-back');
        }, 1000);

        Auth.vars.login_btn.innerText = '登录';

        Auth.vars.login_btn.removeEventListener("click", Auth.vars.option.eventForget);

        Auth.vars.login_btn.addEventListener("click", Auth.vars.option.eventLogin);

        Auth.setHeight(Auth.vars.lowin_wrapper_height);

        e.preventDefault();
    },
    setHeight(height) {
        Auth.vars.lowin_wrapper.style.minHeight = height + 'px';
    },
    brand() {
        Auth.vars.lowin_brand.classList += ' lowin-animated';
        setTimeout(() => {
            Auth.vars.lowin_brand.classList.remove('lowin-animated');
        }, 1000);
    },
    init(option) {
        Auth.setHeight(Auth.vars.box[0].offsetHeight + Auth.vars.lowin_footer.offsetHeight);

        Auth.vars.password_group.style.height = Auth.vars.password_group.offsetHeight + 'px';
        Auth.vars.password_group_height = Auth.vars.password_group.offsetHeight;
        Auth.vars.lowin_wrapper_height = Auth.vars.lowin_wrapper.offsetHeight;

        Auth.vars.option = option;

        let len = Auth.vars.box.length - 1;

        for (let i = 0; i <= len; i++) {
            if (i !== 0) {
                Auth.vars.box[i].className += ' lowin-flip';
            }
        }
        window.addEventListener('keydown' ,(e) => {
            let code = 0;
            if (e.key !== undefined) {
                code = e.key;
            }
            else if (e.keyIdentifier !== undefined)
            {
                code = e.keyIdentifier;
            }
            else if (e.keyCode !== undefined)
            {
                code = e.keyCode;
            }
            if(code === 13 || code === 'Enter'){
                Auth.vars.option.doLogin();
            }
        });

        // Auth.vars.forgot_link.addEventListener("click", (e) => {
        //     Auth.forgot(e);
        // });

        Auth.vars.login_back_link.addEventListener("click", (e) => {
            Auth.loginback(e);
        });

        Auth.vars.option.eventLogin = (e) => {Auth.vars.option.doLogin();};
        // Auth.vars.option.eventForget = (e) => {Auth.vars.option.doForget();};

        Auth.vars.login_btn.addEventListener("click", Auth.vars.option.eventLogin );
    }
};