// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.viadeo.kasper.exception.KasperRuntimeException;
import com.viadeo.kasper.platform.IPlatform;

public class KasperPlatformBootListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final ServletContext ctx = sce.getServletContext();
		final WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ctx);
		
		if (null == springContext) {
			throw new KasperRuntimeException("Unable to find Spring context !");
		}
		
		final IPlatform platform = (IPlatform) springContext.getBean("platform");
		
		if (null == platform) {
			throw new KasperRuntimeException("No Kasper platform bean found in current Spring context !");
		}
		
		platform.boot();
	}

}