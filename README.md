# 茂名职业技术学院官网新闻APP
- 这是一个使用jsoup爬取茂职院官网和正方教务系统信息的app。
- 采用viewpager+fragment+tablayout结构，支持下拉刷新和上拉加载。
- 主要功能有登录正方教务管理系统查询个人成绩、课表等信息，还有茂职院官网首页新闻、图书馆、失物招领等信息。
- 2.0版本以上使用了[MobTech](http://www.mob.com/)和[Bmob](https://www.bmob.cn/)第三方服务，刚开始构建的时候可能会比较慢，如果嫌慢请下载v1.0.14版，下载方式：打开 Branch: master并切到tags，推荐参考这篇文章:point_right:[加速Android Studio编译速度](https://www.jianshu.com/p/83b18775e21b):point_left:加快Android studio的编译速度。

> 技术栈：Jsoup + okhttp3 + sweet-alert-dialog + XUpdate + glide + banner + gson + rxandroid + rxjava + eventbus

> [APK下载地址](https://buqiyuan.xyz/mmvtc-news/mmvtc_news.apk)

## 运行效果：

<table>
    <tr>
        <td align="center">
			<p align="center">教务处登录</p>
			<img style="max-width: 150px;" src="https://raw.githubusercontent.com/buqiyuan/mmvtc-news/master/screenshots/教务处登录.jpg" />
		</td>
		 <td align="center">
			<p align="center">学院首页</p>
			<img style="max-width: 150px;" src="https://raw.githubusercontent.com/buqiyuan/mmvtc-news/master/screenshots/学院首页.jpg" />
		</td>
		 <td align="center">
			<p align="center">系部新闻</p>
			<img style="max-width: 150px;" src="https://raw.githubusercontent.com/buqiyuan/mmvtc-news/master/screenshots/系部新闻.jpg" />
		</td>
		 <td align="center">
			<p align="center">课程表</p>
        	<img style="max-width: 150px;" src="https://raw.githubusercontent.com/buqiyuan/mmvtc-news/master/screenshots/课程表.jpg" />
		</td>
    </tr>
    <tr>
        <td align="center">
			<p align="center">图书馆</p>
       	 	<img style="max-width: 150px;" src="https://raw.githubusercontent.com/buqiyuan/mmvtc-news/master/screenshots/图书馆.jpg" />
		</td>
        <td align="center">
			<p align="center">学校概况</p>
        	<img style="max-width: 150px;" src="https://raw.githubusercontent.com/buqiyuan/mmvtc-news/master/screenshots/学校概况.jpg" />
		</td>
		 <td align="center">
			<p align="center">其他</p>
        	<img style="max-width: 150px;" src="https://raw.githubusercontent.com/buqiyuan/mmvtc-news/master/screenshots/更多.jpg" />
		</td>
    </tr>
</table>

## 功能
### 主界面
- [x] 学院首页
- [x] 系部新闻
- [x] 学院概况
- [x] 图书馆 
- [x] 其他 
### 左侧抽屉
- [x] 教务处登录
- [x] 历年成绩
- [x] 个人信息
- [x] 修改密码
- [x] 课程表
- [x] 我的图书馆
- [ ] 绑定第三方账号(由于没有相关执照，所以没办法实现)
### 其他
- [x] 失物招领
- [x] 检查更新

## 写在最后
- 本项目为作者2019年在茂名职业技术学院的毕业设计作品，仅供学习参考，切勿直接用于作业或毕设。