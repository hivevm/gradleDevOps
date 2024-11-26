package org.hivevm.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Stack;

import org.hivevm.util.git.PDVersion;
import org.hivevm.util.xml.StAX;

public class XmlTest {

	private static class Version {

		private final String version;
		private final String revision;
		private final String svnnumber;
		private final String date;

		private String title;
		private String code;

		private Version(String version, String revision, String svnnumber, String date) {
			this.version = version;
			this.revision = revision;
			this.svnnumber = svnnumber;
			this.date = date;
		}
	}

	public static void main(String[] args) throws Exception {
		var userHome = new File(System.getProperty("user.home"), "Documents").getAbsoluteFile();

		var records = new Stack<Version>();
		try (var stream = new FileInputStream(new File(userHome, "Produktversionen.xml"))) {
			StAX.parse(stream, (n, a, c) -> {
				switch (n) {
					case "Produktversion":
						records.push(new Version(
							a.get("Version"), a.get("Revision"),
							a.get("SVNNummer"), a.get("Releasedatum")
						));
						break;
					case "Name":
						records.peek().title = a.get("Text");
						break;
					case "Produkt":
						records.peek().code = a.get("Code");
						break;
				}
			});
		}

		var vid = 100;
		var products = PDVersion.getProducts();
		for (var record : records) {
			var rev = record.revision == null ? "": record.revision;
			var code = products.getOrDefault(record.code, -1);
			if (code < 0)
				System.out.printf(
					"""
						insert into sdtaskprodver(id, id_tenant, id_betrieb, plname, version, revision, svnnummer, reldate)
							values (%s, 1, 1, '%s', %s, '%s', %s, '%s');
						""",
					vid++, record.title, record.version, rev, record.svnnumber, record.date
				);
			else
				System.out.printf(
					"""
						insert into sdtaskprodver(id, id_tenant, id_betrieb, plname, version, revision, svnnummer, reldate, id_produkt)
							values (%s, 1, 1, '%s', %s, '%s', %s, '%s', %s);
						""",
					vid++, record.title, record.version, rev, record.svnnumber, record.date, code
				);
		}

//		for (var record : records) {
//			System.out.printf(
//				"""
//					new PDVersion(%s, "%s", %s, "%s"),
//					""",
//				record.version, record.revision, record.svnnumber, record.code
//			);
//		}
	}
}
