package com.kc.integration.config.constants;

import com.kc.integration.config.AesEncryption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.Base64;

@Configuration
@Slf4j
public class InitConstants {

    File file;

    public static String OS_PATH;

    private void osCheck(){
        String osName = System.getProperty("os.name");
        if(osName.startsWith("Windows")){
            String filePath = new FileSystemResource("").getFile().getAbsolutePath();
            file = new File(filePath,"/CARD_PRINT_SERVICE.conf");
            OS_PATH = filePath + "/";
        }
        else {
            if(osName.startsWith("Linux") || (osName.startsWith("HP-UX")) || (osName.startsWith("Mac"))){
                String fileCatalina = System.getProperty("catalina.base");
                File absolutePath = new File(fileCatalina).getAbsoluteFile();
                file = new File(absolutePath,"bin/CARD_PRINT_SERVICE.conf");
                OS_PATH = fileCatalina + "/bin/";
            }
            else {
                System.out.println("Sorry This File Is Not Supporting for "+osName+" Operating System");
            }
        }
    }

    @Bean
    public void readConfig(){
        osCheck();

//        configure-name = decryptValue(bundle.getString("configure-name"), secretkey)

    }

    private String decryptValue(String keyValue) throws Exception {
        return AesEncryption.decrypt(keyValue);
    }
}
