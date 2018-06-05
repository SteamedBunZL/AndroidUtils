package top.zl.com.kotlin

/**
 * Created by Steve on 2018/4/24.
 */
class KotlinManager {

    var otherName:String? = null

    //条件判断
    fun judge(){
        var text:String = null
        text?.let {
            val length = text.length
        }
    }

    //字符串拼接
    fun strConnect(){
        val firstName = "amit"
        val lastName = "shekhar"
        val message = "My name is : $firstName $lastName"
    }

    //三元表达式
    fun san_yuan_biao_da_shi(){
        var x:Int = 1
        var y:Int = 2

        val text = if (x > 5) "x>5" else "x<=5"
    }






}