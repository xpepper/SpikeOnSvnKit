package com.sourcesense.spikes;

import java.io.File;
import java.util.Collection;

import javancss.Javancss;

import org.apache.commons.io.FileUtils;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.SVNClient;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class History {

	{
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	public static void main(String[] args) throws Exception {
		String url = "https://dev.sourcesense.com/repos/dev/confluence/civil-service/trunk";
		String name = "p.dibello";
		String password = "namu1253";
		long startRevision = 0;

		if (args != null) {
			url = (args.length >= 1) ? args[0] : url;
			name = (args.length >= 2) ? args[1] : name;
			password = (args.length >= 3) ? args[2] : password;
		}

		SVNClient svnClient = new SVNClient();
		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		ISVNAuthenticationManager authenticationManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		repository.setAuthenticationManager(authenticationManager);

		long endRevision = repository.getLatestRevision();
		Collection<SVNLogEntry> logEntries = repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);
		for (SVNLogEntry eachLogEntry : logEntries) {
			System.out.println("------------------------------------------");
			System.out.println("revision: " + eachLogEntry.getRevision());
			System.out.println("date: " + eachLogEntry.getDate());
			System.out.println("log message: " + eachLogEntry.getMessage());
			String checkoutPath = "/tmp/checkout/" + eachLogEntry.getRevision();
			svnClient.checkout(url, checkoutPath, Revision.getInstance(eachLogEntry.getRevision()), true);
			new Javancss(new String[] { "-all", "-recursive", checkoutPath + "/src" });
			FileUtils.deleteQuietly(new File(checkoutPath));
		}
	}
}
