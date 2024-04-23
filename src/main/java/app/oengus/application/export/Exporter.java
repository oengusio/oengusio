package app.oengus.application.export;

import javassist.NotFoundException;

import java.io.IOException;
import java.io.Writer;

public interface Exporter {

	Writer export(String marathonId, String zoneId, String locale) throws IOException, NotFoundException;

}
