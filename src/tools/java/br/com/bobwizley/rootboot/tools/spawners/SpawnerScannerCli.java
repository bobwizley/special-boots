package br.com.bobwizley.rootboot.tools.spawners;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

public final class SpawnerScannerCli {
    private static final Gson JSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private SpawnerScannerCli() {
    }

    public static void main(String[] arguments) {
        System.exit(run(arguments, System.out, System.err));
    }

    public static int run(String[] arguments, PrintStream output, PrintStream error) {
        if (arguments.length != 1) {
            error.println("Usage: scanSpawners <world-path>");
            return 1;
        }

        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            String minecraftVersion = SharedConstants.getCurrentVersion().name();
            ScanReport report = new WorldScanner().scan(Path.of(arguments[0]), minecraftVersion);
            for (ScanError scanError : report.errors()) {
                error.println(scanError.scope() + ": " + scanError.message());
            }
            output.println(JSON.toJson(report));
            return report.complete() ? 0 : 2;
        } catch (InvalidPathException | WorldScanException exception) {
            error.println(exception.getMessage());
            return 1;
        }
    }
}
