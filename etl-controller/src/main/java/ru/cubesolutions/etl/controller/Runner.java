package ru.cubesolutions.etl.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import ru.cubesolutions.etl.clickhousepusher.DestConfig;
import ru.cubesolutions.etl.clickhousepusher.DestRunner;
import ru.cubesolutions.etl.dbreader.SrcConfig;
import ru.cubesolutions.etl.dbreader.SrcRunner;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ru.cubesolutions.etl.clickhousepusher.Utils.isNotNull;
import static ru.cubesolutions.etl.clickhousepusher.Utils.isNull;

/**
 * Created by Garya on 05.07.2018.
 */
public class Runner {

    private final static Logger log = Logger.getLogger(Runner.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        if (args == null || args.length == 1) {
            log.info("type:");
            log.info("-src <file properties name of source> (optional)");
            log.info("-dest <file properties name of destination> (optional)");
            return;
        }
        String srcProps = null;
        String destProps = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-src")) {
                srcProps = args[i + 1];
            }
            if (args[i].equals("-dest")) {
                destProps = args[i + 1];
            }
        }

        if (isNotNull(srcProps)) {
            SrcRunner srcRunner = new SrcRunner(new SrcConfig(srcProps));
            if (isNull(destProps)) {
                srcRunner.run();
            } else {
                Executor exec = Executors.newSingleThreadExecutor();
                exec.execute(srcRunner::run);
            }
        }
        if (isNotNull(destProps)) {
            DestRunner destRunner = new DestRunner(new DestConfig(destProps));
            destRunner.run();
        }
    }

}
