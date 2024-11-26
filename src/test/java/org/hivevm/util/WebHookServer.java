/*
 * WebHookServer  2025-10-24
 *
 * Copyright (c) Pro Data GmbH & ASA KG. All rights reserved.
 */

package org.hivevm.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * WebHookServer
 *
 * @author Markus Brigl
 * @since 2025-10-24
 */
public class WebHookServer implements HttpHandler {




	@Override
	public void handle(HttpExchange exchange) throws IOException {
		var uri = exchange.getRequestURI().getPath();

		var bytes = exchange.getRequestBody().readAllBytes();
		var content = new String(bytes);
		System.out.println(content);


		System.out.println(uri);
//		var relativePath = uri.substring(ServiceDefaults.UPDATE_PATH.length() + 1);
//		var parts = relativePath.split("/");
//
//		if (parts.length < 1) {
//			exchange.sendResponseHeaders(404, -1);
//			return;
//		}
//
//		var builder = new UpdaterBuilder(name, HOSTNAME, privateKey, WORKING_DIR.toPath());
//		// Get the service.xml
//		if (parts.length == 1) {
//			if (parts[0].equals("service.xml"))
//				handleXmlRequest(exchange, builder.serviceConfig(true));
//			else {
//				var file = new File(builder.getServicePath().toFile(), parts[0]);
//				UpdateServer.handleFileRequest(exchange, file);
//			}
//			return;
//		}
//
//		var family = OS.valueOf(parts[0].toUpperCase());
//		// Get the updater.xml
//		if (parts.length == 2 && parts[1].equals("updater.xml"))
//			handleXmlRequest(exchange, builder.updaterConfig(family));
//			// Get the agent.xml
//		else if (parts.length == 2 && parts[1].equals("agent.xml"))
//			handleXmlRequest(exchange, builder.agentConfig(family));
//			// Get the updater
//		else if (parts.length == 2 && parts[1].startsWith("CloudConLocal")) {
//			var filename = String.format("%s/%s", parts[0], parts[1]);
//			var file = new File(builder.getUpdaterPath().toFile(), filename);
//			UpdateServer.handleFileRequest(exchange, file);
//		}
//		// Get the agent
//		else {
//			var filename = String.format("%s-agent/%s", parts[0], relativePath.substring(parts[0].length() + 1));
//			var file = new File(builder.getAgentPath().toFile(), filename);
//			UpdateServer.handleFileRequest(exchange, file);
//		}
		exchange.sendResponseHeaders(204, -1);
	}

	public static void main(String... args) throws Exception {
		var port = new InetSocketAddress(8085); // 10.150.242.231
		var server = HttpServer.create(port, 0);
		server.createContext("/", new WebHookServer());
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
	}

	private static final String TEXT = """
		{
		  "ref": "refs/heads/main",
		  "before": "c1563c82131748e8fa5bf9b051ebc299ae91bc3a",
		  "after": "de31a044076d9565d1d8b1f4dd1700f2547009f2",
		  "compare_url": "http://localhost:3000/ombis/ombis/compare/c1563c82131748e8fa5bf9b051ebc299ae91bc3a...de31a044076d9565d1d8b1f4dd1700f2547009f2",
		  "commits": [
		    {
		      "id": "de31a044076d9565d1d8b1f4dd1700f2547009f2",
		      "message": "ObstPol - 44634 - AX: Warendimensionsgruppen Part 2 (Logik) - Zwischencommit\\n - Beim Anlieferungslieferschein gibt es nun ein Zeilen- und Listenkommando \\"Merkmalgruppen aktualisieren\\"\\n - Beim Erstellen der Anlieferungsmerkmalgruppen und deren Merkmalkombinationen gibt es nun eine Validierung\\n - Bei den Anlieferungsmerkmalgruppen gibt es nun das Kommando \\"Kopieren nach Jahr\\"\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/trunk@144132 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		      "url": "http://localhost:3000/ombis/ombis/commit/de31a044076d9565d1d8b1f4dd1700f2547009f2",
		      "author": {
		        "name": "Alex Wenter",
		        "email": "alex.wenter@prodata.it",
		        "username": ""
		      },
		      "committer": {
		        "name": "Alex Wenter",
		        "email": "alex.wenter@prodata.it",
		        "username": ""
		      },
		      "verification": null,
		      "timestamp": "2025-10-24T12:25:13Z",
		      "added": [
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/CopyAnlieferungsmerkmalgruppenToYearTask.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/UpdateAnliefLiefMerkmalgruppenTask.java"
		      ],
		      "removed": [],
		      "modified": [
		        "obst-base/src/main/java/it/prodata/gest/obst/einkauf/BsObstAnliefLieferschein.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnliefMerkmalgruppeMerkmalkombination.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnlieferungsmerkmalgruppe.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnlieferungsmerkmalgruppeTyp.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsObstPoolAnliefLieferschein.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnAnlieferungsmerkmalgruppe.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnExtPoolAnliefLieferschein.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnObstPoolAnliefLieferschein.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/RbObstPoolAnliefLieferschein_de.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/RbObstPoolAnliefLieferschein_it.java",
		        "script/cfg/ObstPool/ExtPoolAnliefLieferschein/BTN.FORM.xml"
		      ]
		    },
		    {
		      "id": "1fd0e28146ba0e0f2f1a5d1a1ead492f61d46e9f",
		      "message": "DataHub - 44614 - Zwischen-Commit - Schreiben von konfigurierten Datawarehouse Datenbanken (Teil 2)\\n- Bei der Tabelle ist die Datenbank der Parent und nicht mehr ControllingParent, damit mehrere Benutzer gleichzeitig Tabellen einer Datenbank bearbeiten k�nnen.\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/trunk@144130 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		      "url": "http://localhost:3000/ombis/ombis/commit/1fd0e28146ba0e0f2f1a5d1a1ead492f61d46e9f",
		      "author": {
		        "name": "Andreas Rabensteiner",
		        "email": "andreas.rabensteiner@prodata.it",
		        "username": ""
		      },
		      "committer": {
		        "name": "Andreas Rabensteiner",
		        "email": "andreas.rabensteiner@prodata.it",
		        "username": ""
		      },
		      "verification": null,
		      "timestamp": "2025-10-24T12:22:06Z",
		      "added": [],
		      "removed": [],
		      "modified": [
		        "datahub/src/main/java/it/prodata/gest/datahub/extdwh/BsExtDwhTable.java"
		      ]
		    }
		  ],
		  "total_commits": 2,
		  "head_commit": {
		    "id": "de31a044076d9565d1d8b1f4dd1700f2547009f2",
		    "message": "ObstPol - 44634 - AX: Warendimensionsgruppen Part 2 (Logik) - Zwischencommit\\n - Beim Anlieferungslieferschein gibt es nun ein Zeilen- und Listenkommando \\"Merkmalgruppen aktualisieren\\"\\n - Beim Erstellen der Anlieferungsmerkmalgruppen und deren Merkmalkombinationen gibt es nun eine Validierung\\n - Bei den Anlieferungsmerkmalgruppen gibt es nun das Kommando \\"Kopieren nach Jahr\\"\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/trunk@144132 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		    "url": "http://localhost:3000/ombis/ombis/commit/de31a044076d9565d1d8b1f4dd1700f2547009f2",
		    "author": {
		      "name": "Alex Wenter",
		      "email": "alex.wenter@prodata.it",
		      "username": ""
		    },
		    "committer": {
		      "name": "Alex Wenter",
		      "email": "alex.wenter@prodata.it",
		      "username": ""
		    },
		    "verification": null,
		    "timestamp": "2025-10-24T12:25:13Z",
		    "added": [
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/CopyAnlieferungsmerkmalgruppenToYearTask.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/UpdateAnliefLiefMerkmalgruppenTask.java"
		    ],
		    "removed": [],
		    "modified": [
		      "obst-base/src/main/java/it/prodata/gest/obst/einkauf/BsObstAnliefLieferschein.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnliefMerkmalgruppeMerkmalkombination.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnlieferungsmerkmalgruppe.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnlieferungsmerkmalgruppeTyp.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsObstPoolAnliefLieferschein.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnAnlieferungsmerkmalgruppe.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnExtPoolAnliefLieferschein.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnObstPoolAnliefLieferschein.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/RbObstPoolAnliefLieferschein_de.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/RbObstPoolAnliefLieferschein_it.java",
		      "script/cfg/ObstPool/ExtPoolAnliefLieferschein/BTN.FORM.xml"
		    ]
		  },
		  "repository": {
		    "id": 41,
		    "owner": {
		      "id": 2,
		      "login": "ombis",
		      "login_name": "",
		      "source_id": 0,
		      "full_name": "",
		      "email": "",
		      "avatar_url": "http://localhost:3000/avatars/331e1405232cbfb35bf8f442df868723",
		      "html_url": "http://localhost:3000/ombis",
		      "language": "",
		      "is_admin": false,
		      "last_login": "0001-01-01T00:00:00Z",
		      "created": "2025-03-10T08:11:20+01:00",
		      "restricted": false,
		      "active": false,
		      "prohibit_login": false,
		      "location": "",
		      "website": "",
		      "description": "",
		      "visibility": "public",
		      "followers_count": 0,
		      "following_count": 0,
		      "starred_repos_count": 0,
		      "username": "ombis"
		    },
		    "name": "ombis",
		    "full_name": "ombis/ombis",
		    "description": "",
		    "empty": false,
		    "private": false,
		    "fork": false,
		    "template": false,
		    "mirror": false,
		    "size": 2591622,
		    "language": "",
		    "languages_url": "http://localhost:3000/api/v1/repos/ombis/ombis/languages",
		    "html_url": "http://localhost:3000/ombis/ombis",
		    "url": "http://localhost:3000/api/v1/repos/ombis/ombis",
		    "link": "",
		    "ssh_url": "git@localhost:ombis/ombis.git",
		    "clone_url": "http://localhost:3000/ombis/ombis.git",
		    "original_url": "",
		    "website": "",
		    "stars_count": 0,
		    "forks_count": 0,
		    "watchers_count": 1,
		    "open_issues_count": 0,
		    "open_pr_counter": 0,
		    "release_counter": 0,
		    "default_branch": "main",
		    "archived": false,
		    "created_at": "2025-09-24T14:37:43+02:00",
		    "updated_at": "2025-10-24T14:19:57+02:00",
		    "archived_at": "1970-01-01T01:00:00+01:00",
		    "permissions": {
		      "admin": true,
		      "push": true,
		      "pull": true
		    },
		    "has_issues": true,
		    "internal_tracker": {
		      "enable_time_tracker": true,
		      "allow_only_contributors_to_track_time": true,
		      "enable_issue_dependencies": true
		    },
		    "has_wiki": true,
		    "has_pull_requests": true,
		    "has_projects": true,
		    "projects_mode": "all",
		    "has_releases": true,
		    "has_packages": true,
		    "has_actions": true,
		    "ignore_whitespace_conflicts": false,
		    "allow_merge_commits": true,
		    "allow_rebase": true,
		    "allow_rebase_explicit": true,
		    "allow_squash_merge": true,
		    "allow_fast_forward_only_merge": true,
		    "allow_rebase_update": true,
		    "default_delete_branch_after_merge": false,
		    "default_merge_style": "merge",
		    "default_allow_maintainer_edit": false,
		    "avatar_url": "",
		    "internal": false,
		    "mirror_interval": "",
		    "object_format_name": "sha1",
		    "mirror_updated": "0001-01-01T00:00:00Z",
		    "topics": [],
		    "licenses": []
		  },
		  "pusher": {
		    "id": 1,
		    "login": "gitea",
		    "login_name": "",
		    "source_id": 0,
		    "full_name": "",
		    "email": "gitea@noreply.localhost",
		    "avatar_url": "http://localhost:3000/avatars/08a61c42280936f1384569b99f76d711",
		    "html_url": "http://localhost:3000/gitea",
		    "language": "",
		    "is_admin": false,
		    "last_login": "0001-01-01T00:00:00Z",
		    "created": "2025-01-30T17:06:55+01:00",
		    "restricted": false,
		    "active": false,
		    "prohibit_login": false,
		    "location": "",
		    "website": "",
		    "description": "",
		    "visibility": "public",
		    "followers_count": 0,
		    "following_count": 0,
		    "starred_repos_count": 0,
		    "username": "gitea"
		  },
		  "sender": {
		    "id": 1,
		    "login": "gitea",
		    "login_name": "",
		    "source_id": 0,
		    "full_name": "",
		    "email": "gitea@noreply.localhost",
		    "avatar_url": "http://localhost:3000/avatars/08a61c42280936f1384569b99f76d711",
		    "html_url": "http://localhost:3000/gitea",
		    "language": "",
		    "is_admin": false,
		    "last_login": "0001-01-01T00:00:00Z",
		    "created": "2025-01-30T17:06:55+01:00",
		    "restricted": false,
		    "active": false,
		    "prohibit_login": false,
		    "location": "",
		    "website": "",
		    "description": "",
		    "visibility": "public",
		    "followers_count": 0,
		    "following_count": 0,
		    "starred_repos_count": 0,
		    "username": "gitea"
		  }
		}
		/test
		{
		  "ref": "refs/heads/release/2510",
		  "before": "751f7377ea2bc86d573343254f4bc87b5c19615f",
		  "after": "2563e5ea4f7f0d7394751a76a748cd099cb3b2f5",
		  "compare_url": "http://localhost:3000/ombis/ombis/compare/751f7377ea2bc86d573343254f4bc87b5c19615f...2563e5ea4f7f0d7394751a76a748cd099cb3b2f5",
		  "commits": [
		    {
		      "id": "2563e5ea4f7f0d7394751a76a748cd099cb3b2f5",
		      "message": "Merged revision(s) 144130 from Ombis/trunk:\\nDataHub - 44614 - Zwischen-Commit - Schreiben von konfigurierten Datawarehouse Datenbanken (Teil 2)\\n- Bei der Tabelle ist die Datenbank der Parent und nicht mehr ControllingParent, damit mehrere Benutzer gleichzeitig Tabellen einer Datenbank bearbeiten k�nnen.\\n........\\n\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/branches/Release-V2510@144131 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		      "url": "http://localhost:3000/ombis/ombis/commit/2563e5ea4f7f0d7394751a76a748cd099cb3b2f5",
		      "author": {
		        "name": "Andreas Rabensteiner",
		        "email": "andreas.rabensteiner@prodata.it",
		        "username": ""
		      },
		      "committer": {
		        "name": "Andreas Rabensteiner",
		        "email": "andreas.rabensteiner@prodata.it",
		        "username": ""
		      },
		      "verification": null,
		      "timestamp": "2025-10-24T12:22:32Z",
		      "added": [],
		      "removed": [],
		      "modified": [
		        "datahub/src/main/java/it/prodata/gest/datahub/extdwh/BsExtDwhTable.java"
		      ]
		    },
		    {
		      "id": "aa7ae6f4cee860781533461d96a8b079aaf9c9c6",
		      "message": "Merged revision(s) 144126 from Ombis/trunk:\\nHotel - 44449 - Einzel-Ressourcen k�nnen innerhalb von gebuchten Ressourcen angelegt werden\\n- Nachtrag R143613: Im Formular der Raumreservierung wird nun die Farbe eingeblendet.\\n........\\n\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/branches/Release-V2510@144129 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		      "url": "http://localhost:3000/ombis/ombis/commit/aa7ae6f4cee860781533461d96a8b079aaf9c9c6",
		      "author": {
		        "name": "Fabian.Roalter",
		        "email": "fabian.roalter@asahotel.com",
		        "username": ""
		      },
		      "committer": {
		        "name": "Fabian.Roalter",
		        "email": "fabian.roalter@asahotel.com",
		        "username": ""
		      },
		      "verification": null,
		      "timestamp": "2025-10-24T12:19:37Z",
		      "added": [],
		      "removed": [],
		      "modified": [
		        "script/cfg/Hotel/ResRaum/STD.WEBFORM.xml"
		      ]
		    }
		  ],
		  "total_commits": 2,
		  "head_commit": {
		    "id": "2563e5ea4f7f0d7394751a76a748cd099cb3b2f5",
		    "message": "Merged revision(s) 144130 from Ombis/trunk:\\nDataHub - 44614 - Zwischen-Commit - Schreiben von konfigurierten Datawarehouse Datenbanken (Teil 2)\\n- Bei der Tabelle ist die Datenbank der Parent und nicht mehr ControllingParent, damit mehrere Benutzer gleichzeitig Tabellen einer Datenbank bearbeiten k�nnen.\\n........\\n\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/branches/Release-V2510@144131 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		    "url": "http://localhost:3000/ombis/ombis/commit/2563e5ea4f7f0d7394751a76a748cd099cb3b2f5",
		    "author": {
		      "name": "Andreas Rabensteiner",
		      "email": "andreas.rabensteiner@prodata.it",
		      "username": ""
		    },
		    "committer": {
		      "name": "Andreas Rabensteiner",
		      "email": "andreas.rabensteiner@prodata.it",
		      "username": ""
		    },
		    "verification": null,
		    "timestamp": "2025-10-24T12:22:32Z",
		    "added": [],
		    "removed": [],
		    "modified": [
		      "datahub/src/main/java/it/prodata/gest/datahub/extdwh/BsExtDwhTable.java"
		    ]
		  },
		  "repository": {
		    "id": 41,
		    "owner": {
		      "id": 2,
		      "login": "ombis",
		      "login_name": "",
		      "source_id": 0,
		      "full_name": "",
		      "email": "",
		      "avatar_url": "http://localhost:3000/avatars/331e1405232cbfb35bf8f442df868723",
		      "html_url": "http://localhost:3000/ombis",
		      "language": "",
		      "is_admin": false,
		      "last_login": "0001-01-01T00:00:00Z",
		      "created": "2025-03-10T08:11:20+01:00",
		      "restricted": false,
		      "active": false,
		      "prohibit_login": false,
		      "location": "",
		      "website": "",
		      "description": "",
		      "visibility": "public",
		      "followers_count": 0,
		      "following_count": 0,
		      "starred_repos_count": 0,
		      "username": "ombis"
		    },
		    "name": "ombis",
		    "full_name": "ombis/ombis",
		    "description": "",
		    "empty": false,
		    "private": false,
		    "fork": false,
		    "template": false,
		    "mirror": false,
		    "size": 2591622,
		    "language": "",
		    "languages_url": "http://localhost:3000/api/v1/repos/ombis/ombis/languages",
		    "html_url": "http://localhost:3000/ombis/ombis",
		    "url": "http://localhost:3000/api/v1/repos/ombis/ombis",
		    "link": "",
		    "ssh_url": "git@localhost:ombis/ombis.git",
		    "clone_url": "http://localhost:3000/ombis/ombis.git",
		    "original_url": "",
		    "website": "",
		    "stars_count": 0,
		    "forks_count": 0,
		    "watchers_count": 1,
		    "open_issues_count": 0,
		    "open_pr_counter": 0,
		    "release_counter": 0,
		    "default_branch": "main",
		    "archived": false,
		    "created_at": "2025-09-24T14:37:43+02:00",
		    "updated_at": "2025-10-24T14:19:57+02:00",
		    "archived_at": "1970-01-01T01:00:00+01:00",
		    "permissions": {
		      "admin": true,
		      "push": true,
		      "pull": true
		    },
		    "has_issues": true,
		    "internal_tracker": {
		      "enable_time_tracker": true,
		      "allow_only_contributors_to_track_time": true,
		      "enable_issue_dependencies": true
		    },
		    "has_wiki": true,
		    "has_pull_requests": true,
		    "has_projects": true,
		    "projects_mode": "all",
		    "has_releases": true,
		    "has_packages": true,
		    "has_actions": true,
		    "ignore_whitespace_conflicts": false,
		    "allow_merge_commits": true,
		    "allow_rebase": true,
		    "allow_rebase_explicit": true,
		    "allow_squash_merge": true,
		    "allow_fast_forward_only_merge": true,
		    "allow_rebase_update": true,
		    "default_delete_branch_after_merge": false,
		    "default_merge_style": "merge",
		    "default_allow_maintainer_edit": false,
		    "avatar_url": "",
		    "internal": false,
		    "mirror_interval": "",
		    "object_format_name": "sha1",
		    "mirror_updated": "0001-01-01T00:00:00Z",
		    "topics": [],
		    "licenses": []
		  },
		  "pusher": {
		    "id": 1,
		    "login": "gitea",
		    "login_name": "",
		    "source_id": 0,
		    "full_name": "",
		    "email": "gitea@noreply.localhost",
		    "avatar_url": "http://localhost:3000/avatars/08a61c42280936f1384569b99f76d711",
		    "html_url": "http://localhost:3000/gitea",
		    "language": "",
		    "is_admin": false,
		    "last_login": "0001-01-01T00:00:00Z",
		    "created": "2025-01-30T17:06:55+01:00",
		    "restricted": false,
		    "active": false,
		    "prohibit_login": false,
		    "location": "",
		    "website": "",
		    "description": "",
		    "visibility": "public",
		    "followers_count": 0,
		    "following_count": 0,
		    "starred_repos_count": 0,
		    "username": "gitea"
		  },
		  "sender": {
		    "id": 1,
		    "login": "gitea",
		    "login_name": "",
		    "source_id": 0,
		    "full_name": "",
		    "email": "gitea@noreply.localhost",
		    "avatar_url": "http://localhost:3000/avatars/08a61c42280936f1384569b99f76d711",
		    "html_url": "http://localhost:3000/gitea",
		    "language": "",
		    "is_admin": false,
		    "last_login": "0001-01-01T00:00:00Z",
		    "created": "2025-01-30T17:06:55+01:00",
		    "restricted": false,
		    "active": false,
		    "prohibit_login": false,
		    "location": "",
		    "website": "",
		    "description": "",
		    "visibility": "public",
		    "followers_count": 0,
		    "following_count": 0,
		    "starred_repos_count": 0,
		    "username": "gitea"
		  }
		}
		/test
		{
		  "ref": "refs/heads/trunk",
		  "before": "c1563c82131748e8fa5bf9b051ebc299ae91bc3a",
		  "after": "de31a044076d9565d1d8b1f4dd1700f2547009f2",
		  "compare_url": "http://localhost:3000/ombis/ombis/compare/c1563c82131748e8fa5bf9b051ebc299ae91bc3a...de31a044076d9565d1d8b1f4dd1700f2547009f2",
		  "commits": [
		    {
		      "id": "de31a044076d9565d1d8b1f4dd1700f2547009f2",
		      "message": "ObstPol - 44634 - AX: Warendimensionsgruppen Part 2 (Logik) - Zwischencommit\\n - Beim Anlieferungslieferschein gibt es nun ein Zeilen- und Listenkommando \\"Merkmalgruppen aktualisieren\\"\\n - Beim Erstellen der Anlieferungsmerkmalgruppen und deren Merkmalkombinationen gibt es nun eine Validierung\\n - Bei den Anlieferungsmerkmalgruppen gibt es nun das Kommando \\"Kopieren nach Jahr\\"\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/trunk@144132 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		      "url": "http://localhost:3000/ombis/ombis/commit/de31a044076d9565d1d8b1f4dd1700f2547009f2",
		      "author": {
		        "name": "Alex Wenter",
		        "email": "alex.wenter@prodata.it",
		        "username": ""
		      },
		      "committer": {
		        "name": "Alex Wenter",
		        "email": "alex.wenter@prodata.it",
		        "username": ""
		      },
		      "verification": null,
		      "timestamp": "2025-10-24T12:25:13Z",
		      "added": [
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/CopyAnlieferungsmerkmalgruppenToYearTask.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/UpdateAnliefLiefMerkmalgruppenTask.java"
		      ],
		      "removed": [],
		      "modified": [
		        "obst-base/src/main/java/it/prodata/gest/obst/einkauf/BsObstAnliefLieferschein.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnliefMerkmalgruppeMerkmalkombination.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnlieferungsmerkmalgruppe.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnlieferungsmerkmalgruppeTyp.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsObstPoolAnliefLieferschein.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnAnlieferungsmerkmalgruppe.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnExtPoolAnliefLieferschein.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnObstPoolAnliefLieferschein.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/RbObstPoolAnliefLieferschein_de.java",
		        "obstpool/src/main/java/it/prodata/gest/obst/pool/data/RbObstPoolAnliefLieferschein_it.java",
		        "script/cfg/ObstPool/ExtPoolAnliefLieferschein/BTN.FORM.xml"
		      ]
		    },
		    {
		      "id": "1fd0e28146ba0e0f2f1a5d1a1ead492f61d46e9f",
		      "message": "DataHub - 44614 - Zwischen-Commit - Schreiben von konfigurierten Datawarehouse Datenbanken (Teil 2)\\n- Bei der Tabelle ist die Datenbank der Parent und nicht mehr ControllingParent, damit mehrere Benutzer gleichzeitig Tabellen einer Datenbank bearbeiten k�nnen.\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/trunk@144130 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		      "url": "http://localhost:3000/ombis/ombis/commit/1fd0e28146ba0e0f2f1a5d1a1ead492f61d46e9f",
		      "author": {
		        "name": "Andreas Rabensteiner",
		        "email": "andreas.rabensteiner@prodata.it",
		        "username": ""
		      },
		      "committer": {
		        "name": "Andreas Rabensteiner",
		        "email": "andreas.rabensteiner@prodata.it",
		        "username": ""
		      },
		      "verification": null,
		      "timestamp": "2025-10-24T12:22:06Z",
		      "added": [],
		      "removed": [],
		      "modified": [
		        "datahub/src/main/java/it/prodata/gest/datahub/extdwh/BsExtDwhTable.java"
		      ]
		    }
		  ],
		  "total_commits": 2,
		  "head_commit": {
		    "id": "de31a044076d9565d1d8b1f4dd1700f2547009f2",
		    "message": "ObstPol - 44634 - AX: Warendimensionsgruppen Part 2 (Logik) - Zwischencommit\\n - Beim Anlieferungslieferschein gibt es nun ein Zeilen- und Listenkommando \\"Merkmalgruppen aktualisieren\\"\\n - Beim Erstellen der Anlieferungsmerkmalgruppen und deren Merkmalkombinationen gibt es nun eine Validierung\\n - Bei den Anlieferungsmerkmalgruppen gibt es nun das Kommando \\"Kopieren nach Jahr\\"\\n\\ngit-svn-id: svn://svr-develop/develop/Ombis/trunk@144132 3af5239e-d834-e041-af38-f081a51c06f8\\n",
		    "url": "http://localhost:3000/ombis/ombis/commit/de31a044076d9565d1d8b1f4dd1700f2547009f2",
		    "author": {
		      "name": "Alex Wenter",
		      "email": "alex.wenter@prodata.it",
		      "username": ""
		    },
		    "committer": {
		      "name": "Alex Wenter",
		      "email": "alex.wenter@prodata.it",
		      "username": ""
		    },
		    "verification": null,
		    "timestamp": "2025-10-24T12:25:13Z",
		    "added": [
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/CopyAnlieferungsmerkmalgruppenToYearTask.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/UpdateAnliefLiefMerkmalgruppenTask.java"
		    ],
		    "removed": [],
		    "modified": [
		      "obst-base/src/main/java/it/prodata/gest/obst/einkauf/BsObstAnliefLieferschein.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnliefMerkmalgruppeMerkmalkombination.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnlieferungsmerkmalgruppe.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsAnlieferungsmerkmalgruppeTyp.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/BsObstPoolAnliefLieferschein.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnAnlieferungsmerkmalgruppe.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnExtPoolAnliefLieferschein.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/MnObstPoolAnliefLieferschein.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/RbObstPoolAnliefLieferschein_de.java",
		      "obstpool/src/main/java/it/prodata/gest/obst/pool/data/RbObstPoolAnliefLieferschein_it.java",
		      "script/cfg/ObstPool/ExtPoolAnliefLieferschein/BTN.FORM.xml"
		    ]
		  },
		  "repository": {
		    "id": 41,
		    "owner": {
		      "id": 2,
		      "login": "ombis",
		      "login_name": "",
		      "source_id": 0,
		      "full_name": "",
		      "email": "",
		      "avatar_url": "http://localhost:3000/avatars/331e1405232cbfb35bf8f442df868723",
		      "html_url": "http://localhost:3000/ombis",
		      "language": "",
		      "is_admin": false,
		      "last_login": "0001-01-01T00:00:00Z",
		      "created": "2025-03-10T08:11:20+01:00",
		      "restricted": false,
		      "active": false,
		      "prohibit_login": false,
		      "location": "",
		      "website": "",
		      "description": "",
		      "visibility": "public",
		      "followers_count": 0,
		      "following_count": 0,
		      "starred_repos_count": 0,
		      "username": "ombis"
		    },
		    "name": "ombis",
		    "full_name": "ombis/ombis",
		    "description": "",
		    "empty": false,
		    "private": false,
		    "fork": false,
		    "template": false,
		    "mirror": false,
		    "size": 2591622,
		    "language": "",
		    "languages_url": "http://localhost:3000/api/v1/repos/ombis/ombis/languages",
		    "html_url": "http://localhost:3000/ombis/ombis",
		    "url": "http://localhost:3000/api/v1/repos/ombis/ombis",
		    "link": "",
		    "ssh_url": "git@localhost:ombis/ombis.git",
		    "clone_url": "http://localhost:3000/ombis/ombis.git",
		    "original_url": "",
		    "website": "",
		    "stars_count": 0,
		    "forks_count": 0,
		    "watchers_count": 1,
		    "open_issues_count": 0,
		    "open_pr_counter": 0,
		    "release_counter": 0,
		    "default_branch": "main",
		    "archived": false,
		    "created_at": "2025-09-24T14:37:43+02:00",
		    "updated_at": "2025-10-24T14:19:57+02:00",
		    "archived_at": "1970-01-01T01:00:00+01:00",
		    "permissions": {
		      "admin": true,
		      "push": true,
		      "pull": true
		    },
		    "has_issues": true,
		    "internal_tracker": {
		      "enable_time_tracker": true,
		      "allow_only_contributors_to_track_time": true,
		      "enable_issue_dependencies": true
		    },
		    "has_wiki": true,
		    "has_pull_requests": true,
		    "has_projects": true,
		    "projects_mode": "all",
		    "has_releases": true,
		    "has_packages": true,
		    "has_actions": true,
		    "ignore_whitespace_conflicts": false,
		    "allow_merge_commits": true,
		    "allow_rebase": true,
		    "allow_rebase_explicit": true,
		    "allow_squash_merge": true,
		    "allow_fast_forward_only_merge": true,
		    "allow_rebase_update": true,
		    "default_delete_branch_after_merge": false,
		    "default_merge_style": "merge",
		    "default_allow_maintainer_edit": false,
		    "avatar_url": "",
		    "internal": false,
		    "mirror_interval": "",
		    "object_format_name": "sha1",
		    "mirror_updated": "0001-01-01T00:00:00Z",
		    "topics": [],
		    "licenses": []
		  },
		  "pusher": {
		    "id": 1,
		    "login": "gitea",
		    "login_name": "",
		    "source_id": 0,
		    "full_name": "",
		    "email": "gitea@noreply.localhost",
		    "avatar_url": "http://localhost:3000/avatars/08a61c42280936f1384569b99f76d711",
		    "html_url": "http://localhost:3000/gitea",
		    "language": "",
		    "is_admin": false,
		    "last_login": "0001-01-01T00:00:00Z",
		    "created": "2025-01-30T17:06:55+01:00",
		    "restricted": false,
		    "active": false,
		    "prohibit_login": false,
		    "location": "",
		    "website": "",
		    "description": "",
		    "visibility": "public",
		    "followers_count": 0,
		    "following_count": 0,
		    "starred_repos_count": 0,
		    "username": "gitea"
		  },
		  "sender": {
		    "id": 1,
		    "login": "gitea",
		    "login_name": "",
		    "source_id": 0,
		    "full_name": "",
		    "email": "gitea@noreply.localhost",
		    "avatar_url": "http://localhost:3000/avatars/08a61c42280936f1384569b99f76d711",
		    "html_url": "http://localhost:3000/gitea",
		    "language": "",
		    "is_admin": false,
		    "last_login": "0001-01-01T00:00:00Z",
		    "created": "2025-01-30T17:06:55+01:00",
		    "restricted": false,
		    "active": false,
		    "prohibit_login": false,
		    "location": "",
		    "website": "",
		    "description": "",
		    "visibility": "public",
		    "followers_count": 0,
		    "following_count": 0,
		    "starred_repos_count": 0,
		    "username": "gitea"
		  }
		}
		/test
		""";
}
