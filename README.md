# Android_Vocabulary

## 一、实验目的

1. 了解Android数据存储的基本概念；

2. 理解并掌握SQLite数据库的使用方法;

3. 进一步熟悉与掌握Fragment使用。

4. 进一步熟悉各组件、适配器、菜单、对话框等相关概念和技术等。

5. 掌握ContentProvider相关概念和技术。

6. 掌握Handler相关概念和技术。

## 二、实验要求

1. 课前预习实验内容，并查找相关资料。

2. 按照实验步骤完成各个相关内容。

3. 撰写实验报告。

   a) 实验报告格式必须符合学校要求（例如必须采用学校规定的实验封面）；

   b) 写出实验详细步骤，包括主要采用的技术方案、相关分析和核心代码。注意：不要简单地近包括截屏和代码，完整代码可以作为附录放在实验报告结尾；

   c) 总结实验中遇到的问题、分析和解决方法。

   d) 写出心得体会与收获等。

## 三、实验步骤

主要分为几个大步骤进行实现。

1. 需求分析，调研现有单词类应用具有哪些功能，确定界面。

2. 系统设计，对系统进行模块划分，确定技术方案。

3. 编码，编写代码实现各项功能。

4. 测试。

### 1. 需求分析

这里列出主要功能，也可参考其他单词类软件功能。

1. 使用Fragment实现英语词典，当竖屏时显示单词释义，当横屏时显示左右两列，右侧是释义，左侧是单词列表。

2. 单词存放在数据库中，系统预先预置一系列单词，用户可以对单词库进行增加、删除、修改等，并支持查询，包括模糊查询等。

3. 作为ContentProvider，供其它应用程序访问（可以编写一个测试程序验证之）。

4. 用户添加单词或者查询单词时，单词含义和示例等信息可以从网络上获得（推荐有道等）。

5. 可以网络上获取英语新闻，用户可以针对不认识的单词进行查询和添加到单词库（例如在单词上长按，弹出上下文菜单供用户操作）等。

6. 对于不认识的单词，增加到生词本，可以浏览生词本，并提供多种方式背诵这些生单词。

### 2. 系统设计

系统设计给出模块划分和技术方案等。

### 3. 项目实施

主要步骤：

1. 创建工程

2. 编写各界面布局、菜单等资源文件

3. 编写界面处理代码

### 4. 测试

编写完后对项目进行测试，看是否实现了各个功能。