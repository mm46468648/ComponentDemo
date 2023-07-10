package com.mooc.common.dsl


/**
 * Android DSL示例代码
 */
fun main(args: Array<String>) {
    val trip = trip {
        name = "Trip"
        address = " 上海市长宁区金钟路 968 号凌空 SOHO"
        department {
            name = " 机票 "
            nameEn = "flight"
        }
        department {
            name = " 酒店 "
            nameEn = "hotel"
        }
        department {
            name = " 火车票 "
            nameEn = "train"
        }
    }
    trip culture "Customer、Teamwork、Respect、Integrity、Partner"
    System.out.println(trip)
}
data class Trip(var name: String? = "", var address: String? = "", var departments: List<Any>? = mutableListOf(), var city: List<Any>? = mutableListOf(), var culture: String? = "")

data class Department(var name: String = "", var nameEn: String = "")

class TripBuilder {
    var name: String? = ""
    var address: String? = ""
    var departments = mutableListOf<Any>()
    fun department(block: DepartmentBuilder.() -> Unit) {
//            简单的写法 departments.add(DepartmentBuilder().apply(block).build()) 即可
//            演示 invoke 实现
        val departmentBuilder = DepartmentBuilder()
        block.invoke(departmentBuilder)
        departments.add(departmentBuilder.build())
    }
    fun build(): Trip = Trip(name, address, departments)
}
class DepartmentBuilder {
    var name: String = ""
    var nameEn: String = ""
    fun build(): Department = Department(name, nameEn)
}

fun trip(block: TripBuilder.() -> Unit): Trip = TripBuilder().apply(block).build()

infix fun Trip.culture(culture: String) {
    this.culture = culture
}