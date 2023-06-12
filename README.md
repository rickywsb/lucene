Interfaces and Class Implementation Design

Our system will need several interfaces to provide a flexible, maintainable architecture. Here's a description of each interface and its role:

IndexManager: This interface handles the addition, updating, and removal of customers in our Lucene index. It interacts directly with the underlying index, performing necessary operations as data comes in or needs to be modified.

CustomerSearcher: This interface searches the indexed data based on an agent's skills. It returns a list of customers that can be serviced by a given agent.

DataTransformer: This interface transforms the raw data coming from the internal API into a format that our Lucene system can ingest. It also transforms the output of our Lucene queries back into a form suitable for consumption by the rest of our system.

ResultPruner: This interface prunes the search results returned by Lucene. It could filter out customers based on various criteria that are not covered in the Lucene query.

Here are the interfaces and a possible skeleton implementation:

java
Copy code
// IndexManager interface
public interface IndexManager {
    void add(Customer customer) throws IOException;
    void update(Customer customer) throws IOException;
    void remove(String customerId) throws IOException;
}

// CustomerSearcher interface
public interface CustomerSearcher {
    List<Customer> findMatchingCustomers(Agent agent) throws IOException;
}

// DataTransformer interface
public interface DataTransformer {
    Customer transformInputToCustomer(String rawData);
    String transformOutputToString(Customer customer);
}

// ResultPruner interface
public interface ResultPruner {
    List<Customer> prune(List<Customer> customers);
}

// Possible IndexManager implementation
public class LuceneIndexManager implements IndexManager {
    private IndexWriter writer;

    public LuceneIndexManager(IndexWriter writer) {
        this.writer = writer;
    }

    // Add, update, remove methods...

    private Document createDocumentFromCustomer(Customer customer) {
        // Document creation logic...
    }
}

// Possible CustomerSearcher implementation
public class LuceneCustomerSearcher implements CustomerSearcher {
    private IndexSearcher searcher;

    public LuceneCustomerSearcher(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public List<Customer> findMatchingCustomers(Agent agent) throws IOException {
        // Lucene query construction and execution...
    }
}
In the LuceneIndexManager and LuceneCustomerSearcher classes, we use Lucene's IndexWriter and IndexSearcher to interact with the index. Each customer's details are first converted into a Lucene Document before being written to the index or searched. These classes provide basic templates and would need to be expanded to offer full functionality.



public class LuceneIndexManager implements IndexManager {
    private IndexWriter writer;

    public LuceneIndexManager(IndexWriter writer) {
        this.writer = writer;
    }

    public void add(Customer customer) throws IOException {
        Document doc = createDocumentFromCustomer(customer);
        writer.addDocument(doc);
        writer.commit();
    }

    public void update(Customer customer) throws IOException {
        Term term = new Term("id", customer.getId());
        Document doc = createDocumentFromCustomer(customer);
        writer.updateDocument(term, doc);
        writer.commit();
    }

    public void remove(String customerId) throws IOException {
        Term term = new Term("id", customerId);
        writer.deleteDocuments(term);
        writer.commit();
    }

    private Document createDocumentFromCustomer(Customer customer) {
        Document doc = new Document();
        doc.add(new StringField("id", customer.getId(), Field.Store.YES));

        for (Attribute attribute : customer.getAttributes()) {
            doc.add(new StringField("attribute", attribute.getName(), Field.Store.YES));
            doc.add(new IntPoint("proficiency", attribute.getProficiency()));
            doc.add(new StoredField("proficiency", attribute.getProficiency()));
        }

        doc.add(new LongPoint("enqueueTime", customer.getEnqueueTime()));
        doc.add(new StoredField("enqueueTime", customer.getEnqueueTime()));

        return doc;
    }
}

    Use Case:
We have an agent who is an expert with a proficiency level of 5 in multiple skills. In our contact center, we want to ensure that this expert agent is allocated to customers who require a high level of proficiency, specifically greater than or equal to 4. This is because an agent with a high proficiency level might be overqualified for customers with lower proficiency requirements. Therefore, the system should only consider customers with a proficiency requirement of at least 4 for this particular agent.

Solution:
We can achieve this use case in our Lucene project by creating a query for each of the agent's skills that checks if the customer's proficiency requirement for that skill is at least 4. Here is how we can structure it in Java:

public List<Customer> findMatchingCustomers(Agent agent) throws IOException {
List<Customer> matchingCustomers = new ArrayList<>();

```
// This is the final query that we'll be adding to.
BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();

for (Attribute attribute : agent.getAttributes()) {
    if(attribute.getProficiency() > 4) {
        // Create a range query for the proficiency level
        Query rangeQuery = IntPoint.newRangeQuery("proficiency", 4, 5);  // 4 to 5
        Query termQuery = new TermQuery(new Term("attribute", attribute.getName()));

        // Combine the queries with AND logic
        Query combinedQuery = new BooleanQuery.Builder()
                .add(termQuery, BooleanClause.Occur.MUST)
                .add(rangeQuery, BooleanClause.Occur.MUST)
                .build();

        // Add the combined query to the final query with OR logic
        finalQuery.add(combinedQuery, BooleanClause.Occur.SHOULD);
    }
}

// Search for the customers
TopDocs topDocs = searcher.search(finalQuery.build(), MAX_RESULTS);

// Process the results
for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
    Document doc = searcher.doc(scoreDoc.doc);
    Customer customer = new Customer();
    customer.setId(doc.get("id"));
    // Populate other fields here...

    matchingCustomers.add(customer);
}

return matchingCustomers;

```

}
    
    
    Iterate over each skill of the expert agent.
For each skill, create a Lucene range query that checks if the customer's proficiency requirement for that skill is greater than or equal to 4. In Lucene, we can use the IntPoint.newRangeQuery() method for this.
Combine these range queries using a BooleanQuery, which allows us to apply an AND condition. This ensures that we only find customers who meet the proficiency requirements for each of the expert agent's skills.
Execute this combined query using the IndexSearcher.search() method to get a list of matching customers.





# One pager three questions

**Lucene Search Improvement Design Document**

**Objective:**
To enhance the proficiency matching system to address three key problems:

1. Subset Problem: An agent's skills being a subset of a customer's requirements.
2. Proficiency Level Ranges: A customer's proficiency requirement being a "greater than" or "less than" condition.
3. Complex Boolean Logic: A customer's requirement involving an OR condition or more complex logic.

**Approach:**

1. **Subset Problem - Using Global Map**
To ensure we only match agents to customers when the agent's skills entirely cover the customer's requirements, we will create a global map containing the full set of skills and attributes across all agents. This will allow us to identify any skills that an agent lacks.
    
    Before querying Lucene, we will check the agent's skills against the global map to determine any missing skills. These missing skills will then be included in the Lucene query as NOT requirements, ensuring that the results only include customers whose requirements are fully covered by the agent's skills.
    

For example, when we have Agent(P1 : 3, P2:4), All skill sets (P1, P2, P3, P4, P5), 

Contact1(P1:2) Contact2(P2:3) Contact3(P1:2 and P2:5) Contact4(P1:2 and P2:3 and P3:1)

Before going to query, we first check with the global map that the agent does not have P3 to P5

Query **`(P1:[* TO 3] OR P2:[* TO 4]) NOT P3 NOT P4 NOT P5 ...`**

return  Contact1 and Contact2

1. **Proficiency Level Ranges - Using minProficiency and maxProficiency**
To support "greater than" or "less than" conditions in a customer's proficiency requirements, we will add **`minProficiency`** and **`maxProficiency`** fields for each skill in the Lucene document. These fields will define a range of acceptable proficiency levels for the skill.
    
    For instance, when a customer has a requirement like "P1 >= 3", we will transform this into a Lucene indexable format, setting **`minProficiency`** to 3 and **`maxProficiency`** to 5 (assuming 5 is the highest possible level).
    
    - If **`exactProficiency`** is not null, this means that the customer requires a specific proficiency level. In this case, we would want to search only for agents with that specific proficiency. So the query would be: **`"English": "exactProficiency"`**. For example, if **`exactProficiency`** is 3, we would want to search for agents with English proficiency of exactly 3.
    - If **`exactProficiency`** is null, then we check **`minProficiency`** and **`maxProficiency`**. If they are not null, this means the customer has a range of acceptable proficiency levels. So the query would be: **`"English": "[minProficiency TO maxProficiency]"`**. For example, if **`minProficiency`** is 2 and **`maxProficiency`** is 4, we would want to search for agents with English proficiency between 2 and 4.
    - If all three values are null, this means the customer has no requirement for this particular proficiency. We can either exclude this proficiency from the query, or we can treat it as if the customer has a requirement of any proficiency level. In this case, the query would be: **`"English": "[* TO *]"`**, which would match any agent regardless of their English proficiency level.
    
    if (customer.exactProficiency != null) {
    query = "English:" + customer.exactProficiency;
    } else if (customer.minProficiency != null && customer.maxProficiency != null) {
    query = "English:[" + customer.minProficiency + " TO " + customer.maxProficiency + "]";
    } else {
    query = "English:[* TO *]";
    }
    

1. **Complex Boolean Logic - Using Multiple Lucene Documents**
For customers with requirements involving an OR condition or more complex logic, we will create multiple Lucene documents, each representing a different OR clause.
    
    For instance, if a customer's requirement is "(P1 >= 3 AND P2 <= 2) OR P3 = 1", we will index this as two separate documents in Lucene:
    
    - Document 1 for the condition "P1 >= 3 AND P2 <= 2"
    - Document 2 for the condition "P3 = 1"
    
    Step 1: Check with Global Map
    
    Our Global Map might look like this:
    
    ```
    
    GlobalMap = {P1, P2, P3, P4, P5}
    
    ```
    
    Step 2: Constructing the Query
    
    We construct the Lucene query with the known proficiencies of the agent (P1 and P2) and add a NOT clause for each proficiency the agent does not possess.
    
    query = "P1:[* TO 4]" OR "P2:[* TO 3]" AND NOT "P3:[* TO *]" AND NOT "P4:[* TO *]" AND NOT "P5:[* TO *]"
