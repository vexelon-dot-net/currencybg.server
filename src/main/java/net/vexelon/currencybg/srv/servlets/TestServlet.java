package net.vexelon.currencybg.srv.servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class TestServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Type t = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> json = new Gson().fromJson("{a: 'test', b: 124}", t);
		resp.getOutputStream().println("Hi! This is a test." + json.get("a") + " value=" + json.get("b"));
		resp.getOutputStream().flush();
	}

}
