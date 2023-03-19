package general.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.beans.BeanProperty;

@Data
@Component
public class Authorization {
    @Value("${authorization.user}")
    private String varLogin;
    @Value("${authorization.password}")
    private String varPassword;
}
