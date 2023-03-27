package general.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
public class Authorization {
    private String varLogin;
    private String varPassword;
}
