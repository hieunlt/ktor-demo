package com.example.routes

import com.example.models.Order
import com.example.models.orderStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.orderRouting() {
    route("/order") {
        get {
            if (orderStorage.isNotEmpty()) {
                call.respond(orderStorage)
            } else {
                call.respondText("No orders found", status = HttpStatusCode.OK)
            }
        }

        get("{number?}") {
            val number = call.parameters["number"] ?: return@get call.respondText(
                "Missing number", status = HttpStatusCode.BadRequest
            )
            val order = orderStorage.find { it.number == number } ?: return@get call.respondText(
                "No order with number $number", status = HttpStatusCode.NotFound
            )
            call.respond(order)
        }

        get("{number?}/total") {
            val number = call.parameters["number"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val order = orderStorage.find { it.number == number } ?: return@get call.respondText(
                "Not found", status = HttpStatusCode.NotFound
            )
            val total = order.contents.sumOf { it.price * it.amount }
            call.respond(total)
        }

        post {
            val order = call.receive<Order>()
            orderStorage.add(order)
            call.respondText("Order stored correctly", status = HttpStatusCode.Created)
        }

        delete("{number?}") {
            val number = call.parameters["number"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (orderStorage.removeIf { it.number == number }) {
                call.respondText("Order removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
