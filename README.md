######  一个对butterKnife 源码的 学习实践项目。 难点是学习 R2的生成及使用。


> Butter Knife
Attention: This tool is now deprecated. Please switch to view binding. Existing versions will continue to work, obviously, but only critical bug fixes for integration with AGP will be considered. Feature development and general bug fixes have stopped.ding.

上边来自 ButterKnife github 主页。虽然一代神库要落下帷幕了，但是它带给了我们很多。通过学习它可以get
到很多技能，这些很重要。

以上提到了 ViewBinding 之前懵懂的时候听过，也好想使用过，就是当时稀里糊涂，接下来有时间搞一下。


###### 这里有一个问题：把项目clean以后，所有使用到R2的地方都会爆红，IDE会提示异常。为何编译的时候不收影响呢？
具体原因见（asset/pic3.png）


###### 一个问题：依赖R2的这种写法，有一个不太友好的地方，就是每次变动了xml布局文件。例如：增加view，修改View id 的时候，R2肯定又是一片爆红。而且还没有办法像R那样智能提示开发者的能力。
解决思路：提供一个AS插件，实时监测xml文件变动，就像R那样，coding阶段也生成一份R2文件（有别于编译阶段的R2），
这样既可以提示开发者，又能把爆红信息取掉。