
-- 权限系统表基础设计
-- 用户表
create table  if not exists t_user
(
    id bigint not null  primary key  auto_increment comment '主键',
    user_name varchar(60) not null comment '用户名',
    real_name varchar(60) not null  comment '真实姓名',
    password varchar(120) not null comment  '用户密码',
    email varchar(60) not null  comment  '邮箱',
    status tinyint not null  default 0 comment '0-正常 1-冻结 2-删除',
    create_time datetime not null  comment '创建时间',
    modify_time datetime not null  default current_timestamp comment '修改时间'

) engine = innoDB default charset = utf8mb4 comment '用户表';

-- 角色表
create table  if not exists t_role
(
    id bigint not null  primary key  auto_increment comment '主键',
    role_name varchar(20) not null comment '角色名称',
    create_time datetime not null  comment '创建时间',
    modify_time datetime not null  default current_timestamp comment '修改时间'

) engine = innoDB default charset = utf8mb4;

-- 用户角色关联表
create table  if not exists t_user_role_relation
(
    user_id bigint not null comment '用户Id',
    role_id bigint not null  comment '角色id',
    index idx_u_id (user_id),
    index idx_r_id (role_id)

) engine = innoDB default charset = utf8mb4 comment '用户角色关联表';


-- 资源表
create table  if not exists t_resource
(
    id bigint not null  primary key  auto_increment comment '主键',
    resource_name varchar(30) not null  comment '资源名称',
    resource_sign varchar(200)  comment '资源标识,对于菜单来说是url,页面元素来说是元素标识',
    resource_type tinyint not null comment '资源类型 0-菜单 1 -页面元素 2-文件表',
    parent_id bigint not null comment '父节点ID',
    sort int not null  default 0 comment '排序id',
    create_time datetime not null  comment '创建时间',
    modify_time datetime not null  default current_timestamp comment '修改时间'

) engine = innoDB default charset = utf8mb4 comment '资源表';

-- 资源角色关系表
create table  if not exists t_role_resource_relation
(
    resource_id bigint not null comment '资源Id',
    role_id bigint not null  comment '角色id',
    index idx_res_id (resource_id),
    index idx_r_id (role_id)
) engine = innoDB default charset = utf8mb4 comment '资源角色关系表';