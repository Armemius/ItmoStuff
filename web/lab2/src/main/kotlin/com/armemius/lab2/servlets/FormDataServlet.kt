package com.armemius.lab2.servlets

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse


@WebServlet("/form_data")
class FormDataServlet : HttpServlet() {
    private val ATTRIBUTE = "form_data"
    public override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val session = request.session
        val out = response.writer
        val data = session.getAttribute(ATTRIBUTE)
        response.contentType = "text/plain; charset=utf-8"

        if (data == null) {
            response.status = 400
            out.print("No data available")
            return
        }

        response.status = 200
        out.print(data)

        return
    }

    public override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
        val session = request.session
        val out = response.writer
        response.contentType = "text/plain; charset=utf-8"

        val reader = request.reader
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }

        val data = sb.toString()

        session.setAttribute(ATTRIBUTE, data)
        response.status = 200
        out.print("Value of form data has updated")
    }
}