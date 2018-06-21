package ru.cubesolutions.etl.dbreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ru.cubesolutions.rabbitmq.Producer;
import ru.cubesolutions.rabbitmq.RabbitConfig;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Garya on 08.04.2018.
 */
public class Run implements Runnable {

    private final static Logger log = Logger.getLogger(Run.class);

    public static void main(String[] args) throws IOException {
        Run run = new Run();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(run, 0, Config.TIME_BETWEEN_STEPS_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    private void push() {
        log.info("Reader task started..");
        Long lastId;
        try {
            lastId = Long.parseLong(new String(Files.readAllBytes(Paths.get("last-id"))));
        } catch (NoSuchFileException e) {
            lastId = DB.getInitialId(Config.INITIAL_ID_SQL);
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        log.info("Last id is " + lastId);

        long start = System.currentTimeMillis();
        List<Map<String, String>> eventMaps = DB.getRecords(Config.SQL, lastId, Config.STEP_VALUE);
        log.info(eventMaps.size() + " records is read, " + (System.currentTimeMillis() - start) + "ms");

        if (eventMaps.isEmpty()) {
            log.debug("eventMaps.isEmpty()");
            return;
        }
        log.debug("eventMaps is not empty");

        log.debug("Config.MQ_HOST=" + Config.MQ_HOST);
        log.debug("Config.MQ_PORT=" + Config.MQ_PORT);
        log.debug("Config.MQ_V_HOST=" + Config.MQ_V_HOST);
        log.debug("Config.MQ_USER=" + Config.MQ_USER);
        log.debug("Config.MQ_PASSWORD=" + Config.MQ_PASSWORD);

        RabbitConfig rabbitConfig = new RabbitConfig(Config.MQ_HOST, Config.MQ_PORT, Config.MQ_V_HOST, Config.MQ_USER, Config.MQ_PASSWORD);
        log.debug("rabbitConfig");
        Producer producer;
        log.debug("producer");
        try {
            producer = new Producer(rabbitConfig);
            log.debug("producer = new Producer(rabbitConfig);");
            producer.queueDeclare(Config.QUEUE);
            log.debug("producer.queueDeclare(Config.QUEUE);");
        } catch (IOException e) {
            log.error("Can't connect to rabbitmq", e);
            throw new RuntimeException("Can't connect to rabbitmq", e);
        }

        ObjectMapper mapper = new ObjectMapper();
        log.debug("ObjectMapper mapper = new ObjectMapper();");
        try {
            long startPush = System.currentTimeMillis();
            for (Map<String, String> eventMap : eventMaps) {
                byte[] eventBytes = mapper.writer().writeValueAsBytes(eventMap);
                producer.sendMessage(eventBytes, "", Config.QUEUE);
                log.debug("producer.sendMessage(eventBytes, \"\", Config.QUEUE);");
            }
            log.info(eventMaps.size() + " record is pushed, " + (System.currentTimeMillis() - startPush) + "ms");
            producer.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            throw new RuntimeException("Can't parse json", e);
        }

        try {
            Files.write(Paths.get("last-id"), ("" + findMax(eventMaps)).getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
            throw new RuntimeException("Can't write last id", e);
        }

        log.info("Reader task stopped..");
    }

    private Long findMax(List<Map<String, String>> eventMaps) {
        if (eventMaps == null || eventMaps.isEmpty()) {
            return null;
        }
        long max = -1;
        for (Map<String, String> eventMap : eventMaps) {
            long current = Long.parseLong(eventMap.get(Config.FIELD_ID_NAME));
            if (current > max) {
                max = current;
            }
        }
        return max;
    }


    @Override
    public void run() {
        push();
    }
}
