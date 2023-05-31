import java.io.IOException;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    try {
      AgentCustomerMatcher matcher = new AgentCustomerMatcher();
//   Input: doc.txt ...
      // Using java code to read data from doc.txt # Object obj = ...
      // for loop to deal with it
      //
      // for (sample in obj) {
      //    matcher.indexAgent(sample)
      // }

      // Search
      Agent agent1 = new Agent();
      agent1.setId("1");
      agent1.setSkill("English");
      agent1.setProficiencyLevel(3);
      matcher.indexAgent(agent1);

      Agent agent2 = new Agent();
      agent2.setId("2");
      agent2.setSkill("Spanish");
      agent2.setProficiencyLevel(4);
      matcher.indexAgent(agent2);

      Customer customer = new Customer();
      customer.setId("1");
      customer.setSkill("English");
      customer.setProficiencyLevel(3);

      List<Agent> matchingAgents = matcher.findMatchingAgents(customer);
      for (Agent agent : matchingAgents) {
        System.out.println("Matching agent: " + agent.getId());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}