# ulife

个人后台项目，管理后台，通用库积累，java学习等。

## 背景

- 积累了很多个人笔记，有收集端记录的，VS Code写的markdown，还有开源项目写的等，想做一个跨平台个人笔记系统
- 很多个人目标制定和查看不方便，想做一个个人呢OKR系统
- 以前的博客系统使用wordpress写的，当时太卡了占用很多资源，所以想能不能自己用Java写一个
- 一直想整理一个自己的管理后台，方便工作中使用

想整合做一个自己的应用，包括RBAC后台管理、博客、app端笔记记录、markdown预览、甚至个人OKR管理等，总之记录一下个人的生活。

本项目现阶段分成两部分：

- Spring Boot后台部分：<https://github.com/youyouzh/ulife>
- vue3前端部分：<https://github.com/youyouzh/ulife-ui-admin>

## 技术栈

- 后端采用 Spring Boot 多模块架构、MySQL + MyBatis Plus、Redis + Redisson
- 管理后台后端采用 Vue3 的 [vue-element-plus-admin](https://gitee.com/kailong110120130/vue-element-plus-admin)
- 权限认证使用 Spring Security & Token & Redis，支持多终端、多种用户的认证系统，支持 SSO 单点登录
- app端使用 [uni-app](https://github.com/dcloudio/uni-app) 方案，一份代码多终端适配，同时支持 APP、小程序、H5！

参考现在大牛们做的一些开源管理后台，进行一定整合和学习：

- [ruoyi-vue-pro](https://github.com/YunaiV/ruoyi-vue-pro)
  ，芋道源码基于[ruoyi-vue](https://github.com/yangzongzhuan/RuoYi)优化重构的管理后台框架
- [jeecg-boot](https://github.com/jeecgboot/jeecg-boot)非常老牌的Spring Boot通用管理后台框架

## 开发路程

### 架构搭建

- dependencies：maven统一依赖管理
- common: 从apache-common中抽取一部分自己进程用到的工具函数
    - 不直接引入hutool，非常优秀的国产工具库，只引入自己常用的工具函数，按需增加