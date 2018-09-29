# 分支命名规范
- 稳定分支：master
- 开发分支：dev
- 功能分支：f/$featureName or feature/$featureName
- 发布分支：release/$version
- 快修分支：hotFix/$version
- 重构分支：refactor/$featureName
- bug 分支： bugFix/$bugName*（需要花费大量时间，单独切分支，不影响其他人开发）*

# Git flow
## 分支定义
- 稳定分支 master: 该分支永远是最近的一个稳定版本
- 开发分支 dev：该分支负责项目持续开发
- 功能分支 feature/：始终从 dev 上创建。每次开发新功能，都会新建一个功能分支，开发完成合并到 dev 后删除
- 发布分支 release/：始终从 dev 上创建。当完成该版本所有功能后，新建发布分支，在此基础上进行 bug fix
- 快修分支 hotFix/：始终从 master 上创建。介于上一版本和下一个大版本之间，用于快速修复上一个版本的重要 bug，发布中间版本

## 完整的从开发到发布的流程
[流程图](http://stormzhang.com/image/gitflow.png)

### 要点
- 当你要开发新功能的时候，从 dev 创建一个新的 feature 分支，此后一直在该分支上进行开发，直到完成
- 当项目需要进行 codereview 的时候，每一个已经完成的 feature 分支提交完远端后，需要创建出一个 PR（Pull Request） 请求 merge 到目标分支（dev）
- 当一个版本的所有功能都已经开发完成合并到 dev 之后，基于当前的 dev 创建出 release 分支，并一直在这个分支上进行 bug 修复，直到发布
- 创建了 release 分支后，dev 分支可以继续进行开发工作，暂时不去理会 release 分支。
- release 分支修复的问题可以随时合并到 dev 分支上。待完成发布后，分别 merge 到 dev 分支和 master 分支，并对 master 分支打上 tag
- 对于已经发布的稳定版本，如果需要紧急修复 bug，基于 master 创建新的 hotFix 分支，修复完成后合并到 dev 以及 master 并对 master 打上新 tag

# 统一命名
- 训练：Training
- 发现：Discovery
- 小姐姐：Miss
- 姐说：MissTalk
- 对战：Battle
- 股票：Stock
- 期货：Futures
- 自选：Optional
- 消息：Message
- 赞赏、打赏：Reward
- 元宝：Ingot
- 积分：Score
- 评测：Evaluation
- 学分：Credit
- 成绩：Grade
- 竞技场: Arena
- 电台：radio



# FinanceAnd
乐米金融

