package br.com.bobwizley.rootboot.tools.spawners;

final class WorldScanException extends Exception {
    WorldScanException(String message) {
        super(message);
    }

    WorldScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
