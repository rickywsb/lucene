import java.io.IOException;
import java.util.ArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    try {
      AgentCustomerMatcher matcher = new AgentCustomerMatcher();

      // Define attributes for agent and customer
      List<Attribute> agentAttributes = new ArrayList<>();
      agentAttributes.add(new Attribute("English", 5));
      agentAttributes.add(new Attribute("Chinese", 2));

      List<Attribute> customer1Attributes = new ArrayList<>();
      customer1Attributes.add(new Attribute("English", 3));
      customer1Attributes.add(new Attribute("Chinese", 1));

      List<Attribute> customer2Attributes = new ArrayList<>();
      customer2Attributes.add(new Attribute("English", 5));

      // Create instances of Agent and Customers
      Agent agent = new Agent("1", agentAttributes);

      Customer customer1 = new Customer("1", customer1Attributes, System.currentTimeMillis());
      Thread.sleep(1000);  // To simulate passage of time
      Customer customer2 = new Customer("2", customer2Attributes, System.currentTimeMillis());

      // Index customers
      matcher.indexCustomer(customer1);
      matcher.indexCustomer(customer2);

      // Search for matching customers for the agent
      List<Customer> matchingCustomers = matcher.findMatchingCustomers(agent);

      for (Customer customer : matchingCustomers) {
        System.out.println("Matching customer: " + customer.getId());
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}


//   Input: doc.txt ...
// Using java code to read data from doc.txt # Object obj = ...
// for loop to deal with it
//
// for (sample in obj) {
//    matcher.indexAgent(sample)
// }