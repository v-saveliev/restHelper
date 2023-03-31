package general.dto;

import lombok.Data;

@Data
public class GetTicketRequest {
    private String shardUUID;
    private String docUUID;
    private boolean loadBody = true;
    private String transactionType;
}
