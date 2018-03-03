package cn.itcast.rpc.demo02

trait RemoteMessage extends Serializable //进程通信之间，对象需要实现对象化

//Worker -> Master
case class RegisterWorker(id: String, memory: Int, core: Int) extends RemoteMessage

case class Heartbeat(id:String ) extends RemoteMessage

//Master -> Worker
case class RegisteredWorker(masterUrl:String) extends RemoteMessage

//Worker -> 自己发送self
case object SendHeartbeat

//Master -> self
case object CheckTimeOutWorker