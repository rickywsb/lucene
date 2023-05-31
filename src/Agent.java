import lombok.Data;
import java.util.List;

@Data
public class Agent {
  private String id;
  private List<Attribute> attributes;

  public Agent(String id, List<Attribute> attributes) {
    this.id = id;
    this.attributes = attributes;
  }

  public List<Attribute> getAttributes(){
    return attributes;
  }

  public String getId(){
    return id;
  }


}