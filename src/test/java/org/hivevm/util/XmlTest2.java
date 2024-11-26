package org.hivevm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import org.hivevm.util.xml.StAX;

public class XmlTest2 {

	private static boolean isFirst = true;
	private static String number = null;

	public static void main(String[] args) throws Exception {
		var userHome = new File(System.getProperty("user.home"), "Documents").getAbsoluteFile();
		var fileName = "TaskEntwicklungszweige";

		try (var stream = new FileInputStream(new File(userHome, fileName + ".xml"));
			var writer = new PrintWriter(new File(userHome, fileName + ".sql"))

		) {
			writer.println("insert into public.sdtaskentzweig (id, id_tenant, id_betrieb, name, basissvnnr) values");

			StAX.parse(stream, (n, a, c) -> {
				switch (n) {
					case "TaskEntwicklungszweig":
						var name = a.get("Name");
						if (name.startsWith("Release-V")) {
							if (isFirst)
								isFirst = false;
							else
								writer.println(",");
							writer.print(String.format("(%s, 1, 1, '%s', %s)",
								name.substring(name.lastIndexOf('V') + 1),
								name, a.get("BasisSVNNummer")
							));
						}
						break;
				}
			});

			writer.println(";");
		}
		catch (Throwable e) {
			e.printStackTrace();
		}


		isFirst = true;
		fileName = "BacklogList";

		try (var stream = new FileInputStream(new File(userHome, fileName + ".xml"));
			var writer = new PrintWriter(new File(userHome, fileName + ".sql"))

		) {
			writer.println("insert into public.sdtaskbcklog (id, id_tenant, plname, id_betrieb, nr, id_zusatz, id_art, id_bereich, id_thema, id_regvon, svnnummer) values");

			StAX.parse(stream, (n, a, c) -> {
				switch (n) {
					case "Backlog":
						number = a.get("Nummer");
						break;


					case "Name":
						if (isFirst)
							isFirst = false;
						else
							writer.println(",");

						var text = a.get("Text");
						if (text != null)
							text = text.replace("'", "''");
						writer.print(String.format("(%s, 1, '%s', 1, %s, 3, 1, 42, 436, 1, 1)", number, text, number));
						break;
				}
			});

			writer.println(";");
		}
		catch (Throwable e) {
			e.printStackTrace();
		}


//		insert into public.sdtaskbcklog (id, id_tenant, plname, id_betrieb, nr, id_zusatz, id_art, id_bereich, id_thema, id_regvon, svnnummer)
//		values  (5000, 1, 'Tabelle der Aufträge lässt sich nicht öffnen', 1,
//			5000, 3, 1, 42, 436, 1, 1);
	}
}
