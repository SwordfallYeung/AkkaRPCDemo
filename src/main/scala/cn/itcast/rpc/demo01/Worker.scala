package cn.itcast.rpc.demo01

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * @author y15079
  * @create 2018-03-02 10:27
  * @desc
  **/
class Worker(val masterHost:String, val masterPort:Int) extends Actor{

  var master : ActorSelection = _

  //在preStart里面建立连接
  override def preStart(): Unit = {
      master = context.actorSelection(s"akka.tcp://MasterSystem@$masterHost:$masterPort/user/Master") //user必须得写，Master为主Master的名字
      master ! "connect"
  }

  override def receive: Receive = {
    case "reply" => {
      println("a reply from master")
    }
  }
}

object Worker {

  def main(args: Array[String]): Unit = {
    val host = args(0)
    val port = args(1).toInt
    val masterHost = args(2)
    val masterPort = args(3).toInt
    //准备配置
    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin
    val config = ConfigFactory.parseString(configStr)
    //ActorSystem老大，辅助创建和监控下面的Actor，它是单例的
    val actorSystem = ActorSystem("WorkSystem", config)
    actorSystem.actorOf(Props(new Worker(masterHost,masterPort)), "Worker")
    actorSystem.awaitTermination()
  }
}