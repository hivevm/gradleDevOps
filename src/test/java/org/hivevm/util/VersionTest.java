
package org.hivevm.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

public class VersionTest {

  @Test
  void testFormat() {
    Version version = Version.of(19, 3);
    Assertions.assertEquals("19.3", version.toString());
    Assertions.assertEquals("19.03", version.toString("0.00"));
    Assertions.assertEquals("19.03.0", version.toString("0.00.0"));
    Assertions.assertEquals("19.03", version.toString("0.00-0"));
    Assertions.assertEquals("19.03.0", version.toString("0.00.0+0"));

    version = Version.of(19, 3, 5);
    Assertions.assertEquals("19.3.5", version.toString());
    Assertions.assertEquals("19.03", version.toString("0.00"));
    Assertions.assertEquals("19.03.5", version.toString("0.00.0"));
    Assertions.assertEquals("19.03", version.toString("0.00+0"));
    Assertions.assertEquals("19.03.5", version.toString("0.00.0+0"));

    version = Version.of(19, 3, null, "build1234");
    Assertions.assertEquals("19.3+build1234", version.toString());
    Assertions.assertEquals("19.03", version.toString("0.00"));
    Assertions.assertEquals("19.03.0", version.toString("0.00.0"));
    Assertions.assertEquals("19.03+build1234", version.toString("0.00+0"));
    Assertions.assertEquals("19.03.0+build1234", version.toString("0.00.0+0"));

    version = Version.of(19, 3, 2, null, "build1234");
    Assertions.assertEquals("19.3.2+build1234", version.toString());
    Assertions.assertEquals("19.03", version.toString("0.00"));
    Assertions.assertEquals("19.03.2", version.toString("0.00.0"));
    Assertions.assertEquals("19.03+build1234", version.toString("0.00+0"));
    Assertions.assertEquals("19.03.2+build1234", version.toString("0.00.0+0"));

    version = Version.of(19, 3, "alpha1", "build1234");
    Assertions.assertEquals("19.3-alpha1+build1234", version.toString());
    Assertions.assertEquals("19.03", version.toString("0.00"));
    Assertions.assertEquals("19.03.0", version.toString("0.00.0"));
    Assertions.assertEquals("19.03+build1234", version.toString("0.00+0"));
    Assertions.assertEquals("19.03.0-alpha1+build1234", version.toString("0.00.0-0+0"));
  }

  @Test
  void testOf() throws ParseException {
    Version version = Version.parse("19.03");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(-1, version.patch());
    Assertions.assertNull(version.name());
    Assertions.assertNull(version.build());

    version = Version.parse("19.03.1");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(1, version.patch());
    Assertions.assertNull(version.name());
    Assertions.assertNull(version.build());

    version = Version.parse("19.03+build.1");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(-1, version.patch());
    Assertions.assertNull(version.name());
    Assertions.assertEquals("build.1", version.build());

    version = Version.parse("19.03-rc1");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(-1, version.patch());
    Assertions.assertEquals("rc1", version.name());
    Assertions.assertNull(version.build());

    version = Version.parse("19.03.1+build1234");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(1, version.patch());
    Assertions.assertNull(version.name());
    Assertions.assertEquals("build1234", version.build());

    version = Version.parse("19.03.1-beta+build1234");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(1, version.patch());
    Assertions.assertEquals("beta", version.name());
    Assertions.assertEquals("build1234", version.build());
  }

  @Test
  void testParse() throws ParseException {
    Version version = Version.parse("Sample v.19.03_some text");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(-1, version.patch());
    Assertions.assertNull(version.name());
    Assertions.assertNull(version.build());

    version = Version.parse("Sample v.19.03.1_some text");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(1, version.patch());
    Assertions.assertNull(version.name());
    Assertions.assertNull(version.build());

    version = Version.parse("Sample v.19.03+build.1_some text");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(-1, version.patch());
    Assertions.assertNull(version.name());
    Assertions.assertEquals("build.1", version.build());

    version = Version.parse("Sample v.19.03-rc1-some text");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(-1, version.patch());
    Assertions.assertEquals("rc1", version.name());
    Assertions.assertNull(version.build());

    version = Version.parse("Sample v.19.03.1+build1234_some text");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(1, version.patch());
    Assertions.assertNull(version.name());
    Assertions.assertEquals("build1234", version.build());

    version = Version.parse("Sample v.19.03.1-beta+build1234-some text");
    Assertions.assertEquals(19, version.major());
    Assertions.assertEquals(3, version.minor());
    Assertions.assertEquals(1, version.patch());
    Assertions.assertEquals("beta", version.name());
    Assertions.assertEquals("build1234", version.build());
  }
}
