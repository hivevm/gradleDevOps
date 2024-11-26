/*
 * Manifest  2025-10-31
 *
 * Copyright (c) Pro Data GmbH & ASA KG. All rights reserved.
 */

package org.hivevm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Manifest
 *
 * @author Markus Brigl
 * @since 2025-10-31
 */
public class Manifest {

	private static final Pattern PATTERN_BRANCH1 	= Pattern.compile("^refs/heads/(?:(main|trunk)|release/(\\d{4}))$");
	private static final Pattern PATTERN_BRANCH0 	= Pattern.compile("^refs/heads/(main|trunk|release/(\\d{4}))$");

	private static final Pattern PATTERN_BRANCH 	= Pattern.compile("^refs/heads/([^/]+)(?:/([^/]+))?$");
	private static final Pattern PATTERN_MESSAGE 	= Pattern.compile("([^\\n-]+)-([\\s\\d]+)-");


	public static void main(String[] args) throws Exception {
		var matcher = PATTERN_MESSAGE.matcher("""
			Merged revision(s) 144495 from Ombis/trunk:
			
			Framework - 44838 - Fehler beim Versenden einer DMS-Verknüpfung im Dateisystem in der Swing:
			- Wrappen der Exception vereinfacht.
			........
			Merged revision(s) 144492 from Ombis/trunk:
			Framework - 44838 - Fehler beim Versenden einer DMS-Verknüpfung im Dateisystem in der Swing:
			- Falls der Request mit einer Exception scheitert, wird die ursprüngliche Exception nun auch gewrappt weitergeworfen, nicht mehr nur als Message.
			........
			
			Merged revision(s) 144489 from Ombis/trunk:
			Framework - 44838 - Fehler beim Versenden einer DMS-Verknüpfung im Dateisystem in der Swing:
			- Der Fehler kommt, weil die Mail-Bibliothek die Transfer-Encoding ermittelt, indem der Content ein Stück weit gelesen wird, bis das Encoding feststeht.
			  Hier wirft der Jetty InputStreamResponseListener eine Exception, weil der InputStream frühzeitig geschlossen wird (AsynchronousCloseException).
			  Jetty kommt hier zum Einsatz, wenn das DMS im Dateisystem abgelegt wird und die Dateien über den Master abgefragt werden.\s
			- Außerdem wird dadurch Content zweimal abgefragt: einmal für die Ermittlung des Encodings, und einmal zum Versenden der Daten.
			  Beim Versenden der Daten wurde dann fälschlicherweise der ursprüngliche Request wiederverwendet, der sich auch die vorherige Exception noch gemerkt hat und sofort abbricht.
			- Nun wird bei jedem Aufruf von asInputStream() ein neuer Request erstellt, wodurch der Versand wieder funktioniert.
			- Die Exception beim Ermitteln des Encoding wird intern weiterhin geworfen, hat aber auf das Ergebnis keinen Einfluss.
			  Damit sie nicht im Log aufscheint, wird nun StdHttpClient.onSendFailure speziell überschrieben.
			........
			
			
			
			git-svn-id: svn://svr-develop/develop/Ombis/branches/Release-V2510@144496 3af5239e-d834-e041-af38-f081a51c06f8
			47c8a3
			
			Hotel - 44795 - Saldendaten: Neuer Filter für Reservierungssalden
			- 0-Beträge werden nun herausgefiltert
			- Abrechnungskonto wurde nicht befüllt
			- Unnötige Abfragen von Reservierungen/Abrechnungskonten ausgebaut
			........
			Merged revision(s) 144425 from Ombis/trunk:
			Hotel - 44795 - Saldendaten: Neuer Filter für Reservierungssalden
			- Die Reservierungssalden werden nun aus den FiBu-Erlösen ermitteln. Somit wird sichergestellt, dass die Summe der Felder "Nicht verrechnete Erlöse" und "in Folgeperioden verrechnete Erlöse" (Saldenkontrolle) mit der Summe der Beträge der Reservierungssalden übereinstimmt.
			- Es werden nun auch Passantenkonten berücksichtigt.
			- Gast und Firma in den Reservierungssalden als Referenzen hinzugefügt
			........
			""");
		while(matcher.find())
			System.out.println(matcher.group(1).trim() + " - " + matcher.group(2).trim());


		matcher = PATTERN_MESSAGE.matcher("Delivery Web - 44763 - Ombis Delivery Web: Design-Optimierung Thekenmodus\n* Design Umbau\n\ngit-svn-id: svn://svr-develop/develop/Ombis/trunk@144503 3af5239e-d834-e041-af38-f081a51c06f8\n");
		while(matcher.find())
			System.out.println(matcher.group(1).trim() + " - " + matcher.group(2).trim());


		var branches = Arrays.asList("main", "trunk", "feature", "feature/git", "release/2510", "release/25101", "release/2511");
		for (String branch : branches) {
			matcher = PATTERN_BRANCH0.matcher("refs/heads/" + branch);
			var found = matcher.find();
			System.out.printf("%s => %s, %s\n", branch, found, found ? matcher.group(2) : null);
		}
	}
}
