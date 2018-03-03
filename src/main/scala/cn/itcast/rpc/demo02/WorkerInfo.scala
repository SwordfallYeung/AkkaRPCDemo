package cn.itcast.rpc.demo02

/**
  * @author y15079
  * @create 2018-03-02 14:46
  * @desc
  **/
class WorkerInfo(val id:String, val memory:Int, val cores:Int) {

  //TODO 上一次心跳
  var lastHeartbeatTime: Long = _
}
