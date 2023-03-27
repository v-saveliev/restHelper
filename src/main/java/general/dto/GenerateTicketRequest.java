package general.dto;

import lombok.Data;

@Data
public class GenerateTicketRequest {
    private String varToken;
    private String identifier;
    private String signer_fname;
    private String signer_inn;
    private String signer_position;
    private String signer_sname;
    private boolean isRecall;
    private String comment;

}
