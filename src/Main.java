import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    try {
      AgentCustomerMatcher matcher = new AgentCustomerMatcher();

      // Here you should create Attribute instances for your Agents and Customers

      // Define agent1 attributes
      List<Attribute> agent1Attributes = new ArrayList<>();
      agent1Attributes.add(new Attribute("Language", 3));

      // Define agent2 attributes
      List<Attribute> agent2Attributes = new ArrayList<>();
      agent2Attributes.add(new Attribute("Language", 4));

      // Define customer attributes
      List<Attribute> customerAttributes = new ArrayList<>();
      customerAttributes.add(new Attribute("Language", 3));

      // Create Agent and Customer instances with their respective attributes
      Agent agent1 = new Agent("1", agent1Attributes);
      Agent agent2 = new Agent("2", agent2Attributes);
      Customer customer = new Customer("1", customerAttributes);

      // Index the agents
      matcher.indexAgent(agent1);
      matcher.indexAgent(agent2);

      // Find matching agents for the customer
      List<Agent> matchingAgents = matcher.findMatchingAgents(customer);

      for (Agent agent : matchingAgents) {
        System.out.println("Matching agent: " + agent.getId());
      }
    } catch (IOException e) {
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