package cn.itcast.rpc.demo02

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.collection.mutable

/**
  * @author y15079
  * @create 2018-03-02 0:45
  * @desc
  *      Actor是有生命周期方法的
  **/
class Master(val host:String, val port:Int ) extends Actor{

  //workerId -> workerInfo
  val idToWorker = new mutable.HashMap[String, WorkerInfo]()
  //workerInfo
  val workers = new mutable.HashSet[WorkerInfo]()
  //超时检查间隔
  val CHECK_INTERVAL = 15000

  override def preStart(): Unit = { //构造器之后，receive方法之前
    println("preStart invoked")
    //导入隐式转换
    import context.dispatcher
    context.system.scheduler.schedule(0 millis, CHECK_INTERVAL millis, self, CheckTimeOutWorker)
  }

  //用于接收消息
  override def receive: Receive = { //偏函数
    case RegisterWorker(id,memory,cores) => {
      println("Worker注册。。。")
      //判断一下，是不是已经注册过
      if (!idToWorker.contains(id)){
        //把Worker的信息封装起来保存在内存当中
        val workerInfo = new WorkerInfo(id, memory, cores)
        idToWorker(id) = workerInfo
        workers += workerInfo
        sender() ! RegisteredWorker(s"akka.tcp://MasterSystem@$host:$port/user/Master")
      }
    }
    case Heartbeat(id) => {
      if(idToWorker.contains(id)){
        val workerInfo =idToWorker(id)
        //报活
        val currentTime = System.currentTimeMillis()
        workerInfo.lastHeartbeatTime = currentTime
      }
    }
    case CheckTimeOutWorker => {
        val currentTime = System.currentTimeMillis()
        val toRemove = workers.filter(x => currentTime - x.lastHeartbeatTime >CHECK_INTERVAL)
        for (w <- toRemove){
          workers -= w
          idToWorker -= w.id
        }
      println(workers.size)
    }
  }
}

object Master{
  def main(args: Array[String]): Unit = {

    val host = args(0)
    val port = args(1).toInt
    //准备配置
    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin
    val config = ConfigFactory.parseString(configStr)
    //ActorSystem老大，辅助创建和监控下面的Actor，它是单例的
    val actorSystem = ActorSystem("MasterSystem", config)
    //创建Actor
    val master = actorSystem.actorOf(Props(new Master(host,port)), "Master")
    actorSystem.awaitTermination() //让进程等待结束
  }
}