/*
 *  odh-testingplatform: scan git repositories for soapui tests, executes them and shows result on a webpage
 *  
 *  (C) 2018 IDM Suedtirol - Alto Adige - Italy
 *  
 */

package bz.idm.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Servlet that reads test data and produces a xml output for the client
 * datatable library
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class PageServlet extends HttpServlet {

	String jdbc_url;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try
		{
			Properties props = new Properties();
			String file = getServletContext().getRealPath("/WEB-INF/config.properties");
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
			props.load(isr);
			isr.close();
			this.jdbc_url = props.getProperty("jdbc_url");
		}
		catch (IOException ioxxx)
		{
			throw new ServletException(ioxxx);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/xml");

		try {
			Connection conn = DriverManager.getConnection(jdbc_url);

			String sql = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("list.sql").toURI())),
					StandardCharsets.UTF_8);
			ResultSet rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				String xml = rs.getString(1);
				resp.getWriter().write(xml);
			}

		} catch (Exception e) {
			throw new ServletException(e);
		}

	}
}
