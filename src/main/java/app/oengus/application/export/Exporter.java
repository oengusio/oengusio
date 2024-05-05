package app.oengus.application.export;

import java.io.IOException;
import java.io.Writer;

public interface Exporter {

	Writer export(String marathonId, int itemId, String zoneId, String locale) throws IOException;

}
