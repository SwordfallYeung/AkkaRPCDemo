1.ActorSystem是这个进程中的Actor的老大，负责创建和监控所有的actor<br/>
2.ActorSystem是单例的<br/>
3.actor负责通信

Worker -> Master的连接步骤：<br/>
1.先建立连接<br/>
2.拿到Master的代理对象<br/>
3.向Master发送消息<br/>
4.Master向Worker反馈消息

Master负责创建和监控Worker<br/>
Woker负责计算

AKKA的RPC系统：<br/>
要求：①RPC通信框架（AKKA）；②定义2个类Mater、Worker<br/>
通信业务逻辑：<br/>
Master启动后，然后启动所有的Worker<br/>
1.Worker启动后，在preStart方法中与Master建立连接，向Master发送注册，将Worker的信息通过case class封装起来发送给Master。<br/>
2.Master接收到Worker的注册消息后，将Worker的信息保存起来，然后向Worker反馈注册成功<br/>
3.Worker定期向Master发送心跳，为了报活<br/>
4.Master会定时清理超时的Worker<br/>