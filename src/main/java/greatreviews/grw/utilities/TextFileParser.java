package greatreviews.grw.utilities;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Component
public class TextFileParser {

    @Autowired
    private Environment env;


    @SneakyThrows
    public String parseFileAt(String sourceLocation)  {
        StringBuilder sb = new StringBuilder();
        File f = new File(sourceLocation);

        @Cleanup
        BufferedReader br = new BufferedReader(new FileReader(f));

        String line = br.readLine();
        while(line != null){
            sb.append(line);
            line = br.readLine();
        }

        return sb.toString();
    }

}
