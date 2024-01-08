package com.armemius.lab2.servlets

import com.armemius.lab2.data.ResponseData
import com.google.gson.Gson
import jakarta.servlet.ServletException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt
import kotlin.time.ExperimentalTime

@WebServlet("/validator")
class AreaCheckServlet : HttpServlet() {
    private fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
    }

    private fun checkCircle(x: Double, y: Double, r: Double): Boolean {
        return distance(0.0, 0.0, x, y) <= r / 2.0 && x <= 0.0 && y <= 0.0
    }

    private fun checkTriangle(x: Double, y: Double, r: Double): Boolean {
        return x >= 0.0 && y >= 0.0 && x / 2.0 + y - r / 2.0 <= 0.0
    }

    private fun checkRectangle(x: Double, y: Double, r: Double): Boolean {
        return x <= 0.0 && x >= -r / 2.0 && y >= 0.0 && y <= r
    }

    private fun checkHit(x: Double, y: Double, r: Double): Boolean {
        return checkCircle(x, y, r) || checkRectangle(x, y, r) || checkTriangle(x, y, r)
    }

    private fun isNumeric(value: String): Boolean {
        return value.toDoubleOrNull() != null
    }

    private fun checkYLimits(value: String): Boolean {
        return """(-3.0*[^0]+\d*)|(3.0*[^0]+\d*)""".toRegex().matches(value)
    }

    private fun checkRLimits(value: String): Boolean {
        return """(0.\d*)|(4.0*[^0]+\d*)""".toRegex().matches(value)
    }

    private fun concat(value: String): String {
        if (value.length < 5) {
            return value
        }
        return value.substring(0 .. 4)
    }

    @Throws(ServletException::class, IOException::class)
    public override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val start = System.nanoTime()
        val out = response.writer
        val x = request.getParameter("x")
        val y = request.getParameter("y")
        val r = request.getParameter("r")

        if (x == null || y == null || r == null) {
            response.contentType = "text/plain; charset=utf-8"
            response.status = 400
            out.print("Please provide values for all variables")
            return
        }

        if (!isNumeric(x)) {
            response.contentType = "text/plain; charset=utf-8"
            response.status = 400
            out.print("X value cannot be converted to double")
            return
        }
        if (!isNumeric(y)) {
            response.contentType = "text/plain; charset=utf-8"
            response.status = 400
            out.print("Y value cannot be converted to double")
            return
        }
        if (!isNumeric(r)) {
            response.contentType = "text/plain; charset=utf-8"
            response.status = 400
            out.print("R value cannot be converted to double")
            return
        }
        val xValue = x.replace(",", ".").toDouble()
        val yValue = y.replace(",", ".").toDouble()
        val rValue = r.replace(",", ".").toDouble()

        if (-5.0 > xValue || xValue > 3) {
            response.contentType = "text/plain; charset=utf-8"
            response.status = 400
            out.print("X value is out of bounds")
            return
        }
        if (-3.0 > yValue || yValue > 3.0 || checkYLimits(y)) {
            response.contentType = "text/plain; charset=utf-8"
            response.status = 400
            out.print("Y value is out of bounds")
            return
        }
        if (-3.0 > rValue || rValue > 4.0 || checkRLimits(r)) {
            response.contentType = "text/plain; charset=utf-8"
            response.status = 400
            out.print("R value is out of bounds")
            return
        }

        val hitStatus = checkHit(xValue, yValue, rValue)
        val end = System.nanoTime()

        response.contentType = "application/json; charset=utf-8"
        response.status = 200

        val responseData = ResponseData(
            concat(x).toDouble(),
            concat(y).toDouble(),
            concat(r).toDouble(),
            hitStatus,
            SimpleDateFormat("hh:mm:ss dd.MM.yyyy").format(Date()),
            end - start
        )

        out.print(Gson().toJson(responseData))
        return
    }
}