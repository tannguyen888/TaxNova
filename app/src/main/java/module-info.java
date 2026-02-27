module com.taxnova {
    // ==== JavaFX ====
    requires lombok;
    requires info.picocli;
    requires javafx.web;
    requires java.desktop;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires io.xpipe.modulefs;

    // ==== UI Theme ====
    requires atlantafx.base;

    // ==== JSON ====
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    // ==== Logging ====
    requires org.slf4j;

    // ==== Utility ====
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;

    // ==== Icons ====
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;

    // ==== Sentry ====
    requires io.sentry;

    // ==== AtlantaFX Sampler ====
    requires atlantafx.sampler;

    // ==== Monkey Tester ====
    requires monkey_tester;

    // ==== Markdown ====
    requires com.vladsch.flexmark;

    // ==== JVM Management ====
    requires java.management;
    requires jdk.management;

    // ==== GitHub API ====
    requires org.kohsuke.github;

    // ==== Exports ====
    exports io.abc_def.kickstart_fx;
    exports io.abc_def.kickstart_fx.comp;
    exports io.abc_def.kickstart_fx.core;
    exports io.abc_def.kickstart_fx.issue;
    exports io.abc_def.kickstart_fx.page;
    exports io.abc_def.kickstart_fx.platform;
    exports io.abc_def.kickstart_fx.prefs;
    exports io.abc_def.kickstart_fx.update;
    exports io.abc_def.kickstart_fx.util;

    opens io.abc_def.kickstart_fx.core to
            javafx.fxml;
    opens io.abc_def.kickstart_fx.comp to
            javafx.fxml;
}
