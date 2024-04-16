# 数据库初始化
# @author <a href="https://github.com/ligl">程序员鱼皮</a>
# @from <a href="https://gl.icu">编程导航知识星球</a>

create database if not exists code_generator_db;

-- 切换库
use code_generator_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;


-- 代码生成器表
create table if not exists generator
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(128)                       null comment '名称',
    description text                               null comment '描述',
    basePackage varchar(128)                       null comment '基础包',
    version     varchar(128)                       null comment '版本',
    author      varchar(128)                       null comment '作者',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    picture     varchar(256)                       null comment '图片',
    fileConfig  text                               null comment '文件配置（json字符串）',
    modelConfig text                               null comment '模型配置（json字符串）',
    distPath    text                               null comment '代码生成器产物路径',
    status      int      default 0                 not null comment '状态',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '代码生成器' collate = utf8mb4_unicode_ci;

INSERT INTO code_generator_db.generator (id, name, description, basePackage, version, author, tags, picture, fileConfig, modelConfig, distPath, status, userId) VALUES (1, 'ACM 模板项目', 'ACM 模板项目生成器', 'com.yupi', '1.0', '程序员鱼皮', '["Java"]', 'https://pic.yupi.icu/1/_r0_c1851-bf115939332e.jpg', '{}', '{}', null, 0, 1);
INSERT INTO code_generator_db.generator (id, name, description, basePackage, version, author, tags, picture, fileConfig, modelConfig, distPath, status, userId) VALUES (2, 'Spring Boot 初始化模板', 'Spring Boot 初始化模板项目生成器', 'com.yupi', '1.0', '程序员鱼皮', '["Java"]', 'https://pic.yupi.icu/1/_r0_c0726-7e30f8db802a.jpg', '{}', '{}', null, 0, 1);
INSERT INTO code_generator_db.generator (id, name, description, basePackage, version, author, tags, picture, fileConfig, modelConfig, distPath, status, userId) VALUES (3, '鱼皮外卖', '鱼皮外卖项目生成器', 'com.yupi', '1.0', '程序员鱼皮', '["Java", "前端"]', 'https://pic.yupi.icu/1/_r1_c0cf7-f8e4bd865b4b.jpg', '{}', '{}', null, 0, 1);
INSERT INTO code_generator_db.generator (id, name, description, basePackage, version, author, tags, picture, fileConfig, modelConfig, distPath, status, userId) VALUES (4, '鱼皮用户中心', '鱼皮用户中心项目生成器', 'com.yupi', '1.0', '程序员鱼皮', '["Java", "前端"]', 'https://pic.yupi.icu/1/_r1_c1c15-79cdecf24aed.jpg', '{}', '{}', null, 0, 1);
INSERT INTO code_generator_db.generator (id, name, description, basePackage, version, author, tags, picture, fileConfig, modelConfig, distPath, status, userId) VALUES (5, '鱼皮商城', '鱼皮商城项目生成器', 'com.yupi', '1.0', '程序员鱼皮', '["Java", "前端"]', 'https://pic.yupi.icu/1/_r1_c0709-8e80689ac1da.jpg', '{}', '{}', null, 0, 1);

INSERT INTO code_generator_db.generator (id, name, description, basePackage, version, author, tags, picture, fileConfig, modelConfig, distPath, status, userId) VALUES (18, 'acm-template', 'ACM示例模板生成器', 'com.gl', '1.0', 'GL', '["Java"]', 'https://pic.yupi.icu/1/_r1_c0709-8e80689ac1da.jpg', '{
    "file": [
        {
        "groupKey": "git"
        "groupName": "开源"
        "type": "group"
        "condition": "needGit"
        "files": [
          {
            "inputPath": ".gitignore",
            "outputPath": ".gitignore",
            "type": "file",
            "generateType": "static",
            "condition": "needGit"
          },
          {
            "inputPath": "README.md",
            "outputPath": "README.md",
            "type": "file",
            "generateType": "static"
          }
        ]
        },
      {
        "inputPath": "src/com/gl/acm/MainTemplate.java.ftl",
        "outputPath": "src/com/gl/acm/MainTemplate.java",
        "type": "file",
        "generateType": "dynamic"
      }
    ]
  }', '{"models": [
      {
        "fieldName": "needGit",
        "type": "boolean",
        "description": "是否生成.gitignore文件",
        "defaultValue": true
      },
      {
        "fieldName": "loop",
        "type": "boolean",
        "description": "是否生成循环",
        "defaultValue": false,
        "abbr": "l"
      },
      {
        "groupKey": "mainTemplate",
        "groupName": "核心模板",
        "type": "MainTemplate",
        "description": "用于生成核心模板文件",
        "models": [
          {
            "fieldName": "author",
            "type": "String",
            "description": "作者注释",
            "defaultValue": "gl",
            "abbr": "a"
          },
          {
            "fieldName": "outputText",
            "type": "String",
            "description": "输出信息",
            "defaultValue": "sum = ",
            "abbr": "o"
          }
        ],
        "condition": "loop"
      }
    ]}','/generator_dist/123/acm-template-pro-generator.zip', 0, 1779834934542020609);