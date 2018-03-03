package cn.itcast.rpc.demo02

import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

/**
  * @author y15079
  * @create 2018-03-02 10:27
  * @desc
  **/
class Worker(val masterHost:String, val masterPort:Int, val memory:Int, val cores:Int) extends Actor{

  var master : ActorSelection = _
  val workerId = UUID.randomUUID().toString
  val HEART_INTERVAL = 10000

  //在preStart里面建立连接
  override def preStart(): Unit = {
    master = context.actorSelection(s"akka.tcp://MasterSystem@$masterHost:$masterPort/user/Master") //user必须得写，Master为主Master的名字
    //向Master发送注册信息
    master ! RegisterWorker(workerId, memory, cores)
  }

  override def receive: Receive = {
    case RegisteredWorker(masterUrl) => {
      println(masterUrl)
      //导入隐式转换
      import context.dispatcher
      //启动定时器发送心跳
      context.system.scheduler.schedule(0 millis, HEART_INTERVAL millis, self, SendHeartbeat)
    }
    case SendHeartbeat => {
      println("send heartbeat to master")
      master ! Heartbeat(workerId)
    }
  }
}

object Worker {

  def main(args: Array[String]): Unit = {

    //Program arguments 127.0.0.1 9999 127.0.0.1 8888
    //即127.0.0.1为host 9999为port  127.0.0.1为masterHost 8888为masterPort
    val host = args(0)
    val port = args(1).toInt
    val masterHost = args(2)
    val masterPort = args(3).toInt
    val memory = args(4).toInt
    val cores = args(5).toInt
    //准备配置
    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin
    val config = ConfigFactory.parseString(configStr)
    //ActorSystem老大，辅助创建和监控下面的Actor，它是单例的
    val actorSystem = ActorSystem("WorkerSystem", config)
    actorSystem.actorOf(Props(new Worker(masterHost,masterPort,memory,cores)), "Worker")
    actorSystem.awaitTermination()
  }
}