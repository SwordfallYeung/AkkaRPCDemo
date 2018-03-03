package cn.itcast.rpc.demo01

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * @author y15079
  * @create 2018-03-02 0:45
  * @desc
  *      Actor是有生命周期方法的
  **/
class Master extends Actor{

  println("constructor invoked")

  override def preStart(): Unit = { //构造器之后，receive方法之前
    println("preStart invoked")
  }

  //用于接收消息
  override def receive: Receive = { //偏函数
    case "connect" => {
      println("a client connected")
      sender ! "reply"
    }
    case "hello" => {
      println("hello world")
    }
  }
}

object Master{
  def main(args: Array[String]): Unit = {

    //Program arguments 127.0.0.1 8888
    //即127.0.0.1 为host 8888为port
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
    val master = actorSystem.actorOf(Props(new Master), "Master")
    master ! "hello" //向自己发送消息
    actorSystem.awaitTermination() //让进程等待结束
  }
}