package mr.dps.ole;

import lombok.extern.slf4j.Slf4j;
import mr.dps.ole.service.OleImportService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class OleUpdateCarteImagesApplication
{
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(OleUpdateCarteImagesApplication.class, args);
        try {
            context.getBean(OleImportService.class).run();
        } catch (Exception e) {
            log.error("Error : {} :", e);
        }
        context.close();

    }
}
