package general.dto;

import lombok.Data;

@Data
public class AuthorizationResponse {
    private String varToken;
    private String intCode;
    private String varMessage;
}
