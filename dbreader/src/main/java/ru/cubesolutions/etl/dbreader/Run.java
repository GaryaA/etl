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

    private static Long LAST_ID;
    private static Producer producer;

    public static void main(String[] args) throws IOException {
        Run run = new Run();
        initMqProducer();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(run, 0, Config.TIME_BETWEEN_STEPS_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        closeMqProducer();
    }

    private static void initMqProducer() {
        RabbitConfig rabbitConfig = new RabbitConfig(Config.MQ_HOST, Config.MQ_PORT, Config.MQ_V_HOST, Config.MQ_USER, Config.MQ_PASSWORD);
        Producer producerr;
        try {
            producerr = new Producer(rabbitConfig);
            producerr.queueDeclare(Config.QUEUE);
        } catch (IOException e) {
            log.error("Can't connect to rabbitmq", e);
            throw new RuntimeException("Can't connect to rabbitmq", e);
        }
        producer = producerr;
    }

    private static void closeMqProducer() {
        try {
            producer.close();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void push() {
        log.info("Reader task started..");
        Long lastId;
        if (Config.LAST_ID_IN_MEMORY) {
            lastId = LAST_ID != null && LAST_ID > 0 ? LAST_ID : DB.getInitialId(Config.INITIAL_ID_SQL);
        } else {
            try {
                lastId = Long.parseLong(new String(Files.readAllBytes(Paths.get("last-id"))));
            } catch (NoSuchFileException e) {
                lastId = DB.getInitialId(Config.INITIAL_ID_SQL);
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        log.info("Last id is " + lastId);

        long start = System.currentTimeMillis();
        List<Map<String, String>> eventMaps = DB.getRecords(Config.SQL, lastId, Config.STEP_VALUE);
        log.info(eventMaps.size() + " records is read, " + (System.currentTimeMillis() - start) + "ms");

        if (eventMaps.isEmpty()) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            long startPush = System.currentTimeMillis();
            for (Map<String, String> eventMap : eventMaps) {
                byte[] eventBytes = mapper.writer().writeValueAsBytes(eventMap);
                producer.sendMessage(eventBytes, "", Config.QUEUE);
            }
            log.info(eventMaps.size() + " record is pushed, " + (System.currentTimeMillis() - startPush) + "ms");
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException("Can't parse json", e);
        }
        lastId = findMax(eventMaps);
        log.info("last-id is " + lastId);
        if (Config.LAST_ID_IN_MEMORY) {
            LAST_ID = lastId;
        } else {
            try {
                Files.write(Paths.get("last-id"), ("" + lastId).getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e);
                throw new RuntimeException("Can't write last id", e);
            }
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
