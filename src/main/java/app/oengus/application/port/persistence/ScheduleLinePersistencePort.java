package app.oengus.application.port.persistence;

public interface ScheduleLinePersistencePort {
    /**
     * Updates all lines and replaces {oldRunnerId} with {newRunnerId}. Mostly used to move runners to Wibby.
     */
    void reassignRunner(int oldRunnerId, int newRunnerId);
}
