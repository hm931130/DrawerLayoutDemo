# DrawerLayoutDemo
沉浸式状态栏
/**
 * 沉浸式状态栏实现思路
 * 大致流程
 * 1>将内容布局延伸至系统状态栏(以下统称statusBar)下面，4.4.x以上不同系统方式不同
 * 2>设置内容跟视图的panddingTop为statusBar的高度
 * 3> 1. 5.x系统以上支持setStatusBarColor属性，可以直接设置状态栏的高度
 * 2. 4.4.x系统以上5.x以下支持设置状态栏为半透明属性，并在decorView添加占位view,设置占位view的颜色
 * <p>
 * <p>
 * 存在DrawerLayout时，可自由化定制
 * 1> 同样根据不同系统版本将布局全屏化
 * 2> 通过属性clipToPandding使侧滑栏内容延伸至statusBar下面
 * 3> 侧滑栏布局保持，修改DrawerLayout中内容视图
 * 1. 新建线性布局，设置垂直模式
 * 2. 添加占位view，并设置颜色
 * 3. 找到contentView，将其从DrawerLayout中删除
 * 4. 添加contentView到线性布局中
 * 5. 添加线性布局到DrawerLayout中
 */
