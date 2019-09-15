# 多线程练习：实现一个多线程的Word Count

Word Count是一个著名的练手程序。一个文本文件包含若干行，每行包含若干个*只包含小写字母的单词*，单词之间以空格分割。

请编写一个程序，统计一个文件列表中，中每个单词出现的次数。我知道跟课上讲的不太一样——我希望你能举一反三。

例如，文件内容是：

```
i am a boy
i have a dog
```

你应该返回一个Map：{i->2, am->1, a->2, boy->1, have->1, dog->1}

请编写一个多线程的[`WordCount`](https://github.com/hcsp/multithread-word-count/blob/master/src/main/java/com/github/hcsp/multithread/WordCount.java)类，完成以上功能。线程数量通过构造器传入。

祝你好运！

-----
注意！我们只允许你修改以下文件，对其他文件的修改会被拒绝：
- [src/main/java/com/github/hcsp/multithread/WordCount.java](https://github.com/hcsp/multithread-word-count/blob/master/src/main/java/com/github/hcsp/multithread/WordCount.java)
- [pom.xml](https://github.com/hcsp/multithread-word-count/blob/master/pom.xml)
-----


完成题目有困难？不妨来看看[写代码啦的相应课程](https://xiedaimala.com/tasks/9bf0fb20-929d-4e17-891a-4673291d74a0)吧！

回到[写代码啦的题目](https://xiedaimala.com/tasks/9bf0fb20-929d-4e17-891a-4673291d74a0/quizzes/1b0fc390-74ad-4f55-b355-90b8a9154cc5)，继续挑战！ 
