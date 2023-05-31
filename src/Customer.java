import lombok.Data;
import java.util.List;

@Data
public class Customer {
  private String id;
  private List<Attribute> attributes;

  public Customer(String id, List<Attribute> attributes) {
    this.id = id;
    this.attributes = attributes;
  }

  public List<Attribute> getAttributes(){
    return attributes;
  }

}