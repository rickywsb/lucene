import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
  private String id;
  private List<Attribute> attributes;
  private long enqueueTime;

//  public Customer(String id, List<Attribute> attributes) {
//   this.id = id;
//   this.attributes = attributes;
//    this.enqueueTime = enqueueTime;
// }
//
//  public List<Attribute> getAttributes(){
//    return attributes;
//  }
//
//  public long getEnqueueTime(){
//    return enqueueTime;
//  }
//
//  public String getId(){
//    return id;
////  }

}