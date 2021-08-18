package app.oengus.service.export;

import javassist.NotFoundException;

import java.io.IOException;
import java.io.Writer;

@FunctionalInterface
public interface Exporter {
    Writer export(String marathonId, String zoneId, String locale) throws IOException, NotFoundException;
}
