
package uy.com.md.common.dto;

import java.util.Map;

public class StatusResponseDto {

    protected String version;
    public String getVersion() {return version;}
    public void setVersion(String version) {this.version = version;}
    
    protected Map<String, String> additionalInfo;
    public Map<String, String> getAdditionalInfo() {return additionalInfo;}
    public void setAdditionalInfo(Map<String, String> additionalInfo) {this.additionalInfo = additionalInfo;}
    
    protected Boolean ok;
    public Boolean getOk() {return ok;}
    public void setOk(Boolean ok) {this.ok = ok;}
    
    protected String message;
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
    
}

