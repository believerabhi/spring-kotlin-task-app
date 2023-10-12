package com.abhi.spring_task_app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringTaskAppApplication

fun main(args: Array<String>) {
    runApplication<SpringTaskAppApplication>(*args)
    println("Testing the setup of springboot")
}
