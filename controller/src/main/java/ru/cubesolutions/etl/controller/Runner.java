package ru.cubesolutions.etl.controller;

import ru.cubesolutions.etl.clickhousepusher.DestConfig;
import ru.cubesolutions.etl.clickhousepusher.DestRunner;
import ru.cubesolutions.etl.dbreader.SrcConfig;
import ru.cubesolutions.etl.dbreader.SrcRunner;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Garya on 05.07.2018.
 */
public class Runner {

    public static void main(String[] args) {
        SrcRunner srcRunner = new SrcRunner(new SrcConfig("etl-reader-public.alfabank_clientprofile_lstat.properties"));
        DestRunner destRunner = new DestRunner(new DestConfig("etl-pusher-public.alfabank_clientprofile_lstat.properties"));

        Executor exec = Executors.newSingleThreadExecutor();
        exec.execute(srcRunner::run);
        destRunner.run();
    }

}
