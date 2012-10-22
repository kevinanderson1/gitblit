package com.gitblit.wicket;

import java.security.cert.X509Certificate;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import com.gitblit.GitBlit;
import com.gitblit.models.UserModel;

public class GitBlitRequestCycle extends WebRequestCycle {

	public GitBlitRequestCycle(WebApplication application, WebRequest request,
			Response response) {
		super(application, request, response);

		if (request.getHttpServletRequest().getAttribute(
				"javax.servlet.request.X509Certificate") != null) {
			X509Certificate[] certChain = (X509Certificate[]) request.getHttpServletRequest().getAttribute(
							"javax.servlet.request.X509Certificate");

			if (certChain != null) {
				String username = null;
				try {
					LdapName dn = new LdapName(certChain[0].getSubjectX500Principal().getName("CANONICAL"));
					username = dn.get(dn.size() - 1);
				} catch (InvalidNameException e) {
					System.out.println(e.getMessage());
				}

				if (username != null) {

					UserModel user = GitBlit.self().getUserModel(username);
					if (user != null) {
						((GitBlitWebSession) Session.get()).setUser(user);
					}
				}
			}
		}
	}
}
