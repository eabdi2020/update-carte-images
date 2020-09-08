package mr.dps.ole.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OleImportService {

    private final JdbcTemplate jdbcTemplate;

    private static final String FILE_NAME = "images/img.jpg";

    @Value("${imapges.rphoto}")
    private String rphoto;
    @Value("${imapges.sphoto}")
    private String sphoto;
    @Value("${imapges.qr}")
    private String qr;

    public OleImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void run() throws IOException {
        this.loadFiles(rphoto).forEach( (id, photo) -> this.saveImage(id, "RPhoto", photo));
        this.loadFiles(sphoto).forEach( (id, photo) -> this.saveImage(id, "SPhoto", photo));
        this.loadFiles(qr).forEach( (id, photo) -> this.saveImage(id, "QR", photo));
    }

    private void saveImage(String id, String fieldName, byte[] bytes) {
        try {
            LobHandler lobHandler = new DefaultLobHandler();
            jdbcTemplate.update(
                    "UPDATE cartes SET " + fieldName +" = ? WHERE MenId = ?",
                    new Object[] {
                            new SqlLobValue(bytes, lobHandler),
                            id,
                    },
                    new int[] {Types.BLOB, Types.VARCHAR});
            log.info("update {} , id : {}, image length : {},", fieldName, id, bytes.length);
        } catch (DataAccessException e) {
            log.error("error when updating {} , id : {}, image length : {},", fieldName, id);
            e.printStackTrace();
        }
    }


    private Map<String, byte[]> loadFiles(String dir) throws IOException {
        Map<String, byte[]> fileList = new HashMap<>();
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (!Files.isDirectory(file)) {
                    byte[] bytes = Files.readAllBytes(file);
                    String fileName = file.getFileName().toString();
                    String[] fileNameParts = fileName.split("\\.");
                    String id = fileNameParts[0];
                    fileList.put(id, bytes);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

}
