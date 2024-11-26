// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: MIT


package org.hivevm.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

/**
 *
 */
public abstract class StAX {

	/**
     *
     */
	public static void parse(InputStream stream, Handler handler) throws IOException {
		try {
			var xmlInputFactory = XMLInputFactory.newInstance();
			var reader = xmlInputFactory.createXMLEventReader(stream);

			String tag = null;
			Attributes attrs = null;
			StringBuilder buffer = null;

			while (reader.hasNext()) {
				var event = reader.nextEvent();

				switch (event.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						if (tag != null)
							handler.handleEvent(tag, attrs, null);

						var start = event.asStartElement();
						tag = start.getName().getLocalPart();
						attrs = StAX.parseAttributes(start);
						buffer = null;
						break;

					case XMLStreamConstants.END_ELEMENT:
						var end = event.asEndElement();
						if (tag != null)
							handler.handleEvent(end.getName().getLocalPart(), attrs, buffer != null ? buffer.toString() : null);
						tag = null;
						attrs = null;
						break;

					case XMLStreamConstants.CDATA:
					case XMLStreamConstants.CHARACTERS:
						if (buffer == null)
							buffer = new StringBuilder();
						buffer.append(event.asCharacters().getData());
						break;

					default:
						break;
				}
			}
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	/**
     *
     */
	private static Attributes parseAttributes(StartElement elem) {
		var attributes = new Attributes(new HashMap<>());
		var iterator = elem.getAttributes();
		while (iterator.hasNext()) {
			var name = iterator.next().getName();
			var value = elem.getAttributeByName(name).getValue();
			attributes.attrs.put(name.getLocalPart(), value);
		}
		return attributes;
	}

	/**
	 * The {@link StAX} class.
	 */
	@FunctionalInterface
	public interface Handler {

		void handleEvent(String name, Attributes attributes, String content);
	}

	public record Attributes(Map<String, String> attrs) {

		public boolean isSet(String name) {
			return this.attrs.containsKey(name);
		}

		public String get(String name) {
			return this.attrs.get(name);
		}

		public String get(String name, String value) {
			return isSet(name) ? this.attrs.get(name) : value;
		}

		public boolean getBool(String name) {
			return Boolean.parseBoolean(get(name, "false"));
		}

		public void onAttribute(String name, Consumer<String> consumer) {
			if (this.attrs.containsKey(name)) {
				consumer.accept(this.attrs.get(name));
			}
		}
	}
}
